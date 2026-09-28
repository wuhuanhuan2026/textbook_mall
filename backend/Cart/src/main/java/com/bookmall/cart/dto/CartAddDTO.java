package com.bookmall.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 添加购物车请求DTO
 *
 * @author BookMall Team
 */
@Data
@Schema(description = "添加购物车请求")
public class CartAddDTO {

    @NotNull(message = "教材ID不能为空")
    @Schema(description = "教材ID", example = "1")
    private Long bookId;

    @Min(value = 1, message = "数量至少为1")
    @Schema(description = "数量", example = "2")
    private Integer quantity = 1;
}