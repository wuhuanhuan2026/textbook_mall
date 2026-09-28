package com.bookmall.cart.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 教材信息DTO（用于Feign调用）
 *
 * @author BookMall Team
 */
@Data
public class BookDTO {
    private Long bookId;
    private String bookName;
    private String isbn;
    private String author;
    private String publisher;
    private Long categoryId;
    private String categoryName;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer stock;
    private String coverImage;
    private String description;
    private Integer status;  // 1-上架 0-下架
    private Integer sales;
    private Double avgRating;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}