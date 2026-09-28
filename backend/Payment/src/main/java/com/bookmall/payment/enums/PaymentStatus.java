package com.bookmall.payment.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 支付状态枚举
 *
 * @author BookMall Team
 */
@Getter
@AllArgsConstructor
public enum PaymentStatus {

    PENDING(0, "待支付"),
    PROCESSING(1, "处理中"),
    SUCCESS(2, "支付成功"),
    FAILED(3, "支付失败"),
    CANCELLED(4, "已取消"),
    REFUNDING(5, "退款中"),
    REFUNDED(6, "已退款");

    private final int code;
    private final String desc;

    public static PaymentStatus fromCode(int code) {
        for (PaymentStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }

    public static String getDesc(int code) {
        PaymentStatus status = fromCode(code);
        return status != null ? status.desc : "未知";
    }

    /**
     * 判断是否已支付成功
     */
    public static boolean isSuccess(int code) {
        return code == SUCCESS.code;
    }

    /**
     * 判断是否可退款
     */
    public static boolean isRefundable(int code) {
        return code == SUCCESS.code;
    }
}