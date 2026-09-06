package com.bookmall.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 更新购物车请求DTO
 *
 * @author BookMall Team
 */
@Data
@Schema(description = "更新购物车请求")
public class CartUpdateDTO {

    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量至少为1")
    @Schema(description = "数量", example = "3")
    private Integer quantity;
}