package com.bookmall.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 创建评价请求DTO
 *
 * @author BookMall Team
 */
@Data
@Schema(description = "创建评价请求")
public class ReviewCreateDTO {

    @NotNull(message = "教材ID不能为空")
    @Schema(description = "教材ID", example = "1")
    private Long bookId;

    @NotNull(message = "订单号不能为空")
    @Schema(description = "订单号", example = "202609040001")
    private String orderId;

    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分最低1星")
    @Max(value = 5, message = "评分最高5星")
    @Schema(description = "评分：1-5星", example = "5")
    private Integer rating;

    @NotBlank(message = "评价内容不能为空")
    @Schema(description = "评价内容", example = "这本书太好了，讲得非常详细")
    private String content;
}