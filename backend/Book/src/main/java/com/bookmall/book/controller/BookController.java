package com.bookmall.book.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bookmall.book.dto.*;
import com.bookmall.book.service.BookService;
import com.bookmall.common.entity.PageResult;
import com.bookmall.common.entity.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 教材控制器
 *
 * 路径说明：
 * - 前端请求: /api/books/** → Gateway 转发 → /api/books/**
 * - Controller 映射: /api/books (匹配 Feign 和 Gateway 的请求)
 * - Feign 调用: /api/books (由 FeignClient 的 path 决定)
 *
 * @author BookMall Team
 */
@Slf4j
@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
@Validated
@Tag(name = "教材接口", description = "教材管理")
public class BookController {

    private final BookService bookService;

    // ==================== 公开接口 ====================

    /**
     * 分页查询教材列表
     */
    @GetMapping
    @Operation(summary = "分页查询教材列表")
    public Result<PageResult<BookDTO>> getBookList(@Valid BookSearchDTO searchDTO) {
        log.info("分页查询教材列表: {}", searchDTO);
        Page<BookDTO> page = bookService.getBookList(searchDTO);
        return Result.success(PageResult.of(page));
    }

    /**
     * 获取教材详情
     */
    @GetMapping("/{bookId}")
    @Operation(summary = "获取教材详情")
    public Result<BookDTO> getBookDetail(
            @Parameter(description = "教材ID", required = true)
            @PathVariable @NotNull Long bookId) {
        log.info("获取教材详情: bookId={}", bookId);
        BookDTO book = bookService.getBookDetail(bookId);
        return Result.success(book);
    }

    /**
     * 获取热门教材
     */
    @GetMapping("/hot")
    @Operation(summary = "获取热门教材", description = "按销量排序")
    public Result<List<BookDTO>> getHotBooks(
            @Parameter(description = "数量限制", example = "10")
            @RequestParam(defaultValue = "10") int limit) {
        log.info("获取热门教材: limit={}", limit);
        List<BookDTO> books = bookService.getHotBooks(limit);
        return Result.success(books);
    }

    /**
     * 获取新品教材
     */
    @GetMapping("/new")
    @Operation(summary = "获取新品教材", description = "按创建时间排序")
    public Result<List<BookDTO>> getNewBooks(
            @Parameter(description = "数量限制", example = "10")
            @RequestParam(defaultValue = "10") int limit) {
        log.info("获取新品教材: limit={}", limit);
        List<BookDTO> books = bookService.getNewBooks(limit);
        return Result.success(books);
    }

    /**
     * 按分类获取教材
     */
    @GetMapping("/category/{categoryId}")
    @Operation(summary = "按分类获取教材")
    public Result<PageResult<BookDTO>> getBooksByCategory(
            @Parameter(description = "分类ID", required = true)
            @PathVariable @NotNull Long categoryId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("按分类获取教材: categoryId={}, pageNum={}, pageSize={}", categoryId, pageNum, pageSize);
        Page<BookDTO> page = bookService.getBooksByCategory(categoryId, pageNum, pageSize);
        return Result.success(PageResult.of(page));
    }

    /**
     * 搜索教材
     */
    @GetMapping("/search")
    @Operation(summary = "搜索教材")
    public Result<PageResult<BookDTO>> searchBooks(
            @Parameter(description = "关键词", required = true)
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("搜索教材: keyword={}, pageNum={}, pageSize={}", keyword, pageNum, pageSize);
        Page<BookDTO> page = bookService.searchBooks(keyword, pageNum, pageSize);
        return Result.success(PageResult.of(page));
    }

    // ==================== 管理接口（需要管理员权限） ====================

    /**
     * 创建教材
     */
    @PostMapping
    @Operation(summary = "创建教材", description = "管理员操作")
    public Result<BookDTO> createBook(@Valid @RequestBody BookCreateDTO request) {
        log.info("创建教材: {}", request);
        BookDTO book = bookService.createBook(request);
        return Result.success("创建成功", book);
    }

    /**
     * 更新教材
     */
    @PutMapping("/{bookId}")
    @Operation(summary = "更新教材", description = "管理员操作")
    public Result<BookDTO> updateBook(
            @Parameter(description = "教材ID", required = true)
            @PathVariable @NotNull Long bookId,
            @Valid @RequestBody BookUpdateDTO request) {
        log.info("更新教材: bookId={}, request={}", bookId, request);
        BookDTO book = bookService.updateBook(bookId, request);
        return Result.success("更新成功", book);
    }

    /**
     * 删除教材
     */
    @DeleteMapping("/{bookId}")
    @Operation(summary = "删除教材", description = "管理员操作")
    public Result<Void> deleteBook(
            @Parameter(description = "教材ID", required = true)
            @PathVariable @NotNull Long bookId) {
        log.info("删除教材: bookId={}", bookId);
        bookService.deleteBook(bookId);
        return Result.success("删除成功");
    }

    /**
     * 批量删除教材
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除教材", description = "管理员操作")
    public Result<Void> batchDeleteBooks(@RequestBody List<Long> bookIds) {
        log.info("批量删除教材: bookIds={}", bookIds);
        bookService.batchDeleteBooks(bookIds);
        return Result.success("批量删除成功");
    }

    /**
     * 上架教材
     */
    @PutMapping("/{bookId}/on-sale")
    @Operation(summary = "上架教材", description = "管理员操作")
    public Result<Void> onSale(
            @Parameter(description = "教材ID", required = true)
            @PathVariable @NotNull Long bookId) {
        log.info("上架教材: bookId={}", bookId);
        bookService.onSale(bookId);
        return Result.success("上架成功");
    }

