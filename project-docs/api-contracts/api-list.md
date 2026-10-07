# 教材商城系统 - 接口清单

**版本：** v1.0  
**日期：** 2026-09-25  
**项目：** 教材商城系统（book-mall）  
**架构：** Spring Cloud 微服务 + Vue 3 前端

---

## 一、接口总览

| 模块 | 服务名 | 端口 | 接口数 | 说明 |
|------|--------|------|--------|------|
| 用户 | user-service | 8081 | 12 | 用户注册、登录、信息管理 |
| 分类 | category-service | 8082 | 8 | 教材分类管理 |
| 教材 | book-service | 8083 | 19 | 教材CRUD、上下架 |
| 购物车 | cart-service | 8084 | 8 | 购物车管理 |
| 订单 | order-service | 8085 | 12 | 订单创建、支付、发货 |
| 支付 | payment-service | 8086 | 6 | 支付记录管理 |
| 管理员 | admin-service | 8087 | 16 | 后台管理接口 |
| 地址 | address-service | 8088 | 8 | 收货地址管理 |
| 评价 | review-service | 8089 | 7 | 教材评价 |
| 网关 | gateway-service | 8090 | - | 统一入口 + 认证 |

**接口总计：** 96 个

---

## 二、用户模块（user-service）

**服务端口：** 8081  
**网关前缀：** /api/user

| 编号 | 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|------|
| U-01 | POST | /api/user/login | 用户登录 | 公开 |
| U-02 | POST | /api/user/register | 用户注册 | 公开 |
| U-03 | POST | /api/user/logout | 用户退出 | 登录 |
| U-04 | GET | /api/user/info | 获取当前用户信息 | 登录 |
| U-05 | GET | /api/user/info/{userId} | 获取指定用户信息 | 内部调用 |
| U-06 | PUT | /api/user/password | 修改密码 | 登录 |
| U-07 | PUT | /api/user/info | 修改用户信息 | 登录 |
| U-08 | GET | /api/user/check-username | 检查用户名可用 | 公开 |
| U-09 | GET | /api/user/exists/{userId} | 检查用户是否存在 | 内部调用 |
| U-10 | GET | /api/user/balance/{userId} | 获取用户余额 | 内部调用 |
| U-11 | POST | /api/user/balance/deduct | 扣减余额 | 内部调用 |
| U-12 | POST | /api/user/balance/add | 增加余额（退款） | 内部调用 |

---

## 三、分类模块（category-service）

**服务端口：** 8082  
**网关前缀：** /api/categories

| 编号 | 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|------|
| C-01 | GET | /api/categories | 获取所有分类 | 公开 |
| C-02 | GET | /api/categories/{categoryId} | 获取分类详情 | 公开 |
| C-03 | GET | /api/categories/{categoryId}/book-count | 获取分类下教材数量 | 公开 |
| C-04 | GET | /api/categories/exists/{categoryId} | 检查分类是否存在 | 内部调用 |
| C-05 | POST | /api/categories | 创建分类 | 管理员 |
| C-06 | PUT | /api/categories/{categoryId} | 更新分类 | 管理员 |
| C-07 | DELETE | /api/categories/{categoryId} | 删除分类 | 管理员 |
| C-08 | PUT | /api/categories/{categoryId}/sort | 更新排序 | 管理员 |

---

## 四、教材模块（book-service）

**服务端口：** 8083  
**网关前缀：** /api/books

| 编号 | 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|------|
| B-01 | GET | /api/books | 分页查询教材列表 | 公开 |
| B-02 | GET | /api/books/{bookId} | 获取教材详情 | 公开 |
| B-03 | GET | /api/books/hot | 获取热门教材 | 公开 |
| B-04 | GET | /api/books/new | 获取新品教材 | 公开 |
| B-05 | GET | /api/books/search | 搜索教材 | 公开 |
| B-06 | GET | /api/books/category/{categoryId} | 按分类获取教材 | 公开 |
| B-07 | POST | /api/books | 创建教材 | 管理员 |
| B-08 | PUT | /api/books/{bookId} | 更新教材 | 管理员 |
| B-09 | DELETE | /api/books/{bookId} | 删除教材 | 管理员 |
| B-10 | DELETE | /api/books/batch | 批量删除教材 | 管理员 |
| B-11 | PUT | /api/books/{bookId}/on-sale | 上架教材 | 管理员 |
| B-12 | PUT | /api/books/{bookId}/off-sale | 下架教材 | 管理员 |
| B-13 | PUT | /api/books/batch/on-sale | 批量上架 | 管理员 |
| B-14 | PUT | /api/books/batch/off-sale | 批量下架 | 管理员 |
| B-15 | GET | /api/books/statistics | 教材统计信息 | 管理员 |
| B-16 | GET | /api/books/exists/{bookId} | 检查教材是否存在 | 内部调用 |
| B-17 | GET | /api/books/{bookId}/stock | 获取库存 | 内部调用 |
| B-18 | POST | /api/books/{bookId}/deduct-stock | 扣减库存 | 内部调用 |
| B-19 | POST | /api/books/{bookId}/add-sales | 增加销量 | 内部调用 |

---

## 五、购物车模块（cart-service）

**服务端口：** 8084  
**网关前缀：** /api/cart

