package com.bookmall.review.feign;

import com.bookmall.common.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 用户服务 Feign 客户端
 *
 * @author BookMall Team
 */
@FeignClient(name = "user-service", path = "/api/user")
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
}