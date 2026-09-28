package com.bookmall.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bookmall.common.entity.PageResult;
import com.bookmall.common.entity.Result;
import com.bookmall.user.dto.UserInfoVO;
import com.bookmall.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

/**
 * 管理员 - 用户管理接口
 *
 * @author BookMall Team
 */
@Slf4j
@RestController
@RequestMapping("/user/admin")
@RequiredArgsConstructor
@Validated
@Tag(name = "管理员-用户管理", description = "管理后台用户管理接口")
public class AdminUserController {

    private final UserService userService;

    /**
     * 分页查询用户列表（管理员）
     * 对应 Feign: GET /user/admin/list
     */
    @GetMapping("/list")
    @Operation(summary = "分页查询用户列表")
    public Result<PageResult<UserInfoVO>> getUserList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<UserInfoVO> page = userService.getUserListForAdmin(keyword, status, pageNum, pageSize);
        return Result.success(PageResult.of(page));
    }

    /**
     * 获取用户详情（管理员）
     * 对应 Feign: GET /user/admin/{userId}
     */
    @GetMapping("/{userId}")
    @Operation(summary = "获取用户详情")
    public Result<UserInfoVO> getUserDetail(@PathVariable @NotNull Long userId) {
        return Result.success(userService.getUserInfo(userId));
    }

    /**
     * 启用/禁用用户（管理员）
     * 对应 Feign: PUT /user/admin/{userId}/status
     */
    @PutMapping("/{userId}/status")
    @Operation(summary = "启用/禁用用户")
    public Result<Void> updateUserStatus(
            @PathVariable @NotNull Long userId,
            @RequestParam @NotNull Integer status) {
        userService.updateUserStatus(userId, status);
        return Result.success(status == 1 ? "启用成功" : "禁用成功");
    }
}