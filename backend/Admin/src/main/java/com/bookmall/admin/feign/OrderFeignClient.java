package com.bookmall.admin.feign;

import com.bookmall.common.entity.PageResult;
import com.bookmall.common.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 订单服务 Feign 客户端
 *
 * 路径说明：
 * - Order 服务的 OrderController 映射为 @RequestMapping("/order")
 * - 管理员接口路径为 /order/admin/orders/**
 * - Feign 的 path 必须与之一致，即 /order
 *
 * @author BookMall Team
 */
@FeignClient(name = "order-service", path = "/order")
public interface OrderFeignClient {

    // ✅ 修正：/admin/list → /admin/orders
    @GetMapping("/admin/orders")
    Result<PageResult<Map<String, Object>>> getAllOrders(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "startTime", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(value = "endTime", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize);

    @GetMapping("/admin/orders/{orderId}")
    Result<Map<String, Object>> getOrderDetail(@PathVariable("orderId") String orderId);

    @PutMapping("/admin/orders/{orderId}/deliver")
    Result<Void> deliverOrder(@PathVariable("orderId") String orderId);

    @PutMapping("/admin/orders/batch/deliver")
    Result<Void> batchDeliverOrders(@RequestBody List<String> orderIds);

    @PutMapping("/admin/orders/{orderId}/cancel")
    Result<Void> cancelOrderByAdmin(@PathVariable("orderId") String orderId);

    @PutMapping("/admin/orders/batch/cancel")
    Result<Void> batchCancelOrders(@RequestBody List<String> orderIds);

    @PutMapping("/admin/orders/{orderId}/remark")
    Result<Void> updateOrderRemark(
            @PathVariable("orderId") String orderId,
            @RequestParam("remark") String remark);

    @PutMapping("/admin/orders/{orderId}/receiver")
    Result<Void> updateReceiverInfo(
            @PathVariable("orderId") String orderId,
            @RequestBody Map<String, String> receiverInfo);

    @GetMapping("/admin/orders/statistics")
    Result<Map<String, Object>> getOrderStatistics();

    @GetMapping("/admin/orders/statistics/status")
    Result<Map<Integer, Long>> countByStatus();

    @GetMapping("/admin/orders/statistics/user/{userId}")
    Result<Map<String, Object>> getUserOrderStatistics(@PathVariable("userId") Long userId);

    @GetMapping("/admin/orders/statistics/today")
    Result<Map<String, Object>> getTodayStatistics();

    @GetMapping("/admin/orders/statistics/range")
    Result<Map<String, Object>> getRangeStatistics(
            @RequestParam("startTime")
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam("endTime")
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime);

    @GetMapping("/admin/orders/export")
    Result<String> exportOrders(
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "startTime", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(value = "endTime", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @RequestParam(value = "format", defaultValue = "csv") String format);
}