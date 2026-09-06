package com.bookmall.user.service;

import com.bookmall.user.dto.*;
import com.bookmall.user.entity.User;

public interface UserService {

    /**
     * 用户登录
     */
    LoginResponseVO login(LoginRequestDTO request);

    /**
     * 用户注册
     */
    void register(RegisterRequestDTO request);

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

    /**
     * 扣减余额
     */
    void deductBalance(Long userId, java.math.BigDecimal amount);

    /**
     * 增加余额
     */
    void addBalance(Long userId, java.math.BigDecimal amount);

    /**
     * 退出登录
     */
    void logout(String token);
}