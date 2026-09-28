package com.bookmall.user.controller;

import com.bookmall.common.constant.Constants;
import com.bookmall.common.entity.Result;
import com.bookmall.common.utils.JwtUtil;
import com.bookmall.user.dto.UpdatePasswordDTO;
import com.bookmall.user.dto.UpdateUserInfoDTO;
import com.bookmall.user.dto.UserInfoVO;
import com.bookmall.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Validated
@Tag(name = "用户接口", description = "用户信息管理")
public class UserController {

    private final UserService userService;

    /**
     * 从请求中获取用户ID - 支持多种方式
     * 优先级: X-User-Id > 请求属性 > Authorization Token
     */
    private Long getUserId(HttpServletRequest request) {
        // 1. 从请求头获取（Gateway 转发）
        String userIdHeader = request.getHeader("X-User-Id");
        if (userIdHeader != null && !userIdHeader.isEmpty()) {
            try {
                Long userId = Long.parseLong(userIdHeader);
                log.debug("从 X-User-Id 获取用户ID: {}", userId);
                return userId;
            } catch (NumberFormatException e) {
                log.warn("解析 X-User-Id 失败: {}", userIdHeader);
            }
        }

        // 2. 从请求属性获取
        Object userIdAttr = request.getAttribute(Constants.REQ_ATTR_USER_ID);
        if (userIdAttr instanceof Long) {
            Long userId = (Long) userIdAttr;
            log.debug("从请求属性获取用户ID: {}", userId);
            return userId;
        }

        // 3. ✅ 从 Authorization Header 解析 Token
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                // ✅ 直接使用 JwtUtil.getUserIdFromToken(token)
                Long userId = JwtUtil.getUserIdFromToken(token);
                if (userId != null) {
                    log.debug("从 Token 解析用户ID: {}", userId);
                    return userId;
                }
            } catch (Exception e) {
                log.warn("解析 Token 失败: {}", e.getMessage());
            }
        }

        // 4. 尝试从请求参数获取（兼容特殊场景）
        String userIdParam = request.getParameter("userId");
        if (userIdParam != null && !userIdParam.isEmpty()) {
            try {
                Long userId = Long.parseLong(userIdParam);
                log.debug("从请求参数获取用户ID: {}", userId);
                return userId;
            } catch (NumberFormatException e) {
                log.warn("解析 userId 参数失败: {}", userIdParam);
            }
        }

        throw new RuntimeException("用户未登录");
    }

    // ==================== 用户信息接口 ====================

    @GetMapping("/info")
    @Operation(summary = "获取当前用户信息")
    public Result<UserInfoVO> getUserInfo(HttpServletRequest request) {
        try {
            Long userId = getUserId(request);
            UserInfoVO userInfo = userService.getUserInfo(userId);
            return Result.success(userInfo);
        } catch (RuntimeException e) {
            log.warn("获取用户信息失败: {}", e.getMessage());
            return Result.error(401, "请先登录");
        }
    }

    @GetMapping("/info/{userId}")
    @Operation(summary = "获取指定用户信息（内部调用）")
    public Result<UserInfoVO> getUserInfoById(@PathVariable @NotNull Long userId) {
        try {
            UserInfoVO userInfo = userService.getUserInfo(userId);
            return Result.success(userInfo);
        } catch (Exception e) {
            log.error("获取用户信息失败: userId={}", userId, e);
            return Result.error("用户不存在");
        }
    }

    @PutMapping("/password")
    @Operation(summary = "修改密码")
    public Result<Void> updatePassword(
            HttpServletRequest request,
            @Valid @RequestBody UpdatePasswordDTO updatePasswordDTO) {
        try {
            Long userId = getUserId(request);
            userService.updatePassword(userId, updatePasswordDTO);
            return Result.success("密码修改成功");
        } catch (RuntimeException e) {
            log.warn("修改密码失败: {}", e.getMessage());
            return Result.error(401, "请先登录");
        } catch (Exception e) {
            log.error("修改密码失败", e);
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/info")
    @Operation(summary = "修改用户信息")
    public Result<Void> updateUserInfo(
            HttpServletRequest request,
            @Valid @RequestBody UpdateUserInfoDTO updateUserInfoDTO) {
        try {
            Long userId = getUserId(request);
            userService.updateUserInfo(userId, updateUserInfoDTO);
            return Result.success("用户信息修改成功");
        } catch (RuntimeException e) {
            log.warn("修改用户信息失败: {}", e.getMessage());
            return Result.error(401, "请先登录");
        } catch (Exception e) {
            log.error("修改用户信息失败", e);
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/exists/{userId}")
    @Operation(summary = "检查用户是否存在（内部调用）")
    public Result<Boolean> checkUserExists(@PathVariable Long userId) {
        try {
            boolean exists = userService.checkUserExists(userId);
            return Result.success(exists);
        } catch (Exception e) {
            log.error("检查用户是否存在失败: userId={}", userId, e);
            return Result.success(false);
        }
    }

    // ==================== 余额操作（内部调用） ====================

    @GetMapping("/balance/{userId}")
    @Operation(summary = "获取用户余额", description = "内部调用")
    public Result<BigDecimal> getUserBalance(@PathVariable Long userId) {
        log.info("获取用户余额: userId={}", userId);
        try {
            UserInfoVO user = userService.getUserInfo(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }
            return Result.success(user.getBalance());
        } catch (Exception e) {
            log.error("获取用户余额失败: userId={}", userId, e);
            return Result.error("获取余额失败");
        }
    }

    @PostMapping("/balance/deduct")
    @Operation(summary = "扣减余额", description = "内部调用")
    public Result<Void> deductBalance(
            @RequestParam Long userId,
            @RequestParam BigDecimal amount) {
        log.info("扣减余额: userId={}, amount={}", userId, amount);
        try {
            userService.deductBalance(userId, amount);
            return Result.success("扣减成功");
        } catch (Exception e) {
            log.error("扣减余额失败: userId={}, amount={}", userId, amount, e);
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/balance/add")
    @Operation(summary = "增加余额", description = "内部调用")
    public Result<Void> addBalance(
            @RequestParam Long userId,
            @RequestParam BigDecimal amount) {
        log.info("增加余额: userId={}, amount={}", userId, amount);
        try {
            userService.addBalance(userId, amount);
            return Result.success("增加成功");
        } catch (Exception e) {
            log.error("增加余额失败: userId={}, amount={}", userId, amount, e);
            return Result.error(e.getMessage());
        }
    }
}