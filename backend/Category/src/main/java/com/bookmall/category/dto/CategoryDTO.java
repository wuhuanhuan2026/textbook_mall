package com.bookmall.category.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分类数据传输对象
 *
 * @author BookMall Team
 */
@Data
@Builder
@NoArgsConstructor  // 添加无参构造方法（Jackson 反序列化需要）
@AllArgsConstructor // 添加全参构造方法（Builder 需要）
@Schema(description = "分类信息")
public class CategoryDTO {

    @Schema(description = "分类ID", example = "1")
    private Long categoryId;

    @Schema(description = "分类名称", example = "计算机")
    private String categoryName;

    @Schema(description = "排序", example = "1")
    private Integer sortOrder;

    @Schema(description = "该分类下教材数量", example = "6")
    private Integer bookCount;
}