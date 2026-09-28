package com.bookmall.review.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bookmall.common.constant.Constants;
import com.bookmall.common.entity.PageResult;
import com.bookmall.common.entity.Result;
import com.bookmall.review.dto.ReviewCreateDTO;
import com.bookmall.review.dto.ReviewDTO;
import com.bookmall.review.dto.ReviewStatisticsVO;
import com.bookmall.review.dto.ReviewUpdateDTO;
import com.bookmall.review.service.ReviewService;
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
 * 评价控制器
 *
 * @author BookMall Team
 */
@Slf4j
@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
@Validated
@Tag(name = "评价接口", description = "评价管理")
public class ReviewController {

    private final ReviewService reviewService;

    // ==================== 用户端 ====================

    /**
     * 创建评价
     */
    @PostMapping
    @Operation(summary = "创建评价")
    public Result<ReviewDTO> createReview(
            @RequestAttribute(Constants.REQ_ATTR_USER_ID) Long userId,
            @Valid @RequestBody ReviewCreateDTO request) {
        ReviewDTO review = reviewService.createReview(userId, request);
        return Result.success("评价成功", review);
    }

    /**
     * 获取教材评价列表
     */
    @GetMapping("/book/{bookId}")
    @Operation(summary = "获取教材评价列表")
    public Result<PageResult<ReviewDTO>> getBookReviews(
            @Parameter(description = "教材ID", required = true)
            @PathVariable @NotNull Long bookId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<ReviewDTO> page = reviewService.getBookReviews(bookId, pageNum, pageSize);
        return Result.success(PageResult.of(page));
    }

    /**
     * 获取用户评价列表
     */
    @GetMapping("/user")
    @Operation(summary = "获取用户评价列表")
    public Result<PageResult<ReviewDTO>> getUserReviews(
            @RequestAttribute(Constants.REQ_ATTR_USER_ID) Long userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<ReviewDTO> page = reviewService.getUserReviews(userId, pageNum, pageSize);
        return Result.success(PageResult.of(page));
    }

    /**
     * 获取评价详情
     */
    @GetMapping("/{reviewId}")
    @Operation(summary = "获取评价详情")
    public Result<ReviewDTO> getReviewDetail(
            @RequestAttribute(Constants.REQ_ATTR_USER_ID) Long userId,
            @Parameter(description = "评价ID", required = true)
            @PathVariable @NotNull Long reviewId) {
        ReviewDTO review = reviewService.getReviewDetail(userId, reviewId);
        return Result.success(review);
    }

    /**
     * 更新评价
     */
    @PutMapping("/{reviewId}")
    @Operation(summary = "更新评价")
    public Result<ReviewDTO> updateReview(
            @RequestAttribute(Constants.REQ_ATTR_USER_ID) Long userId,
            @Parameter(description = "评价ID", required = true)
            @PathVariable @NotNull Long reviewId,
            @Valid @RequestBody ReviewUpdateDTO request) {
        ReviewDTO review = reviewService.updateReview(userId, reviewId, request);
        return Result.success("更新成功", review);
    }

    /**
     * 删除评价
     */
    @DeleteMapping("/{reviewId}")
    @Operation(summary = "删除评价")
    public Result<Void> deleteReview(
            @RequestAttribute(Constants.REQ_ATTR_USER_ID) Long userId,
            @Parameter(description = "评价ID", required = true)
            @PathVariable @NotNull Long reviewId) {
        reviewService.deleteReview(userId, reviewId);
        return Result.success("删除成功");
    }

    /**
     * 获取教材评价统计信息
     */
    @GetMapping("/statistics/book/{bookId}")
    @Operation(summary = "获取教材评价统计信息")
    public Result<ReviewStatisticsVO> getBookReviewStatistics(
            @Parameter(description = "教材ID", required = true)
            @PathVariable @NotNull Long bookId) {
        ReviewStatisticsVO statistics = reviewService.getBookReviewStatistics(bookId);
        return Result.success(statistics);
    }

    /**
     * 获取最新评价列表
     */
    @GetMapping("/latest")
    @Operation(summary = "获取最新评价列表")
    public Result<List<ReviewDTO>> getLatestReviews(
            @RequestParam(defaultValue = "10") int limit) {
        List<ReviewDTO> reviews = reviewService.getLatestReviews(limit);
        return Result.success(reviews);
    }

    // ==================== 管理员端 ====================

    /**
     * 删除评价（管理员）
     */
    @DeleteMapping("/admin/{reviewId}")
    @Operation(summary = "删除评价", description = "管理员操作")
    public Result<Void> deleteReviewByAdmin(
            @Parameter(description = "评价ID", required = true)
            @PathVariable @NotNull Long reviewId) {
        reviewService.deleteReviewByAdmin(reviewId);
        return Result.success("删除成功");
    }

    /**
     * 批量删除评价（管理员）
     */
    @DeleteMapping("/admin/batch")
    @Operation(summary = "批量删除评价", description = "管理员操作")
    public Result<Void> batchDeleteReviews(@RequestBody List<Long> reviewIds) {
        reviewService.batchDeleteReviews(reviewIds);
        return Result.success("批量删除成功");
    }

    /**
     * 获取系统评价统计信息（管理员）
     */
    @GetMapping("/admin/statistics")
    @Operation(summary = "获取系统评价统计信息", description = "管理员操作")
    public Result<ReviewStatisticsVO> getSystemReviewStatistics() {
        ReviewStatisticsVO statistics = reviewService.getSystemReviewStatistics();
        return Result.success(statistics);
    }
}