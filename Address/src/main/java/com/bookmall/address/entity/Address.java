package com.bookmall.address.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 收货地址实体类 - 对应 tb_address 表
 *
 * @author BookMall Team
 */
@Data
@TableName("tb_address")
public class Address {

    @TableId(value = "address_id", type = IdType.AUTO)
    private Long addressId;

    @TableField("user_id")
    private Long userId;

    @TableField("receiver_name")
    private String receiverName;

    @TableField("receiver_phone")
    private String receiverPhone;

    @TableField("province")
    private String province;

    @TableField("city")
    private String city;

    @TableField("district")
    private String district;

    @TableField("detail_address")
    private String detailAddress;

    @TableField("is_default")
    private Integer isDefault;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 完整地址（非数据库字段）
     */
    @TableField(exist = false)
    private String fullAddress;
}