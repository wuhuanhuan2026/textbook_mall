package com.bookmall.order.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bookmall.common.constant.Constants;
import com.bookmall.common.entity.PageResult;
import com.bookmall.common.entity.Result;
import com.bookmall.order.dto.OrderCreateDTO;
import com.bookmall.order.dto.OrderDTO;
import com.bookmall.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 订单控制器
 *
 * @author BookMall Team
 */
@Slf4j
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
@Validated
@Tag(name = "订单接口", description = "订单管理")
public class OrderController {

    private final OrderService orderService;

    private Long getUserId(HttpServletRequest request) {
        String userIdHeader = request.getHeader("X-User-Id");
        if (userIdHeader != null && !userIdHeader.isEmpty()) {
            try {
                return Long.parseLong(userIdHeader);
            } catch (NumberFormatException e) {
                log.warn("解析 userId 失败: {}", userIdHeader);
            }
        }
        Object userIdAttr = request.getAttribute(Constants.REQ_ATTR_USER_ID);
        if (userIdAttr instanceof Long) {
            return (Long) userIdAttr;
        }
        throw new RuntimeException("用户未登录");
    }

    // ==================== 用户端 ====================

    @PostMapping
    @Operation(summary = "创建订单")
    public Result<OrderDTO> createOrder(
            HttpServletRequest request,
            @Valid @RequestBody OrderCreateDTO orderCreateDTO) {
        Long userId = getUserId(request);
        OrderDTO order = orderService.createOrderFromCart(userId, orderCreateDTO);
        return Result.success("订单创建成功", order);
    }

    @PostMapping("/direct")
    @Operation(summary = "立即购买")
    public Result<OrderDTO> directBuy(
            HttpServletRequest request,
            @RequestParam @NotNull Long bookId,
            @RequestParam(defaultValue = "1") Integer quantity,
            @RequestParam @NotNull Long addressId,
            @RequestParam(required = false) String remark) {
        Long userId = getUserId(request);
        OrderDTO order = orderService.createOrderDirect(userId, bookId, quantity, addressId, remark);
        return Result.success("订单创建成功", order);
    }

    @GetMapping
    @Operation(summary = "获取用户订单列表")
    public Result<PageResult<OrderDTO>> getUserOrders(
            HttpServletRequest request,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = getUserId(request);
        Page<OrderDTO> page = orderService.getUserOrders(userId, status, pageNum, pageSize);
        return Result.success(PageResult.of(page));
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "获取订单详情")
    public Result<OrderDTO> getOrderDetail(
            HttpServletRequest request,
            @PathVariable @NotBlank String orderId) {
        Long userId = getUserId(request);
        OrderDTO order = orderService.getOrderDetail(userId, orderId);
        return Result.success(order);
    }

    @PostMapping("/{orderId}/pay")
    @Operation(summary = "支付订单")
    public Result<Void> payOrder(
            HttpServletRequest request,
            @PathVariable @NotBlank String orderId) {
        Long userId = getUserId(request);
        orderService.payOrder(userId, orderId);
        return Result.success("支付成功");
    }

    @PutMapping("/{orderId}/cancel")
    @Operation(summary = "取消订单")
    public Result<Void> cancelOrder(
            HttpServletRequest request,
            @PathVariable @NotBlank String orderId) {
        Long userId = getUserId(request);
        orderService.cancelOrder(userId, orderId);
        return Result.success("取消成功");
    }

    @PutMapping("/{orderId}/receive")
    @Operation(summary = "确认收货")
    public Result<Void> confirmReceive(
            HttpServletRequest request,
            @PathVariable @NotBlank String orderId) {
        Long userId = getUserId(request);
        orderService.confirmReceive(userId, orderId);
        return Result.success("确认收货成功");
    }

    @GetMapping("/count/status")
    @Operation(summary = "统计用户各状态订单数量")
    public Result<List<Map<String, Object>>> countUserOrdersByStatus(HttpServletRequest request) {
        Long userId = getUserId(request);
        List<Map<String, Object>> counts = orderService.countUserOrdersByStatus(userId);
        return Result.success(counts);
    }

    // ==================== 管理员端 ====================

    @GetMapping("/admin/orders")
    @Operation(summary = "获取所有订单列表", description = "管理员操作")
    public Result<PageResult<OrderDTO>> getAllOrders(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<OrderDTO> page = orderService.getAllOrders(userId, status, startTime, endTime, pageNum, pageSize);
        return Result.success(PageResult.of(page));
    }

