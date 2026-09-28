package com.bookmall.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@Schema(description = "登录响应")
public class LoginResponseVO {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "余额")
    private BigDecimal balance;

    @Schema(description = "Token")
    private String token;

    @Schema(description = "Token过期时间（毫秒）")
    private Long expiresIn;
}