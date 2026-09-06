package com.bookmall.admin.controller;

import com.bookmall.admin.dto.AdminLoginDTO;
import com.bookmall.admin.dto.AdminLoginVO;
import com.bookmall.admin.service.AdminService;
import com.bookmall.common.entity.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 管理员认证控制器
 *
 * @author BookMall Team
 */
@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Validated
@Tag(name = "管理员认证接口", description = "管理员登录、退出")
public class AdminAuthController {

    private final AdminService adminService;

    /**
     * 管理员登录
     */
    @PostMapping("/login")
    @Operation(summary = "管理员登录")
    public Result<AdminLoginVO> login(@Valid @RequestBody AdminLoginDTO request,
                                      HttpServletRequest httpRequest) {
        String ip = getClientIp(httpRequest);
        AdminLoginVO result = adminService.login(request, ip);
        return Result.success("登录成功", result);
    }

    /**
     * 管理员退出登录
     */
    @PostMapping("/logout")
    @Operation(summary = "管理员退出登录")
    public Result<Void> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        adminService.logout(token);
        return Result.success("退出成功");
    }

    /**
     * 获取客户端IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 如果有多个IP，取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}