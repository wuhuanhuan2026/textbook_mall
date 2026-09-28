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

    // 注入 Service（不再自己 new 对象）
    private final AdminService adminService;

    /**
     * 获取仪表盘统计数据
     */
    @GetMapping("/statistics")
    @Operation(summary = "获取仪表盘统计数据")
    public Result<DashboardStatisticsVO> getDashboardStatistics() {
        // 直接调用 Service，由 Service 去数据库查真实数据
        return Result.success(adminService.getDashboardStatistics());
    }

    /**
     * 获取趋势数据
     */
    @GetMapping("/trend")
    @Operation(summary = "获取趋势数据")
    public Result<DashboardTrendVO> getDashboardTrend(
            @RequestParam(defaultValue = "day") String type) {
        // 调用 Service
        return Result.success(adminService.getDashboardTrend(type));
    }
}