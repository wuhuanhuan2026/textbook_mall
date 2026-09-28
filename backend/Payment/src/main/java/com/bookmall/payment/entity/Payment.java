package com.bookmall.payment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付实体类 - 对应 tb_payment 表
 * 注意：需要先在数据库中创建此表
 *
 * @author BookMall Team
 */
@Data
@TableName("tb_payment")
public class Payment {

    @TableId(value = "payment_id", type = IdType.AUTO)
    private Long paymentId;

    @TableField("payment_no")
    private String paymentNo;

    @TableField("order_id")
    private String orderId;

    @TableField("user_id")
    private Long userId;

    @TableField("amount")
    private BigDecimal amount;

    @TableField("pay_type")
    private Integer payType;

    @TableField("status")
    private Integer status;

    @TableField("transaction_id")
    private String transactionId;

    @TableField("callback_data")
    private String callbackData;

    @TableField("pay_time")
    private LocalDateTime payTime;

    @TableField("expire_time")
    private LocalDateTime expireTime;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // ==================== 非数据库字段 ====================

    @TableField(exist = false)
    private String statusDesc;

    @TableField(exist = false)
    private String payTypeDesc;

    @TableField(exist = false)
    private String username;
}