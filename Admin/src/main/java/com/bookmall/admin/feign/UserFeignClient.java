package com.bookmall.admin.feign;

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
}