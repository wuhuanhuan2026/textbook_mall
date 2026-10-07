**命名规则：**

| 类别 | 规则 | 示例 |
|------|------|------|
| 资源名 | 复数、小写 | books, orders, users |
| 动作 | 子资源 | /books/:id/on-sale |
| 分页参数 | page, pageSize | ?page=1&pageSize=10 |
| 关键词 | keyword | ?keyword=Java |
| 状态 | status | ?status=PUBLISHED |
| 排序 | sortBy | ?sortBy=price_asc |

**接口路径基线：**

```
GET    /api/books
GET    /api/books/{id}
POST   /api/books
PUT    /api/books/{id}
DELETE /api/books/{id}
POST   /api/books/{id}/on-sale
POST   /api/books/{id}/off-sale
POST   /api/books/{id}/purchase
GET    /api/cart
POST   /api/cart
PUT    /api/cart/{id}
DELETE /api/cart/{id}
GET    /api/order
POST   /api/order
GET    /api/order/{id}
PUT    /api/order/{id}/cancel
POST   /api/order/{id}/pay
GET    /api/me/orders
GET    /api/admin/orders
PUT    /api/admin/orders/{id}/deliver
```

**预期结果与人工检查：**
- URL使用小写英文和短横线，不含中文。
- 同一资源的列表、详情、新增、修改路径一致。
- HTTP方法与操作语义合理。

**实际结果：**
- ✅ 20 条接口路径已定义。
- ✅ 全部使用小写英文和短横线。
- ✅ HTTP 方法语义正确。

**常见错误与定位方法：**
- `/getBookList`等动词式路径混用时应统一。
- 删除订单若需要保留历史，可改为取消动作而非物理DELETE。

---
