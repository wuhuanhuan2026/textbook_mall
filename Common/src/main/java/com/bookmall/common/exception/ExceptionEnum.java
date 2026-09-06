package com.bookmall.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 异常枚举
 */
@Getter
@AllArgsConstructor
public enum ExceptionEnum {

    // ========== 系统异常 ==========
    SYSTEM_ERROR(500, "系统繁忙，请稍后再试"),
    PARAM_ERROR(400, "参数错误"),
    REQUEST_METHOD_ERROR(405, "请求方法不支持"),
    MEDIA_TYPE_ERROR(415, "不支持的媒体类型"),

    // ========== 用户异常 (1000-1999) ==========
    USER_NOT_EXIST(1001, "用户不存在"),
    USER_DISABLED(1002, "用户已被禁用"),
    PASSWORD_ERROR(1003, "密码错误"),
    TOKEN_INVALID(1004, "Token无效"),
    TOKEN_EXPIRED(1005, "Token已过期"),
    USERNAME_EXIST(1006, "用户名已存在"),
    PHONE_EXIST(1007, "手机号已存在"),
    EMAIL_EXIST(1008, "邮箱已存在"),
    PERMISSION_DENIED(1009, "权限不足"),
    LOGIN_REQUIRED(1010, "请先登录"),
    USER_LOCKED(1011, "用户已被锁定"),
    PASSWORD_RETRY_EXCEED(1012, "密码错误次数过多，请稍后再试"),
    OLD_PASSWORD_ERROR(1013, "原密码错误"),
    USER_NOT_LOGGED_IN(1014, "用户未登录"),

    // ========== 商品异常 (2000-2999) ==========
    BOOK_NOT_EXIST(2001, "商品不存在"),
    BOOK_OFF_SALE(2002, "商品已下架"),
    STOCK_INSUFFICIENT(2003, "库存不足"),
    BOOK_ALREADY_EXIST(2004, "商品已存在"),
    BOOK_CATEGORY_ERROR(2005, "商品分类错误"),
    BOOK_PRICE_ERROR(2006, "商品价格错误"),

    // ========== 订单异常 (3000-3999) ==========
    ORDER_NOT_EXIST(3001, "订单不存在"),
    ORDER_STATUS_ERROR(3002, "订单状态错误"),
    ORDER_CANNOT_CANCEL(3003, "订单无法取消"),
    ORDER_CANNOT_PAY(3004, "订单无法支付"),
    ORDER_NOT_BELONG_TO_USER(3005, "订单不属于该用户"),
    ORDER_CANNOT_REFUND(3006, "订单无法退款"),
    ORDER_ALREADY_PAID(3007, "订单已支付"),
    ORDER_PAYMENT_TIMEOUT(3008, "订单支付超时"),

    // ========== 支付异常 (4000-4999) ==========
    BALANCE_INSUFFICIENT(4001, "余额不足"),
    PAY_ERROR(4002, "支付失败"),
    PAYMENT_NOT_EXIST(4003, "支付记录不存在"),
    PAYMENT_EXPIRED(4004, "支付已过期"),
    PAYMENT_ALREADY_SUCCESS(4005, "支付已成功"),
    PAYMENT_REFUND_ERROR(4006, "退款失败"),
    PAYMENT_REFUND_ALREADY(4007, "已退款"),
    PAYMENT_CALLBACK_ERROR(4008, "支付回调处理失败"),
    PAYMENT_PROCESSING(4009, "支付处理中"),

    // ========== 购物车异常 (5000-5999) ==========
    CART_EMPTY(5001, "购物车为空"),
    CART_ITEM_NOT_EXIST(5002, "购物车项不存在"),
    CART_ITEM_ALREADY_EXIST(5003, "购物车项已存在"),
    CART_QUANTITY_ERROR(5004, "购物车数量错误"),
    CART_FULL(5005, "购物车已满"),

    // ========== 分类异常 (6000-6999) ==========
    CATEGORY_NOT_EXIST(6001, "分类不存在"),
    CATEGORY_HAS_BOOKS(6002, "分类下存在商品，无法删除"),
    CATEGORY_NAME_EXIST(6003, "分类名称已存在"),
    CATEGORY_HAS_CHILDREN(6004, "分类下存在子分类，无法删除"),
    CATEGORY_PARENT_ERROR(6005, "父分类错误"),

    // ========== 验证码异常 (7000-7999) ==========
    CAPTCHA_ERROR(7001, "验证码错误"),
    CAPTCHA_EXPIRED(7002, "验证码已过期"),
    CAPTCHA_SEND_ERROR(7003, "验证码发送失败"),
    CAPTCHA_FREQUENCY_LIMIT(7004, "验证码发送过于频繁"),

    // ========== 地址异常 (8000-8999) ==========
    ADDRESS_NOT_EXIST(8001, "地址不存在"),
    ADDRESS_LIMIT_EXCEEDED(8002, "地址数量已达上限"),
    ADDRESS_NOT_BELONG_TO_USER(8003, "地址不属于该用户"),
    ADDRESS_DEFAULT_CANNOT_DELETE(8004, "默认地址无法删除"),
    ADDRESS_EMPTY(8005, "地址信息不能为空"),

    // ========== 评价异常 (9000-9999) ==========
    REVIEW_NOT_EXIST(9001, "评价不存在"),
    REVIEW_ALREADY_EXISTS(9002, "您已评价过该商品"),
    REVIEW_CANNOT_DELETE(9003, "评价无法删除"),
    REVIEW_NOT_BELONG_TO_USER(9004, "评价不属于该用户"),
    REVIEW_STATUS_ERROR(9005, "评价状态错误"),
    REVIEW_IMAGE_ERROR(9006, "评价图片上传失败"),

    // ========== Feign调用异常 (10000-10999) ==========
    FEIGN_CALL_ERROR(10001, "远程服务调用失败"),
    FEIGN_TIMEOUT(10002, "远程服务调用超时"),
    FEIGN_SERVICE_UNAVAILABLE(10003, "远程服务不可用"),

    // ========== 文件异常 (11000-11999) ==========
    FILE_UPLOAD_ERROR(11001, "文件上传失败"),
    FILE_DOWNLOAD_ERROR(11002, "文件下载失败"),
    FILE_NOT_EXIST(11003, "文件不存在"),
    FILE_FORMAT_ERROR(11004, "文件格式错误"),
    FILE_SIZE_EXCEED(11005, "文件大小超出限制"),

    // ========== 库存异常 (12000-12999) ==========
    STOCK_DEDUCT_ERROR(12001, "库存扣减失败"),
    STOCK_RESTORE_ERROR(12002, "库存恢复失败"),
    STOCK_LOCK_ERROR(12003, "库存锁定失败"),
    STOCK_RELEASE_ERROR(12004, "库存释放失败");

    private final int code;
    private final String message;
}