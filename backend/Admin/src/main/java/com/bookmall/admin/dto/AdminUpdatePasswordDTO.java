package com.bookmall.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 管理员修改密码请求DTO
 *
 * @author BookMall Team
 */
@Data
@Schema(description = "管理员修改密码请求")
public class AdminUpdatePasswordDTO {

    @NotBlank(message = "旧密码不能为空")
    @Schema(description = "旧密码", example = "admin123")
    private String oldPassword;

    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度为6-20位")
    @Schema(description = "新密码", example = "admin456")
    private String newPassword;
}