package com.bookmall.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bookmall.common.constant.Constants;
import com.bookmall.common.exception.BusinessException;
import com.bookmall.common.exception.ExceptionEnum;
import com.bookmall.common.utils.BCryptUtil;
import com.bookmall.common.utils.JwtUtil;
import com.bookmall.common.utils.RedisUtil;
import com.bookmall.user.dto.*;
import com.bookmall.user.entity.User;
import com.bookmall.user.mapper.UserMapper;
import com.bookmall.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final RedisUtil redisUtil;

    // ==================== 认证 ====================

    @Override
    public LoginResponseVO login(LoginRequestDTO request) {
        // 1. 根据用户名查询用户
        User user = userMapper.selectByUsername(request.getUsername());
        if (user == null) {
            throw new BusinessException(ExceptionEnum.USER_NOT_EXIST);
        }

        // 2. 检查用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException(ExceptionEnum.USER_DISABLED);
        }

        // 3. 验证密码
        if (!BCryptUtil.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ExceptionEnum.PASSWORD_ERROR);
        }

        // 4. 生成Token
        String token = JwtUtil.generateToken(user.getUserId(), user.getUsername(), Constants.ROLE_USER);

        // 5. 缓存用户信息到Redis
        String cacheKey = Constants.REDIS_USER_TOKEN + user.getUserId();
        redisUtil.set(cacheKey, token, Constants.JWT_EXPIRATION, TimeUnit.MILLISECONDS);

        // 6. 返回登录信息
        return LoginResponseVO.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .avatar(user.getAvatar())
                .balance(user.getBalance())
                .token(token)
                .expiresIn(Constants.JWT_EXPIRATION)
                .build();
    }

    @Override
    @Transactional
    public void register(RegisterRequestDTO request) {
        // 1. 检查用户名是否已存在
        if (userMapper.selectByUsername(request.getUsername()) != null) {
            throw new BusinessException(ExceptionEnum.USERNAME_EXIST);
        }

        // 2. 检查手机号是否已存在
        if (StringUtils.hasText(request.getPhone()) && userMapper.selectByPhone(request.getPhone()) != null) {
            throw new BusinessException(ExceptionEnum.PHONE_EXIST);
        }

        // 3. 检查邮箱是否已存在
        if (StringUtils.hasText(request.getEmail()) && userMapper.selectByEmail(request.getEmail()) != null) {
            throw new BusinessException(ExceptionEnum.EMAIL_EXIST);
        }

        // 4. 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(BCryptUtil.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setBalance(BigDecimal.ZERO);
        user.setStatus(Constants.USER_STATUS_NORMAL);

        userMapper.insert(user);
        log.info("用户注册成功: username={}", request.getUsername());
    }

    @Override
    public void logout(String token) {
        if (StringUtils.hasText(token)) {
            Long userId = JwtUtil.getUserIdFromToken(token);
            if (userId != null) {
                redisUtil.delete(Constants.REDIS_USER_TOKEN + userId);
                log.info("用户退出登录: userId={}", userId);
            }
        }
    }

    // ==================== 用户信息 ====================

    @Override
    public UserInfoVO getUserInfo(Long userId) {
        User user = getUserById(userId);
        return UserInfoVO.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .phone(user.getPhone())
                .email(user.getEmail())
                .avatar(user.getAvatar())
                .balance(user.getBalance())
                .status(user.getStatus())
                .createTime(user.getCreateTime())
                .build();
    }

    @Override
    @Transactional
    public void updatePassword(Long userId, UpdatePasswordDTO request) {
        User user = getUserById(userId);

        if (!BCryptUtil.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException(ExceptionEnum.PASSWORD_ERROR);
        }

        user.setPassword(BCryptUtil.encode(request.getNewPassword()));
        userMapper.updateById(user);
        log.info("用户密码修改成功: userId={}", userId);

        redisUtil.delete(Constants.REDIS_USER_TOKEN + userId);
    }

    @Override
    @Transactional
    public void updateUserInfo(Long userId, UpdateUserInfoDTO request) {
        User user = getUserById(userId);

        if (StringUtils.hasText(request.getPhone())) {
            User existUser = userMapper.selectByPhone(request.getPhone());
            if (existUser != null && !existUser.getUserId().equals(userId)) {
                throw new BusinessException(ExceptionEnum.PHONE_EXIST);
            }
            user.setPhone(request.getPhone());
        }

        if (StringUtils.hasText(request.getEmail())) {
            User existUser = userMapper.selectByEmail(request.getEmail());
            if (existUser != null && !existUser.getUserId().equals(userId)) {
                throw new BusinessException(ExceptionEnum.EMAIL_EXIST);
            }
            user.setEmail(request.getEmail());
        }

        if (StringUtils.hasText(request.getAvatar())) {
            user.setAvatar(request.getAvatar());
        }

        userMapper.updateById(user);
        log.info("用户信息修改成功: userId={}", userId);
    }

    @Override
    public User getUserById(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ExceptionEnum.USER_NOT_EXIST);
        }
        return user;
    }

    @Override
    public boolean checkUserExists(Long userId) {
        return userMapper.selectById(userId) != null;
    }

    // ==================== 余额操作 ====================

    @Override
    @Transactional
    public void deductBalance(Long userId, BigDecimal amount) {
        int result = userMapper.deductBalance(userId, amount);
        if (result <= 0) {
            throw new BusinessException(ExceptionEnum.BALANCE_INSUFFICIENT);
        }
    }

    @Override
    @Transactional
    public void addBalance(Long userId, BigDecimal amount) {
        userMapper.addBalance(userId, amount);
    }

    // ==================== 管理员端 ====================

    /**
     * 管理员分页查询用户
     */
    @Override
    public Page<UserInfoVO> getUserListForAdmin(String keyword, Integer status, Integer pageNum, Integer pageSize) {
        Page<User> page = new Page<>(pageNum, pageSize);

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(User::getUsername, keyword);
        }
        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }
        wrapper.orderByDesc(User::getCreateTime);

        Page<User> userPage = userMapper.selectPage(page, wrapper);

        Page<UserInfoVO> dtoPage = new Page<>(pageNum, pageSize, userPage.getTotal());
        dtoPage.setRecords(userPage.getRecords().stream()
                .map(u -> UserInfoVO.builder()
                        .userId(u.getUserId())
                        .username(u.getUsername())
                        .phone(u.getPhone())
                        .email(u.getEmail())
                        .avatar(u.getAvatar())
                        .balance(u.getBalance())
                        .status(u.getStatus())
                        .createTime(u.getCreateTime())
                        .build())
                .collect(Collectors.toList()));
        return dtoPage;
    }

    /**
     * 管理员启用/禁用用户
     */
    @Override
    @Transactional
    public void updateUserStatus(Long userId, Integer status) {
        User user = getUserById(userId);

        // 状态合法性校验
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException("状态值非法，只能是 0 或 1");
        }

        user.setStatus(status);
        userMapper.updateById(user);

        // 如果禁用用户，清除其 Token 缓存，强制下线
        if (status == 0) {
            redisUtil.delete(Constants.REDIS_USER_TOKEN + userId);
            log.info("用户已被禁用，清除登录缓存: userId={}", userId);
        }

        log.info("用户状态更新成功: userId={}, status={}", userId, status);
    }
}