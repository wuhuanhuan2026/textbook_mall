**字段字典（教材列表页）：**

| 标签 | 代码名 | 类型 | 来源 | 格式 | 可编辑 |
|------|--------|------|------|------|--------|
| 教材标题 | title | string | book | 1-100字符 | 只读 |
| 作者 | author | string | book | - | 只读 |
| 封面 | coverImage | string | book | URL | 只读 |
| 售价 | price | number | book | 2位小数 | 只读 |
| 原价 | originalPrice | number | book | 2位小数 | 只读 |
| 库存 | stock | number | book | 整数 | 只读 |
| 教材状态 | status | enum | book | DRAFT/PUBLISHED/OFF_SALE | 只读 |
| 关键词 | keyword | string | 前端状态 | - | 可编辑 |
| 分类筛选 | categoryId | number | 前端状态 | - | 可编辑 |
| 排序方式 | sortBy | string | 前端状态 | default/price_asc/price_desc/sales | 可编辑 |
| 当前页码 | pageNum | number | 前端状态 | ≥1 | 可编辑 |
| 每页条数 | pageSize | number | 前端状态 | 8/16/24/32 | 可编辑 |

**字段字典（教材详情页）：**

| 标签 | 代码名 | 类型 | 来源 | 格式 | 可编辑 |
|------|--------|------|------|------|--------|
| 教材ID | bookId | number | book | - | 只读 |
| 教材标题 | title | string | book | 1-100字符 | 只读 |
| 作者 | author | string | book | - | 只读 |
| 出版社 | publisher | string | book | - | 只读 |
| ISBN | isbn | string | book | - | 只读 |
| 分类名称 | categoryName | string | book | - | 只读 |
| 售价 | price | number | book | 2位小数 | 只读 |
| 原价 | originalPrice | number | book | 2位小数 | 只读 |
| 库存 | stock | number | book | 整数 | 只读 |
| 已售 | sales | number | book | 整数 | 只读 |
| 简介 | description | string | book | - | 只读 |
| 剩余库存 | remainingCapacity | number | 派生 | capacity - registeredCount | 只读 |
| 购买数量 | quantity | number | 前端状态 | 1-库存 | 可编辑 |

**按钮规则（教材详情页）：**

| 按钮 | 显示条件 | 禁用条件 | 点击结果 |
|------|---------|---------|---------|
| 加入购物车 | 教材 PUBLISHED | 未登录、库存≤0 | 加入购物车，提示成功 |
| 已售罄 | PUBLISHED 且库存=0 | 是 | - |
| 教材未上架 | 教材非 PUBLISHED | 是 | - |

**预期结果与人工检查：**
- 同一字段在列表、详情和表单使用相同英文名。
- 每个按钮都有显示条件和点击后结果。
- 派生字段明确计算来源，不要求数据库重复保存。

**实际结果：**
- ✅ 教材列表页 12 个字段已定义。
- ✅ 教材详情页 13 个字段已定义。
- ✅ 3 种按钮状态已定义。

**常见错误与定位方法：**
- 原型文字和接口字段命名不一致会导致联调映射混乱。
- "状态"未定义枚举值时回到需求基线补充。

---
