package com.bookmall.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 仪表盘趋势数据VO
 *
 * @author BookMall Team
 */
@Data
@Builder
@Schema(description = "仪表盘趋势数据")
public class DashboardTrendVO {

    @Schema(description = "日期列表")
    private List<String> dates;

    @Schema(description = "订单数量趋势")
    private List<Long> orderCounts;

    @Schema(description = "销售额趋势")
    private List<BigDecimal> salesAmounts;

    @Schema(description = "用户注册趋势")
    private List<Long> userRegistrations;

    @Schema(description = "趋势类型：day-日, week-周, month-月", example = "day")
    private String trendType;
}