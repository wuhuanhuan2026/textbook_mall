package com.bookmall.payment.feign;

import com.bookmall.common.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**
 * 订单服务 Feign 客户端
 *
 * @author BookMall Team
 */
@FeignClient(name = "order-service", path = "/api/order")
public interface OrderFeignClient {

    /**
     * 获取订单详情
     */
    @GetMapping("/{orderId}")
    Result<Object> getOrderDetail(@PathVariable("orderId") String orderId);

    /**
     * 支付订单
     */
    @PostMapping("/{orderId}/pay")
    Result<Void> payOrder(@PathVariable("orderId") String orderId);

    /**
     * 更新订单状态为已支付
     */
    @PostMapping("/{orderId}/pay-success")
    Result<Void> paySuccess(@PathVariable("orderId") String orderId);

    /**
     * 检查订单是否属于用户
     */
    @GetMapping("/belongs/{orderId}/{userId}")
    Result<Boolean> belongsToUser(@PathVariable("orderId") String orderId,
                                  @PathVariable("userId") Long userId);
}