package com.bookmall.order.feign;

import com.bookmall.common.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 用户服务 Feign 客户端
 *
 * @author BookMall Team
 */
@FeignClient(name = "user-service", path = "/user")  // ✅ 修改为 /user
public interface UserFeignClient {

    /**
     * 检查用户是否存在
     */
    @GetMapping("/exists/{userId}")
    Result<Boolean> checkUserExists(@PathVariable("userId") Long userId);

    /**
     * 获取用户信息
     */
    @GetMapping("/info/{userId}")
    Result<Object> getUserInfo(@PathVariable("userId") Long userId);

    /**
     * 扣减余额
     */
    @PostMapping("/balance/deduct")
    Result<Void> deductBalance(@RequestParam("userId") Long userId,
                               @RequestParam("amount") BigDecimal amount);

    /**
     * 增加余额
     */
    @PostMapping("/balance/add")
    Result<Void> addBalance(@RequestParam("userId") Long userId,
                            @RequestParam("amount") BigDecimal amount);
}