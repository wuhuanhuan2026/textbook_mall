**需求评审记录：**

| 序号 | 检查项 | 问题 | 严重程度 | 处理结论 | 处理位置 |
|------|--------|------|---------|---------|---------|
| 1 | REQ-001 完整性 | 已有 /books 页面 | - | 通过 | - |
| 2 | REQ-003 一致性 | "购买"与"加入购物车"术语混用 | 一般 | 统一为"加入购物车" | `user-problems.md` |
| 3 | REQ-009 可行性 | 余额支付无法对接真实支付 | 严重 | 保留模拟实现 | `system-boundary.md` |
| 4 | REQ-004 可测试性 | 缺少库存不足验收条件 | 一般 | 补充 AC-ORDER-03 | `acceptance-criteria.md` |
| 5 | BR-003 判定顺序 | 未明确"登录"是否在第1位 | 一般 | 判定顺序调整为登录优先 | `business-rules.md` |
| 6 | 追踪样例 | US-03 追踪 | - | 完成 | `acceptance-criteria.md` |

**需求追踪样例：**

```
REQ-003 学生可以加入购物车
  → 用户故事：US-03
  → 角色：学生
  → 页面：/book/:id（教材详情页）
  → 流程：查看详情 → 点击加入购物车 → 显示成功提示
  → 对象：Cart
  → 验收条件：AC-CART-01
```

**需求基线冻结：**

| 项目 | 内容 |
|------|------|
| 版本号 | v1.0 |
| 冻结日期 | 2026-09-05 |
| 冻结人 | 吴欢欢 |
| 包含文件 | `business-flow.md`、`use-case-detailed.md`、`business-rules.md`、`acceptance-criteria.md` |
| 变更规则 | 变更必须提升版本号并记录影响 |

**Git 提交命令：**
```cmd
git add project-docs/requirements
git commit -m "docs: freeze requirements baseline v1.0 for training 5"
git log --oneline -3
```

**预期结果与人工检查：**
- Must 需求不存在无页面、无流程或无验收条件情况。
- 评审问题均有关闭或延期结论。
- Git 提交包含需求基线且可通过 commit id 定位。

**实际结果：**
- ✅ 6 项评审问题已记录。
- ✅ 1 个追踪样例已完成。
- ✅ 需求基线 v1.0 已冻结。
- ⏳ Git 提交待执行。

**常见错误与定位方法：**
- 评审只改文档不记录原因会失去追踪，应保留评审表。
- 冻结不是禁止修改；变更时应提升版本号并记录影响。
