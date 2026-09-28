package com.bookmall.admin.controller;

import com.bookmall.admin.dto.AdminInfoVO;
import com.bookmall.admin.dto.AdminUpdatePasswordDTO;
import com.bookmall.admin.service.AdminService;
import com.bookmall.common.constant.Constants;
import com.bookmall.common.entity.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 管理员个人信息控制器
 *
 * @author BookMall Team
 */
@Slf4j
@RestController
@RequestMapping("/admin/profile")
@RequiredArgsConstructor
@Validated
@Tag(name = "管理员个人信息接口", description = "管理员个人信息管理")
public class AdminProfileController {

    private final AdminService adminService;

    /**
     * 获取当前管理员信息
     */
    @GetMapping
    @Operation(summary = "获取当前管理员信息")
    public Result<AdminInfoVO> getProfile(@RequestAttribute(Constants.REQ_ATTR_USER_ID) Long adminId) {
        AdminInfoVO info = adminService.getAdminInfo(adminId);
        return Result.success(info);
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    @Operation(summary = "修改密码")
    public Result<Void> updatePassword(
            @RequestAttribute(Constants.REQ_ATTR_USER_ID) Long adminId,
            @Valid @RequestBody AdminUpdatePasswordDTO request) {
        adminService.updatePassword(adminId, request);
        return Result.success("密码修改成功，请重新登录");
    }

    /**
     * 修改个人信息
     */
    @PutMapping
    @Operation(summary = "修改个人信息")
    public Result<Void> updateProfile(
            @RequestAttribute(Constants.REQ_ATTR_USER_ID) Long adminId,
            @Valid @RequestBody AdminInfoVO request) {
        adminService.updateProfile(adminId, request);
        return Result.success("信息修改成功");
    }
}