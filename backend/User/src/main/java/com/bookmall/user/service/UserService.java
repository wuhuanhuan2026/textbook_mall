package com.bookmall.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bookmall.user.dto.*;
import com.bookmall.user.entity.User;

public interface UserService {

    // ==================== 认证 ====================

    /**
     * 用户登录
     */
    LoginResponseVO login(LoginRequestDTO request);

    /**
     * 用户注册
     */
    void register(RegisterRequestDTO request);

    /**
     * 退出登录
     */
    void logout(String token);

    // ==================== 用户信息 ====================

    /**
     * 获取用户信息
     */
    UserInfoVO getUserInfo(Long userId);

    /**
     * 修改密码
     */
    void updatePassword(Long userId, UpdatePasswordDTO request);

    /**
     * 修改用户信息
     */
    void updateUserInfo(Long userId, UpdateUserInfoDTO request);

    /**
     * 获取用户实体
     */
    User getUserById(Long userId);

    /**
     * 验证用户是否存在
     */
    boolean checkUserExists(Long userId);

    // ==================== 余额操作 ====================

    /**
     * 扣减余额
     */
    void deductBalance(Long userId, java.math.BigDecimal amount);

    /**
     * 增加余额
     */
    void addBalance(Long userId, java.math.BigDecimal amount);

    // ==================== 管理员端 ====================

    /**
     * 管理员分页查询用户
     *
     * @param keyword  关键词（用户名模糊匹配）
     * @param status   状态：0-禁用 1-正常，null 表示全部
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 用户分页
     */
    Page<UserInfoVO> getUserListForAdmin(String keyword, Integer status, Integer pageNum, Integer pageSize);

    /**
     * 管理员启用/禁用用户
     *
     * @param userId 用户ID
     * @param status 状态：0-禁用 1-正常
     */
    void updateUserStatus(Long userId, Integer status);
}