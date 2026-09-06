package com.bookmall.admin.feign;

import com.bookmall.common.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;

/**
 * 支付服务 Feign 客户端
 *
 * @author BookMall Team
 */
@FeignClient(name = "payment-service", path = "/api/payment")
public interface PaymentFeignClient {

    /**
     * 获取总支付金额
     */
    @GetMapping("/statistics/amount/total")
    Result<BigDecimal> getTotalPaymentAmount();

    /**
     * 获取今日支付金额
     */
    @GetMapping("/statistics/amount/today")
    Result<BigDecimal> getTodayPaymentAmount();
}