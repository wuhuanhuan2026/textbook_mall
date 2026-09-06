package com.bookmall.order.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bookmall.common.constant.Constants;
import com.bookmall.common.entity.PageResult;
import com.bookmall.common.entity.Result;
import com.bookmall.order.dto.OrderCreateDTO;
import com.bookmall.order.dto.OrderDTO;
import com.bookmall.order.dto.OrderStatisticsVO;
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

    /**
     * 从请求头获取用户ID
     */
    private Long getUserId(HttpServletRequest request) {
        // 优先从请求头获取
        String userIdHeader = request.getHeader("X-User-Id");
        if (userIdHeader != null && !userIdHeader.isEmpty()) {
            try {
                return Long.parseLong(userIdHeader);
            } catch (NumberFormatException e) {
                log.warn("解析 userId 失败: {}", userIdHeader);
            }
        }
        // 兼容从请求属性获取
        Object userIdAttr = request.getAttribute(Constants.REQ_ATTR_USER_ID);
        if (userIdAttr instanceof Long) {
            return (Long) userIdAttr;
        }
        throw new RuntimeException("用户未登录");
    }

    // ==================== 用户端 ====================

    /**
     * 创建订单
     */
    @PostMapping
    @Operation(summary = "创建订单")
    public Result<OrderDTO> createOrder(
            HttpServletRequest request,
            @Valid @RequestBody OrderCreateDTO orderCreateDTO) {
        Long userId = getUserId(request);
        OrderDTO order = orderService.createOrderFromCart(userId, orderCreateDTO);
        return Result.success("订单创建成功", order);
    }

    /**
     * 立即购买
     */
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

    /**
     * 获取用户订单列表
     */
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

    /**
     * 获取订单详情
     */
    @GetMapping("/{orderId}")
    @Operation(summary = "获取订单详情")
    public Result<OrderDTO> getOrderDetail(
            HttpServletRequest request,
            @Parameter(description = "订单号", required = true)
            @PathVariable @NotBlank String orderId) {
        Long userId = getUserId(request);
        OrderDTO order = orderService.getOrderDetail(userId, orderId);
        return Result.success(order);
    }

    /**
     * 支付订单
     */
    @PostMapping("/{orderId}/pay")
    @Operation(summary = "支付订单")
    public Result<Void> payOrder(
            HttpServletRequest request,
            @Parameter(description = "订单号", required = true)
            @PathVariable @NotBlank String orderId) {
        Long userId = getUserId(request);
        orderService.payOrder(userId, orderId);
        return Result.success("支付成功");
    }

    /**
     * 取消订单
     */
    @PutMapping("/{orderId}/cancel")
    @Operation(summary = "取消订单")
    public Result<Void> cancelOrder(
            HttpServletRequest request,
            @Parameter(description = "订单号", required = true)
            @PathVariable @NotBlank String orderId) {
        Long userId = getUserId(request);
        orderService.cancelOrder(userId, orderId);
        return Result.success("取消成功");
    }

    /**
     * 确认收货
     */
    @PutMapping("/{orderId}/receive")
    @Operation(summary = "确认收货")
    public Result<Void> confirmReceive(
            HttpServletRequest request,
            @Parameter(description = "订单号", required = true)
            @PathVariable @NotBlank String orderId) {
        Long userId = getUserId(request);
        orderService.confirmReceive(userId, orderId);
        return Result.success("确认收货成功");
    }

    /**
     * 统计用户各状态订单数量
     */
    @GetMapping("/count/status")
    @Operation(summary = "统计用户各状态订单数量")
    public Result<List<Map<String, Object>>> countUserOrdersByStatus(
            HttpServletRequest request) {
        Long userId = getUserId(request);
        List<Map<String, Object>> counts = orderService.countUserOrdersByStatus(userId);
        return Result.success(counts);
    }

    // ==================== 管理员端 ====================

    /**
     * 获取所有订单列表（管理员）
     */
    @GetMapping("/admin/list")
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

    /**
     * 发货（管理员）
     */
    @PutMapping("/admin/{orderId}/deliver")
    @Operation(summary = "发货", description = "管理员操作")
    public Result<Void> deliverOrder(
            @Parameter(description = "订单号", required = true)
            @PathVariable @NotBlank String orderId) {
        orderService.deliverOrder(orderId);
        return Result.success("发货成功");
    }

    /**
     * 取消订单（管理员）
     */
    @PutMapping("/admin/{orderId}/cancel")
    @Operation(summary = "取消订单", description = "管理员操作")
    public Result<Void> cancelOrderByAdmin(
            @Parameter(description = "订单号", required = true)
            @PathVariable @NotBlank String orderId) {
        orderService.cancelOrderByAdmin(orderId);
        return Result.success("取消成功");
    }

    /**
     * 获取订单统计信息（管理员）
     */
    @GetMapping("/admin/statistics")
    @Operation(summary = "获取订单统计信息", description = "管理员操作")
    public Result<OrderStatisticsVO> getOrderStatistics() {
        OrderStatisticsVO statistics = orderService.getOrderStatistics();
        return Result.success(statistics);
    }

    // ==================== 内部调用接口 ====================

    /**
     * 支付成功回调（Payment 服务调用）
     */
    @PostMapping("/{orderId}/pay-success")
    @Operation(summary = "支付成功回调", description = "内部调用")
    public Result<Void> paySuccess(@PathVariable @NotBlank String orderId) {
        orderService.paySuccess(orderId);
        return Result.success("支付成功");
    }
}