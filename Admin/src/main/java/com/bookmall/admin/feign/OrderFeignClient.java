package com.bookmall.admin.feign;

import com.bookmall.common.entity.PageResult;
import com.bookmall.common.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@FeignClient(name = "order-service", path = "/api/order")
public interface OrderFeignClient {

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

    @GetMapping("/statistics/total")
    Result<Long> getTotalOrders();

    @GetMapping("/statistics/today")
    Result<Long> getTodayOrders();

    @GetMapping("/statistics/pending")
    Result<Long> getPendingOrders();

    @GetMapping("/statistics/sales/total")
    Result<BigDecimal> getTotalSales();

    @GetMapping("/statistics/sales/today")
    Result<BigDecimal> getTodaySales();
}