package com.bookmall.admin.controller;

import com.bookmall.admin.dto.DashboardStatisticsVO;
import com.bookmall.admin.dto.DashboardTrendVO;
import com.bookmall.admin.service.AdminService;
import com.bookmall.common.entity.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 管理后台仪表盘控制器
 *
 * @author BookMall Team
 */
@Slf4j
@RestController
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
@Tag(name = "仪表盘接口", description = "管理后台仪表盘数据")
public class AdminDashboardController {

    // 这里注入各个服务的Feign客户端
    // 实际实现中需要调用其他模块获取统计数据

    /**
     * 获取仪表盘统计数据
     */
    @GetMapping("/statistics")
    @Operation(summary = "获取仪表盘统计数据")
    public Result<DashboardStatisticsVO> getDashboardStatistics() {
        // TODO: 调用各个服务获取统计数据
        // - UserService: 用户总数、今日新增
        // - BookService: 教材总数、上架数
        // - OrderService: 订单总数、今日订单、待处理订单、销售额
        // - ReviewService: 评价总数、平均评分
        // - PaymentService: 支付总额

        DashboardStatisticsVO statistics = DashboardStatisticsVO.builder()
                .totalUsers(1000L)
                .todayNewUsers(10L)
                .totalBooks(500L)
                .onSaleBooks(450L)
                .totalOrders(2000L)
                .todayOrders(20L)
                .pendingOrders(15L)
                .totalSales(java.math.BigDecimal.valueOf(100000.00))
                .todaySales(java.math.BigDecimal.valueOf(5000.00))
                .totalReviews(800L)
                .avgRating(4.5)
                .totalPaymentAmount(java.math.BigDecimal.valueOf(98000.00))
                .todayPaymentAmount(java.math.BigDecimal.valueOf(4800.00))
                .build();

        return Result.success(statistics);
    }

    /**
     * 获取趋势数据
     */
    @GetMapping("/trend")
    @Operation(summary = "获取趋势数据")
    public Result<DashboardTrendVO> getDashboardTrend(
            @RequestParam(defaultValue = "day") String type) {
        // TODO: 根据类型（日/周/月）获取趋势数据
        // 调用各服务获取时间序列数据

        DashboardTrendVO trend = DashboardTrendVO.builder()
                .dates(java.util.Arrays.asList("2026-09-01", "2026-09-02", "2026-09-03", "2026-09-04"))
                .orderCounts(java.util.Arrays.asList(15L, 18L, 22L, 20L))
                .salesAmounts(java.util.Arrays.asList(
                        java.math.BigDecimal.valueOf(3500.00),
                        java.math.BigDecimal.valueOf(4200.00),
                        java.math.BigDecimal.valueOf(5800.00),
                        java.math.BigDecimal.valueOf(5000.00)
                ))
                .userRegistrations(java.util.Arrays.asList(8L, 12L, 10L, 10L))
                .trendType(type)
                .build();

        return Result.success(trend);
    }
}