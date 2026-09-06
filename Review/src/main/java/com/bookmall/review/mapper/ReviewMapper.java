package com.bookmall.review.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bookmall.review.entity.Review;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 评价数据访问层
 *
 * @author BookMall Team
 */
@Mapper
public interface ReviewMapper extends BaseMapper<Review> {

    /**
     * 分页查询教材评价列表
     */
    Page<Review> selectBookReviewPage(Page<Review> page,
                                      @Param("bookId") Long bookId);

    /**
     * 分页查询用户评价列表
     */
    Page<Review> selectUserReviewPage(Page<Review> page,
                                      @Param("userId") Long userId);

    /**
     * 查询评价详情（含用户和教材信息）
     */
    Review selectReviewDetail(@Param("reviewId") Long reviewId);

    /**
     * 根据用户、教材、订单查询评价
     */
    @Select("SELECT review_id, user_id, book_id, order_id, rating, content, create_time " +
            "FROM tb_review WHERE user_id = #{userId} AND book_id = #{bookId} AND order_id = #{orderId}")
    Review selectByUserBookOrder(@Param("userId") Long userId,
                                 @Param("bookId") Long bookId,
                                 @Param("orderId") String orderId);

    /**
     * 统计教材评价数量
     */
    @Select("SELECT COUNT(*) FROM tb_review WHERE book_id = #{bookId}")
    Long countByBookId(@Param("bookId") Long bookId);

    /**
     * 统计教材各星级数量
     */
    @Select("SELECT rating, COUNT(*) AS count FROM tb_review " +
            "WHERE book_id = #{bookId} GROUP BY rating")
    List<Map<String, Object>> countRatingByBookId(@Param("bookId") Long bookId);

    /**
     * 计算教材平均评分
     */
    @Select("SELECT AVG(rating) FROM tb_review WHERE book_id = #{bookId}")
    Double avgRatingByBookId(@Param("bookId") Long bookId);

    /**
     * 统计用户评价数量
     */
    @Select("SELECT COUNT(*) FROM tb_review WHERE user_id = #{userId}")
    Long countByUserId(@Param("userId") Long userId);

    /**
     * 统计用户各星级数量
     */
    @Select("SELECT rating, COUNT(*) AS count FROM tb_review " +
            "WHERE user_id = #{userId} GROUP BY rating")
    List<Map<String, Object>> countRatingByUserId(@Param("userId") Long userId);

    /**
     * 检查用户是否已评价该订单的教材
     */
    @Select("SELECT COUNT(*) FROM tb_review " +
            "WHERE user_id = #{userId} AND book_id = #{bookId} AND order_id = #{orderId}")
    int countByUserBookOrder(@Param("userId") Long userId,
                             @Param("bookId") Long bookId,
                             @Param("orderId") String orderId);

    /**
     * 批量删除评价（管理员）
     */
    int batchDelete(@Param("reviewIds") List<Long> reviewIds);

    /**
     * 批量删除用户评价（用户注销时使用）
     */
    int batchDeleteByUserIds(@Param("userIds") List<Long> userIds);

    /**
     * 批量删除教材评价（教材删除时使用）
     */
    int batchDeleteByBookIds(@Param("bookIds") List<Long> bookIds);

    /**
     * 统计系统评价总数
     */
    @Select("SELECT COUNT(*) FROM tb_review")
    Long countTotal();

    /**
     * 统计系统各星级数量
     */
    @Select("SELECT rating, COUNT(*) AS count FROM tb_review GROUP BY rating")
    List<Map<String, Object>> countAllRating();

    /**
     * 统计今日新增评价数
     */
    @Select("SELECT COUNT(*) FROM tb_review WHERE DATE(create_time) = CURDATE()")
    Long countTodayReviews();

    /**
     * 获取最新评价（带用户信息）
     */
    @Select("SELECT r.*, u.username, u.avatar as user_avatar, b.book_name, b.cover_image as book_cover " +
            "FROM tb_review r " +
            "LEFT JOIN tb_user u ON r.user_id = u.user_id " +
            "LEFT JOIN tb_book b ON r.book_id = b.book_id " +
            "ORDER BY r.create_time DESC LIMIT #{limit}")
    List<Review> selectLatestReviews(@Param("limit") int limit);
}