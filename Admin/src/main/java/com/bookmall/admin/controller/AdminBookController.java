package com.bookmall.admin.controller;

import com.bookmall.admin.feign.BookFeignClient;
import com.bookmall.common.constant.Constants;
import com.bookmall.common.entity.PageResult;
import com.bookmall.common.entity.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 管理后台 - 教材管理控制器
 * 通过Feign调用Book服务
 *
 * @author BookMall Team
 */
@Slf4j
@RestController
@RequestMapping("/admin/books")
@RequiredArgsConstructor
@Validated
@Tag(name = "管理后台-教材管理", description = "管理员教材管理接口")
public class AdminBookController {

    private final BookFeignClient bookFeignClient;

    // ==================== 教材管理 ====================

    /**
     * 分页查询教材列表（管理员）
     */
    @GetMapping
    @Operation(summary = "分页查询教材列表")
    public Result<PageResult<Map<String, Object>>> getBookList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        Result<PageResult<Map<String, Object>>> result = bookFeignClient.getBookList(
                keyword, categoryId, status, minPrice, maxPrice, sortBy, pageNum, pageSize);
        return result;
    }

    /**
     * 获取教材详情（管理员）
     */
    @GetMapping("/{bookId}")
    @Operation(summary = "获取教材详情")
    public Result<Map<String, Object>> getBookDetail(
            @Parameter(description = "教材ID", required = true)
            @PathVariable @NotNull Long bookId) {
        return bookFeignClient.getBookDetail(bookId);
    }

    /**
     * 创建教材
     */
    @PostMapping
    @Operation(summary = "创建教材")
    public Result<Map<String, Object>> createBook(@RequestBody Map<String, Object> request) {
        log.info("管理员创建教材: {}", request);
        return bookFeignClient.createBook(request);
    }

    /**
     * 更新教材
     */
    @PutMapping("/{bookId}")
    @Operation(summary = "更新教材")
    public Result<Map<String, Object>> updateBook(
            @Parameter(description = "教材ID", required = true)
            @PathVariable @NotNull Long bookId,
            @RequestBody Map<String, Object> request) {
        log.info("管理员更新教材: bookId={}, request={}", bookId, request);
        return bookFeignClient.updateBook(bookId, request);
    }

    /**
     * 删除教材
     */
    @DeleteMapping("/{bookId}")
    @Operation(summary = "删除教材")
    public Result<Void> deleteBook(
            @Parameter(description = "教材ID", required = true)
            @PathVariable @NotNull Long bookId) {
        log.info("管理员删除教材: bookId={}", bookId);
        return bookFeignClient.deleteBook(bookId);
    }

    /**
     * 批量删除教材
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除教材")
    public Result<Void> batchDeleteBooks(@RequestBody List<Long> bookIds) {
        log.info("管理员批量删除教材: bookIds={}", bookIds);
        return bookFeignClient.batchDeleteBooks(bookIds);
    }

    /**
     * 上架教材
     */
    @PutMapping("/{bookId}/on-sale")
    @Operation(summary = "上架教材")
    public Result<Void> onSale(
            @Parameter(description = "教材ID", required = true)
            @PathVariable @NotNull Long bookId) {
        log.info("管理员上架教材: bookId={}", bookId);
        return bookFeignClient.onSale(bookId);
    }

    /**
     * 下架教材
     */
    @PutMapping("/{bookId}/off-sale")
    @Operation(summary = "下架教材")
    public Result<Void> offSale(
            @Parameter(description = "教材ID", required = true)
            @PathVariable @NotNull Long bookId) {
        log.info("管理员下架教材: bookId={}", bookId);
        return bookFeignClient.offSale(bookId);
    }

    /**
     * 批量上架
     */
    @PutMapping("/batch/on-sale")
    @Operation(summary = "批量上架")
    public Result<Void> batchOnSale(@RequestBody List<Long> bookIds) {
        log.info("管理员批量上架教材: bookIds={}", bookIds);
        return bookFeignClient.batchOnSale(bookIds);
    }

    /**
     * 批量下架
     */
    @PutMapping("/batch/off-sale")
    @Operation(summary = "批量下架")
    public Result<Void> batchOffSale(@RequestBody List<Long> bookIds) {
        log.info("管理员批量下架教材: bookIds={}", bookIds);
        return bookFeignClient.batchOffSale(bookIds);
    }

    /**
     * 更新库存
     */
    @PutMapping("/{bookId}/stock")
    @Operation(summary = "更新库存")
    public Result<Void> updateStock(
            @Parameter(description = "教材ID", required = true)
            @PathVariable @NotNull Long bookId,
            @RequestParam @NotNull Integer stock) {
        log.info("管理员更新库存: bookId={}, stock={}", bookId, stock);
        return bookFeignClient.updateStock(bookId, stock);
    }

    // ==================== 统计接口 ====================

    /**
     * 获取教材统计信息
     */
    @GetMapping("/statistics")
    @Operation(summary = "获取教材统计信息")
    public Result<Map<String, Object>> getStatistics() {
        return bookFeignClient.getStatistics();
    }

    /**
     * 获取热门教材
     */
    @GetMapping("/hot")
    @Operation(summary = "获取热门教材")
    public Result<List<Map<String, Object>>> getHotBooks(
            @RequestParam(defaultValue = "10") int limit) {
        return bookFeignClient.getHotBooks(limit);
    }

    /**
     * 获取新品教材
     */
    @GetMapping("/new")
    @Operation(summary = "获取新品教材")
    public Result<List<Map<String, Object>>> getNewBooks(
            @RequestParam(defaultValue = "10") int limit) {
        return bookFeignClient.getNewBooks(limit);
    }

    /**
     * 按分类统计教材数量
     */
    @GetMapping("/statistics/category")
    @Operation(summary = "按分类统计教材数量")
    public Result<List<Map<String, Object>>> countByCategory() {
        return bookFeignClient.countByCategory();
    }
}