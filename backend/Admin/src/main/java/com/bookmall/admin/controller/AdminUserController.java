package com.bookmall.admin.controller;

import com.bookmall.admin.dto.AdminInfoVO;
import com.bookmall.admin.feign.UserFeignClient;
import com.bookmall.admin.service.AdminService;
import com.bookmall.common.constant.Constants;
import com.bookmall.common.entity.PageResult;
import com.bookmall.common.entity.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 管理员管理控制器（超级管理员专用）
 *
 * 包含两个部分：
 * 1. /admin/users       —— 管理员账号管理（tb_admin）
 * 2. /admin/users/members —— 普通用户管理（tb_user）
 *
 * @author BookMall Team
 */
@Slf4j
@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Validated
@Tag(name = "管理员管理接口", description = "管理员账号管理 + 普通用户管理")
public class AdminUserController {

    private final AdminService adminService;
    private final UserFeignClient userFeignClient;

    // ============================================================
    // 一、管理员账号管理（原有功能，保留不变）
    // ============================================================

    /**
     * 获取所有管理员列表
     */
    @GetMapping
    @Operation(summary = "获取所有管理员列表")
    public Result<List<AdminInfoVO>> getAllAdmins() {
        List<AdminInfoVO> admins = adminService.getAllAdmins();
        return Result.success(admins);
    }

    /**
     * 获取启用的管理员列表
     */
    @GetMapping("/active")
    @Operation(summary = "获取启用的管理员列表")
    public Result<List<AdminInfoVO>> getActiveAdmins() {
        List<AdminInfoVO> admins = adminService.getActiveAdmins();
        return Result.success(admins);
    }

    /**
     * 获取管理员详情
     */
    @GetMapping("/{adminId}")
    @Operation(summary = "获取管理员详情")
    public Result<AdminInfoVO> getAdminById(
            @Parameter(description = "管理员ID", required = true)
            @PathVariable @NotNull Long adminId) {
        AdminInfoVO admin = adminService.getAdminInfoById(adminId);
        return Result.success(admin);
    }

    /**
     * 创建管理员
     */
    @PostMapping
    @Operation(summary = "创建管理员")
    public Result<AdminInfoVO> createAdmin(@RequestBody AdminInfoVO request) {
        AdminInfoVO admin = adminService.createAdmin(request);
        return Result.success("创建成功", admin);
    }

    /**
     * 更新管理员
     */
    @PutMapping("/{adminId}")
    @Operation(summary = "更新管理员")
    public Result<AdminInfoVO> updateAdmin(
            @Parameter(description = "管理员ID", required = true)
            @PathVariable @NotNull Long adminId,
            @RequestBody AdminInfoVO request) {
        AdminInfoVO admin = adminService.updateAdmin(adminId, request);
        return Result.success("更新成功", admin);
    }

    /**
     * 启用管理员
     */
    @PutMapping("/{adminId}/enable")
    @Operation(summary = "启用管理员")
    public Result<Void> enableAdmin(
            @Parameter(description = "管理员ID", required = true)
            @PathVariable @NotNull Long adminId) {
        adminService.enableAdmin(adminId);
        return Result.success("启用成功");
    }

    /**
     * 禁用管理员
     */
    @PutMapping("/{adminId}/disable")
    @Operation(summary = "禁用管理员")
    public Result<Void> disableAdmin(
            @Parameter(description = "管理员ID", required = true)
            @PathVariable @NotNull Long adminId) {
        adminService.disableAdmin(adminId);
        return Result.success("禁用成功");
    }

    /**
     * 删除管理员
     */
    @DeleteMapping("/{adminId}")
    @Operation(summary = "删除管理员")
    public Result<Void> deleteAdmin(
            @Parameter(description = "管理员ID", required = true)
            @PathVariable @NotNull Long adminId) {
        adminService.deleteAdmin(adminId);
        return Result.success("删除成功");
    }

    /**
     * 批量删除管理员
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除管理员")
    public Result<Void> batchDeleteAdmins(@RequestBody List<Long> adminIds) {
        adminService.batchDeleteAdmins(adminIds);
        return Result.success("批量删除成功");
    }

    // ============================================================
    // 二、普通用户管理（新增，通过 Feign 调用 user-service）
    // 注意：路径统一加 /members 前缀，避免和管理员接口冲突
    // ============================================================

    /**
     * 分页查询普通用户列表
     * 支持按用户名模糊搜索、按状态筛选
     */
    @GetMapping("/members")
    @Operation(summary = "分页查询普通用户列表")
    public Result<PageResult<Map<String, Object>>> getUserList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("管理员查询普通用户列表: keyword={}, status={}, pageNum={}, pageSize={}",
                keyword, status, pageNum, pageSize);
        return userFeignClient.getUserList(keyword, status, pageNum, pageSize);
    }

    /**
     * 获取普通用户详情
     */
    @GetMapping("/members/{userId}")
    @Operation(summary = "获取普通用户详情")
    public Result<Map<String, Object>> getUserDetail(
            @Parameter(description = "用户ID", required = true)
            @PathVariable @NotNull Long userId) {
        log.info("管理员查询普通用户详情: userId={}", userId);
        return userFeignClient.getUserDetail(userId);
    }

    /**
     * 启用/禁用普通用户
     * status: 0-禁用 1-正常
     */
    @PutMapping("/members/{userId}/status")
    @Operation(summary = "启用/禁用普通用户")
    public Result<Void> updateUserStatus(
            @Parameter(description = "用户ID", required = true)
            @PathVariable @NotNull Long userId,
            @RequestParam @NotNull Integer status) {
        log.info("管理员修改普通用户状态: userId={}, status={}", userId, status);
        return userFeignClient.updateUserStatus(userId, status);
    }

    /**
     * 获取普通用户的余额
     */
    @GetMapping("/members/{userId}/balance")
    @Operation(summary = "获取普通用户余额")
    public Result<BigDecimal> getUserBalance(
            @Parameter(description = "用户ID", required = true)
            @PathVariable @NotNull Long userId) {
        log.info("管理员查询普通用户余额: userId={}", userId);
        return userFeignClient.getUserBalance(userId);
    }

    /**
     * 删除普通用户（物理删除）
     */
    @DeleteMapping("/members/{userId}")
    @Operation(summary = "删除普通用户")
    public Result<Void> deleteUser(
            @Parameter(description = "用户ID", required = true)
            @PathVariable @NotNull Long userId) {
        log.info("管理员删除普通用户: userId={}", userId);
        return userFeignClient.deleteUser(userId);
    }
}