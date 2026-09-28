package com.bookmall.common.constant;

/**
 * 系统常量
 */
public class Constants {

    // ========== JWT ==========
    public static final String JWT_SECRET = "book_mall_secret_key_2024_secure";
    public static final long JWT_EXPIRATION = 7 * 24 * 60 * 60 * 1000L; // 7天
    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    // ========== Redis Key前缀 ==========
    public static final String REDIS_USER_TOKEN = "user:token:";
    public static final String REDIS_USER_INFO = "user:info:";
    public static final String REDIS_CAPTCHA = "captcha:";
    public static final String REDIS_BOOK_CACHE = "book:cache:";
    public static final String REDIS_CATEGORY_CACHE = "category:cache:";
    public static final String REDIS_ORDER_LOCK = "order:lock:";

    // ========== 订单状态 ==========
    public static final int ORDER_STATUS_PENDING = 0;        // 待付款
    public static final int ORDER_STATUS_PAID = 1;           // 已支付/待发货
    public static final int ORDER_STATUS_SHIPPED = 2;        // 已发货/待收货
    public static final int ORDER_STATUS_COMPLETED = 3;      // 已完成
    public static final int ORDER_STATUS_CANCELLED = 4;      // 已取消

    // ========== 用户状态 ==========
    public static final int USER_STATUS_NORMAL = 1;
    public static final int USER_STATUS_DISABLED = 0;

    // ========== 商品状态 ==========
    public static final int BOOK_STATUS_ON = 1;
    public static final int BOOK_STATUS_OFF = 0;

    // ========== 支付方式 ==========
    public static final int PAY_TYPE_BALANCE = 0;            // 余额支付
    public static final int PAY_TYPE_ALIPAY = 1;             // 支付宝
    public static final int PAY_TYPE_WECHAT = 2;             // 微信

    // ========== 默认分页 ==========
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int DEFAULT_PAGE_NUM = 1;
    public static final int MAX_PAGE_SIZE = 100;

    // ========== 角色 ==========
    public static final String ROLE_USER = "user";
    public static final String ROLE_ADMIN = "admin";

    // ========== 验证码 ==========
    public static final int CAPTCHA_EXPIRE_TIME = 5;         // 5分钟
    public static final int CAPTCHA_LENGTH = 4;              // 4位数字

    // ========== 请求属性 ==========
    public static final String REQ_ATTR_USER_ID = "userId";
    public static final String REQ_ATTR_USERNAME = "username";
    public static final String REQ_ATTR_ROLE = "role";
}