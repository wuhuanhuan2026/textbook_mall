package com.bookmall.cart.feign;

import com.bookmall.book.dto.BookDTO;
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
 * 路径说明：
 * - Book 服务的 Controller 映射为 @RequestMapping("/books")
 * - Feign 的 path 必须与之一致，即 /books
 * - 拼接后完整路径示例：http://book-service/books/3/stock
 *
 * @author BookMall Team
 */
@FeignClient(name = "book-service", path = "/books")
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
     * ✅ 获取教材详情（用于检查图书是否存在）
     */
    @GetMapping("/{bookId}")
    Result<BookDTO> getBookDetail(@PathVariable("bookId") Long bookId);
}