package com.bookmall.admin.controller;

import com.bookmall.admin.dto.AdminInfoVO;
import com.bookmall.admin.service.AdminService;
import com.bookmall.common.entity.Result;
import com.bookmall.common.constant.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 管理员管理控制器（超级管理员专用）
 *
 * @author BookMall Team
 */
@Slf4j
@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Validated
@Tag(name = "管理员管理接口", description = "管理员账号管理（超级管理员）")
public class AdminUserController {

    private final AdminService adminService;

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
}