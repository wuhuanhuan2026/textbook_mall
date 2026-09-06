package com.bookmall.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理员实体类 - 对应 tb_admin 表
 *
 * @author BookMall Team
 */
@Data
@TableName("tb_admin")
public class Admin {

    @TableId(value = "admin_id", type = IdType.AUTO)
    private Long adminId;

    @TableField("username")
    private String username;

    @TableField("password")
    private String password;

    @TableField("role")
    private String role;

    @TableField("status")
    private Integer status;

    @TableField("real_name")
    private String realName;

    @TableField("phone")
    private String phone;

    @TableField("email")
    private String email;

    @TableField("avatar")
    private String avatar;

    @TableField("last_login_ip")
    private String lastLoginIp;

    @TableField("last_login_time")
    private LocalDateTime lastLoginTime;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    // ==================== 非数据库字段 ====================

    @TableField(exist = false)
    private String roleDesc;

    @TableField(exist = false)
    private String statusDesc;
}