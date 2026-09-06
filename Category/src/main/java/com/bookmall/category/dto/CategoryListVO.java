package com.bookmall.category.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 分类列表响应VO
 *
 * @author BookMall Team
 */
@Data
@Builder
@Schema(description = "分类列表响应")
public class CategoryListVO {

    @Schema(description = "分类列表")
    private List<CategoryDTO> list;

    @Schema(description = "总数")
    private Long total;
}