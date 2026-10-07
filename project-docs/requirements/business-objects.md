
业务对象表：

对象	标识字段	业务字段	状态	关联关系
User	userId	username, password, phone, email, balance, status	NORMAL/DISABLED	1:n Order
Book	bookId	bookName, author, publisher, price, stock, coverImage, status	DRAFT/PUBLISHED/OFF_SALE	n:1 Category
Category	categoryId	categoryName, sortOrder	-	1:n Book
Cart	cartId	userId, bookId, quantity	-	n:1 User, n:1 Book
Order	orderId	userId, totalAmount, orderStatus, receiverName, receiverPhone, receiverAddress	PENDING/PAID/SHIPPED/COMPLETED/CANCELLED	n:1 User, 1:n OrderItem


Book 状态机（Mermaid 草稿）：

stateDiagram-v2
  [*] --> DRAFT
  DRAFT --> PUBLISHED: 上架
  PUBLISHED --> OFF_SALE: 下架
  OFF_SALE --> PUBLISHED: 重新上架





状态允许/禁止操作：

状态	允许操作	禁止操作
DRAFT	编辑、上架、删除	被学生浏览、被购买
PUBLISHED	编辑、下架、被浏览、被购买	删除
OFF_SALE	编辑、重新上架	被浏览、被购买

Order 状态机：

stateDiagram-v2
  [*] --> PENDING
  PENDING --> PAID: 支付
  PENDING --> CANCELLED: 取消
  PAID --> SHIPPED: 发货
  PAID --> CANCELLED: 退款取消
  SHIPPED --> COMPLETED: 确认收货
  COMPLETED --> [*]
  CANCELLED --> [*]





预期结果与人工检查：

订单记录关联一个用户和一个教材。

Book 状态值在需求、原型和后续代码中保持一致。

不存在从 OFF_SALE 直接恢复到 DRAFT 等未定义变化。


实际结果：

✅ 5 个核心业务对象已识别。

✅ 2 个状态机（Book 和 Order）已描述。

✅ 状态允许/禁止操作已明确。


常见错误与定位方法：

把页面临时显示字段误当数据库字段时，应标记为派生字段。

状态名称中英文混用时建立枚举映射并统一保存值。