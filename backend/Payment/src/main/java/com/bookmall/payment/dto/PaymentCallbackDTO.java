package com.bookmall.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 支付回调请求DTO
 *
 * @author BookMall Team
 */
@Data
@Schema(description = "支付回调请求")
public class PaymentCallbackDTO {

    @NotBlank(message = "支付单号不能为空")
    @Schema(description = "支付单号", example = "PAY202609040001")
    private String paymentNo;

    @NotBlank(message = "订单号不能为空")
    @Schema(description = "订单号", example = "202609040001")
    private String orderId;

    @NotNull(message = "支付金额不能为空")
    @Schema(description = "支付金额", example = "158.00")
    private BigDecimal amount;

    @NotBlank(message = "交易流水号不能为空")
    @Schema(description = "交易流水号", example = "TXN20260904001")
    private String transactionId;

    @NotNull(message = "支付状态不能为空")
    @Schema(description = "支付状态：2-成功 3-失败", example = "2")
    private Integer status;

    @Schema(description = "回调数据（JSON格式）")
    private String callbackData;
}