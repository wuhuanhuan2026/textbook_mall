package com.bookmall.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 更新评价请求DTO
 *
 * @author BookMall Team
 */
@Data
@Schema(description = "更新评价请求")
public class ReviewUpdateDTO {

    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分最低1星")
    @Max(value = 5, message = "评分最高5星")
    @Schema(description = "评分：1-5星", example = "4")
    private Integer rating;

    @NotBlank(message = "评价内容不能为空")
    @Schema(description = "评价内容", example = "这本书很好，推荐给大家")
    private String content;
}