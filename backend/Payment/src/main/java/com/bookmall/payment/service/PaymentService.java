package com.bookmall.payment.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bookmall.payment.dto.PaymentCallbackDTO;
import com.bookmall.payment.dto.PaymentCreateDTO;
import com.bookmall.payment.dto.PaymentDTO;
import com.bookmall.payment.dto.PaymentStatisticsVO;
import com.bookmall.payment.entity.Payment;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 支付服务接口
 *
 * @author BookMall Team
 */
public interface PaymentService {

    // ==================== 用户端 ====================

    /**
     * 创建支付
     */
    PaymentDTO createPayment(Long userId, PaymentCreateDTO request);

    /**
     * 获取支付详情
     */
    PaymentDTO getPaymentDetail(Long userId, String paymentNo);

    /**
     * 获取用户支付记录列表
     */
    Page<PaymentDTO> getUserPayments(Long userId, Integer status, Integer pageNum, Integer pageSize);

    /**
     * 取消支付
     */
    void cancelPayment(Long userId, String paymentNo);

    /**
     * 支付回调处理
     */
    void handlePaymentCallback(PaymentCallbackDTO callback);

    // ==================== 管理员端 ====================

    /**
     * 获取所有支付记录（管理员）
     */
    Page<PaymentDTO> getAllPayments(Long userId, Integer status, LocalDateTime startTime,
                                    LocalDateTime endTime, Integer pageNum, Integer pageSize);

    /**
     * 退款（管理员）
     */
    void refund(Long userId, String paymentNo);

    // ==================== 统计 ====================

    /**
     * 获取支付统计信息
     */
    PaymentStatisticsVO getPaymentStatistics();

    // ==================== 定时任务 ====================

    /**
     * 取消超时未支付记录
     */
    void cancelTimeoutPayments();

    // ==================== 内部调用 ====================

    /**
     * 获取支付实体
     */
    Payment getPaymentEntity(String paymentNo);

    /**
     * 检查支付是否属于用户
     */
    boolean belongsToUser(String paymentNo, Long userId);
}