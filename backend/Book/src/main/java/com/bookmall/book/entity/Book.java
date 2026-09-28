package com.bookmall.book.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 教材实体类 - 对应 tb_book 表
 *
 * @author BookMall Team
 */
@Data
@TableName("tb_book")
public class Book {

    @TableId(value = "book_id", type = IdType.AUTO)
    private Long bookId;

    @TableField("book_name")
    private String bookName;

    @TableField("isbn")
    private String isbn;

    @TableField("author")
    private String author;

    @TableField("publisher")
    private String publisher;

    @TableField("category_id")
    private Long categoryId;

    @TableField("price")
    private BigDecimal price;

    @TableField("original_price")
    private BigDecimal originalPrice;

    @TableField("stock")
    private Integer stock;

    @TableField("cover_image")
    private String coverImage;

    @TableField("description")
    private String description;

    @TableField("status")
    private Integer status;

    @TableField("sales")
    private Integer sales;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // ==================== 非数据库字段 ====================

    @TableField(exist = false)
    private String categoryName;

    @TableField(exist = false)
    private Double avgRating;
}