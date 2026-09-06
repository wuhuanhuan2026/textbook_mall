package com.bookmall.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单商品详情DTO
 *
 * @author BookMall Team
 */
@Data
@Builder
@Schema(description = "订单商品详情")
public class OrderItemDTO {

    @Schema(description = "详情ID", example = "1")
    private Long itemId;

    @Schema(description = "订单号", example = "202609040001")
    private String orderId;

    @Schema(description = "教材ID", example = "1")
    private Long bookId;

    @Schema(description = "教材名称", example = "Java核心技术 卷I")
    private String bookName;

    @Schema(description = "教材封面", example = "/images/java-core.jpg")
    private String bookCover;

    @Schema(description = "下单时单价", example = "79.00")
    private BigDecimal price;

    @Schema(description = "购买数量", example = "2")
    private Integer quantity;

    @Schema(description = "小计金额", example = "158.00")
    private BigDecimal totalPrice;
}