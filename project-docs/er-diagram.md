# 教材商城系统 - ER 关系图

**版本：** v1.0  
**日期：** 2026-09-25  
**数据库：** book_mall

---

## 一、核心实体关系（文本图）

    tb_user (用户)
      ├── 1:n (user_id) ──> tb_cart (购物车)
      ├── 1:n (user_id) ──> tb_address (收货地址)
      ├── 1:n (user_id) ──> tb_order (订单)
      ├── 1:n (user_id) ──> tb_review (评价)
      └── 1:n (user_id) ──> tb_payment (支付)

    tb_category (分类)
      └── 1:n (category_id) ──> tb_book (教材)

    tb_book (教材)
      ├── 1:n (book_id) ──> tb_cart (购物车)
      ├── 1:n (book_id) ──> tb_order_item (订单详情)
      └── 1:n (book_id) ──> tb_review (评价)

    tb_order (订单)
      ├── 1:n (order_id) ──> tb_order_item (订单详情)
      └── 1:n (order_id) ──> tb_payment (支付)

    tb_admin (管理员)  ← 独立表

---

## 二、Mermaid 格式 ER 图

    erDiagram
        tb_user ||--o{ tb_cart : "拥有"
        tb_user ||--o{ tb_address : "拥有"
        tb_user ||--o{ tb_order : "下单"
        tb_user ||--o{ tb_review : "评价"
        tb_user ||--o{ tb_payment : "支付"
        tb_category ||--o{ tb_book : "包含"
        tb_book ||--o{ tb_cart : "加入"
        tb_book ||--o{ tb_order_item : "被购买"
        tb_book ||--o{ tb_review : "被评价"
        tb_order ||--o{ tb_order_item : "包含"
        tb_order ||--o{ tb_payment : "支付"

        tb_user {
            BIGINT user_id PK
            VARCHAR username UK
            VARCHAR password
            VARCHAR phone
            VARCHAR email
            DECIMAL balance
            TINYINT status
        }

        tb_book {
            BIGINT book_id PK
            VARCHAR book_name
            VARCHAR isbn
            VARCHAR author
            BIGINT category_id FK
            DECIMAL price
            INT stock
            TINYINT status
            INT sales
        }

        tb_cart {
            BIGINT cart_id PK
            BIGINT user_id FK
            BIGINT book_id FK
            INT quantity
        }

        tb_address {
            BIGINT address_id PK
            BIGINT user_id FK
            VARCHAR receiver_name
            VARCHAR receiver_phone
            VARCHAR detail_address
            TINYINT is_default
        }

        tb_order {
            VARCHAR order_id PK
            BIGINT user_id FK
            DECIMAL total_amount
            TINYINT order_status
            TINYINT pay_type
        }

        tb_order_item {
            BIGINT item_id PK
            VARCHAR order_id FK
            BIGINT book_id
            VARCHAR book_name
            DECIMAL price
            INT quantity
            DECIMAL total_price
        }

        tb_review {
            BIGINT review_id PK
            BIGINT user_id FK
            BIGINT book_id FK
            VARCHAR order_id
            TINYINT rating
            VARCHAR content
        }

        tb_payment {
            BIGINT payment_id PK
            VARCHAR payment_no UK
            VARCHAR order_id FK
            BIGINT user_id FK
            DECIMAL amount
            TINYINT status
        }

---

## 三、关系基数说明

| 主表 | 从表 | 关系 | 外键 | 说明 |
|------|------|------|------|------|
| tb_user | tb_cart | 1:n | user_id | 一个用户多个购物车项 |
| tb_user | tb_address | 1:n | user_id | 一个用户多个收货地址 |
| tb_user | tb_order | 1:n | user_id | 一个用户多个订单 |
| tb_user | tb_review | 1:n | user_id | 一个用户多条评价 |
| tb_user | tb_payment | 1:n | user_id | 一个用户多条支付记录 |
| tb_category | tb_book | 1:n | category_id | 一个分类多本教材 |
| tb_book | tb_cart | 1:n | book_id | 一本教材被多个购物车引用 |
| tb_book | tb_order_item | 1:n | book_id | 一本教材被多次购买 |
| tb_book | tb_review | 1:n | book_id | 一本教材多条评价 |
| tb_order | tb_order_item | 1:n | order_id | 一个订单多个商品 |
| tb_order | tb_payment | 1:n | order_id | 一个订单多条支付记录 |

---

## 四、设计说明

### 1. 为什么 tb_order_item 要保存 book_name 和 book_cover？

**原因：** 订单是历史快照。如果教材后来改名或下架，历史订单中仍应显示下单时的教材信息。

### 2. 为什么 tb_payment 与 tb_order 是 1:n？

**原因：** 一个订单可能经历多次支付尝试（比如第一次失败、重试、退款），每次都有独立的支付记录。

### 3. 为什么 tb_cart 要加 (user_id, book_id) 唯一约束？

**原因：** 避免同一用户重复添加同一本教材到购物车。重复添加时应该增加数量而不是新增记录。

### 4. 为什么 tb_order 的 order_id 用 VARCHAR 而不是 BIGINT？

**原因：** 订单号需要可读、可追溯，格式为 yyyyMMddHHmmss + 6位序号，方便运维排查。

### 5. 为什么 tb_review 关联了 order_id？

**原因：** 评价必须基于已完成的订单，防止用户刷评。(user_id, book_id, order_id) 可以唯一确定一条评价。

---

**文档版本：** v1.0  
**最后更新：** 2026-09-25  
**维护人：** 吴欢欢