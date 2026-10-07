# 教材商城系统 - 数据字典

**版本：** v1.0  
**日期：** 2026-09-25  
**数据库：** book_mall  
**字符集：** utf8mb4  
**引擎：** InnoDB

---

## 一、表清单

| 序号 | 表名 | 中文名 | 说明 |
|------|------|--------|------|
| 1 | tb_user | 用户表 | 存储普通用户信息 |
| 2 | tb_admin | 管理员表 | 存储后台管理员信息 |
| 3 | tb_category | 分类表 | 教材分类 |
| 4 | tb_book | 教材表 | 教材主数据 |
| 5 | tb_cart | 购物车表 | 用户购物车 |
| 6 | tb_address | 收货地址表 | 用户收货地址 |
| 7 | tb_order | 订单表 | 订单主表 |
| 8 | tb_order_item | 订单详情表 | 订单商品明细 |
| 9 | tb_review | 评价表 | 教材评价 |
| 10 | tb_payment | 支付表 | 支付记录 |

---

## 二、tb_user（用户表）

| 字段名 | 类型 | 空 | 默认值 | 说明 |
|--------|------|-----|--------|------|
| user_id | BIGINT | 否 | AUTO_INCREMENT | 主键，用户ID |
| username | VARCHAR(50) | 否 | - | 用户名（唯一） |
| password | VARCHAR(100) | 否 | - | 密码（BCrypt） |
| phone | VARCHAR(20) | 是 | NULL | 手机号 |
| email | VARCHAR(100) | 是 | NULL | 邮箱 |
| avatar | VARCHAR(200) | 是 | NULL | 头像URL |
| balance | DECIMAL(10,2) | 是 | 0.00 | 余额 |
| status | TINYINT | 是 | 1 | 0-禁用 1-正常 |
| create_time | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 是 | CURRENT_TIMESTAMP | 更新时间 |

**索引：** PRIMARY KEY (user_id), UNIQUE KEY (username)

---

## 三、tb_admin（管理员表）

| 字段名 | 类型 | 空 | 默认值 | 说明 |
|--------|------|-----|--------|------|
| admin_id | BIGINT | 否 | AUTO_INCREMENT | 主键 |
| username | VARCHAR(50) | 否 | - | 管理员名（唯一） |
| password | VARCHAR(100) | 否 | - | 密码（BCrypt） |
| role | VARCHAR(20) | 是 | 'admin' | 角色 |
| status | TINYINT | 是 | 1 | 0-禁用 1-正常 |
| real_name | VARCHAR(50) | 是 | NULL | 真实姓名 |
| phone | VARCHAR(20) | 是 | NULL | 手机号 |
| email | VARCHAR(100) | 是 | NULL | 邮箱 |
| avatar | VARCHAR(255) | 是 | NULL | 头像 |
| last_login_ip | VARCHAR(50) | 是 | NULL | 最后登录IP |
| last_login_time | DATETIME | 是 | NULL | 最后登录时间 |
| create_time | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 是 | CURRENT_TIMESTAMP | 更新时间 |

**索引：** PRIMARY KEY (admin_id), UNIQUE KEY (username)

---

## 四、tb_category（分类表）

| 字段名 | 类型 | 空 | 默认值 | 说明 |
|--------|------|-----|--------|------|
| category_id | BIGINT | 否 | AUTO_INCREMENT | 主键 |
| category_name | VARCHAR(50) | 否 | - | 分类名称 |
| sort_order | INT | 是 | 0 | 排序 |
| create_time | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |

**索引：** PRIMARY KEY (category_id)

---

## 五、tb_book（教材表）

