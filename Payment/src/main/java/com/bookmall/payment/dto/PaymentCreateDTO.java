package com.bookmall.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 创建支付请求DTO
 *
 * @author BookMall Team
 */
@Data
@Schema(description = "创建支付请求")
public class PaymentCreateDTO {

    @NotBlank(message = "订单号不能为空")
    @Schema(description = "订单号", example = "202609040001")
    private String orderId;

    @NotNull(message = "支付方式不能为空")
    @Schema(description = "支付方式：0-余额 1-支付宝 2-微信", example = "0")
    private Integer payType;
}