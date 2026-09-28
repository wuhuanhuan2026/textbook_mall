package com.bookmall.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车结算VO
 *
 * @author BookMall Team
 */
@Data
@Builder
@Schema(description = "购物车结算信息")
public class CartCheckVO {

    @Schema(description = "选中的商品列表")
    private List<CartDTO> items;

    @Schema(description = "商品总数量")
    private Integer totalQuantity;

    @Schema(description = "商品总金额")
    private BigDecimal totalAmount;

    @Schema(description = "运费")
    private BigDecimal shippingFee;

    @Schema(description = "实付金额")
    private BigDecimal payAmount;
}