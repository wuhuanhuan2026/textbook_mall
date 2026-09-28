package com.bookmall.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 购物车响应DTO
 *
 * @author BookMall Team
 */
@Data
@Builder
@Schema(description = "购物车信息")
public class CartDTO {

    @Schema(description = "购物车ID", example = "1")
    private Long cartId;

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "教材ID", example = "1")
    private Long bookId;

    @Schema(description = "教材名称", example = "Java核心技术 卷I")
    private String bookName;

    @Schema(description = "教材封面", example = "/images/java-core.jpg")
    private String bookCover;

    @Schema(description = "作者", example = "Cay S. Horstmann")
    private String author;

    @Schema(description = "单价", example = "79.00")
    private BigDecimal price;

    @Schema(description = "数量", example = "2")
    private Integer quantity;

    @Schema(description = "小计", example = "158.00")
    private BigDecimal subtotal;

    @Schema(description = "库存", example = "100")
    private Integer stock;

    @Schema(description = "是否上架", example = "true")
    private Boolean onSale;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}