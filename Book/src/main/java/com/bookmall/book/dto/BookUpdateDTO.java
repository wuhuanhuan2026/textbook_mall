package com.bookmall.book.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 更新教材请求DTO
 *
 * @author BookMall Team
 */
@Data
@Schema(description = "更新教材请求")
public class BookUpdateDTO {

    @NotBlank(message = "教材名称不能为空")
    @Schema(description = "教材名称", example = "Spring Boot实战")
    private String bookName;

    @Schema(description = "ISBN编号", example = "978-7-121-34567-8")
    private String isbn;

    @Schema(description = "作者", example = "Craig Walls")
    private String author;

    @Schema(description = "出版社", example = "电子工业出版社")
    private String publisher;

    @NotNull(message = "分类ID不能为空")
    @Schema(description = "分类ID", example = "1")
    private Long categoryId;

    @NotNull(message = "售价不能为空")
    @DecimalMin(value = "0.01", message = "售价必须大于0")
    @Schema(description = "售价", example = "69.00")
    private BigDecimal price;

    @DecimalMin(value = "0.00", message = "原价不能小于0")
    @Schema(description = "原价", example = "89.00")
    private BigDecimal originalPrice;

    @Min(value = 0, message = "库存不能小于0")
    @Schema(description = "库存数量", example = "80")
    private Integer stock;

    @Schema(description = "封面图片URL", example = "/images/springboot.jpg")
    private String coverImage;

    @Schema(description = "教材简介", example = "Spring Boot开发实战指南")
    private String description;

    @NotNull(message = "状态不能为空")
    @Schema(description = "状态：0-下架 1-上架", example = "1")
    private Integer status;
}