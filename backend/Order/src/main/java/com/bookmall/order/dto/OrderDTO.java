package com.bookmall.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单响应DTO
 *
 * @author BookMall Team
 */
@Data
@Builder
@Schema(description = "订单信息")
public class OrderDTO {

    @Schema(description = "订单号", example = "202609040001")
    private String orderId;

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "用户名", example = "testuser")
    private String username;

    @Schema(description = "订单总金额", example = "158.00")
    private BigDecimal totalAmount;

    @Schema(description = "运费", example = "0.00")
    private BigDecimal shippingFee;

    @Schema(description = "订单状态：0-待付款 1-待发货 2-待收货 3-已完成 4-已取消", example = "0")
    private Integer orderStatus;

    @Schema(description = "订单状态描述", example = "待付款")
    private String statusDesc;

    @Schema(description = "支付方式：0-余额", example = "0")
    private Integer payType;

    @Schema(description = "收货人姓名", example = "张三")
    private String receiverName;

    @Schema(description = "收货人电话", example = "13800138000")
    private String receiverPhone;

    @Schema(description = "收货地址", example = "江西省南昌市青山湖区江西财经大学南区12栋301")
    private String receiverAddress;

    @Schema(description = "订单备注", example = "请尽快发货")
    private String remark;

    @Schema(description = "支付时间")
    private LocalDateTime payTime;

    @Schema(description = "发货时间")
    private LocalDateTime deliverTime;

    @Schema(description = "收货时间")
    private LocalDateTime receiveTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "订单商品列表")
    private List<OrderItemDTO> items;
}