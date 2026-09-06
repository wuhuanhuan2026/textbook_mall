package com.bookmall.payment.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bookmall.common.constant.Constants;
import com.bookmall.common.entity.PageResult;
import com.bookmall.common.entity.Result;
import com.bookmall.payment.dto.PaymentCallbackDTO;
import com.bookmall.payment.dto.PaymentCreateDTO;
import com.bookmall.payment.dto.PaymentDTO;
import com.bookmall.payment.dto.PaymentStatisticsVO;
import com.bookmall.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 支付控制器
 *
 * @author BookMall Team
 */
@Slf4j
@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
@Validated
@Tag(name = "支付接口", description = "支付管理")
public class PaymentController {

    private final PaymentService paymentService;

    // ==================== 用户端 ====================

    /**
     * 创建支付
     */
    @PostMapping
    @Operation(summary = "创建支付")
    public Result<PaymentDTO> createPayment(
            @RequestAttribute(Constants.REQ_ATTR_USER_ID) Long userId,
            @Valid @RequestBody PaymentCreateDTO request) {
        PaymentDTO payment = paymentService.createPayment(userId, request);
        return Result.success("支付创建成功", payment);
    }

    /**
     * 获取支付详情
     */
    @GetMapping("/{paymentNo}")
    @Operation(summary = "获取支付详情")
    public Result<PaymentDTO> getPaymentDetail(
            @RequestAttribute(Constants.REQ_ATTR_USER_ID) Long userId,
            @Parameter(description = "支付单号", required = true)
            @PathVariable @NotBlank String paymentNo) {
        PaymentDTO payment = paymentService.getPaymentDetail(userId, paymentNo);
        return Result.success(payment);
    }

    /**
     * 获取用户支付记录列表
     */
    @GetMapping
    @Operation(summary = "获取用户支付记录列表")
    public Result<PageResult<PaymentDTO>> getUserPayments(
            @RequestAttribute(Constants.REQ_ATTR_USER_ID) Long userId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<PaymentDTO> page = paymentService.getUserPayments(userId, status, pageNum, pageSize);
        return Result.success(PageResult.of(page));
    }

    /**
     * 取消支付
     */
    @PutMapping("/{paymentNo}/cancel")
    @Operation(summary = "取消支付")
    public Result<Void> cancelPayment(
            @RequestAttribute(Constants.REQ_ATTR_USER_ID) Long userId,
            @Parameter(description = "支付单号", required = true)
            @PathVariable @NotBlank String paymentNo) {
        paymentService.cancelPayment(userId, paymentNo);
        return Result.success("取消成功");
    }

    /**
     * 支付回调接口（外部系统调用）
     */
    @PostMapping("/callback")
    @Operation(summary = "支付回调", description = "第三方支付回调接口")
    public Result<Void> paymentCallback(@Valid @RequestBody PaymentCallbackDTO callback) {
        paymentService.handlePaymentCallback(callback);
        return Result.success("回调处理成功");
    }

    // ==================== 管理员端 ====================

    /**
     * 获取所有支付记录（管理员）
     */
    @GetMapping("/admin/list")
    @Operation(summary = "获取所有支付记录", description = "管理员操作")
    public Result<PageResult<PaymentDTO>> getAllPayments(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<PaymentDTO> page = paymentService.getAllPayments(userId, status, startTime, endTime, pageNum, pageSize);
        return Result.success(PageResult.of(page));
    }

    /**
     * 退款（管理员）
     */
    @PostMapping("/admin/{paymentNo}/refund")
    @Operation(summary = "退款", description = "管理员操作")
    public Result<Void> refund(
            @Parameter(description = "支付单号", required = true)
            @PathVariable @NotBlank String paymentNo,
            @RequestAttribute(Constants.REQ_ATTR_USER_ID) Long userId) {
        paymentService.refund(userId, paymentNo);
        return Result.success("退款成功");
    }

    /**
     * 获取支付统计信息（管理员）
     */
    @GetMapping("/admin/statistics")
    @Operation(summary = "获取支付统计信息", description = "管理员操作")
    public Result<PaymentStatisticsVO> getPaymentStatistics() {
        PaymentStatisticsVO statistics = paymentService.getPaymentStatistics();
        return Result.success(statistics);
    }
}