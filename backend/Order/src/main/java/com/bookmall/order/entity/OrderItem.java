package com.bookmall.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单详情实体类 - 对应 tb_order_item 表
 *
 * @author BookMall Team
 */
@Data
@TableName("tb_order_item")
public class OrderItem {

    @TableId(value = "item_id", type = IdType.AUTO)
    private Long itemId;

    @TableField("order_id")
    private String orderId;

    @TableField("book_id")
    private Long bookId;

    @TableField("book_name")
    private String bookName;

    @TableField("book_cover")
    private String bookCover;

    @TableField("price")
    private BigDecimal price;

    @TableField("quantity")
    private Integer quantity;

    @TableField("total_price")
    private BigDecimal totalPrice;
}