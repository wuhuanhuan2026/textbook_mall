package com.bookmall.order.feign;

import com.bookmall.common.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 教材服务 Feign 客户端
 *
 * 说明：
 * - 直连 book-service，路径以 BookController 的 @RequestMapping("/books") 为准
 * - 不走网关，因此不带 /api 前缀（/api 仅为网关对外统一入口）
 *
 * @author BookMall Team
 */
@FeignClient(name = "book-service", path = "/books")
public interface BookFeignClient {

    @GetMapping("/{bookId}")
    Result<Object> getBookDetail(@PathVariable("bookId") Long bookId);

    @GetMapping("/{bookId}/on-sale")
    Result<Boolean> isOnSale(@PathVariable("bookId") Long bookId);

    @GetMapping("/{bookId}/price")
    Result<BigDecimal> getPrice(@PathVariable("bookId") Long bookId);

    @GetMapping("/{bookId}/stock")
    Result<Integer> getStock(@PathVariable("bookId") Long bookId);

    @PostMapping("/{bookId}/deduct-stock")
    Result<Void> deductStock(@PathVariable("bookId") Long bookId,
                             @RequestParam("quantity") Integer quantity);

    @PostMapping("/{bookId}/add-sales")
    Result<Void> addSales(@PathVariable("bookId") Long bookId,
                          @RequestParam("quantity") Integer quantity);
}