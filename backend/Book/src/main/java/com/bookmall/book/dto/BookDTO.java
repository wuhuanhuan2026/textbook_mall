package com.bookmall.book.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 教材响应DTO
 *
 * @author BookMall Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "教材信息")
public class BookDTO {

    @Schema(description = "教材ID", example = "1")
    private Long bookId;

    @Schema(description = "教材名称", example = "Java核心技术 卷I")
    private String bookName;

    @Schema(description = "ISBN编号", example = "978-7-111-12345-6")
    private String isbn;

    @Schema(description = "作者", example = "Cay S. Horstmann")
    private String author;

    @Schema(description = "出版社", example = "机械工业出版社")
    private String publisher;

    @Schema(description = "分类ID", example = "1")
    private Long categoryId;

    @Schema(description = "分类名称", example = "计算机")
    private String categoryName;

    @Schema(description = "售价", example = "79.00")
    private BigDecimal price;

    @Schema(description = "原价", example = "99.00")
    private BigDecimal originalPrice;

    @Schema(description = "库存数量", example = "100")
    private Integer stock;

    @Schema(description = "封面图片URL", example = "/images/java-core.jpg")
    private String coverImage;

    @Schema(description = "教材简介", example = "Java经典教材，深入浅出")
    private String description;

    @Schema(description = "状态：0-下架 1-上架", example = "1")
    private Integer status;

    @Schema(description = "销量", example = "50")
    private Integer sales;

    @Schema(description = "平均评分", example = "4.5")
    private Double avgRating;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updateTime;
}