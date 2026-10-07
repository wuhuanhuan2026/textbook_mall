# 教材商城系统 - 请求响应示例

**版本：** v1.0  
**日期：** 2026-09-25

---

## 一、统一响应格式

### 1.1 成功响应

    {
      "code": 200,
      "message": "操作成功",
      "data": { },
      "timestamp": 1758787200000
    }

### 1.2 分页响应

    {
      "code": 200,
      "message": "操作成功",
      "data": {
        "total": 60,
        "pageNum": 1,
        "pageSize": 10,
        "totalPages": 6,
        "list": []
      },
      "timestamp": 1758787200000
    }

### 1.3 错误响应

    {
      "code": 2001,
      "message": "商品不存在",
      "data": null,
      "timestamp": 1758787200000
    }

---

## 二、用户模块

### U-01 用户登录

请求：

    POST /api/user/login
    Content-Type: application/json

    {
      "username": "testuser",
      "password": "123456"
    }

成功响应：

    {
      "code": 200,
      "message": "登录成功",
      "data": {
        "userId": 1,
        "username": "testuser",
        "avatar": "",
        "balance": 500.00,
        "token": "eyJhbGciOiJIUzI1NiJ9...",
        "expiresIn": 604800000
      },
      "timestamp": 1758787200000
    }

失败响应（密码错误）：

    {
      "code": 1003,
      "message": "密码错误",
      "data": null,
      "timestamp": 1758787200000
    }

---

### U-02 用户注册

请求：

    POST /api/user/register
    Content-Type: application/json

    {
      "username": "newuser",
      "password": "123456",
      "phone": "13800138000",
      "email": "newuser@qq.com",
      "captcha": "1234"
    }

成功响应：

    {
      "code": 200,
      "message": "注册成功",
      "data": null,
      "timestamp": 1758787200000
    }

---

## 三、教材模块

### B-01 分页查询教材列表

请求：

    GET /api/books?pageNum=1&pageSize=8&keyword=Java&categoryId=1&sortBy=price_asc

成功响应：

    {
      "code": 200,
      "message": "操作成功",
      "data": {
        "total": 6,
        "pageNum": 1,
        "pageSize": 8,
        "totalPages": 1,
        "list": [
          {
            "bookId": 1,
            "bookName": "Java核心技术 卷I",
            "isbn": "978-7-111-12345-6",
            "author": "Cay S. Horstmann",
            "publisher": "机械工业出版社",
            "categoryId": 1,
            "categoryName": "计算机",
            "price": 79.00,
            "originalPrice": 99.00,
            "stock": 100,
            "sales": 50,
            "coverImage": "/images/java-core.jpg",
            "description": "Java经典教材，深入浅出",
            "status": 1,
            "avgRating": 4.5,
            "createTime": "2026-09-01T10:00:00"
          }
        ]
      },
      "timestamp": 1758787200000
    }

---

### B-02 获取教材详情

请求：

    GET /api/books/1

成功响应：

    {
      "code": 200,
      "message": "操作成功",
      "data": {
        "bookId": 1,
        "bookName": "Java核心技术 卷I",
        "isbn": "978-7-111-12345-6",
        "author": "Cay S. Horstmann",
        "publisher": "机械工业出版社",
        "categoryId": 1,
        "categoryName": "计算机",
        "price": 79.00,
        "originalPrice": 99.00,
        "stock": 100,
        "sales": 50,
        "coverImage": "/images/java-core.jpg",
        "description": "Java经典教材，深入浅出",
        "status": 1,
        "avgRating": 4.5,
        "createTime": "2026-09-01T10:00:00",
        "updateTime": "2026-09-01T10:00:00"
      },
      "timestamp": 1758787200000
    }

失败响应（教材不存在）：

    {
      "code": 2001,
      "message": "商品不存在",
      "data": null,
      "timestamp": 1758787200000
    }

---

## 四、购物车模块

### CT-01 获取购物车列表

请求：

    GET /api/cart
    Authorization: Bearer <token>

成功响应：

    {
      "code": 200,
      "message": "操作成功",
      "data": [
        {
          "cartId": 1,
          "userId": 1,
          "bookId": 1,
          "bookName": "Java核心技术 卷I",
          "bookCover": "/images/java-core.jpg",
          "author": "Cay S. Horstmann",
          "price": 79.00,
          "quantity": 2,
          "subtotal": 158.00,
          "stock": 100,
          "onSale": true,
          "createTime": "2026-09-01T10:00:00"
        }
      ],
      "timestamp": 1758787200000
    }

---

### CT-02 添加到购物车

请求：

    POST /api/cart
    Authorization: Bearer <token>
    Content-Type: application/json

    {
      "bookId": 1,
      "quantity": 2
    }

成功响应：

    {
      "code": 200,
      "message": "添加成功",
      "data": {
        "cartId": 1,
        "bookId": 1,
        "quantity": 2
      },
      "timestamp": 1758787200000
    }

