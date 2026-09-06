package com.bookmall.book.feign;

import com.bookmall.book.dto.BookDTO;
import com.bookmall.common.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 教材服务 Feign 客户端
 * 供其他微服务调用
 *
 * @author BookMall Team
 */
@FeignClient(name = "book-service", path = "/api/books")
public interface BookFeignClient {

    /**
     * 获取教材详情
     */
    @GetMapping("/{bookId}")
    Result<BookDTO> getBookDetail(@PathVariable("bookId") Long bookId);

    /**
     * 获取热门教材
     */
    @GetMapping("/hot")
    Result<List<BookDTO>> getHotBooks(@RequestParam(defaultValue = "10") int limit);

    /**
     * 检查教材是否存在
     */
    @GetMapping("/exists/{bookId}")
    Result<Boolean> existsById(@PathVariable("bookId") Long bookId);

    /**
     * 检查教材是否上架
     */
    @GetMapping("/{bookId}/on-sale")
    Result<Boolean> isOnSale(@PathVariable("bookId") Long bookId);

    /**
     * 扣减库存
     */
    @PostMapping("/{bookId}/deduct-stock")
    Result<Void> deductStock(@PathVariable("bookId") Long bookId,
                             @RequestParam Integer quantity);

    /**
     * 增加销量
     */
    @PostMapping("/{bookId}/add-sales")
    Result<Void> addSales(@PathVariable("bookId") Long bookId,
                          @RequestParam Integer quantity);

    /**
     * 获取库存
     */
    @GetMapping("/{bookId}/stock")
    Result<Integer> getStock(@PathVariable("bookId") Long bookId);
}