-- ============================================================
-- 教材商城系统 - 完整建表SQL + 完整测试数据
-- 数据库: book_mall
-- ============================================================

CREATE DATABASE IF NOT EXISTS book_mall DEFAULT CHARACTER SET utf8mb4;

USE book_mall;

-- ============================================================
-- 1. 用户表
-- ============================================================
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
INSERT INTO tb_admin (username, password) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi');

-- ============================================================
-- 10.2 分类 (15个分类)
-- ============================================================
INSERT INTO tb_category (category_name, sort_order) VALUES
('计算机', 1),
('数学', 2),
('英语', 3),
('物理', 4),
('化学', 5),
('经济学', 6),
('管理学', 7),
('法学', 8),
('文学', 9),
('历史学', 10),
('哲学', 11),
('医学', 12),
('艺术', 13),
('教育', 14),
('心理学', 15);

-- ============================================================
-- 10.3 测试用户 (密码统一: 123456)
-- ============================================================
INSERT INTO tb_user (username, password, phone, email, balance, status) VALUES
('testuser',  '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '13800138000', 'testuser@qq.com', 100.00, 1),
('zhangsan',  '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '13800138001', 'zhangsan@qq.com', 200.00, 1),
('lisi',      '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '13800138002', 'lisi@qq.com', 150.00, 1),
('wangwu',    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '13800138003', 'wangwu@qq.com', 80.00, 1),
('zhaoliu',   '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '13800138004', 'zhaoliu@qq.com', 300.00, 1),
('sunqi',     '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '13800138005', 'sunqi@qq.com', 50.00, 0),   -- 已禁用
('testuser2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '13800138006', 'test2@qq.com', 500.00, 1);

-- ============================================================
-- 10.4 教材数据 (30本)
-- ============================================================

-- 计算机类 (category_id=1)
INSERT INTO tb_book (book_name, isbn, author, publisher, category_id, price, original_price, stock, cover_image, description, status, sales) VALUES
('Java核心技术 卷I', '978-7-111-12345-6', 'Cay S. Horstmann', '机械工业出版社', 1, 79.00, 99.00, 100, '/images/java-core.jpg', 'Java经典教材，深入浅出', 1, 50),
('深入理解Java虚拟机', '978-7-111-23456-7', '周志明', '机械工业出版社', 1, 89.00, 109.00, 50, '/images/jvm.jpg', 'Java开发者必读，深入JVM底层原理', 1, 45),
('Spring Boot实战', '978-7-121-34567-8', 'Craig Walls', '电子工业出版社', 1, 69.00, 89.00, 80, '/images/springboot.jpg', 'Spring Boot开发实战指南', 1, 38),
('数据结构与算法分析', '978-7-111-45678-9', 'Mark Allen Weiss', '机械工业出版社', 1, 79.00, 99.00, 60, '/images/ds.jpg', '数据结构经典教材，Java语言描述', 1, 25),
('计算机网络：自顶向下', '978-7-111-56789-0', 'James Kurose', '机械工业出版社', 1, 85.00, 99.00, 40, '/images/network.jpg', '计算机网络经典教材', 1, 32),
('操作系统导论', '978-7-111-67890-1', 'Remzi Arpaci-Dusseau', '机械工业出版社', 1, 99.00, 129.00, 30, '/images/os.jpg', '操作系统经典教材，深入浅出', 1, 18);

-- 数学类 (category_id=2)
INSERT INTO tb_book (book_name, isbn, author, publisher, category_id, price, original_price, stock, cover_image, description, status, sales) VALUES
('高等数学 上册', '978-7-04-12345-7', '同济大学数学系', '高等教育出版社', 2, 49.00, 58.00, 80, '/images/gaoshang.jpg', '同济大学经典教材上册', 1, 30),
('高等数学 下册', '978-7-04-23456-8', '同济大学数学系', '高等教育出版社', 2, 49.00, 58.00, 70, '/images/gaoxia.jpg', '同济大学经典教材下册', 1, 25),
('线性代数', '978-7-04-34567-9', '同济大学数学系', '高等教育出版社', 2, 39.00, 46.00, 90, '/images/linear.jpg', '线性代数经典教材', 1, 40),
('概率论与数理统计', '978-7-04-45678-0', '浙江大学数学系', '高等教育出版社', 2, 42.00, 50.00, 60, '/images/probability.jpg', '概率统计经典教材', 1, 22),
('数学分析 上册', '978-7-04-56789-1', '华东师范大学数学系', '高等教育出版社', 2, 45.00, 55.00, 40, '/images/mathanalysis.jpg', '数学分析经典教材', 1, 15),
('数学分析 下册', '978-7-04-67890-2', '华东师范大学数学系', '高等教育出版社', 2, 45.00, 55.00, 35, '/images/mathanalysis2.jpg', '数学分析经典教材下册', 1, 12);

-- 英语类 (category_id=3)
INSERT INTO tb_book (book_name, isbn, author, publisher, category_id, price, original_price, stock, cover_image, description, status, sales) VALUES
('大学英语 精读', '978-7-56-12345-8', '董亚芬', '上海外语教育出版社', 3, 45.00, 52.00, 60, '/images/english.jpg', '大学英语精读经典教材', 1, 20),
('新概念英语 第三册', '978-7-56-23456-9', 'L.G. Alexander', '上海外语教育出版社', 3, 38.00, 45.00, 100, '/images/newconcept3.jpg', '英语学习经典教材', 1, 35),
('新概念英语 第四册', '978-7-56-34567-0', 'L.G. Alexander', '上海外语教育出版社', 3, 42.00, 50.00, 80, '/images/newconcept4.jpg', '英语学习经典教材进阶', 1, 28),
('商务英语口语', '978-7-56-45678-1', '张红', '上海外语教育出版社', 3, 45.00, 55.00, 50, '/images/business.jpg', '商务英语口语实战训练', 1, 20);

-- 物理类 (category_id=4)
INSERT INTO tb_book (book_name, isbn, author, publisher, category_id, price, original_price, stock, cover_image, description, status, sales) VALUES
('大学物理 上册', '978-7-04-78901-3', '张三慧', '高等教育出版社', 4, 45.00, 55.00, 60, '/images/physics1.jpg', '大学物理经典教材上册', 1, 18),
('大学物理 下册', '978-7-04-89012-4', '张三慧', '高等教育出版社', 4, 45.00, 55.00, 50, '/images/physics2.jpg', '大学物理经典教材下册', 1, 15),
('量子力学', '978-7-04-90123-5', '曾谨言', '高等教育出版社', 4, 55.00, 68.00, 25, '/images/quantum.jpg', '量子力学经典教材', 1, 8);

-- 化学类 (category_id=5)
INSERT INTO tb_book (book_name, isbn, author, publisher, category_id, price, original_price, stock, cover_image, description, status, sales) VALUES
('有机化学', '978-7-04-01234-6', '邢其毅', '高等教育出版社', 5, 65.00, 78.00, 40, '/images/organic.jpg', '有机化学经典教材', 1, 12),
('无机化学', '978-7-04-12345-7', '张祖德', '高等教育出版社', 5, 55.00, 68.00, 45, '/images/inorganic.jpg', '无机化学经典教材', 1, 10),
('物理化学', '978-7-04-23456-8', '傅献彩', '高等教育出版社', 5, 58.00, 70.00, 30, '/images/chem.jpg', '物理化学经典教材', 1, 8);

-- 经济学类 (category_id=6)
INSERT INTO tb_book (book_name, isbn, author, publisher, category_id, price, original_price, stock, cover_image, description, status, sales) VALUES
('经济学原理 微观分册', '978-7-30-34567-8', '曼昆', '北京大学出版社', 6, 68.00, 79.00, 55, '/images/micro.jpg', '经济学经典入门教材', 1, 30),
('经济学原理 宏观分册', '978-7-30-45678-9', '曼昆', '北京大学出版社', 6, 68.00, 79.00, 50, '/images/macro.jpg', '经济学经典入门教材', 1, 28),
('计量经济学', '978-7-30-56789-0', '古扎拉蒂', '北京大学出版社', 6, 72.00, 85.00, 25, '/images/econometrics.jpg', '计量经济学经典教材', 1, 10);

-- 法学类 (category_id=8)
INSERT INTO tb_book (book_name, isbn, author, publisher, category_id, price, original_price, stock, cover_image, description, status, sales) VALUES
('民法总论', '978-7-30-67890-1', '梁慧星', '法律出版社', 8, 48.00, 58.00, 40, '/images/civil.jpg', '民法经典教材', 1, 12),
('刑法学', '978-7-30-78901-2', '张明楷', '法律出版社', 8, 52.00, 62.00, 35, '/images/criminal.jpg', '刑法学经典教材', 1, 10),
('法理学', '978-7-30-89012-3', '张文显', '法律出版社', 8, 42.00, 50.00, 30, '/images/jurisprudence.jpg', '法理学经典教材', 1, 8);

-- 文学类 (category_id=9)
INSERT INTO tb_book (book_name, isbn, author, publisher, category_id, price, original_price, stock, cover_image, description, status, sales) VALUES
('中国文学史', '978-7-02-90123-4', '袁行霈', '人民文学出版社', 9, 88.00, 108.00, 30, '/images/literature.jpg', '中国文学史经典教材', 1, 15),
('外国文学史', '978-7-02-01234-5', '郑克鲁', '人民文学出版社', 9, 75.00, 90.00, 25, '/images/foreignlit.jpg', '外国文学史经典教材', 1, 10);

-- ============================================================
-- 10.5 收货地址
-- ============================================================
INSERT INTO tb_address (user_id, receiver_name, receiver_phone, province, city, district, detail_address, is_default) VALUES

-- testuser (user_id=1)
(1, '张三', '13800138000', '江西省', '南昌市', '青山湖区', '江西财经大学南区12栋301', 1),
(1, '张三', '13800138001', '江西省', '南昌市', '红谷滩区', '万达广场A座1203', 0),

-- zhangsan (user_id=2)
(2, '李四', '13800138002', '上海市', '上海市', '浦东新区', '张江高科技园区博霞路50号', 1),
(2, '李四', '13800138003', '浙江省', '杭州市', '西湖区', '文三路100号A座801', 0),

-- lisi (user_id=3)
(3, '王五', '13800138004', '广东省', '广州市', '天河区', '天河路200号财富广场1506', 1),
(3, '王五', '13800138005', '广东省', '深圳市', '南山区', '科技园南区科苑路15号', 0),

-- wangwu (user_id=4)
(4, '赵六', '13800138006', '四川省', '成都市', '武侯区', '天府大道北段1700号环球中心', 1),

-- zhaoliu (user_id=5)
(5, '孙七', '13800138007', '湖北省', '武汉市', '洪山区', '珞喻路1037号华中科技大学', 1),

-- testuser2 (user_id=7)
(7, '周八', '13800138008', '北京市', '北京市', '海淀区', '中关村大街18号B座502', 1);

-- ============================================================
-- 10.6 购物车数据
-- ============================================================
INSERT INTO tb_cart (user_id, book_id, quantity) VALUES
(1, 1, 2),
(1, 3, 1),
(1, 5, 1),
(2, 2, 1),
(2, 4, 3),
(3, 6, 1),
(3, 8, 2),
(4, 10, 1),
(4, 12, 1),
(5, 14, 2),
(7, 1, 1),
(7, 7, 1);

-- ============================================================
-- 10.7 订单数据 (使用存储过程批量生成)
-- ============================================================

-- 删除已存在的存储过程
DROP PROCEDURE IF EXISTS generate_test_orders;

DELIMITER //

CREATE PROCEDURE generate_test_orders()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE v_order_id VARCHAR(64);
    DECLARE v_user_id BIGINT;
    DECLARE v_total DECIMAL(10,2);
    DECLARE v_status TINYINT;
    DECLARE v_days_ago INT;
    DECLARE v_book_count INT;
    DECLARE v_j INT;
    DECLARE v_book_id BIGINT;
    DECLARE v_quantity INT;
    DECLARE v_price DECIMAL(10,2);
    DECLARE v_item_total DECIMAL(10,2);
    DECLARE v_order_total DECIMAL(10,2);

    WHILE i <= 30 DO
        -- 随机用户 (1-7)
        SET v_user_id = FLOOR(1 + RAND() * 7);
        -- 随机状态 (0-3)
        SET v_status = FLOOR(RAND() * 4);
        -- 随机天数 (1-90天前)
        SET v_days_ago = FLOOR(1 + RAND() * 90);

        -- 生成订单号
        SET v_order_id = CONCAT(
            DATE_FORMAT(DATE_SUB(NOW(), INTERVAL v_days_ago DAY), '%Y%m%d'),
            LPAD(FLOOR(1 + RAND() * 9999), 4, '0'),
            SUBSTRING(MD5(RAND()), 1, 4)
        );

        -- 随机收货地址信息
        SET @addr_name = CONCAT('收货人', v_user_id);
        SET @addr_phone = CONCAT('138', LPAD(FLOOR(100000000 + RAND() * 900000000), 9, '0'));
        SET @addr_detail = CONCAT('测试地址', FLOOR(1 + RAND() * 999));

        -- 初始化订单总金额
        SET v_order_total = 0;

        -- 插入订单
        INSERT INTO tb_order (
            order_id, user_id, total_amount, shipping_fee, order_status, pay_type,
            receiver_name, receiver_phone, receiver_address, remark,
            pay_time, deliver_time, receive_time, create_time
        ) VALUES (
            v_order_id,
            v_user_id,
            0,  -- 先插入0，后面更新
            0.00,
            v_status,
            0,
            @addr_name,
            @addr_phone,
            @addr_detail,
            CONCAT('订单备注', i),
            CASE WHEN v_status > 0 THEN DATE_SUB(NOW(), INTERVAL v_days_ago DAY) ELSE NULL END,
            CASE WHEN v_status > 1 THEN DATE_SUB(NOW(), INTERVAL (v_days_ago - 1) DAY) ELSE NULL END,
            CASE WHEN v_status > 2 THEN DATE_SUB(NOW(), INTERVAL (v_days_ago - 2) DAY) ELSE NULL END,
            DATE_SUB(NOW(), INTERVAL v_days_ago DAY)
        );

        -- 随机订单详情 (1-3本书)
        SET v_book_count = FLOOR(1 + RAND() * 3);
        SET v_j = 1;

        WHILE v_j <= v_book_count DO
            SET v_book_id = FLOOR(1 + RAND() * 30);
            SET v_quantity = FLOOR(1 + RAND() * 3);
            SET v_price = ROUND(30 + RAND() * 80, 2);
            SET v_item_total = v_price * v_quantity;
            SET v_order_total = v_order_total + v_item_total;

            INSERT INTO tb_order_item (
                order_id, book_id, book_name, book_cover, price, quantity, total_price
            )
            SELECT
                v_order_id,
                book_id,
                book_name,
                cover_image,
                v_price,
                v_quantity,
                v_item_total
            FROM tb_book
            WHERE book_id = v_book_id
            LIMIT 1;

            SET v_j = v_j + 1;
        END WHILE;

        -- 更新订单总金额（修复：使用 v_order_id 变量）
        UPDATE tb_order SET total_amount = v_order_total WHERE order_id = v_order_id;

        SET i = i + 1;
    END WHILE;
END //

DELIMITER ;

-- 执行存储过程生成订单
CALL generate_test_orders();

-- 删除存储过程（执行完后清理）
DROP PROCEDURE IF EXISTS generate_test_orders;

-- ============================================================
-- 10.8 评价数据
-- ============================================================
INSERT INTO tb_review (user_id, book_id, order_id, rating, content) VALUES
(1, 1, (SELECT order_id FROM tb_order WHERE user_id = 1 LIMIT 1), 5, '这本书太好了，讲得非常详细，强烈推荐！'),
(1, 2, (SELECT order_id FROM tb_order WHERE user_id = 1 LIMIT 1), 4, '数学经典教材，内容全面，但有点难'),
(2, 3, (SELECT order_id FROM tb_order WHERE user_id = 2 LIMIT 1), 5, '英语学习必备，很好用'),
(3, 1, (SELECT order_id FROM tb_order WHERE user_id = 3 LIMIT 1), 4, 'Java入门经典，值得一看'),
(4, 4, (SELECT order_id FROM tb_order WHERE user_id = 4 LIMIT 1), 3, '网络知识讲得不错，但有些章节太旧了'),
(1, 5, (SELECT order_id FROM tb_order WHERE user_id = 1 LIMIT 1 OFFSET 1), 5, '操作系统必读，强烈推荐'),
(2, 6, (SELECT order_id FROM tb_order WHERE user_id = 2 LIMIT 1 OFFSET 1), 4, '线性代数讲得很清晰'),
(3, 7, (SELECT order_id FROM tb_order WHERE user_id = 3 LIMIT 1 OFFSET 1), 5, '概率统计经典，考研必备');

-- ============================================================
-- ============================================================
-- 11. 数据验证查询
-- ============================================================
-- ============================================================

-- 查看所有用户
SELECT user_id, username, phone, email, balance,
       CASE status WHEN 1 THEN '正常' ELSE '禁用' END AS status_name
FROM tb_user;

-- 查看各分类教材数量
SELECT c.category_name, COUNT(b.book_id) AS book_count
FROM tb_category c
LEFT JOIN tb_book b ON c.category_id = b.category_id AND b.status = 1
GROUP BY c.category_id
ORDER BY book_count DESC;

-- 查看销量排行前10
SELECT book_id, book_name, author, price, sales, stock
FROM tb_book
WHERE status = 1
ORDER BY sales DESC
LIMIT 10;

-- 查看订单统计
SELECT
    COUNT(*) AS total_orders,
    ROUND(SUM(total_amount), 2) AS total_amount,
    ROUND(AVG(total_amount), 2) AS avg_amount,
    ROUND(MAX(total_amount), 2) AS max_amount,
    ROUND(MIN(total_amount), 2) AS min_amount
FROM tb_order;

-- 查看各状态订单数量
SELECT
    order_status,
    CASE order_status
        WHEN 0 THEN '待付款'
        WHEN 1 THEN '待发货'
        WHEN 2 THEN '待收货'
        WHEN 3 THEN '已完成'
        WHEN 4 THEN '已取消'
    END AS status_name,
    COUNT(*) AS count
FROM tb_order
GROUP BY order_status
ORDER BY order_status;

-- 查看购物车
SELECT u.username, b.book_name, c.quantity
FROM tb_cart c
JOIN tb_user u ON c.user_id = u.user_id
JOIN tb_book b ON c.book_id = b.book_id
ORDER BY u.user_id;

-- 查看评价
SELECT u.username, b.book_name, r.rating, r.content
FROM tb_review r
JOIN tb_user u ON r.user_id = u.user_id
JOIN tb_book b ON r.book_id = b.book_id
LIMIT 10;

-- ============================================================
-- ============================================================
-- 12. 清理数据（如需重置，取消注释执行）
-- ============================================================
-- ============================================================

/*
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE tb_order_item;
TRUNCATE tb_order;
TRUNCATE tb_review;
TRUNCATE tb_cart;
TRUNCATE tb_address;
TRUNCATE tb_book;
TRUNCATE tb_category;
TRUNCATE tb_user;
TRUNCATE tb_admin;
SET FOREIGN_KEY_CHECKS = 1;
*/

-- ============================================================
-- 13. 测试账号汇总
-- ============================================================
--
-- | 角色     | 用户名    | 密码     | 余额   | 状态 |
-- |----------|-----------|----------|--------|------|
-- | 管理员   | admin     | admin123 | -      | 正常 |
-- | 用户     | testuser  | 123456   | 100.00 | 正常 |
-- | 用户     | zhangsan  | 123456   | 200.00 | 正常 |
-- | 用户     | lisi      | 123456   | 150.00 | 正常 |
-- | 用户     | wangwu    | 123456   | 80.00  | 正常 |
-- | 用户     | zhaoliu   | 123456   | 300.00 | 正常 |
-- | 用户     | sunqi     | 123456   | 50.00  | 禁用 |
-- | 用户     | testuser2 | 123456   | 500.00 | 正常 |
--
-- ============================================================