失败响应（库存不足）：

    {
      "code": 2003,
      "message": "库存不足",
      "data": null,
      "timestamp": 1758787200000
    }

---

## 五、订单模块

### O-01 创建订单

请求：

    POST /api/order
    Authorization: Bearer <token>
    Content-Type: application/json

    {
      "addressId": 1,
      "remark": "请尽快发货",
      "cartIds": [1, 2]
    }

成功响应：

    {
      "code": 200,
      "message": "订单创建成功",
      "data": {
        "orderId": "20260925101500123456",
        "userId": 1,
        "totalAmount": 158.00,
        "shippingFee": 0.00,
        "orderStatus": 0,
        "statusDesc": "待付款",
        "receiverName": "张三",
        "receiverPhone": "13800138000",
        "receiverAddress": "江西省南昌市青山湖区江西财经大学南区12栋301",
        "remark": "请尽快发货",
        "createTime": "2026-09-25T10:15:00",
        "items": [
          {
            "itemId": 1,
            "bookId": 1,
            "bookName": "Java核心技术 卷I",
            "bookCover": "/images/java-core.jpg",
            "price": 79.00,
            "quantity": 2,
            "totalPrice": 158.00
          }
        ]
      },
      "timestamp": 1758787200000
    }

---

### O-05 支付订单

请求：

    POST /api/order/20260925101500123456/pay
    Authorization: Bearer <token>

成功响应：

    {
      "code": 200,
      "message": "支付成功",
      "data": null,
      "timestamp": 1758787200000
    }

失败响应（余额不足）：

    {
      "code": 4001,
      "message": "余额不足",
      "data": null,
      "timestamp": 1758787200000
    }

---

## 六、地址模块

### AD-03 新增地址

请求：

    POST /api/address
    Authorization: Bearer <token>
    Content-Type: application/json

    {
      "receiverName": "张三",
      "receiverPhone": "13800138000",
      "province": "江西省",
      "city": "南昌市",
      "district": "青山湖区",
      "detailAddress": "江西财经大学南区12栋301",
      "isDefault": 1
    }

成功响应：

    {
      "code": 200,
      "message": "添加成功",
      "data": {
        "addressId": 1,
        "receiverName": "张三",
        "receiverPhone": "13800138000",
        "province": "江西省",
        "city": "南昌市",
        "district": "青山湖区",
        "detailAddress": "江西财经大学南区12栋301",
        "isDefault": 1,
        "fullAddress": "江西省南昌市青山湖区江西财经大学南区12栋301",
        "createTime": "2026-09-25T10:00:00"
      },
      "timestamp": 1758787200000
    }

---

## 七、评价模块

### R-01 创建评价

请求：

    POST /api/review
    Authorization: Bearer <token>
    Content-Type: application/json

    {
      "bookId": 1,
      "orderId": "20260925101500123456",
      "rating": 5,
      "content": "这本书太好了，讲得非常详细！"
    }

成功响应：

    {
      "code": 200,
      "message": "评价成功",
      "data": {
        "reviewId": 1,
        "userId": 1,
        "bookId": 1,
        "orderId": "20260925101500123456",
        "rating": 5,
        "ratingStars": "5星",
        "ratingDesc": "非常满意",
        "content": "这本书太好了，讲得非常详细！",
        "createTime": "2026-09-25T10:30:00"
      },
      "timestamp": 1758787200000
    }

失败响应（重复评价）：

    {
      "code": 9002,
      "message": "您已评价过该商品",
      "data": null,
      "timestamp": 1758787200000
    }

---

## 八、管理员模块

### A-01 管理员登录

请求：

    POST /api/admin/login
    Content-Type: application/json

    {
      "username": "admin",
      "password": "admin123"
    }

成功响应：

    {
      "code": 200,
      "message": "登录成功",
      "data": {
        "adminId": 1,
        "username": "admin",
        "role": "admin",
        "roleDesc": "管理员",
        "realName": "张三",
        "avatar": "",
        "token": "eyJhbGciOiJIUzI1NiJ9...",
        "expiresIn": 604800000
      },
      "timestamp": 1758787200000
    }

---

### A-12 仪表盘统计

请求：

    GET /api/admin/dashboard/statistics
    Authorization: Bearer <admin-token>

成功响应：

    {
      "code": 200,
      "message": "操作成功",
      "data": {
        "totalUsers": 7,
        "todayNewUsers": 0,
        "totalBooks": 60,
        "onSaleBooks": 60,
        "totalOrders": 30,
        "todayOrders": 0,
        "pendingOrders": 5,
        "totalSales": 12580.00,
        "todaySales": 0.00,
        "totalReviews": 8,
        "avgRating": 4.4
      },
      "timestamp": 1758787200000
    }

---

**文档版本：** v1.0  
**最后更新：** 2026-09-25  
**维护人：** 吴欢欢