package com.bookmall.order.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 订单状态枚举
 *
 * @author BookMall Team
 */
@Getter
@AllArgsConstructor
public enum OrderStatus {

    PENDING(0, "待付款"),
    PAID(1, "待发货"),
    SHIPPED(2, "待收货"),
    COMPLETED(3, "已完成"),
    CANCELLED(4, "已取消");

    private final int code;
    private final String desc;

    public static OrderStatus fromCode(int code) {
        for (OrderStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }

    public static String getDesc(int code) {
        OrderStatus status = fromCode(code);
        return status != null ? status.desc : "未知";
    }
}