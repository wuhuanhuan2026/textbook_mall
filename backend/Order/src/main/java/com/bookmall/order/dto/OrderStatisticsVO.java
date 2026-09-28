package com.bookmall.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单统计信息VO
 *
 * @author BookMall Team
 */
@Data
@Builder
@Schema(description = "订单统计信息")
public class OrderStatisticsVO {

    @Schema(description = "总订单数")
    private Long totalOrders;

    @Schema(description = "待付款订单数")
    private Long pendingOrders;

    @Schema(description = "待发货订单数")
    private Long paidOrders;

    @Schema(description = "待收货订单数")
    private Long shippedOrders;

    @Schema(description = "已完成订单数")
    private Long completedOrders;

    @Schema(description = "已取消订单数")
    private Long cancelledOrders;

    @Schema(description = "总销售额")
    private BigDecimal totalSales;

    @Schema(description = "平均订单金额")
    private BigDecimal avgAmount;

    @Schema(description = "今日订单数")
    private Long todayOrders;

    @Schema(description = "今日销售额")
    private BigDecimal todaySales;
}