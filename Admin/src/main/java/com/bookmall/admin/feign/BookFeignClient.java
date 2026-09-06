package com.bookmall.admin.feign;

import com.bookmall.common.entity.PageResult;
import com.bookmall.common.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@FeignClient(name = "book-service", path = "/api/books")
public interface BookFeignClient {

    @GetMapping("/admin/list")
    Result<PageResult<Map<String, Object>>> getBookList(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize);

    @GetMapping("/admin/{bookId}")
    Result<Map<String, Object>> getBookDetail(@PathVariable("bookId") Long bookId);

    @PostMapping("/admin")
    Result<Map<String, Object>> createBook(@RequestBody Map<String, Object> request);

    @PutMapping("/admin/{bookId}")
    Result<Map<String, Object>> updateBook(
            @PathVariable("bookId") Long bookId,
            @RequestBody Map<String, Object> request);

    @DeleteMapping("/admin/{bookId}")
    Result<Void> deleteBook(@PathVariable("bookId") Long bookId);

    @DeleteMapping("/admin/batch")
    Result<Void> batchDeleteBooks(@RequestBody List<Long> bookIds);

    @PutMapping("/admin/{bookId}/on-sale")
    Result<Void> onSale(@PathVariable("bookId") Long bookId);

    @PutMapping("/admin/{bookId}/off-sale")
    Result<Void> offSale(@PathVariable("bookId") Long bookId);

    @PutMapping("/admin/batch/on-sale")
    Result<Void> batchOnSale(@RequestBody List<Long> bookIds);

    @PutMapping("/admin/batch/off-sale")
    Result<Void> batchOffSale(@RequestBody List<Long> bookIds);

    @PutMapping("/admin/{bookId}/stock")
    Result<Void> updateStock(
            @PathVariable("bookId") Long bookId,
            @RequestParam("stock") Integer stock);

    @GetMapping("/statistics/total")
    Result<Long> getTotalBooks();

    @GetMapping("/statistics/on-sale")
    Result<Long> getOnSaleBooks();

    @GetMapping("/admin/statistics")
    Result<Map<String, Object>> getStatistics();

    @GetMapping("/admin/hot")
    Result<List<Map<String, Object>>> getHotBooks(
            @RequestParam(value = "limit", defaultValue = "10") int limit);

    @GetMapping("/admin/new")
    Result<List<Map<String, Object>>> getNewBooks(
            @RequestParam(value = "limit", defaultValue = "10") int limit);

    @GetMapping("/admin/statistics/category")
    Result<List<Map<String, Object>>> countByCategory();
}