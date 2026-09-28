package com.bookmall.user.controller;

import com.bookmall.common.entity.Result;
import com.bookmall.user.dto.LoginRequestDTO;
import com.bookmall.user.dto.LoginResponseVO;
import com.bookmall.user.dto.RegisterRequestDTO;
import com.bookmall.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 * 处理用户登录、注册、退出等认证相关请求
 */
@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Validated
@Tag(name = "认证接口", description = "用户登录、注册、退出")
public class AuthController {

    private final UserService userService;

    // ==================== 测试接口（浏览器可直接访问） ====================

    /**
     * GET 测试接口 - 用于浏览器验证服务是否正常运行
     * 访问地址: http://localhost:8081/api/user/test
     */
    @GetMapping("/test")
    @Operation(summary = "测试接口（GET）", description = "用于验证服务是否正常运行")
    public Result<String> test() {
        log.info("测试接口被调用");
        return Result.success("✅ User 模块运行正常！当前时间: " + System.currentTimeMillis());
    }

    /**
     * GET 测试接口 - 返回服务状态信息
     * 访问地址: http://localhost:8081/api/user/status
     */
    @GetMapping("/status")
    @Operation(summary = "服务状态（GET）", description = "返回服务运行状态")
    public Result<Map<String, Object>> status() {
        Map<String, Object> data = new HashMap<>();
        data.put("service", "user-service");
        data.put("status", "running");
        data.put("port", 8081);
        data.put("timestamp", System.currentTimeMillis());
        return Result.success(data);
    }

    /**
     * GET 欢迎接口
     * 访问地址: http://localhost:8081/api/user/welcome
     */
    @GetMapping("/welcome")
    @Operation(summary = "欢迎接口（GET）", description = "返回欢迎信息")
    public Result<String> welcome() {
        log.info("欢迎接口被调用");
        return Result.success("📚 欢迎使用教材商城 API！");
    }

    /**
     * GET 接口信息 - 显示所有可用接口
     * 访问地址: http://localhost:8081/api/user/endpoints
     */
    @GetMapping("/endpoints")
    @Operation(summary = "接口列表（GET）", description = "显示所有可用的接口列表")
    public Result<Map<String, String>> endpoints() {
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("GET  /api/user/test", "测试接口");
        endpoints.put("GET  /api/user/status", "服务状态");
        endpoints.put("GET  /api/user/welcome", "欢迎信息");
        endpoints.put("GET  /api/user/endpoints", "接口列表（当前）");
        endpoints.put("GET  /api/user/check-username", "检查用户名");
        endpoints.put("POST /api/user/login", "用户登录");
        endpoints.put("POST /api/user/register", "用户注册");
        endpoints.put("POST /api/user/logout", "用户退出");
        endpoints.put("GET  /api/user/info", "获取用户信息（需要Token）");
        endpoints.put("PUT  /api/user/password", "修改密码（需要Token）");
        endpoints.put("PUT  /api/user/info", "修改用户信息（需要Token）");
        return Result.success(endpoints);
    }

    // ==================== 业务接口 ====================

    /**
     * 用户登录
     * 请求方式: POST
     * 请求体: { "username": "testuser", "password": "123456" }
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public Result<LoginResponseVO> login(@Valid @RequestBody LoginRequestDTO request) {
        log.info("用户登录请求: {}", request.getUsername());
        LoginResponseVO response = userService.login(request);
        return Result.success("登录成功", response);
    }

    /**
     * 用户注册
     * 请求方式: POST
     * 请求体: { "username": "newuser", "password": "123456", "phone": "13800138000", "email": "user@qq.com", "captcha": "1234" }
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public Result<Void> register(@Valid @RequestBody RegisterRequestDTO request) {
        log.info("用户注册请求: {}", request.getUsername());
        userService.register(request);
        return Result.success("注册成功");
    }

    /**
     * 用户退出登录
     * 请求方式: POST
     * 需要携带 Token: Authorization: Bearer <token>
     */
    @PostMapping("/logout")
    @Operation(summary = "用户退出登录")
    public Result<Void> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        userService.logout(token);
        log.info("用户退出登录");
        return Result.success("退出成功");
    }

    /**
     * 检查用户名是否可用
     * 访问地址: http://localhost:8081/api/user/check-username?username=testuser
     */
    @GetMapping("/check-username")
    @Operation(summary = "检查用户名是否可用")
    public Result<Boolean> checkUsername(@RequestParam String username) {
        log.info("检查用户名是否可用: {}", username);
        // TODO: 实际应该调用 Service 检查
        // boolean exists = userService.checkUsernameExists(username);
        // return Result.success(!exists);
        return Result.success(true);
    }
}