    /**
     * 下架教材
     */
    @PutMapping("/{bookId}/off-sale")
    @Operation(summary = "下架教材", description = "管理员操作")
    public Result<Void> offSale(
            @Parameter(description = "教材ID", required = true)
            @PathVariable @NotNull Long bookId) {
        log.info("下架教材: bookId={}", bookId);
        bookService.offSale(bookId);
        return Result.success("下架成功");
    }

    /**
     * 批量上架
     */
    @PutMapping("/batch/on-sale")
    @Operation(summary = "批量上架", description = "管理员操作")
    public Result<Void> batchOnSale(@RequestBody List<Long> bookIds) {
        log.info("批量上架: bookIds={}", bookIds);
        bookService.batchOnSale(bookIds);
        return Result.success("批量上架成功");
    }

    /**
     * 批量下架
     */
    @PutMapping("/batch/off-sale")
    @Operation(summary = "批量下架", description = "管理员操作")
    public Result<Void> batchOffSale(@RequestBody List<Long> bookIds) {
        log.info("批量下架: bookIds={}", bookIds);
        bookService.batchOffSale(bookIds);
        return Result.success("批量下架成功");
    }

    /**
     * 获取教材统计信息
     */
    @GetMapping("/statistics")
    @Operation(summary = "获取教材统计信息", description = "管理员操作")
    public Result<BookStatisticsVO> getStatistics() {
        log.info("获取教材统计信息");
        BookStatisticsVO statistics = bookService.getStatistics();
        return Result.success(statistics);
    }

    // ==================== 内部调用接口（Feign 调用） ====================

    /**
     * 检查教材是否存在（供其他服务调用）
     */
    @GetMapping("/exists/{bookId}")
    @Operation(summary = "检查教材是否存在", description = "内部调用")
    public Result<Boolean> existsById(
            @Parameter(description = "教材ID", required = true)
            @PathVariable @NotNull Long bookId) {
        log.info("检查教材是否存在: bookId={}", bookId);
        boolean exists = bookService.existsById(bookId);
        return Result.success(exists);
    }

    /**
     * 检查教材是否上架（供其他服务调用）
     */
    @GetMapping("/{bookId}/on-sale")
    @Operation(summary = "检查教材是否上架", description = "内部调用")
    public Result<Boolean> isOnSale(
            @Parameter(description = "教材ID", required = true)
            @PathVariable @NotNull Long bookId) {
        log.info("检查教材是否上架: bookId={}", bookId);
        boolean onSale = bookService.isOnSale(bookId);
        return Result.success(onSale);
    }

    /**
     * 获取教材价格（供其他服务调用）
     */
    @GetMapping("/{bookId}/price")
    @Operation(summary = "获取教材价格", description = "内部调用")
    public Result<java.math.BigDecimal> getBookPrice(
            @Parameter(description = "教材ID", required = true)
            @PathVariable @NotNull Long bookId) {
        log.info("获取教材价格: bookId={}", bookId);
        java.math.BigDecimal price = bookService.getBookPrice(bookId);
        return Result.success(price);
    }

    /**
     * 获取教材库存（供其他服务调用）
     */
    @GetMapping("/{bookId}/stock")
    @Operation(summary = "获取教材库存", description = "内部调用")
    public Result<Integer> getStock(
            @Parameter(description = "教材ID", required = true)
            @PathVariable @NotNull Long bookId) {
        log.info("获取教材库存: bookId={}", bookId);
        Integer stock = bookService.getStock(bookId);
        return Result.success(stock);
    }

    /**
     * 扣减库存（订单服务调用）
     */
    @PostMapping("/{bookId}/deduct-stock")
    @Operation(summary = "扣减库存", description = "内部调用")
    public Result<Void> deductStock(
            @Parameter(description = "教材ID", required = true)
            @PathVariable @NotNull Long bookId,
            @Parameter(description = "扣减数量", required = true)
            @RequestParam @NotNull Integer quantity) {
        log.info("扣减库存: bookId={}, quantity={}", bookId, quantity);
        bookService.deductStock(bookId, quantity);
        return Result.success("扣减成功");
    }

    /**
     * 增加销量（订单服务调用）
     */
    @PostMapping("/{bookId}/add-sales")
    @Operation(summary = "增加销量", description = "内部调用")
    public Result<Void> addSales(
            @Parameter(description = "教材ID", required = true)
            @PathVariable @NotNull Long bookId,
            @Parameter(description = "增加数量", required = true)
            @RequestParam @NotNull Integer quantity) {
        log.info("增加销量: bookId={}, quantity={}", bookId, quantity);
        bookService.addSales(bookId, quantity);
        return Result.success("更新成功");
    }

    /**
     * 增加库存（退货时调用）
     */
    @PostMapping("/{bookId}/add-stock")
    @Operation(summary = "增加库存", description = "内部调用")
    public Result<Void> addStock(
            @Parameter(description = "教材ID", required = true)
            @PathVariable @NotNull Long bookId,
            @Parameter(description = "增加数量", required = true)
            @RequestParam @NotNull Integer quantity) {
        log.info("增加库存: bookId={}, quantity={}", bookId, quantity);
        bookService.addStock(bookId, quantity);
        return Result.success("更新成功");
    }

    /**
     * 批量获取教材信息（订单服务调用）
     */
    @PostMapping("/batch")
    @Operation(summary = "批量获取教材信息", description = "内部调用")
    public Result<List<BookDTO>> getBooksByIds(@RequestBody List<Long> bookIds) {
        log.info("批量获取教材信息: bookIds={}", bookIds);
        List<BookDTO> books = bookService.getBooksByIds(bookIds);
        return Result.success(books);
    }
}