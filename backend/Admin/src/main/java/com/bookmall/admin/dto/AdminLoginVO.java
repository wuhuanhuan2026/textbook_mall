package com.bookmall.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 管理员登录响应VO
 *
 * @author BookMall Team
 */
@Data
@Builder
@Schema(description = "管理员登录响应")
public class AdminLoginVO {

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

    @Schema(description = "头像", example = "/images/admin.jpg")
    private String avatar;

    @Schema(description = "Token")
    private String token;

    @Schema(description = "Token过期时间（毫秒）")
    private Long expiresIn;
}