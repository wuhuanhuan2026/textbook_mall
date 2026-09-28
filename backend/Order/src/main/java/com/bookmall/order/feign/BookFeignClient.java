package com.bookmall.order.feign;

import com.bookmall.common.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 教材服务 Feign 客户端
 *
 * @author BookMall Team
 */
@FeignClient(name = "book-service", path = "/api/books")  // ✅ 改为 /api/books
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