package com.bookmall.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理员信息响应VO
 *
 * @author BookMall Team
 */
@Data
@Builder
@Schema(description = "管理员信息")
public class AdminInfoVO {

    @Schema(description = "管理员ID", example = "1")
    private Long adminId;

    @Schema(description = "用户名", example = "admin")
    private String username;

    @Schema(description = "角色", example = "admin")
    private String role;

    @Schema(description = "角色描述", example = "管理员")
    private String roleDesc;

    @Schema(description = "真实姓名", example = "张三")
    private String realName;

    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    @Schema(description = "邮箱", example = "admin@bookmall.com")
    private String email;

    @Schema(description = "头像", example = "/images/admin.jpg")
    private String avatar;

    @Schema(description = "状态：0-禁用 1-正常", example = "1")
    private Integer status;

    @Schema(description = "状态描述", example = "正常")
    private String statusDesc;

    @Schema(description = "最后登录IP", example = "192.168.1.1")
    private String lastLoginIp;

    @Schema(description = "最后登录时间")
    private LocalDateTime lastLoginTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}