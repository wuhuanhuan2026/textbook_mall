package com.bookmall.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 订单状态更新请求DTO
 *
 * @author BookMall Team
 */
@Data
@Schema(description = "订单状态更新请求")
public class OrderStatusDTO {

    @NotBlank(message = "订单号不能为空")
    @Schema(description = "订单号", example = "202609040001")
    private String orderId;
}