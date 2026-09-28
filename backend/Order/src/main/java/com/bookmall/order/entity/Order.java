package com.bookmall.order.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类 - 对应 tb_order 表
 *
 * @author BookMall Team
 */
@Data
@TableName("tb_order")
public class Order {

    @TableId("order_id")
    private String orderId;

    @TableField("user_id")
    private Long userId;

    @TableField("total_amount")
    private BigDecimal totalAmount;

    @TableField("shipping_fee")
    private BigDecimal shippingFee;

    @TableField("order_status")
    private Integer orderStatus;

    @TableField("pay_type")
    private Integer payType;

    @TableField("receiver_name")
    private String receiverName;

    @TableField("receiver_phone")
    private String receiverPhone;

    @TableField("receiver_address")
    private String receiverAddress;

    @TableField("remark")
    private String remark;

    @TableField("pay_time")
    private LocalDateTime payTime;

    @TableField("deliver_time")
    private LocalDateTime deliverTime;

    @TableField("receive_time")
    private LocalDateTime receiveTime;

    @TableField("create_time")
    private LocalDateTime createTime;

    // ==================== 非数据库字段 ====================

    @TableField(exist = false)
    private String username;

    @TableField(exist = false)
    private String statusDesc;

    @TableField(exist = false)
    private java.util.List<OrderItem> items;
}