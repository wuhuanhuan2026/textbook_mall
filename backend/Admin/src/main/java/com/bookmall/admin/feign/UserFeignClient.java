package com.bookmall.admin.feign;

import com.bookmall.common.entity.PageResult;
import com.bookmall.common.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 用户服务 Feign 客户端
 *
 * 路径说明：
 * - User 服务的 UserController / AdminUserController 都映射在 /user 下
 * - Feign 的 path 用 /user（不带 /api，/api 是网关用的）
 *
 * @author BookMall Team
 */
@FeignClient(name = "user-service", path = "/user")
public interface UserFeignClient {

    // ==================== 原有：统计接口 ====================

    /**
     * 获取用户总数
     */
    @GetMapping("/statistics/total")
    Result<Long> getTotalUsers();

    /**
     * 获取今日新增用户数
     */
    @GetMapping("/statistics/today")
    Result<Long> getTodayNewUsers();

    /**
     * 获取用户是否存在
     */
    @GetMapping("/exists/{userId}")
    Result<Boolean> checkUserExists(@PathVariable("userId") Long userId);

    /**
     * 获取用户余额
     */
    @GetMapping("/balance/{userId}")
    Result<BigDecimal> getUserBalance(@PathVariable("userId") Long userId);

    // ==================== 新增：普通用户管理 ====================

    /**
     * 分页查询普通用户列表（管理员）
     * 对应 user-service 的 GET /user/admin/list
     */
    @GetMapping("/admin/list")
    Result<PageResult<Map<String, Object>>> getUserList(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize);

    /**
     * 获取用户详情（管理员）
     * 对应 user-service 的 GET /user/admin/{userId}
     */
    @GetMapping("/admin/{userId}")
    Result<Map<String, Object>> getUserDetail(@PathVariable("userId") Long userId);

    /**
     * 启用/禁用用户（管理员）
     * 对应 user-service 的 PUT /user/admin/{userId}/status
     */
    @PutMapping("/admin/{userId}/status")
    Result<Void> updateUserStatus(
            @PathVariable("userId") Long userId,
            @RequestParam("status") Integer status);

    /**
     * 删除用户（管理员）
     * 对应 user-service 的 DELETE /user/admin/{userId}
     */
    @DeleteMapping("/admin/{userId}")
    Result<Void> deleteUser(@PathVariable("userId") Long userId);
}