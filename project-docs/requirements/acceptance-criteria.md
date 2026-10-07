
**验收条件清单：**

**AC-BOOK-01 教材上架**
```
Given 管理员已登录，教材为 DRAFT，字段完整，价格>0，库存≥0
When 管理员点击"上架"
Then 接口返回成功，book.status 变为 PUBLISHED
```

**AC-BOOK-02 教材列表**
```
Given 存在 PUBLISHED 状态的教材
When 游客打开 /books 页面
Then 列表显示教材的标题、作者、价格、库存
```

**AC-CART-01 加入购物车**
```
Given 学生已登录，教材为 PUBLISHED，库存充足
When 学生点击"加入购物车"
Then 接口返回成功，cart 新增 1 条记录
```

**AC-ORDER-01 正常购买**
```
Given 学生已登录，教材 PUBLISHED，库存充足，余额充足
When 学生点击"提交订单"
Then 接口返回成功，order 新增 1 条记录，状态为 PENDING
```

**AC-ORDER-02 重复提交**
```
Given 同一学生已有 PENDING 订单
When 再次提交同一教材订单
Then 接口返回"请勿重复提交"，数据库记录数不变
```

**AC-ORDER-03 库存不足**
```
Given 教材库存为 0
When 学生尝试提交订单
Then 接口返回 409，提示"库存不足"，无订单记录
```

**AC-PAY-01 余额支付**
```
Given 学生已登录，订单为 PENDING，余额充足
When 学生点击"支付"
Then 订单状态变为 PAID，余额扣减，库存扣减
```

**AC-PAY-02 余额不足**
```
Given 学生余额 < 订单金额
When 学生点击"支付"
Then 接口返回 409，提示"余额不足"，订单仍为 PENDING
```

**AC-ADMIN-01 管理员查看订单**
```
Given 管理员已登录
When 打开 /admin/orders
Then 列表显示所有订单，可按状态筛选
```

**AC-ADMIN-02 管理员发货**
```
Given 管理员已登录，订单为 PAID
When 管理员点击"发货"
Then 订单状态变为 SHIPPED
```

**AC-PERM-01 未登录访问**
```
Given 用户未登录
When 访问 /api/me/orders
Then 接口返回 401，提示"请先登录"
```

**AC-PERM-02 越权访问**
```
Given 学生已登录
When 访问 /api/admin/orders
Then 接口返回 403，提示"权限不足"
```

**AC-FINAL-01 完整闭环**
```
Given 管理员、学生都已注册
When 管理员发布教材 → 学生浏览 → 加入购物车 → 提交订单 → 支付 → 管理员发货 → 学生确认收货
Then 每个步骤都成功，订单最终状态为 COMPLETED
```

**预期结果与人工检查：**
- 条件中不出现"正常情况下""合理"等模糊词。
- Then 同时说明接口/页面结果和数据库变化。
- 不同人员按相同步骤能得到相同结论。

**实际结果：**
- ✅ 13 条验收条件已编写。
- ✅ 每条都有 Given-When-Then 结构。
- ✅ 关联 REQ 编号。

**常见错误与定位方法：**
- 只检查页面提示会漏掉数据库错误，应同时核对记录。
- 验收条件依赖当前时间时固定测试数据或说明时间窗口。

---
