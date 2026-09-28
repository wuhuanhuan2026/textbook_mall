package com.bookmall.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 创建订单请求DTO
 *
 * @author BookMall Team
 */
@Data
@Schema(description = "创建订单请求")
public class OrderCreateDTO {

    @NotNull(message = "收货地址ID不能为空")
    @Schema(description = "收货地址ID", example = "1")
    private Long addressId;

    @Schema(description = "订单备注", example = "请尽快发货")
    private String remark;

    @Schema(description = "选中的购物车ID列表", example = "[1, 2, 3]")
    private List<Long> cartIds;

    @Schema(description = "是否从购物车创建", example = "true")
    private Boolean fromCart = true;
}