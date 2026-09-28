package com.bookmall.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付信息响应DTO
 *
 * @author BookMall Team
 */
@Data
@Builder
@Schema(description = "支付信息")
public class PaymentDTO {

    @Schema(description = "支付ID", example = "1")
    private Long paymentId;

    @Schema(description = "支付单号", example = "PAY202609040001")
    private String paymentNo;

    @Schema(description = "订单号", example = "202609040001")
    private String orderId;

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "用户名", example = "testuser")
    private String username;

    @Schema(description = "支付金额", example = "158.00")
    private BigDecimal amount;

    @Schema(description = "支付方式：0-余额 1-支付宝 2-微信", example = "0")
    private Integer payType;

    @Schema(description = "支付方式描述", example = "余额支付")
    private String payTypeDesc;

    @Schema(description = "支付状态：0-待支付 1-处理中 2-支付成功 3-支付失败 4-已取消 5-退款中 6-已退款", example = "2")
    private Integer status;

    @Schema(description = "支付状态描述", example = "支付成功")
    private String statusDesc;

    @Schema(description = "交易流水号", example = "TXN20260904001")
    private String transactionId;

    @Schema(description = "支付时间")
    private LocalDateTime payTime;

    @Schema(description = "过期时间")
    private LocalDateTime expireTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "剩余支付时间（秒）")
    private Long remainingSeconds;
}