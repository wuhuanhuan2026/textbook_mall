package com.bookmall.cart.feign;

import com.bookmall.common.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**
 * 教材服务 Feign 客户端
 *
 * @author BookMall Team
 */
@FeignClient(name = "book-service", path = "/api/books")
public interface BookFeignClient {

    /**
     * 检查教材是否上架
     */
    @GetMapping("/{bookId}/on-sale")
    Result<Boolean> isOnSale(@PathVariable("bookId") Long bookId);

    /**
     * 获取教材价格
     */
    @GetMapping("/{bookId}/price")
    Result<BigDecimal> getPrice(@PathVariable("bookId") Long bookId);

    /**
     * 获取教材库存
     */
    @GetMapping("/{bookId}/stock")
    Result<Integer> getStock(@PathVariable("bookId") Long bookId);

    /**
     * 检查教材是否存在
     */
    @GetMapping("/exists/{bookId}")
    Result<Boolean> existsById(@PathVariable("bookId") Long bookId);
}