| 字段名 | 类型 | 空 | 默认值 | 说明 |
|--------|------|-----|--------|------|
| book_id | BIGINT | 否 | AUTO_INCREMENT | 主键 |
| book_name | VARCHAR(200) | 否 | - | 教材名称 |
| isbn | VARCHAR(20) | 是 | NULL | ISBN编号 |
| author | VARCHAR(100) | 是 | NULL | 作者 |
| publisher | VARCHAR(100) | 是 | NULL | 出版社 |
| category_id | BIGINT | 是 | NULL | 分类ID（外键） |
| price | DECIMAL(10,2) | 否 | - | 售价 |
| original_price | DECIMAL(10,2) | 是 | NULL | 原价 |
| stock | INT | 是 | 0 | 库存 |
| cover_image | VARCHAR(200) | 是 | NULL | 封面URL |
| description | TEXT | 是 | NULL | 教材简介 |
| status | TINYINT | 是 | 1 | 0-下架 1-上架 |
| sales | INT | 是 | 0 | 销量 |
| create_time | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 是 | CURRENT_TIMESTAMP | 更新时间 |

**索引：** PRIMARY KEY (book_id), FOREIGN KEY (category_id) REFERENCES tb_category

---

## 六、tb_cart（购物车表）

| 字段名 | 类型 | 空 | 默认值 | 说明 |
|--------|------|-----|--------|------|
| cart_id | BIGINT | 否 | AUTO_INCREMENT | 主键 |
| user_id | BIGINT | 否 | - | 用户ID（外键） |
| book_id | BIGINT | 否 | - | 教材ID（外键） |
| quantity | INT | 是 | 1 | 数量 |
| create_time | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 是 | CURRENT_TIMESTAMP | 更新时间 |

**索引：**
- PRIMARY KEY (cart_id)
- UNIQUE KEY uk_user_book (user_id, book_id)
- FOREIGN KEY (user_id) REFERENCES tb_user
- FOREIGN KEY (book_id) REFERENCES tb_book

---

## 七、tb_address（收货地址表）

| 字段名 | 类型 | 空 | 默认值 | 说明 |
|--------|------|-----|--------|------|
| address_id | BIGINT | 否 | AUTO_INCREMENT | 主键 |
| user_id | BIGINT | 否 | - | 用户ID（外键） |
| receiver_name | VARCHAR(50) | 否 | - | 收货人姓名 |
| receiver_phone | VARCHAR(20) | 否 | - | 收货人电话 |
| province | VARCHAR(50) | 是 | NULL | 省份 |
| city | VARCHAR(50) | 是 | NULL | 城市 |
| district | VARCHAR(50) | 是 | NULL | 区/县 |
| detail_address | VARCHAR(200) | 否 | - | 详细地址 |
| is_default | TINYINT | 是 | 0 | 0-否 1-是 |
| create_time | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |

**索引：** PRIMARY KEY (address_id), FOREIGN KEY (user_id) REFERENCES tb_user

---

## 八、tb_order（订单表）

| 字段名 | 类型 | 空 | 默认值 | 说明 |
|--------|------|-----|--------|------|
| order_id | VARCHAR(64) | 否 | - | 主键，订单号 |
| user_id | BIGINT | 否 | - | 用户ID（外键） |
| total_amount | DECIMAL(10,2) | 否 | - | 订单总金额 |
| shipping_fee | DECIMAL(10,2) | 是 | 0.00 | 运费 |
| order_status | TINYINT | 是 | 0 | 0-待付款 1-待发货 2-待收货 3-已完成 4-已取消 |
| pay_type | TINYINT | 是 | 0 | 0-余额 |
| receiver_name | VARCHAR(50) | 否 | - | 收货人姓名 |
| receiver_phone | VARCHAR(20) | 否 | - | 收货人电话 |
| receiver_address | VARCHAR(500) | 否 | - | 收货地址 |
| remark | VARCHAR(500) | 是 | NULL | 订单备注 |
| pay_time | DATETIME | 是 | NULL | 支付时间 |
| deliver_time | DATETIME | 是 | NULL | 发货时间 |
| receive_time | DATETIME | 是 | NULL | 收货时间 |
| create_time | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |

**索引：** PRIMARY KEY (order_id), FOREIGN KEY (user_id) REFERENCES tb_user

