package com.bookmall.admin.feign;

import com.bookmall.common.entity.PageResult;
import com.bookmall.common.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 教材服务 Feign 客户端
 *
 * 路径说明：
 * - Book 服务的 BookController 映射为 @RequestMapping("/books")
 * - 管理员接口与公开接口共用 /books 前缀
 *
 * @author BookMall Team
 */
@FeignClient(name = "book-service", path = "/books")
public interface BookFeignClient {

    @GetMapping
    Result<PageResult<Map<String, Object>>> getBookList(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize);

    @GetMapping("/{bookId}")
    Result<Map<String, Object>> getBookDetail(@PathVariable("bookId") Long bookId);

    @PostMapping
    Result<Map<String, Object>> createBook(@RequestBody Map<String, Object> request);

    @PutMapping("/{bookId}")
    Result<Map<String, Object>> updateBook(
            @PathVariable("bookId") Long bookId,
            @RequestBody Map<String, Object> request);

    @DeleteMapping("/{bookId}")
    Result<Void> deleteBook(@PathVariable("bookId") Long bookId);

    @DeleteMapping("/batch")
    Result<Void> batchDeleteBooks(@RequestBody List<Long> bookIds);

    @PutMapping("/{bookId}/on-sale")
    Result<Void> onSale(@PathVariable("bookId") Long bookId);

    @PutMapping("/{bookId}/off-sale")
    Result<Void> offSale(@PathVariable("bookId") Long bookId);

    @PutMapping("/batch/on-sale")
    Result<Void> batchOnSale(@RequestBody List<Long> bookIds);

    @PutMapping("/batch/off-sale")
    Result<Void> batchOffSale(@RequestBody List<Long> bookIds);

    @PutMapping("/{bookId}/stock")
    Result<Void> updateStock(
            @PathVariable("bookId") Long bookId,
            @RequestParam("stock") Integer stock);

    @GetMapping("/statistics")
    Result<Map<String, Object>> getStatistics();

    @GetMapping("/hot")
    Result<List<Map<String, Object>>> getHotBooks(
            @RequestParam(value = "limit", defaultValue = "10") int limit);

    @GetMapping("/new")
    Result<List<Map<String, Object>>> getNewBooks(
            @RequestParam(value = "limit", defaultValue = "10") int limit);

    @GetMapping("/statistics/category")
    Result<List<Map<String, Object>>> countByCategory();
}