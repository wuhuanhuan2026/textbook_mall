package com.bookmall.admin.controller;

import com.bookmall.admin.feign.OrderFeignClient;
import com.bookmall.common.entity.PageResult;
import com.bookmall.common.entity.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 管理后台 - 订单管理控制器
 * 通过Feign调用Order服务
 *
 * @author BookMall Team
 */
@Slf4j
@RestController
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
@Validated
@Tag(name = "管理后台-订单管理", description = "管理员订单管理接口")
public class AdminOrderController {

    private final OrderFeignClient orderFeignClient;

    // ==================== 订单查询 ====================

    /**
     * 获取所有订单列表（管理员）
     */
    @GetMapping
    @Operation(summary = "获取所有订单列表")
    public Result<PageResult<Map<String, Object>>> getAllOrders(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        log.info("管理员获取订单列表: userId={}, status={}, startTime={}, endTime={}",
                userId, status, startTime, endTime);
        return orderFeignClient.getAllOrders(userId, status, startTime, endTime, pageNum, pageSize);
    }

    /**
     * 获取订单详情（管理员）
     */
    @GetMapping("/{orderId}")
    @Operation(summary = "获取订单详情")
    public Result<Map<String, Object>> getOrderDetail(
            @Parameter(description = "订单号", required = true)
            @PathVariable @NotBlank String orderId) {
        log.info("管理员获取订单详情: orderId={}", orderId);
        return orderFeignClient.getOrderDetail(orderId);
    }

    // ==================== 订单操作 ====================

    /**
     * 发货（管理员）
     */
    @PutMapping("/{orderId}/deliver")
    @Operation(summary = "发货")
    public Result<Void> deliverOrder(
            @Parameter(description = "订单号", required = true)
            @PathVariable @NotBlank String orderId) {
        log.info("管理员发货: orderId={}", orderId);
        return orderFeignClient.deliverOrder(orderId);
    }

    /**
     * 批量发货
     */
    @PutMapping("/batch/deliver")
    @Operation(summary = "批量发货")
    public Result<Void> batchDeliverOrders(@RequestBody java.util.List<String> orderIds) {
        log.info("管理员批量发货: orderIds={}", orderIds);
        return orderFeignClient.batchDeliverOrders(orderIds);
    }

    /**
     * 取消订单（管理员）
     */
    @PutMapping("/{orderId}/cancel")
    @Operation(summary = "取消订单")
    public Result<Void> cancelOrderByAdmin(
            @Parameter(description = "订单号", required = true)
            @PathVariable @NotBlank String orderId) {
        log.info("管理员取消订单: orderId={}", orderId);
        return orderFeignClient.cancelOrderByAdmin(orderId);
    }

    /**
     * 批量取消订单（管理员）
     */
    @PutMapping("/batch/cancel")
    @Operation(summary = "批量取消订单")
    public Result<Void> batchCancelOrders(@RequestBody java.util.List<String> orderIds) {
        log.info("管理员批量取消订单: orderIds={}", orderIds);
        return orderFeignClient.batchCancelOrders(orderIds);
    }

    /**
     * 修改订单备注
     */
    @PutMapping("/{orderId}/remark")
    @Operation(summary = "修改订单备注")
    public Result<Void> updateOrderRemark(
            @Parameter(description = "订单号", required = true)
            @PathVariable @NotBlank String orderId,
            @RequestParam String remark) {
        log.info("管理员修改订单备注: orderId={}, remark={}", orderId, remark);
        return orderFeignClient.updateOrderRemark(orderId, remark);
    }

    /**
     * 修改订单收货信息
     */
    @PutMapping("/{orderId}/receiver")
    @Operation(summary = "修改订单收货信息")
    public Result<Void> updateReceiverInfo(
            @Parameter(description = "订单号", required = true)
            @PathVariable @NotBlank String orderId,
            @RequestBody Map<String, String> receiverInfo) {
        log.info("管理员修改订单收货信息: orderId={}, receiverInfo={}", orderId, receiverInfo);
        return orderFeignClient.updateReceiverInfo(orderId, receiverInfo);
    }

    // ==================== 统计接口 ====================

    /**
     * 获取订单统计信息（管理员）
     */
    @GetMapping("/statistics")
    @Operation(summary = "获取订单统计信息")
    public Result<Map<String, Object>> getOrderStatistics() {
        log.info("管理员获取订单统计信息");
        return orderFeignClient.getOrderStatistics();
    }

    /**
     * 获取各状态订单数量统计
     */
    @GetMapping("/statistics/status")
    @Operation(summary = "获取各状态订单数量统计")
    public Result<Map<Integer, Long>> countByStatus() {
        log.info("管理员获取各状态订单数量统计");
        return orderFeignClient.countByStatus();
    }

    /**
     * 获取用户订单统计
     */
    @GetMapping("/statistics/user/{userId}")
    @Operation(summary = "获取用户订单统计")
    public Result<Map<String, Object>> getUserOrderStatistics(
            @Parameter(description = "用户ID", required = true)
            @PathVariable @NotNull Long userId) {
        log.info("管理员获取用户订单统计: userId={}", userId);
        return orderFeignClient.getUserOrderStatistics(userId);
    }

    /**
     * 获取今日订单统计
     */
    @GetMapping("/statistics/today")
    @Operation(summary = "获取今日订单统计")
    public Result<Map<String, Object>> getTodayStatistics() {
        log.info("管理员获取今日订单统计");
        return orderFeignClient.getTodayStatistics();
    }

    /**
     * 获取指定日期范围内的订单统计
     */
    @GetMapping("/statistics/range")
    @Operation(summary = "获取指定日期范围内的订单统计")
    public Result<Map<String, Object>> getRangeStatistics(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        log.info("管理员获取日期范围订单统计: startTime={}, endTime={}", startTime, endTime);
        return orderFeignClient.getRangeStatistics(startTime, endTime);
    }

    // ==================== 导出功能 ====================

    /**
     * 导出订单数据（CSV/Excel）
     */
    @GetMapping("/export")
    @Operation(summary = "导出订单数据")
    public Result<String> exportOrders(
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @RequestParam(defaultValue = "csv") String format) {
        log.info("管理员导出订单数据: status={}, startTime={}, endTime={}, format={}",
                status, startTime, endTime, format);
        return orderFeignClient.exportOrders(status, startTime, endTime, format);
    }
}