| 编号 | 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|------|
| CT-01 | GET | /api/cart | 获取购物车列表 | 登录 |
| CT-02 | POST | /api/cart | 添加到购物车 | 登录 |
| CT-03 | PUT | /api/cart/{cartId} | 更新购物车数量 | 登录 |
| CT-04 | DELETE | /api/cart/{cartId} | 删除购物车项 | 登录 |
| CT-05 | DELETE | /api/cart/batch | 批量删除 | 登录 |
| CT-06 | DELETE | /api/cart/clear | 清空购物车 | 登录 |
| CT-07 | GET | /api/cart/count | 获取商品总数 | 登录 |
| CT-08 | GET | /api/cart/total | 获取总金额 | 登录 |

---

## 六、订单模块（order-service）

**服务端口：** 8085  
**网关前缀：** /api/order

| 编号 | 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|------|
| O-01 | POST | /api/order | 创建订单（购物车） | 登录 |
| O-02 | POST | /api/order/direct | 立即购买 | 登录 |
| O-03 | GET | /api/order | 获取订单列表 | 登录 |
| O-04 | GET | /api/order/{orderId} | 获取订单详情 | 登录 |
| O-05 | POST | /api/order/{orderId}/pay | 支付订单 | 登录 |
| O-06 | PUT | /api/order/{orderId}/cancel | 取消订单 | 登录 |
| O-07 | PUT | /api/order/{orderId}/receive | 确认收货 | 登录 |
| O-08 | GET | /api/order/count/status | 统计各状态订单数 | 登录 |
| O-09 | GET | /api/order/admin/orders | 获取所有订单 | 管理员 |
| O-10 | PUT | /api/order/admin/orders/{orderId}/deliver | 发货 | 管理员 |
| O-11 | PUT | /api/order/admin/orders/{orderId}/cancel | 取消订单 | 管理员 |
| O-12 | GET | /api/order/admin/orders/statistics | 订单统计 | 管理员 |

---

## 七、支付模块（payment-service）

**服务端口：** 8086  
**网关前缀：** /api/payment

| 编号 | 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|------|
| P-01 | POST | /api/payment | 创建支付 | 登录 |
| P-02 | GET | /api/payment/{paymentNo} | 获取支付详情 | 登录 |
| P-03 | GET | /api/payment | 获取支付记录列表 | 登录 |
| P-04 | PUT | /api/payment/{paymentNo}/cancel | 取消支付 | 登录 |
| P-05 | POST | /api/payment/callback | 支付回调 | 外部 |
| P-06 | GET | /api/payment/admin/statistics | 支付统计 | 管理员 |

---

## 八、管理员模块（admin-service）

**服务端口：** 8087  
**网关前缀：** /api/admin

| 编号 | 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|------|
| A-01 | POST | /api/admin/login | 管理员登录 | 公开 |
| A-02 | POST | /api/admin/logout | 管理员退出 | 管理员 |
| A-03 | GET | /api/admin/profile | 获取管理员信息 | 管理员 |
| A-04 | PUT | /api/admin/profile | 修改管理员信息 | 管理员 |
| A-05 | PUT | /api/admin/profile/password | 修改密码 | 管理员 |
| A-06 | GET | /api/admin/users | 获取管理员列表 | 管理员 |
| A-07 | POST | /api/admin/users | 创建管理员 | 管理员 |
| A-08 | PUT | /api/admin/users/{adminId} | 更新管理员 | 管理员 |
| A-09 | DELETE | /api/admin/users/{adminId} | 删除管理员 | 管理员 |
| A-10 | GET | /api/admin/users/members | 获取普通用户列表 | 管理员 |
| A-11 | PUT | /api/admin/users/members/{userId}/status | 启用/禁用用户 | 管理员 |
| A-12 | GET | /api/admin/dashboard/statistics | 仪表盘统计 | 管理员 |
| A-13 | GET | /api/admin/dashboard/trend | 趋势数据 | 管理员 |
| A-14 | GET | /api/admin/books | 教材管理列表 | 管理员 |
| A-15 | GET | /api/admin/categories | 分类管理列表 | 管理员 |
| A-16 | GET | /api/admin/orders | 订单管理列表 | 管理员 |

---

## 九、地址模块（address-service）

**服务端口：** 8088  
**网关前缀：** /api/address

| 编号 | 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|------|
| AD-01 | GET | /api/address | 获取地址列表 | 登录 |
| AD-02 | GET | /api/address/{addressId} | 获取地址详情 | 登录 |
| AD-03 | POST | /api/address | 新增地址 | 登录 |
| AD-04 | PUT | /api/address/{addressId} | 修改地址 | 登录 |
| AD-05 | DELETE | /api/address/{addressId} | 删除地址 | 登录 |
| AD-06 | PUT | /api/address/{addressId}/default | 设置默认地址 | 登录 |
| AD-07 | GET | /api/address/default | 获取默认地址 | 登录 |
| AD-08 | GET | /api/address/check | 检查用户是否有地址 | 内部调用 |

---

## 十、评价模块（review-service）

**服务端口：** 8089  
**网关前缀：** /api/review

| 编号 | 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|------|
| R-01 | POST | /api/review | 创建评价 | 登录 |
| R-02 | GET | /api/review/book/{bookId} | 获取教材评价列表 | 公开 |
| R-03 | GET | /api/review/user | 获取用户评价列表 | 登录 |
| R-04 | GET | /api/review/{reviewId} | 获取评价详情 | 登录 |
| R-05 | PUT | /api/review/{reviewId} | 更新评价 | 登录 |
| R-06 | DELETE | /api/review/{reviewId} | 删除评价 | 登录 |
| R-07 | GET | /api/review/statistics/book/{bookId} | 评价统计 | 公开 |

---

**文档版本：** v1.0  
**最后更新：** 2026-09-25  
**维护人：** 吴欢欢