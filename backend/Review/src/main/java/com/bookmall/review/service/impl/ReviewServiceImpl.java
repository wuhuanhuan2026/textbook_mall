package com.bookmall.review.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bookmall.common.entity.Result;
import com.bookmall.common.exception.BusinessException;
import com.bookmall.common.exception.ExceptionEnum;
import com.bookmall.review.dto.ReviewCreateDTO;
import com.bookmall.review.dto.ReviewDTO;
import com.bookmall.review.dto.ReviewStatisticsVO;
import com.bookmall.review.dto.ReviewUpdateDTO;
import com.bookmall.review.entity.Review;
import com.bookmall.review.enums.ReviewRating;
import com.bookmall.review.feign.BookFeignClient;
import com.bookmall.review.feign.OrderFeignClient;
import com.bookmall.review.feign.UserFeignClient;
import com.bookmall.review.mapper.ReviewMapper;
import com.bookmall.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 评价服务实现类
 *
 * @author BookMall Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewMapper reviewMapper;
    private final BookFeignClient bookFeignClient;
    private final UserFeignClient userFeignClient;
    private final OrderFeignClient orderFeignClient;

    // ==================== 用户端 ====================

    @Override
    @Transactional
    public ReviewDTO createReview(Long userId, ReviewCreateDTO request) {
        // 1. 验证用户
        validateUser(userId);

        // 2. 验证教材
        Result<Boolean> bookResult = bookFeignClient.existsById(request.getBookId());
        if (bookResult == null || !bookResult.isSuccess() || !bookResult.getData()) {
            throw new BusinessException(ExceptionEnum.BOOK_NOT_EXIST);
        }

        // 3. 验证订单（检查用户是否购买了该教材）
        // TODO: 调用订单服务验证用户是否购买过该教材
        // 这里简化处理，假设用户已购买

        // 4. 检查是否已评价
        int count = reviewMapper.countByUserBookOrder(userId, request.getBookId(), request.getOrderId());
        if (count > 0) {
            throw new BusinessException("您已评价过该教材");
        }

        // 5. 验证评分
        if (!ReviewRating.isValid(request.getRating())) {
            throw new BusinessException("评分必须是1-5星");
        }

        // 6. 创建评价
        Review review = new Review();
        review.setUserId(userId);
        review.setBookId(request.getBookId());
        review.setOrderId(request.getOrderId());
        review.setRating(request.getRating());
        review.setContent(request.getContent());

        reviewMapper.insert(review);

        log.info("评价创建成功: reviewId={}, userId={}, bookId={}, rating={}",
                review.getReviewId(), userId, request.getBookId(), request.getRating());

        return convertToDTO(review);
    }

    @Override
    public Page<ReviewDTO> getBookReviews(Long bookId, Integer pageNum, Integer pageSize) {
        Page<Review> page = new Page<>(pageNum, pageSize);
        Page<Review> reviewPage = reviewMapper.selectBookReviewPage(page, bookId);

        Page<ReviewDTO> dtoPage = new Page<>(pageNum, pageSize, reviewPage.getTotal());
        List<ReviewDTO> dtoList = reviewPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        dtoPage.setRecords(dtoList);

        return dtoPage;
    }

    @Override
    public Page<ReviewDTO> getUserReviews(Long userId, Integer pageNum, Integer pageSize) {
        // 验证用户
        validateUser(userId);

        Page<Review> page = new Page<>(pageNum, pageSize);
        Page<Review> reviewPage = reviewMapper.selectUserReviewPage(page, userId);

        Page<ReviewDTO> dtoPage = new Page<>(pageNum, pageSize, reviewPage.getTotal());
        List<ReviewDTO> dtoList = reviewPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        dtoPage.setRecords(dtoList);

        return dtoPage;
    }

    @Override
    public ReviewDTO getReviewDetail(Long userId, Long reviewId) {
        // 验证评价是否存在且属于该用户
        if (!belongsToUser(reviewId, userId)) {
            throw new BusinessException(ExceptionEnum.REVIEW_NOT_EXIST);
        }

        Review review = reviewMapper.selectReviewDetail(reviewId);
        if (review == null) {
            throw new BusinessException(ExceptionEnum.REVIEW_NOT_EXIST);
        }

        return convertToDTO(review);
    }

    @Override
    @Transactional
    public ReviewDTO updateReview(Long userId, Long reviewId, ReviewUpdateDTO request) {
        // 验证评价是否存在且属于该用户
        if (!belongsToUser(reviewId, userId)) {
            throw new BusinessException(ExceptionEnum.REVIEW_NOT_EXIST);
        }

        Review review = reviewMapper.selectById(reviewId);
        if (review == null) {
            throw new BusinessException(ExceptionEnum.REVIEW_NOT_EXIST);
        }

        // 验证评分
        if (!ReviewRating.isValid(request.getRating())) {
            throw new BusinessException("评分必须是1-5星");
        }

        // 更新
        review.setRating(request.getRating());
        review.setContent(request.getContent());
        reviewMapper.updateById(review);

        log.info("评价更新成功: reviewId={}, userId={}", reviewId, userId);

        return convertToDTO(review);
    }

    @Override
    @Transactional
    public void deleteReview(Long userId, Long reviewId) {
        // 验证评价是否存在且属于该用户
        if (!belongsToUser(reviewId, userId)) {
            throw new BusinessException(ExceptionEnum.REVIEW_NOT_EXIST);
        }

        reviewMapper.deleteById(reviewId);

        log.info("评价删除成功: reviewId={}, userId={}", reviewId, userId);
    }

    // ==================== 管理员端 ====================

    @Override
    @Transactional
    public void deleteReviewByAdmin(Long reviewId) {
        Review review = reviewMapper.selectById(reviewId);
        if (review == null) {
            throw new BusinessException(ExceptionEnum.REVIEW_NOT_EXIST);
        }

        reviewMapper.deleteById(reviewId);

        log.info("管理员删除评价成功: reviewId={}", reviewId);
    }

    @Override
    @Transactional
    public void batchDeleteReviews(List<Long> reviewIds) {
        if (reviewIds == null || reviewIds.isEmpty()) {
            return;
        }

        // 验证所有评价是否存在
        for (Long reviewId : reviewIds) {
            Review review = reviewMapper.selectById(reviewId);
            if (review == null) {
                throw new BusinessException("评价ID " + reviewId + " 不存在");
            }
        }

        reviewMapper.batchDelete(reviewIds);

        log.info("批量删除评价成功: reviewIds={}", reviewIds);
    }

    @Override
    @Transactional
    public void batchDeleteByUserIds(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }
        reviewMapper.batchDeleteByUserIds(userIds);
        log.info("批量删除用户评价成功: userIds={}", userIds);
    }

    @Override
    @Transactional
    public void batchDeleteByBookIds(List<Long> bookIds) {
        if (bookIds == null || bookIds.isEmpty()) {
            return;
        }
        reviewMapper.batchDeleteByBookIds(bookIds);
        log.info("批量删除教材评价成功: bookIds={}", bookIds);
    }

    // ==================== 统计 ====================

    @Override
    public ReviewStatisticsVO getBookReviewStatistics(Long bookId) {
        Long totalReviews = reviewMapper.countByBookId(bookId);
        if (totalReviews == 0) {
            return buildEmptyStatistics(bookId);
        }

        Double avgRating = reviewMapper.avgRatingByBookId(bookId);
        List<Map<String, Object>> ratingCounts = reviewMapper.countRatingByBookId(bookId);

        Map<Integer, Long> countMap = new HashMap<>();
        for (Map<String, Object> map : ratingCounts) {
            Integer rating = (Integer) map.get("rating");
            Long count = (Long) map.get("count");
            countMap.put(rating, count);
        }

        long oneStar = countMap.getOrDefault(1, 0L);
        long twoStar = countMap.getOrDefault(2, 0L);
        long threeStar = countMap.getOrDefault(3, 0L);
        long fourStar = countMap.getOrDefault(4, 0L);
        long fiveStar = countMap.getOrDefault(5, 0L);

        long positiveCount = fourStar + fiveStar;
        long negativeCount = oneStar + twoStar;

        double positiveRate = totalReviews > 0 ? (double) positiveCount / totalReviews : 0;
        double negativeRate = totalReviews > 0 ? (double) negativeCount / totalReviews : 0;

        return ReviewStatisticsVO.builder()
                .bookId(bookId)
                .totalReviews(totalReviews)
                .avgRating(avgRating != null ? Math.round(avgRating * 10) / 10.0 : 0.0)
                .oneStarCount(oneStar)
                .twoStarCount(twoStar)
                .threeStarCount(threeStar)
                .fourStarCount(fourStar)
                .fiveStarCount(fiveStar)
                .positiveRate(Math.round(positiveRate * 100 * 10) / 10.0)
                .negativeRate(Math.round(negativeRate * 100 * 10) / 10.0)
                .build();
    }

    @Override
    public ReviewStatisticsVO getSystemReviewStatistics() {
        Long totalReviews = reviewMapper.countTotal();
        if (totalReviews == 0) {
            return buildEmptyStatistics(null);
        }

        List<Map<String, Object>> ratingCounts = reviewMapper.countAllRating();

        Map<Integer, Long> countMap = new HashMap<>();
        for (Map<String, Object> map : ratingCounts) {
            Integer rating = (Integer) map.get("rating");
            Long count = (Long) map.get("count");
            countMap.put(rating, count);
        }

        long oneStar = countMap.getOrDefault(1, 0L);
        long twoStar = countMap.getOrDefault(2, 0L);
        long threeStar = countMap.getOrDefault(3, 0L);
        long fourStar = countMap.getOrDefault(4, 0L);
        long fiveStar = countMap.getOrDefault(5, 0L);

        double totalScore = oneStar * 1 + twoStar * 2 + threeStar * 3 + fourStar * 4 + fiveStar * 5;
        double avgRating = totalReviews > 0 ? totalScore / totalReviews : 0;

        long positiveCount = fourStar + fiveStar;
        long negativeCount = oneStar + twoStar;

        double positiveRate = totalReviews > 0 ? (double) positiveCount / totalReviews : 0;
        double negativeRate = totalReviews > 0 ? (double) negativeCount / totalReviews : 0;

        return ReviewStatisticsVO.builder()
                .bookId(null)
                .totalReviews(totalReviews)
                .avgRating(Math.round(avgRating * 10) / 10.0)
                .oneStarCount(oneStar)
                .twoStarCount(twoStar)
                .threeStarCount(threeStar)
                .fourStarCount(fourStar)
                .fiveStarCount(fiveStar)
                .positiveRate(Math.round(positiveRate * 100 * 10) / 10.0)
                .negativeRate(Math.round(negativeRate * 100 * 10) / 10.0)
                .build();
    }

    @Override
    public List<ReviewDTO> getLatestReviews(int limit) {
        List<Review> reviews = reviewMapper.selectLatestReviews(limit);
        return reviews.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ==================== 验证 ====================

    @Override
    public boolean belongsToUser(Long reviewId, Long userId) {
        Review review = reviewMapper.selectById(reviewId);
        return review != null && review.getUserId().equals(userId);
    }

    @Override
    public boolean existsById(Long reviewId) {
        return reviewMapper.selectById(reviewId) != null;
    }

    // ==================== 私有方法 ====================

    /**
     * 验证用户
     */
    private void validateUser(Long userId) {
        Result<Boolean> result = userFeignClient.checkUserExists(userId);
        if (result == null || !result.isSuccess() || result.getData() == null || !result.getData()) {
            throw new BusinessException(ExceptionEnum.USER_NOT_EXIST);
        }
    }

    /**
     * 构建空统计
     */
    private ReviewStatisticsVO buildEmptyStatistics(Long bookId) {
        return ReviewStatisticsVO.builder()
                .bookId(bookId)
                .totalReviews(0L)
                .avgRating(0.0)
                .oneStarCount(0L)
                .twoStarCount(0L)
                .threeStarCount(0L)
                .fourStarCount(0L)
                .fiveStarCount(0L)
                .positiveRate(0.0)
                .negativeRate(0.0)
                .build();
    }

    /**
     * 实体转DTO
     */
    private ReviewDTO convertToDTO(Review review) {
        if (review == null) {
            return null;
        }

        return ReviewDTO.builder()
                .reviewId(review.getReviewId())
                .userId(review.getUserId())
                .username(review.getUsername())
                .userAvatar(review.getUserAvatar())
                .bookId(review.getBookId())
                .bookName(review.getBookName())
                .bookCover(review.getBookCover())
                .orderId(review.getOrderId())
                .rating(review.getRating())
                .ratingStars(review.getRatingStars() != null ? review.getRatingStars() :
                        ReviewRating.getStars(review.getRating()))
                .ratingDesc(review.getRatingDesc() != null ? review.getRatingDesc() :
                        ReviewRating.getDesc(review.getRating()))
                .content(review.getContent())
                .createTime(review.getCreateTime())
                .build();
    }
}