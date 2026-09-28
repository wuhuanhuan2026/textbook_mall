package com.bookmall.book.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 教材搜索请求DTO
 *
 * @author BookMall Team
 */
@Data
@Schema(description = "教材搜索请求")
public class BookSearchDTO {

    @Schema(description = "关键词（搜索书名、作者、出版社）", example = "Java")
    private String keyword;

    @Schema(description = "分类ID", example = "1")
    private Long categoryId;

    @Schema(description = "排序方式：default-综合, price_asc-价格低到高, price_desc-价格高到低, sales-销量最高", example = "default")
    private String sortBy;

    @Schema(description = "最低价格", example = "50")
    private BigDecimal minPrice;

    @Schema(description = "最高价格", example = "100")
    private BigDecimal maxPrice;

    @Schema(description = "状态：0-下架 1-上架", example = "1")
    private Integer status;

    @Schema(description = "当前页码", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页大小", example = "10")
    private Integer pageSize = 10;
}