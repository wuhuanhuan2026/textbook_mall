package com.bookmall.cart.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 购物车实体类 - 对应 tb_cart 表
 *
 * @author BookMall Team
 */
@Data
@TableName("tb_cart")
public class Cart {

    @TableId(value = "cart_id", type = IdType.AUTO)
    private Long cartId;

    @TableField("user_id")
    private Long userId;

    @TableField("book_id")
    private Long bookId;

    @TableField("quantity")
    private Integer quantity;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // ==================== 非数据库字段 ====================

    @TableField(exist = false)
    private String bookName;

    @TableField(exist = false)
    private String bookCover;

    @TableField(exist = false)
    private java.math.BigDecimal price;

    @TableField(exist = false)
    private Integer stock;

    @TableField(exist = false)
    private String author;

    @TableField(exist = false)
    private Boolean onSale;
}