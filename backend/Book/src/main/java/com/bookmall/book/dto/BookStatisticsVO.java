package com.bookmall.book.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 教材统计信息VO
 *
 * @author BookMall Team
 */
@Data
@Builder
@Schema(description = "教材统计信息")
public class BookStatisticsVO {

    @Schema(description = "教材总数")
    private Long totalBooks;

    @Schema(description = "上架教材数")
    private Long onSaleBooks;

    @Schema(description = "下架教材数")
    private Long offSaleBooks;

    @Schema(description = "总库存")
    private Long totalStock;

    @Schema(description = "总销量")
    private Long totalSales;

    @Schema(description = "平均售价")
    private BigDecimal avgPrice;

    @Schema(description = "最高售价")
    private BigDecimal maxPrice;

    @Schema(description = "最低售价")
    private BigDecimal minPrice;
}