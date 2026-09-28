package com.bookmall.admin.service;

import com.bookmall.admin.dto.*;
import com.bookmall.admin.entity.Admin;

import java.util.List;

/**
 * 管理员服务接口
 *
 * @author BookMall Team
 */
public interface AdminService {

    // ==================== 认证 ====================

    /**
     * 管理员登录
     */
    AdminLoginVO login(AdminLoginDTO request, String ip);

    /**
     * 管理员退出登录
     */
    void logout(String token);

    // ==================== 信息管理 ====================

    /**
     * 获取当前管理员信息
     */
    AdminInfoVO getAdminInfo(Long adminId);

    /**
     * 获取管理员信息（内部调用）
     */
    AdminInfoVO getAdminInfoById(Long adminId);

    /**
     * 修改密码
     */
    void updatePassword(Long adminId, AdminUpdatePasswordDTO request);

    /**
     * 修改个人信息
     */
    void updateProfile(Long adminId, AdminInfoVO request);

    // ==================== 管理员管理 ====================

    /**
     * 获取所有管理员列表
     */
    List<AdminInfoVO> getAllAdmins();

    /**
     * 获取启用的管理员列表
     */
    List<AdminInfoVO> getActiveAdmins();

    /**
     * 创建管理员
     */
    AdminInfoVO createAdmin(AdminInfoVO request);

    /**
     * 更新管理员
     */
    AdminInfoVO updateAdmin(Long adminId, AdminInfoVO request);

    /**
     * 启用管理员
     */
    void enableAdmin(Long adminId);

    /**
     * 禁用管理员
     */
    void disableAdmin(Long adminId);

    /**
     * 删除管理员
     */
    void deleteAdmin(Long adminId);

    /**
     * 批量删除管理员
     */
    void batchDeleteAdmins(List<Long> adminIds);

    // ==================== 验证 ====================

    /**
     * 检查管理员是否存在
     */
    boolean existsById(Long adminId);

    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 获取管理员实体
     */
    Admin getAdminEntity(Long adminId);

    /**
     * 获取管理员实体（用户名）
     */
    Admin getAdminEntityByUsername(String username);

    // ==================== 仪表盘统计（新增） ====================

    /**
     * 获取仪表盘统计数据
     */
    DashboardStatisticsVO getDashboardStatistics();

    /**
     * 获取仪表盘趋势数据
     * @param type 时间维度（day/week/month）
     */
    DashboardTrendVO getDashboardTrend(String type);
}