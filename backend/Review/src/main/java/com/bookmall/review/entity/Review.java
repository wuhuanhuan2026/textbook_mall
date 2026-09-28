package com.bookmall.review.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评价实体类 - 对应 tb_review 表
 *
 * @author BookMall Team
 */
@Data
@TableName("tb_review")
public class Review {

    @TableId(value = "review_id", type = IdType.AUTO)
    private Long reviewId;

    @TableField("user_id")
    private Long userId;

    @TableField("book_id")
    private Long bookId;

    @TableField("order_id")
    private String orderId;

    @TableField("rating")
    private Integer rating;

    @TableField("content")
    private String content;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    // ==================== 非数据库字段 ====================

    @TableField(exist = false)
    private String username;

    @TableField(exist = false)
    private String userAvatar;

    @TableField(exist = false)
    private String bookName;

    @TableField(exist = false)
    private String bookCover;

    @TableField(exist = false)
    private String ratingStars;

    @TableField(exist = false)
    private String ratingDesc;
}