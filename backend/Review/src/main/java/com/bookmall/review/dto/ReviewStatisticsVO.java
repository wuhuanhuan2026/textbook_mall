package com.bookmall.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 评价统计信息VO
 *
 * @author BookMall Team
 */
@Data
@Builder
@Schema(description = "评价统计信息")
public class ReviewStatisticsVO {

    @Schema(description = "教材ID", example = "1")
    private Long bookId;

    @Schema(description = "评价总数")
    private Long totalReviews;

    @Schema(description = "平均评分")
    private Double avgRating;

    @Schema(description = "1星数量")
    private Long oneStarCount;

    @Schema(description = "2星数量")
    private Long twoStarCount;

    @Schema(description = "3星数量")
    private Long threeStarCount;

    @Schema(description = "4星数量")
    private Long fourStarCount;

    @Schema(description = "5星数量")
    private Long fiveStarCount;

    @Schema(description = "好评率（4星及以上）")
    private Double positiveRate;

    @Schema(description = "差评率（2星及以下）")
    private Double negativeRate;
}