package com.bookmall.payment.schedule;

import com.bookmall.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 支付超时定时任务
 * 每5分钟执行一次，取消30分钟未支付的支付记录
 *
 * @author BookMall Team
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentTimeoutTask {

    private final PaymentService paymentService;

    /**
     * 每5分钟执行一次
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void cancelTimeoutPayments() {
        log.info("开始执行超时支付取消任务");
        try {
            paymentService.cancelTimeoutPayments();
        } catch (Exception e) {
            log.error("超时支付取消任务执行失败: {}", e.getMessage(), e);
        }
        log.info("超时支付取消任务执行完成");
    }
}