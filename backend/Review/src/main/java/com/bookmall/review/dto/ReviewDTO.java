package com.bookmall.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评价响应DTO
 *
 * @author BookMall Team
 */
@Data
@Builder
@Schema(description = "评价信息")
public class ReviewDTO {

    @Schema(description = "评价ID", example = "1")
    private Long reviewId;

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "用户名", example = "testuser")
    private String username;

    @Schema(description = "用户头像", example = "/images/avatar.jpg")
    private String userAvatar;

    @Schema(description = "教材ID", example = "1")
    private Long bookId;

    @Schema(description = "教材名称", example = "Java核心技术 卷I")
    private String bookName;

    @Schema(description = "教材封面", example = "/images/java-core.jpg")
    private String bookCover;

    @Schema(description = "订单号", example = "202609040001")
    private String orderId;

    @Schema(description = "评分：1-5星", example = "5")
    private Integer rating;

    @Schema(description = "星级显示", example = "⭐⭐⭐⭐⭐")
    private String ratingStars;

    @Schema(description = "评分描述", example = "非常满意")
    private String ratingDesc;

    @Schema(description = "评价内容", example = "这本书太好了，讲得非常详细")
    private String content;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}