    @GetMapping("/admin/orders/{orderId}")
    @Operation(summary = "获取订单详情（管理员）", description = "管理员操作")
    public Result<OrderDTO> getOrderDetailByAdmin(@PathVariable @NotBlank String orderId) {
        OrderDTO order = orderService.getOrderDetailByAdmin(orderId);
        return Result.success(order);
    }

    @PutMapping("/admin/orders/{orderId}/deliver")
    @Operation(summary = "发货", description = "管理员操作")
    public Result<Void> deliverOrder(@PathVariable @NotBlank String orderId) {
        orderService.deliverOrder(orderId);
        return Result.success("发货成功");
    }

    @PutMapping("/admin/orders/{orderId}/cancel")
    @Operation(summary = "取消订单", description = "管理员操作")
    public Result<Void> cancelOrderByAdmin(@PathVariable @NotBlank String orderId) {
        orderService.cancelOrderByAdmin(orderId);
        return Result.success("取消成功");
    }

    // ===== 补充接口（Feign 调用需要） =====

    @PutMapping("/admin/orders/batch/deliver")
    @Operation(summary = "批量发货", description = "管理员操作")
    public Result<Void> batchDeliverOrders(@RequestBody List<String> orderIds) {
        for (String orderId : orderIds) {
            orderService.deliverOrder(orderId);
        }
        return Result.success("批量发货成功");
    }

    @PutMapping("/admin/orders/batch/cancel")
    @Operation(summary = "批量取消订单", description = "管理员操作")
    public Result<Void> batchCancelOrders(@RequestBody List<String> orderIds) {
        for (String orderId : orderIds) {
            orderService.cancelOrderByAdmin(orderId);
        }
        return Result.success("批量取消成功");
    }

    @PutMapping("/admin/orders/{orderId}/remark")
    @Operation(summary = "修改订单备注", description = "管理员操作")
    public Result<Void> updateOrderRemark(
            @PathVariable @NotBlank String orderId,
            @RequestParam String remark) {
        orderService.updateOrderRemark(orderId, remark);
        return Result.success("备注修改成功");
    }

    @PutMapping("/admin/orders/{orderId}/receiver")
    @Operation(summary = "修改收货信息", description = "管理员操作")
    public Result<Void> updateReceiverInfo(
            @PathVariable @NotBlank String orderId,
            @RequestBody Map<String, String> receiverInfo) {
        orderService.updateReceiverInfo(orderId, receiverInfo);
        return Result.success("收货信息修改成功");
    }

    @GetMapping("/admin/orders/statistics")
    @Operation(summary = "获取订单统计信息", description = "管理员操作")
    public Result<Map<String, Object>> getOrderStatistics() {
        return Result.success(orderService.getOrderStatisticsMap());
    }

    @GetMapping("/admin/orders/statistics/status")
    @Operation(summary = "获取各状态订单数量统计", description = "管理员操作")
    public Result<Map<Integer, Long>> countByStatus() {
        return Result.success(orderService.countByStatusMap());
    }

    @GetMapping("/admin/orders/statistics/user/{userId}")
    @Operation(summary = "获取用户订单统计", description = "管理员操作")
    public Result<Map<String, Object>> getUserOrderStatistics(@PathVariable Long userId) {
        return Result.success(orderService.getUserOrderStatistics(userId));
    }

    @GetMapping("/admin/orders/statistics/today")
    @Operation(summary = "获取今日订单统计", description = "管理员操作")
    public Result<Map<String, Object>> getTodayStatistics() {
        return Result.success(orderService.getTodayStatistics());
    }

    @GetMapping("/admin/orders/statistics/range")
    @Operation(summary = "获取指定日期范围内的订单统计", description = "管理员操作")
    public Result<Map<String, Object>> getRangeStatistics(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        return Result.success(orderService.getRangeStatistics(startTime, endTime));
    }

    @GetMapping("/admin/orders/export")
    @Operation(summary = "导出订单数据", description = "管理员操作")
    public Result<String> exportOrders(
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @RequestParam(defaultValue = "csv") String format) {
        return Result.success("导出功能待实现");
    }

    // ==================== 内部调用接口 ====================

    @PostMapping("/{orderId}/pay-success")
    @Operation(summary = "支付成功回调", description = "内部调用")
    public Result<Void> paySuccess(@PathVariable @NotBlank String orderId) {
        orderService.paySuccess(orderId);
        return Result.success("支付成功");
    }
}