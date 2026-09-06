package com.bookmall.admin.service.impl;

import com.bookmall.admin.dto.*;
import com.bookmall.admin.entity.Admin;
import com.bookmall.admin.enums.AdminRole;
import com.bookmall.admin.mapper.AdminMapper;
import com.bookmall.admin.service.AdminService;
import com.bookmall.common.constant.Constants;
import com.bookmall.common.exception.BusinessException;
import com.bookmall.common.exception.ExceptionEnum;
import com.bookmall.common.utils.BCryptUtil;
import com.bookmall.common.utils.JwtUtil;
import com.bookmall.common.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 管理员服务实现类
 *
 * @author BookMall Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminMapper adminMapper;
    private final RedisUtil redisUtil;

    // Redis Key前缀 - 管理员Token
    private static final String REDIS_ADMIN_TOKEN_KEY = "admin:token:";

    // ==================== 认证 ====================

    @Override
    public AdminLoginVO login(AdminLoginDTO request, String ip) {
        // 1. 根据用户名查询管理员
        Admin admin = adminMapper.selectByUsername(request.getUsername());
        if (admin == null) {
            throw new BusinessException("管理员不存在");
        }

        // 2. 检查状态
        if (admin.getStatus() == 0) {
            throw new BusinessException("管理员已被禁用");
        }

        // 3. 验证密码
        if (!BCryptUtil.matches(request.getPassword(), admin.getPassword())) {
            throw new BusinessException("密码错误");
        }

        // 4. 生成Token
        String token = JwtUtil.generateToken(admin.getAdminId(), admin.getUsername(), admin.getRole());

        // 5. 缓存Token到Redis
        String cacheKey = REDIS_ADMIN_TOKEN_KEY + admin.getAdminId();
        redisUtil.set(cacheKey, token, Constants.JWT_EXPIRATION, TimeUnit.MILLISECONDS);

        // 6. 更新登录信息
        adminMapper.updateLoginInfo(admin.getAdminId(), ip);

        log.info("管理员登录成功: username={}, ip={}", admin.getUsername(), ip);

        // 7. 返回登录信息
        return AdminLoginVO.builder()
                .adminId(admin.getAdminId())
                .username(admin.getUsername())
                .role(admin.getRole())
                .roleDesc(AdminRole.getDesc(admin.getRole()))
                .realName(admin.getRealName())
                .avatar(admin.getAvatar())
                .token(token)
                .expiresIn(Constants.JWT_EXPIRATION)
                .build();
    }

    @Override
    public void logout(String token) {
        if (StringUtils.hasText(token)) {
            Long adminId = JwtUtil.getUserIdFromToken(token);
            if (adminId != null) {
                redisUtil.delete(REDIS_ADMIN_TOKEN_KEY + adminId);
                log.info("管理员退出登录: adminId={}", adminId);
            }
        }
    }

    // ==================== 信息管理 ====================

    @Override
    public AdminInfoVO getAdminInfo(Long adminId) {
        Admin admin = getAdminEntity(adminId);
        return convertToInfoVO(admin);
    }

    @Override
    public AdminInfoVO getAdminInfoById(Long adminId) {
        Admin admin = getAdminEntity(adminId);
        return convertToInfoVO(admin);
    }

    @Override
    @Transactional
    public void updatePassword(Long adminId, AdminUpdatePasswordDTO request) {
        Admin admin = getAdminEntity(adminId);

        // 验证旧密码
        if (!BCryptUtil.matches(request.getOldPassword(), admin.getPassword())) {
            throw new BusinessException("旧密码错误");
        }

        // 更新密码
        String newEncodedPassword = BCryptUtil.encode(request.getNewPassword());
        adminMapper.updatePassword(adminId, newEncodedPassword);

        // 清除Token缓存，强制重新登录
        redisUtil.delete(REDIS_ADMIN_TOKEN_KEY + adminId);

        log.info("管理员密码修改成功: adminId={}", adminId);
    }

    @Override
    @Transactional
    public void updateProfile(Long adminId, AdminInfoVO request) {
        Admin admin = getAdminEntity(adminId);

        if (StringUtils.hasText(request.getRealName())) {
            admin.setRealName(request.getRealName());
        }
        if (StringUtils.hasText(request.getPhone())) {
            admin.setPhone(request.getPhone());
        }
        if (StringUtils.hasText(request.getEmail())) {
            admin.setEmail(request.getEmail());
        }
        if (StringUtils.hasText(request.getAvatar())) {
            admin.setAvatar(request.getAvatar());
        }

        adminMapper.updateById(admin);

        log.info("管理员信息修改成功: adminId={}", adminId);
    }

    // ==================== 管理员管理 ====================

    @Override
    public List<AdminInfoVO> getAllAdmins() {
        List<Admin> admins = adminMapper.selectAllAdmins();
        return admins.stream()
                .map(this::convertToInfoVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AdminInfoVO> getActiveAdmins() {
        List<Admin> admins = adminMapper.selectActiveAdmins();
        return admins.stream()
                .map(this::convertToInfoVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AdminInfoVO createAdmin(AdminInfoVO request) {
        // 检查用户名是否存在
        if (adminMapper.countByUsername(request.getUsername()) > 0) {
            throw new BusinessException("用户名已存在");
        }

        // 创建管理员
        Admin admin = new Admin();
        admin.setUsername(request.getUsername());
        admin.setPassword(BCryptUtil.encode("123456")); // 默认密码
        admin.setRole(request.getRole());
        admin.setStatus(1);
        admin.setRealName(request.getRealName());
        admin.setPhone(request.getPhone());
        admin.setEmail(request.getEmail());
        admin.setAvatar(request.getAvatar());

        adminMapper.insert(admin);

        log.info("创建管理员成功: adminId={}, username={}", admin.getAdminId(), admin.getUsername());

        return convertToInfoVO(admin);
    }

    @Override
    @Transactional
    public AdminInfoVO updateAdmin(Long adminId, AdminInfoVO request) {
        Admin admin = getAdminEntity(adminId);

        // 检查用户名是否被其他管理员使用
        int count = adminMapper.countByUsernameExcludeId(request.getUsername(), adminId);
        if (count > 0) {
            throw new BusinessException("用户名已被其他管理员使用");
        }

        admin.setUsername(request.getUsername());
        admin.setRole(request.getRole());
        admin.setRealName(request.getRealName());
        admin.setPhone(request.getPhone());
        admin.setEmail(request.getEmail());
        admin.setAvatar(request.getAvatar());

        adminMapper.updateById(admin);

        log.info("更新管理员成功: adminId={}", adminId);

        return convertToInfoVO(admin);
    }

    @Override
    @Transactional
    public void enableAdmin(Long adminId) {
        Admin admin = getAdminEntity(adminId);
        if (admin.getStatus() == 1) {
            throw new BusinessException("管理员已是启用状态");
        }
        adminMapper.enableAdmin(adminId);
        log.info("启用管理员成功: adminId={}", adminId);
    }

    @Override
    @Transactional
    public void disableAdmin(Long adminId) {
        Admin admin = getAdminEntity(adminId);
        if (admin.getStatus() == 0) {
            throw new BusinessException("管理员已是禁用状态");
        }
        adminMapper.disableAdmin(adminId);
        // 清除Token缓存
        redisUtil.delete(REDIS_ADMIN_TOKEN_KEY + adminId);
        log.info("禁用管理员成功: adminId={}", adminId);
    }

    @Override
    @Transactional
    public void deleteAdmin(Long adminId) {
        Admin admin = getAdminEntity(adminId);
        adminMapper.deleteById(adminId);
        // 清除Token缓存
        redisUtil.delete(REDIS_ADMIN_TOKEN_KEY + adminId);
        log.info("删除管理员成功: adminId={}, username={}", adminId, admin.getUsername());
    }

    @Override
    @Transactional
    public void batchDeleteAdmins(List<Long> adminIds) {
        if (adminIds == null || adminIds.isEmpty()) {
            return;
        }
        adminMapper.batchDelete(adminIds);
        // 清除Token缓存
        for (Long adminId : adminIds) {
            redisUtil.delete(REDIS_ADMIN_TOKEN_KEY + adminId);
        }
        log.info("批量删除管理员成功: adminIds={}", adminIds);
    }

    // ==================== 验证 ====================

    @Override
    public boolean existsById(Long adminId) {
        return adminMapper.selectById(adminId) != null;
    }

    @Override
    public boolean existsByUsername(String username) {
        return adminMapper.countByUsername(username) > 0;
    }

    @Override
    public Admin getAdminEntity(Long adminId) {
        Admin admin = adminMapper.selectById(adminId);
        if (admin == null) {
            throw new BusinessException("管理员不存在");
        }
        return admin;
    }

    @Override
    public Admin getAdminEntityByUsername(String username) {
        Admin admin = adminMapper.selectByUsername(username);
        if (admin == null) {
            throw new BusinessException("管理员不存在");
        }
        return admin;
    }

    // ==================== 私有方法 ====================

    /**
     * 实体转VO
     */
    private AdminInfoVO convertToInfoVO(Admin admin) {
        if (admin == null) {
            return null;
        }

        return AdminInfoVO.builder()
                .adminId(admin.getAdminId())
                .username(admin.getUsername())
                .role(admin.getRole())
                .roleDesc(admin.getRoleDesc() != null ? admin.getRoleDesc() :
                        AdminRole.getDesc(admin.getRole()))
                .realName(admin.getRealName())
                .phone(admin.getPhone())
                .email(admin.getEmail())
                .avatar(admin.getAvatar())
                .status(admin.getStatus())
                .statusDesc(admin.getStatusDesc() != null ? admin.getStatusDesc() :
                        (admin.getStatus() == 1 ? "正常" : "禁用"))
                .lastLoginIp(admin.getLastLoginIp())
                .lastLoginTime(admin.getLastLoginTime())
                .createTime(admin.getCreateTime())
                .build();
    }
}