package com.bookmall.common.constant;

/**
 * 状态码常量
 *
 * 规范：
 * 200: 成功
 * 400-499: 客户端错误
 * 500-599: 服务端错误
 * 1000-1999: 用户相关
 * 2000-2999: 商品相关
 * 3000-3999: 订单相关
 * 4000-4999: 支付相关
 * 5000-5999: 购物车相关
 * 6000-6999: 分类相关
 * 7000-7999: 验证码相关
 * 8000-8999: 地址相关
 * 9000-9999: 评价相关
 */
public class StatusCode {

    // ========== 成功 ==========
    public static final int SUCCESS = 200;

    // ========== 客户端错误 ==========
    public static final int BAD_REQUEST = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int FORBIDDEN = 403;
    public static final int NOT_FOUND = 404;
    public static final int METHOD_NOT_ALLOWED = 405;
    public static final int CONFLICT = 409;
    public static final int UNPROCESSABLE_ENTITY = 422;

    // ========== 服务端错误 ==========
    public static final int INTERNAL_SERVER_ERROR = 500;
    public static final int SERVICE_UNAVAILABLE = 503;

    // ========== 用户相关 (1000-1999) ==========
    public static final int USER_NOT_EXIST = 1001;
    public static final int USER_DISABLED = 1002;
    public static final int PASSWORD_ERROR = 1003;
    public static final int TOKEN_INVALID = 1004;
    public static final int TOKEN_EXPIRED = 1005;
    public static final int USERNAME_EXIST = 1006;
    public static final int PHONE_EXIST = 1007;
    public static final int EMAIL_EXIST = 1008;
    public static final int PERMISSION_DENIED = 1009;
    public static final int LOGIN_REQUIRED = 1010;

    // ========== 商品相关 (2000-2999) ==========
    public static final int BOOK_NOT_EXIST = 2001;
    public static final int BOOK_OFF_SALE = 2002;
    public static final int STOCK_INSUFFICIENT = 2003;

    // ========== 订单相关 (3000-3999) ==========
    public static final int ORDER_NOT_EXIST = 3001;
    public static final int ORDER_STATUS_ERROR = 3002;
    public static final int ORDER_CANNOT_CANCEL = 3003;
    public static final int ORDER_CANNOT_PAY = 3004;

    // ========== 支付相关 (10000-10999) ==========
    public static final int PAYMENT_NOT_EXIST = 10001;
    public static final int PAYMENT_STATUS_ERROR = 10002;
    public static final int PAYMENT_ALREADY_PAID = 10003;
    public static final int PAYMENT_REFUND_FAILED = 10004;

    // ========== 购物车相关 (5000-5999) ==========
    public static final int CART_EMPTY = 5001;
    public static final int CART_ITEM_NOT_EXIST = 5002;

    // ========== 分类相关 (6000-6999) ==========
    public static final int CATEGORY_NOT_EXIST = 6001;
    public static final int CATEGORY_HAS_BOOKS = 6002;

    // ========== 验证码相关 (7000-7999) ==========
    public static final int CAPTCHA_ERROR = 7001;
    public static final int CAPTCHA_EXPIRED = 7002;

    // ========== 地址相关 (8000-8999) ==========
    public static final int ADDRESS_NOT_EXIST = 8001;
    public static final int ADDRESS_LIMIT_EXCEEDED = 8002;
    public static final int ADDRESS_NOT_BELONG_TO_USER = 8003;

    // ========== 评价相关 (9000-9999) ==========
    public static final int REVIEW_NOT_EXIST = 9001;
    public static final int REVIEW_ALREADY_EXISTS = 9002;
    public static final int REVIEW_CANNOT_DELETE = 9003;

    // ========== 通用业务错误 ==========
    public static final int BUSINESS_ERROR = 1000;
}