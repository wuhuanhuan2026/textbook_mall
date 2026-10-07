# 教材商城系统 - 表结构脚本
# 依赖：需先执行 01-create-db.sql

USE book_mall;

CREATE TABLE tb_user (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码(BCrypt加密)',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    avatar VARCHAR(200) COMMENT '头像URL',
    balance DECIMAL(10,2) DEFAULT 0.00 COMMENT '余额',
    status TINYINT DEFAULT 1 COMMENT '0-禁用 1-正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Z创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ============================================================
-- 2. 管理员表
-- ============================================================
CREATE TABLE tb_admin (
    admin_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '管理员名',
    password VARCHAR(100) NOT NULL COMMENT '密码(BCrypt加密)',
    role VARCHAR(20) DEFAULT 'admin' COMMENT '角色',
    status TINYINT DEFAULT 1 COMMENT '0-禁用 1-正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

-- ============================================================
-- 3. 分类表
-- ============================================================
CREATE TABLE tb_category (
    category_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    category_name VARCHAR(50) NOT NULL COMMENT '分类名称',
    sort_order INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教材分类表';

-- ============================================================
-- 4. 教材表
-- ============================================================
CREATE TABLE tb_book (
    book_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    book_name VARCHAR(200) NOT NULL COMMENT '教材名称',
    isbn VARCHAR(20) COMMENT 'ISBN编号',
    author VARCHAR(100) COMMENT '作者',
    publisher VARCHAR(100) COMMENT '出版社',
    category_id BIGINT COMMENT '分类ID',
    price DECIMAL(10,2) NOT NULL COMMENT '售价',
    original_price DECIMAL(10,2) COMMENT '原价',
    stock INT DEFAULT 0 COMMENT '库存数量',
    cover_image VARCHAR(200) COMMENT '封面图片URL',
    description TEXT COMMENT '教材简介',
    status TINYINT DEFAULT 1 COMMENT '0-下架 1-上架',
    sales INT DEFAULT 0 COMMENT '销量',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (category_id) REFERENCES tb_category(category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教材表';

-- ============================================================
-- 5. 购物车表
-- ============================================================
CREATE TABLE tb_cart (
    cart_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    book_id BIGINT NOT NULL COMMENT '教材ID',
    quantity INT DEFAULT 1 COMMENT '数量',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES tb_user(user_id),
    FOREIGN KEY (book_id) REFERENCES tb_book(book_id),
    UNIQUE KEY uk_user_book (user_id, book_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- ============================================================
-- 6. 收货地址表
-- ============================================================
CREATE TABLE tb_address (
    address_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    receiver_name VARCHAR(50) NOT NULL COMMENT '收货人姓名',
    receiver_phone VARCHAR(20) NOT NULL COMMENT '收货人电话',
    province VARCHAR(50) COMMENT '省份',
    city VARCHAR(50) COMMENT '城市',
    district VARCHAR(50) COMMENT '区/县',
    detail_address VARCHAR(200) NOT NULL COMMENT '详细地址',
    is_default TINYINT DEFAULT 0 COMMENT '是否默认 0-否 1-是',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES tb_user(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收货地址表';

-- ============================================================
-- 7. 订单表
-- ============================================================
CREATE TABLE tb_order (
    order_id VARCHAR(64) PRIMARY KEY COMMENT '订单号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
    shipping_fee DECIMAL(10,2) DEFAULT 0.00 COMMENT '运费',
    order_status TINYINT DEFAULT 0 COMMENT '0-待付款 1-待发货 2-待收货 3-已完成 4-已取消',
    pay_type TINYINT DEFAULT 0 COMMENT '支付方式 0-余额',
    receiver_name VARCHAR(50) NOT NULL COMMENT '收货人姓名',
    receiver_phone VARCHAR(20) NOT NULL COMMENT '收货人电话',
    receiver_address VARCHAR(500) NOT NULL COMMENT '收货地址',
    remark VARCHAR(500) COMMENT '订单备注',
    pay_time DATETIME COMMENT '支付时间',
    deliver_time DATETIME COMMENT '发货时间',
    receive_time DATETIME COMMENT '收货时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES tb_user(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- ============================================================
-- 8. 订单详情表
-- ============================================================
CREATE TABLE tb_order_item (
    item_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id VARCHAR(64) NOT NULL COMMENT '订单号',
    book_id BIGINT NOT NULL COMMENT '教材ID',
    book_name VARCHAR(200) NOT NULL COMMENT '教材名称',
    book_cover VARCHAR(200) COMMENT '教材封面',
    price DECIMAL(10,2) NOT NULL COMMENT '下单时单价',
    quantity INT NOT NULL COMMENT '购买数量',
    total_price DECIMAL(10,2) NOT NULL COMMENT '小计金额',
    FOREIGN KEY (order_id) REFERENCES tb_order(order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单详情表';

-- ============================================================
-- 9. 评价表（扩展）
-- ============================================================
CREATE TABLE IF NOT EXISTS tb_review (
    review_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    book_id BIGINT NOT NULL COMMENT '教材ID',
    order_id VARCHAR(64) NOT NULL COMMENT '订单号',
    rating TINYINT NOT NULL COMMENT '评分 1-5星',
    content VARCHAR(500) COMMENT '评价内容',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES tb_user(user_id),
    FOREIGN KEY (book_id) REFERENCES tb_book(book_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价表';


-- ============================================================
-- 支付表
-- ============================================================
CREATE TABLE IF NOT EXISTS tb_payment (
    payment_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '支付ID',
    payment_no VARCHAR(32) NOT NULL UNIQUE COMMENT '支付单号',
    order_id VARCHAR(64) NOT NULL COMMENT '订单号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    amount DECIMAL(10,2) NOT NULL COMMENT '支付金额',
    pay_type TINYINT DEFAULT 0 COMMENT '支付方式：0-余额 1-支付宝 2-微信',
    status TINYINT DEFAULT 0 COMMENT '状态：0-待支付 1-处理中 2-支付成功 3-支付失败 4-已取消 5-退款中 6-已退款',
    transaction_id VARCHAR(64) COMMENT '交易流水号',
    callback_data TEXT COMMENT '回调数据',
    pay_time DATETIME COMMENT '支付时间',
    expire_time DATETIME COMMENT '过期时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_order_id (order_id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_payment_no (payment_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付表';



-- ============================================================
-- ============================================================
-- 10. 插入测试数据
-- ============================================================
-- ============================================================

-- ============================================================
-- 10.1 管理员 (密码: admin123)
-- ============================================================
