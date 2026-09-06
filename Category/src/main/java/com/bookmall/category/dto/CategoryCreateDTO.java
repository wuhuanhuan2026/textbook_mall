package com.bookmall.category.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 创建/修改分类请求DTO
 *
 * @author BookMall Team
 */
@Data
@Schema(description = "创建/修改分类请求")
public class CategoryCreateDTO {

    @NotBlank(message = "分类名称不能为空")
    @Schema(description = "分类名称", example = "人工智能")
    private String categoryName;

    @NotNull(message = "排序不能为空")
    @Schema(description = "排序", example = "16")
    private Integer sortOrder;
}