---

## 九、tb_order_item（订单详情表）

| 字段名 | 类型 | 空 | 默认值 | 说明 |
|--------|------|-----|--------|------|
| item_id | BIGINT | 否 | AUTO_INCREMENT | 主键 |
| order_id | VARCHAR(64) | 否 | - | 订单号（外键） |
| book_id | BIGINT | 否 | - | 教材ID |
| book_name | VARCHAR(200) | 否 | - | 教材名称（快照） |
| book_cover | VARCHAR(200) | 是 | NULL | 教材封面（快照） |
| price | DECIMAL(10,2) | 否 | - | 下单时单价 |
| quantity | INT | 否 | - | 购买数量 |
| total_price | DECIMAL(10,2) | 否 | - | 小计金额 |

**索引：** PRIMARY KEY (item_id), FOREIGN KEY (order_id) REFERENCES tb_order

---

## 十、tb_review（评价表）

| 字段名 | 类型 | 空 | 默认值 | 说明 |
|--------|------|-----|--------|------|
| review_id | BIGINT | 否 | AUTO_INCREMENT | 主键 |
| user_id | BIGINT | 否 | - | 用户ID（外键） |
| book_id | BIGINT | 否 | - | 教材ID（外键） |
| order_id | VARCHAR(64) | 否 | - | 订单号 |
| rating | TINYINT | 否 | - | 评分 1-5星 |
| content | VARCHAR(500) | 是 | NULL | 评价内容 |
| create_time | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |

**索引：**
- PRIMARY KEY (review_id)
- FOREIGN KEY (user_id) REFERENCES tb_user
- FOREIGN KEY (book_id) REFERENCES tb_book

---

## 十一、tb_payment（支付表）

| 字段名 | 类型 | 空 | 默认值 | 说明 |
|--------|------|-----|--------|------|
| payment_id | BIGINT | 否 | AUTO_INCREMENT | 主键 |
| payment_no | VARCHAR(32) | 否 | - | 支付单号（唯一） |
| order_id | VARCHAR(64) | 否 | - | 订单号 |
| user_id | BIGINT | 否 | - | 用户ID |
| amount | DECIMAL(10,2) | 否 | - | 支付金额 |
| pay_type | TINYINT | 是 | 0 | 0-余额 1-支付宝 2-微信 |
| status | TINYINT | 是 | 0 | 0-待支付 1-处理中 2-支付成功 3-支付失败 4-已取消 5-退款中 6-已退款 |
| transaction_id | VARCHAR(64) | 是 | NULL | 交易流水号 |
| callback_data | TEXT | 是 | NULL | 回调数据 |
| pay_time | DATETIME | 是 | NULL | 支付时间 |
| expire_time | DATETIME | 是 | NULL | 过期时间 |
| create_time | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 是 | CURRENT_TIMESTAMP | 更新时间 |

**索引：**
- PRIMARY KEY (payment_id)
- UNIQUE KEY (payment_no)
- INDEX idx_order_id (order_id)
- INDEX idx_user_id (user_id)
- INDEX idx_status (status)

---

## 十二、状态枚举汇总

### 用户状态
| 值 | 含义 |
|----|------|
| 0 | 禁用 |
| 1 | 正常 |

### 教材状态
| 值 | 含义 |
|----|------|
| 0 | 下架 |
| 1 | 上架 |

### 订单状态
| 值 | 含义 |
|----|------|
| 0 | 待付款 |
| 1 | 待发货 |
| 2 | 待收货 |
| 3 | 已完成 |
| 4 | 已取消 |

### 支付状态
| 值 | 含义 |
|----|------|
| 0 | 待支付 |
| 1 | 处理中 |
| 2 | 支付成功 |
| 3 | 支付失败 |
| 4 | 已取消 |
| 5 | 退款中 |
| 6 | 已退款 |

---

**文档版本：** v1.0  
**最后更新：** 2026-09-25  
**维护人：** 吴欢欢