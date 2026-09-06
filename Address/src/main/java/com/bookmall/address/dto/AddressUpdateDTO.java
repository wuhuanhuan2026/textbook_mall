package com.bookmall.address.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 更新收货地址请求DTO
 *
 * @author BookMall Team
 */
@Data
@Schema(description = "更新收货地址请求")
public class AddressUpdateDTO {

    @NotBlank(message = "收货人姓名不能为空")
    @Size(max = 50, message = "收货人姓名不能超过50个字符")
    @Schema(description = "收货人姓名", example = "张三")
    private String receiverName;

    @NotBlank(message = "收货人电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "收货人电话", example = "13800138000")
    private String receiverPhone;

    @Schema(description = "省份", example = "江西省")
    private String province;

    @Schema(description = "城市", example = "南昌市")
    private String city;

    @Schema(description = "区/县", example = "青山湖区")
    private String district;

    @NotBlank(message = "详细地址不能为空")
    @Size(max = 200, message = "详细地址不能超过200个字符")
    @Schema(description = "详细地址", example = "江西财经大学南区12栋301")
    private String detailAddress;

    @Schema(description = "是否默认：0-否 1-是", example = "1")
    private Integer isDefault = 0;
}