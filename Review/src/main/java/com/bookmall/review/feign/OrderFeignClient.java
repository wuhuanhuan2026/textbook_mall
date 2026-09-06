package com.bookmall.review.feign;

import com.bookmall.common.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 订单服务 Feign 客户端
 *
 * @author BookMall Team
 */
@FeignClient(name = "order-service", path = "/api/order")
public interface OrderFeignClient {

    /**
     * 检查订单是否属于用户
     */
    @GetMapping("/belongs/{orderId}/{userId}")
    Result<Boolean> belongsToUser(@PathVariable("orderId") String orderId,
                                  @PathVariable("userId") Long userId);

    /**
     * 检查用户是否购买过该教材（用于评价验证）
     */
    @GetMapping("/check-purchased/{userId}/{bookId}")
    Result<Boolean> checkUserPurchased(@PathVariable("userId") Long userId,
                                       @PathVariable("bookId") Long bookId);
}