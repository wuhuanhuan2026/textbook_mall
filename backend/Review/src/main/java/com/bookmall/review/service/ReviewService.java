package com.bookmall.review.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bookmall.review.dto.ReviewCreateDTO;
import com.bookmall.review.dto.ReviewDTO;
import com.bookmall.review.dto.ReviewStatisticsVO;
import com.bookmall.review.dto.ReviewUpdateDTO;
import com.bookmall.review.entity.Review;

import java.util.List;

/**
 * 评价服务接口
 *
 * @author BookMall Team
 */
public interface ReviewService {

    // ==================== 用户端 ====================

    /**
     * 创建评价
     */
    ReviewDTO createReview(Long userId, ReviewCreateDTO request);

    /**
     * 分页查询教材评价列表
     */
    Page<ReviewDTO> getBookReviews(Long bookId, Integer pageNum, Integer pageSize);

    /**
     * 分页查询用户评价列表
     */
    Page<ReviewDTO> getUserReviews(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 获取评价详情
     */
    ReviewDTO getReviewDetail(Long userId, Long reviewId);

    /**
     * 更新评价
     */
    ReviewDTO updateReview(Long userId, Long reviewId, ReviewUpdateDTO request);

    /**
     * 删除评价
     */
    void deleteReview(Long userId, Long reviewId);

    // ==================== 管理员端 ====================

    /**
     * 删除评价（管理员）
     */
    void deleteReviewByAdmin(Long reviewId);

    /**
     * 批量删除评价（管理员）
     */
    void batchDeleteReviews(List<Long> reviewIds);

    /**
     * 批量删除用户评价（用户注销时使用）
     */
    void batchDeleteByUserIds(List<Long> userIds);

    /**
     * 批量删除教材评价（教材删除时使用）
     */
    void batchDeleteByBookIds(List<Long> bookIds);

    // ==================== 统计 ====================

    /**
     * 获取教材评价统计信息
     */
    ReviewStatisticsVO getBookReviewStatistics(Long bookId);

    /**
     * 获取系统评价统计信息
     */
    ReviewStatisticsVO getSystemReviewStatistics();

    /**
     * 获取最新评价列表
     */
    List<ReviewDTO> getLatestReviews(int limit);

    // ==================== 验证 ====================

    /**
     * 检查评价是否属于用户
     */
    boolean belongsToUser(Long reviewId, Long userId);

    /**
     * 检查评价是否存在
     */
    boolean existsById(Long reviewId);
}