package com.bookmall.payment.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bookmall.common.constant.Constants;
import com.bookmall.common.entity.Result;
import com.bookmall.common.exception.BusinessException;
import com.bookmall.common.exception.ExceptionEnum;
import com.bookmall.common.utils.RedisUtil;
import com.bookmall.common.utils.SnowflakeIdWorker;
import com.bookmall.payment.dto.PaymentCallbackDTO;
import com.bookmall.payment.dto.PaymentCreateDTO;
import com.bookmall.payment.dto.PaymentDTO;
import com.bookmall.payment.dto.PaymentStatisticsVO;
import com.bookmall.payment.entity.Payment;
import com.bookmall.payment.enums.PaymentStatus;
import com.bookmall.payment.feign.OrderFeignClient;
import com.bookmall.payment.feign.UserFeignClient;
import com.bookmall.payment.mapper.PaymentMapper;
import com.bookmall.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 支付服务实现类
 *
 * @author BookMall Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentMapper paymentMapper;
    private final SnowflakeIdWorker snowflakeIdWorker;
    private final RedisUtil redisUtil;

    // Feign Clients
    private final OrderFeignClient orderFeignClient;
    private final UserFeignClient userFeignClient;

    // Redis Key前缀 - 支付锁
    private static final String REDIS_PAYMENT_LOCK_KEY = "payment:lock:";

    // 支付超时时间（30分钟）
    private static final int PAYMENT_TIMEOUT_MINUTES = 30;

    // ==================== 用户端 ====================

    @Override
    @Transactional
    public PaymentDTO createPayment(Long userId, PaymentCreateDTO request) {
        // 1. 获取分布式锁（防止重复支付）
        String lockKey = REDIS_PAYMENT_LOCK_KEY + request.getOrderId();
        Boolean locked = redisUtil.setIfAbsent(lockKey, "1", 10, TimeUnit.SECONDS);
        if (locked == null || !locked) {
            throw new BusinessException("支付处理中，请勿重复操作");
        }

        try {
            // 2. 验证用户
            validateUser(userId);

            // 3. 验证订单
            Result<Object> orderResult = orderFeignClient.getOrderDetail(request.getOrderId());
            if (orderResult == null || !orderResult.isSuccess() || orderResult.getData() == null) {
                throw new BusinessException(ExceptionEnum.ORDER_NOT_EXIST);
            }
            Map<String, Object> order = (Map<String, Object>) orderResult.getData();

            // 验证订单是否属于该用户
            if (!userId.equals(((Number) order.get("userId")).longValue())) {
                throw new BusinessException("订单不属于该用户");
            }

            // 验证订单状态（只有待付款才能支付）
            Integer orderStatus = (Integer) order.get("orderStatus");
            if (orderStatus != Constants.ORDER_STATUS_PENDING) {
                throw new BusinessException("订单状态不正确，无法支付");
            }

            // 验证支付方式
            if (request.getPayType() != Constants.PAY_TYPE_BALANCE) {
                throw new BusinessException("当前仅支持余额支付");
            }

            // 4. 检查是否已存在支付记录
            Payment existPayment = paymentMapper.selectByOrderId(request.getOrderId());
            if (existPayment != null) {
                if (PaymentStatus.isSuccess(existPayment.getStatus())) {
                    throw new BusinessException("该订单已支付成功");
                }
                if (existPayment.getStatus() == PaymentStatus.PENDING.getCode()) {
                    // 存在待支付的记录，直接返回
                    return convertToDTO(existPayment);
                }
            }

            // 5. 验证余额（余额支付）
            BigDecimal amount = (BigDecimal) order.get("totalAmount");
            Result<BigDecimal> balanceResult = userFeignClient.getUserBalance(userId);
            if (balanceResult == null || !balanceResult.isSuccess()) {
                throw new BusinessException("获取余额失败");
            }
            BigDecimal balance = balanceResult.getData();
            if (balance == null || balance.compareTo(amount) < 0) {
                throw new BusinessException(ExceptionEnum.BALANCE_INSUFFICIENT);
            }

            // 6. 创建支付记录
            String paymentNo = generatePaymentNo();
            Payment payment = new Payment();
            payment.setPaymentNo(paymentNo);
            payment.setOrderId(request.getOrderId());
            payment.setUserId(userId);
            payment.setAmount(amount);
            payment.setPayType(request.getPayType());
            payment.setStatus(PaymentStatus.PENDING.getCode());
            payment.setExpireTime(LocalDateTime.now().plusMinutes(PAYMENT_TIMEOUT_MINUTES));

            paymentMapper.insert(payment);

            log.info("创建支付记录成功: paymentNo={}, orderId={}, userId={}, amount={}",
                    paymentNo, request.getOrderId(), userId, amount);

            // 7. 执行余额支付
            executeBalancePayment(payment);

            return convertToDTO(payment);
        } finally {
            // 释放锁
            redisUtil.delete(lockKey);
        }
    }

    /**
     * 执行余额支付
     */
    @Transactional
    public void executeBalancePayment(Payment payment) {
        try {
            // 1. 扣减余额
            Result<Void> deductResult = userFeignClient.deductBalance(payment.getUserId(), payment.getAmount());
            if (deductResult == null || !deductResult.isSuccess()) {
                paymentMapper.payFailed(payment.getPaymentNo(), "余额扣减失败");
                throw new BusinessException("余额扣减失败：" +
                        (deductResult != null ? deductResult.getMessage() : "服务不可用"));
            }

            // 2. 更新支付状态为成功
            String transactionId = generateTransactionId();
            paymentMapper.paySuccess(payment.getPaymentNo(), transactionId, "余额支付成功");

            // 3. 通知订单服务更新订单状态
            Result<Void> orderResult = orderFeignClient.paySuccess(payment.getOrderId());
            if (orderResult == null || !orderResult.isSuccess()) {
                log.warn("订单状态更新失败: orderId={}", payment.getOrderId());
                // 注意：这里即使订单更新失败，支付状态已更新，需要人工介入
            }

            log.info("余额支付成功: paymentNo={}, orderId={}, userId={}, amount={}",
                    payment.getPaymentNo(), payment.getOrderId(), payment.getUserId(), payment.getAmount());

        } catch (Exception e) {
            log.error("余额支付失败: paymentNo={}, error={}", payment.getPaymentNo(), e.getMessage());
            paymentMapper.payFailed(payment.getPaymentNo(), e.getMessage());
            throw e;
        }
    }

    @Override
    public PaymentDTO getPaymentDetail(Long userId, String paymentNo) {
        if (!belongsToUser(paymentNo, userId)) {
            throw new BusinessException(ExceptionEnum.PAYMENT_NOT_EXIST);
        }

        Payment payment = getPaymentEntity(paymentNo);
        return convertToDTO(payment);
    }

    @Override
    public Page<PaymentDTO> getUserPayments(Long userId, Integer status, Integer pageNum, Integer pageSize) {
        Page<Payment> page = new Page<>(pageNum, pageSize);
        Page<Payment> paymentPage = paymentMapper.selectUserPaymentPage(page, userId, status);

        Page<PaymentDTO> dtoPage = new Page<>(pageNum, pageSize, paymentPage.getTotal());
        List<PaymentDTO> dtoList = paymentPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        dtoPage.setRecords(dtoList);

        return dtoPage;
    }

    @Override
    @Transactional
    public void cancelPayment(Long userId, String paymentNo) {
        if (!belongsToUser(paymentNo, userId)) {
            throw new BusinessException(ExceptionEnum.PAYMENT_NOT_EXIST);
        }

        Payment payment = getPaymentEntity(paymentNo);
        if (payment.getStatus() != PaymentStatus.PENDING.getCode()) {
            throw new BusinessException("当前状态无法取消");
        }

        paymentMapper.cancelPayment(paymentNo);

        log.info("取消支付成功: paymentNo={}, userId={}", paymentNo, userId);
    }

    @Override
    @Transactional
    public void handlePaymentCallback(PaymentCallbackDTO callback) {
        Payment payment = paymentMapper.selectByPaymentNo(callback.getPaymentNo());
        if (payment == null) {
            throw new BusinessException(ExceptionEnum.PAYMENT_NOT_EXIST);
        }

        // 防止重复回调
        if (payment.getStatus() != PaymentStatus.PENDING.getCode()) {
            log.info("支付记录已处理，跳过回调: paymentNo={}, status={}",
                    callback.getPaymentNo(), payment.getStatus());
            return;
        }

        if (PaymentStatus.isSuccess(callback.getStatus())) {
            // 支付成功
            paymentMapper.paySuccess(callback.getPaymentNo(),
                    callback.getTransactionId(),
                    callback.getCallbackData());

            // 通知订单服务
            orderFeignClient.paySuccess(callback.getOrderId());

            log.info("支付回调处理成功: paymentNo={}, orderId={}",
                    callback.getPaymentNo(), callback.getOrderId());
        } else {
            // 支付失败
            paymentMapper.payFailed(callback.getPaymentNo(), callback.getCallbackData());
            log.info("支付回调处理失败: paymentNo={}", callback.getPaymentNo());
        }
    }

    // ==================== 管理员端 ====================

    @Override
    public Page<PaymentDTO> getAllPayments(Long userId, Integer status, LocalDateTime startTime,
                                           LocalDateTime endTime, Integer pageNum, Integer pageSize) {
        Page<Payment> page = new Page<>(pageNum, pageSize);
        Page<Payment> paymentPage = paymentMapper.selectAllPaymentPage(page, userId, status, startTime, endTime);

        Page<PaymentDTO> dtoPage = new Page<>(pageNum, pageSize, paymentPage.getTotal());
        List<PaymentDTO> dtoList = paymentPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        dtoPage.setRecords(dtoList);

        return dtoPage;
    }

    @Override
    @Transactional
    public void refund(Long userId, String paymentNo) {
        Payment payment = getPaymentEntity(paymentNo);

        // 验证用户
        if (!payment.getUserId().equals(userId)) {
            throw new BusinessException(ExceptionEnum.PAYMENT_NOT_EXIST);
        }

        // 验证状态
        if (!PaymentStatus.isRefundable(payment.getStatus())) {
            throw new BusinessException("当前状态无法退款");
        }

        // 开始退款
        paymentMapper.startRefund(paymentNo);

        // 退还余额
        Result<Void> refundResult = userFeignClient.addBalance(userId, payment.getAmount());
        if (refundResult == null || !refundResult.isSuccess()) {
            // 退款失败，回滚状态
            paymentMapper.updateStatus(paymentNo, PaymentStatus.SUCCESS.getCode());
            throw new BusinessException("退款失败：" +
                    (refundResult != null ? refundResult.getMessage() : "服务不可用"));
        }

        // 退款完成
        paymentMapper.refundComplete(paymentNo);

        log.info("退款成功: paymentNo={}, userId={}, amount={}",
                paymentNo, userId, payment.getAmount());
    }

    // ==================== 统计 ====================

    @Override
    public PaymentStatisticsVO getPaymentStatistics() {
        Long totalPayments = paymentMapper.countTotal();
        Long successPayments = paymentMapper.countSuccess();
        Long failedPayments = totalPayments != null && successPayments != null ?
                totalPayments - successPayments : 0L;

        BigDecimal totalAmount = paymentMapper.sumTotalAmount();
        if (totalAmount == null) {
            totalAmount = BigDecimal.ZERO;
        }

        BigDecimal successAmount = paymentMapper.sumSuccessAmount();
        if (successAmount == null) {
            successAmount = BigDecimal.ZERO;
        }

        Long todayPayments = paymentMapper.countTodayPayments();
        if (todayPayments == null) {
            todayPayments = 0L;
        }

        BigDecimal todayAmount = paymentMapper.sumTodayAmount();
        if (todayAmount == null) {
            todayAmount = BigDecimal.ZERO;
        }

        // 按支付方式统计
        List<Map<String, Object>> methodStats = paymentMapper.statsByPayType();
        Map<Integer, PaymentStatisticsVO.PaymentMethodStats> methodStatsMap = new HashMap<>();
        for (Map<String, Object> stat : methodStats) {
            Integer payType = (Integer) stat.get("pay_type");
            Long count = (Long) stat.get("count");
            BigDecimal amount = (BigDecimal) stat.get("amount");
            String methodName = getPayTypeName(payType);

            methodStatsMap.put(payType, PaymentStatisticsVO.PaymentMethodStats.builder()
                    .count(count)
                    .amount(amount != null ? amount : BigDecimal.ZERO)
                    .methodName(methodName)
                    .build());
        }

        return PaymentStatisticsVO.builder()
                .totalPayments(totalPayments != null ? totalPayments : 0L)
                .successPayments(successPayments != null ? successPayments : 0L)
                .failedPayments(failedPayments)
                .totalAmount(totalAmount)
                .successAmount(successAmount)
                .todayPayments(todayPayments)
                .todayAmount(todayAmount)
                .methodStats(methodStatsMap)
                .build();
    }

    // ==================== 定时任务 ====================

    @Override
    @Transactional
    public void cancelTimeoutPayments() {
        List<String> timeoutPaymentNos = paymentMapper.findTimeoutPendingPayments();
        if (timeoutPaymentNos == null || timeoutPaymentNos.isEmpty()) {
            return;
        }

        int successCount = 0;
        for (String paymentNo : timeoutPaymentNos) {
            try {
                int result = paymentMapper.cancelPayment(paymentNo);
                if (result > 0) {
                    successCount++;
                    log.info("定时任务取消超时支付: paymentNo={}", paymentNo);
                }
            } catch (Exception e) {
                log.error("取消超时支付失败: paymentNo={}, error={}", paymentNo, e.getMessage());
            }
        }

        log.info("定时任务取消超时支付完成，共处理 {} 笔，成功取消 {} 笔",
                timeoutPaymentNos.size(), successCount);
    }

    // ==================== 内部调用 ====================

    @Override
    public Payment getPaymentEntity(String paymentNo) {
        Payment payment = paymentMapper.selectByPaymentNo(paymentNo);
        if (payment == null) {
            throw new BusinessException(ExceptionEnum.PAYMENT_NOT_EXIST);
        }
        return payment;
    }

    @Override
    public boolean belongsToUser(String paymentNo, Long userId) {
        Payment payment = paymentMapper.selectByPaymentNo(paymentNo);
        return payment != null && payment.getUserId().equals(userId);
    }

    // ==================== 私有方法 ====================

    /**
     * 验证用户
     */
    private void validateUser(Long userId) {
        Result<Boolean> result = userFeignClient.checkUserExists(userId);
        if (result == null || !result.isSuccess() || result.getData() == null || !result.getData()) {
            throw new BusinessException(ExceptionEnum.USER_NOT_EXIST);
        }
    }

    /**
     * 生成支付单号
     * 格式：PAY + yyyyMMddHHmmss + 6位随机数
     */
    private String generatePaymentNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String seq = String.format("%06d", snowflakeIdWorker.nextId() % 1000000);
        return "PAY" + timestamp + seq;
    }

    /**
     * 生成交易流水号
     */
    private String generateTransactionId() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String seq = String.format("%06d", snowflakeIdWorker.nextId() % 1000000);
        return "TXN" + timestamp + seq;
    }

    /**
     * 获取支付方式名称
     */
    private String getPayTypeName(Integer payType) {
        if (payType == null) return "未知";
        switch (payType) {
            case 0: return "余额支付";
            case 1: return "支付宝";
            case 2: return "微信支付";
            default: return "其他";
        }
    }

    /**
     * 实体转DTO
     */
    private PaymentDTO convertToDTO(Payment payment) {
        if (payment == null) {
            return null;
        }

        // 计算剩余时间
        Long remainingSeconds = null;
        if (payment.getExpireTime() != null && payment.getStatus() == PaymentStatus.PENDING.getCode()) {
            remainingSeconds = Math.max(0,
                    java.time.Duration.between(LocalDateTime.now(), payment.getExpireTime()).getSeconds());
        }

        return PaymentDTO.builder()
                .paymentId(payment.getPaymentId())
                .paymentNo(payment.getPaymentNo())
                .orderId(payment.getOrderId())
                .userId(payment.getUserId())
                .username(payment.getUsername())
                .amount(payment.getAmount())
                .payType(payment.getPayType())
                .payTypeDesc(payment.getPayTypeDesc() != null ? payment.getPayTypeDesc() :
                        getPayTypeName(payment.getPayType()))
                .status(payment.getStatus())
                .statusDesc(payment.getStatusDesc() != null ? payment.getStatusDesc() :
                        PaymentStatus.getDesc(payment.getStatus()))
                .transactionId(payment.getTransactionId())
                .payTime(payment.getPayTime())
                .expireTime(payment.getExpireTime())
                .createTime(payment.getCreateTime())
                .updateTime(payment.getUpdateTime())
                .remainingSeconds(remainingSeconds)
                .build();
    }
}