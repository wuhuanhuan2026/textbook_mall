package com.bookmall.address.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 收货地址响应DTO
 *
 * @author BookMall Team
 */
@Data
@Builder
@Schema(description = "收货地址信息")
public class AddressDTO {

    @Schema(description = "地址ID", example = "1")
    private Long addressId;

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "收货人姓名", example = "张三")
    private String receiverName;

    @Schema(description = "收货人电话", example = "13800138000")
    private String receiverPhone;

    @Schema(description = "省份", example = "江西省")
    private String province;

    @Schema(description = "城市", example = "南昌市")
    private String city;

    @Schema(description = "区/县", example = "青山湖区")
    private String district;

    @Schema(description = "详细地址", example = "江西财经大学南区12栋301")
    private String detailAddress;

    @Schema(description = "是否默认：0-否 1-是", example = "1")
    private Integer isDefault;

    @Schema(description = "完整地址", example = "江西省南昌市青山湖区江西财经大学南区12栋301")
    private String fullAddress;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}