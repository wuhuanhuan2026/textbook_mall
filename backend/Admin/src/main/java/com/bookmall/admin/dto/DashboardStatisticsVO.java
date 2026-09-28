package com.bookmall.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 仪表盘统计信息VO
 *
 * @author BookMall Team
 */
@Data
@Builder
@Schema(description = "仪表盘统计信息")
public class DashboardStatisticsVO {

    @Schema(description = "用户总数")
    private Long totalUsers;

    @Schema(description = "今日新增用户")
    private Long todayNewUsers;

    @Schema(description = "教材总数")
    private Long totalBooks;

    @Schema(description = "上架教材数")
    private Long onSaleBooks;

    @Schema(description = "订单总数")
    private Long totalOrders;

    @Schema(description = "今日订单数")
    private Long todayOrders;

    @Schema(description = "待处理订单数（待付款+待发货）")
    private Long pendingOrders;

    @Schema(description = "总销售额")
    private BigDecimal totalSales;

    @Schema(description = "今日销售额")
    private BigDecimal todaySales;

    @Schema(description = "总评价数")
    private Long totalReviews;

    @Schema(description = "平均评分")
    private Double avgRating;

    @Schema(description = "总支付金额")
    private BigDecimal totalPaymentAmount;

    @Schema(description = "今日支付金额")
    private BigDecimal todayPaymentAmount;
}