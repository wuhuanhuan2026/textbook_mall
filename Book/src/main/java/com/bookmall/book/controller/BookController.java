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
 * Gateway 的 StripPrefix=1 会移除 /api 前缀，所以这里使用 /books
 *
 * @author BookMall Team
 */
@Slf4j
@RestController
@RequestMapping("/books")  // 修改：去掉 /api，Gateway 的 StripPrefix=1 会移除 /api 前缀
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
        bookService.deleteBook(bookId);
        return Result.success("删除成功");
    }

    /**
     * 批量删除教材
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除教材", description = "管理员操作")
    public Result<Void> batchDeleteBooks(@RequestBody List<Long> bookIds) {
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
        bookService.offSale(bookId);
        return Result.success("下架成功");
    }

    /**
     * 批量上架
     */
    @PutMapping("/batch/on-sale")
    @Operation(summary = "批量上架", description = "管理员操作")
    public Result<Void> batchOnSale(@RequestBody List<Long> bookIds) {
        bookService.batchOnSale(bookIds);
        return Result.success("批量上架成功");
    }

    /**
     * 批量下架
     */
    @PutMapping("/batch/off-sale")
    @Operation(summary = "批量下架", description = "管理员操作")
    public Result<Void> batchOffSale(@RequestBody List<Long> bookIds) {
        bookService.batchOffSale(bookIds);
        return Result.success("批量下架成功");
    }

    /**
     * 获取教材统计信息
     */
    @GetMapping("/statistics")
    @Operation(summary = "获取教材统计信息", description = "管理员操作")
    public Result<BookStatisticsVO> getStatistics() {
        BookStatisticsVO statistics = bookService.getStatistics();
        return Result.success(statistics);
    }

    /**
     * 检查教材是否存在
     */
    @GetMapping("/exists/{bookId}")
    @Operation(summary = "检查教材是否存在", description = "内部调用")
    public Result<Boolean> existsById(
            @PathVariable @NotNull Long bookId) {
        boolean exists = bookService.existsById(bookId);
        return Result.success(exists);
    }

    /**
     * 检查教材是否上架
     */
    @GetMapping("/{bookId}/on-sale")
    @Operation(summary = "检查教材是否上架", description = "内部调用")
    public Result<Boolean> isOnSale(
            @PathVariable @NotNull Long bookId) {
        boolean onSale = bookService.isOnSale(bookId);
        return Result.success(onSale);
    }

    /**
     * 扣减库存（订单服务调用）
     */
    @PostMapping("/{bookId}/deduct-stock")
    @Operation(summary = "扣减库存", description = "内部调用")
    public Result<Void> deductStock(
            @PathVariable @NotNull Long bookId,
            @RequestParam @NotNull Integer quantity) {
        bookService.deductStock(bookId, quantity);
        return Result.success("扣减成功");
    }

    /**
     * 增加销量（订单服务调用）
     */
    @PostMapping("/{bookId}/add-sales")
    @Operation(summary = "增加销量", description = "内部调用")
    public Result<Void> addSales(
            @PathVariable @NotNull Long bookId,
            @RequestParam @NotNull Integer quantity) {
        bookService.addSales(bookId, quantity);
        return Result.success("更新成功");
    }
}