**WBS 任务清单：**

| 编号 | 任务 | 负责人 | 依赖 | 预计时长 | 输出 | 验收标准 |
|------|------|--------|------|----------|------|----------|
| WBS-01 | 创建数据库表和初始化脚本 | 吴欢欢 | 无 | 3h | `database/02-schema.sql` | 表创建成功 |
| WBS-02 | 实现 Common 公共模块 | 吴欢欢 | 无 | 4h | `backend/Common/` | 统一响应、异常处理 |
| WBS-03 | 实现 User 用户服务 | 吴欢欢 | WBS-02 | 6h | `backend/User/` | 登录/注册/信息接口 |
| WBS-04 | 实现 Book 教材服务 | 吴欢欢 | WBS-02 | 6h | `backend/Book/` | 教材 CRUD 接口 |
| WBS-05 | 实现 Category 分类服务 | 吴欢欢 | WBS-02 | 3h | `backend/Category/` | 分类 CRUD 接口 |
| WBS-06 | 实现 Cart 购物车服务 | 吴欢欢 | WBS-02, WBS-04 | 4h | `backend/Cart/` | 购物车增删改查 |
| WBS-07 | 实现 Order 订单服务 | 吴欢欢 | WBS-02, WBS-04, WBS-06 | 8h | `backend/Order/` | 订单创建/查询/支付 |
| WBS-08 | 实现 Payment 支付服务 | 吴欢欢 | WBS-02, WBS-07 | 4h | `backend/Payment/` | 余额支付 |
| WBS-09 | 实现 Address 地址服务 | 吴欢欢 | WBS-02 | 3h | `backend/Address/` | 地址 CRUD |
| WBS-10 | 实现 Admin 管理后台 | 吴欢欢 | WBS-02 | 6h | `backend/Admin/` | 管理员登录/管理 |
| WBS-11 | 实现 Gateway 网关 | 吴欢欢 | WBS-02 | 3h | `backend/Gateway/` | 路由/鉴权 |
| WBS-12 | 实现 Review 评价服务 | 吴欢欢 | WBS-02, WBS-07 | 4h | `backend/Review/` | 评价 CRUD |
| WBS-13 | 前端工程初始化 | 吴欢欢 | 无 | 2h | `frontend/` | 项目启动 |
| WBS-14 | 前端路由与布局 | 吴欢欢 | WBS-13 | 3h | `frontend/src/router/` | 页面跳转 |
| WBS-15 | 教材列表/详情页面 | 吴欢欢 | WBS-13 | 4h | `frontend/src/views/` | 显示教材 |
| WBS-16 | 购物车页面 | 吴欢欢 | WBS-15 | 3h | `frontend/src/views/Cart.vue` | 购物车增删改 |
| WBS-17 | 订单页面 | 吴欢欢 | WBS-15 | 4h | `frontend/src/views/Order.vue` | 订单列表/详情 |
| WBS-18 | 管理端页面 | 吴欢欢 | WBS-14 | 6h | `frontend/src/views/admin/` | 教材/订单管理 |
| WBS-19 | 前后端联调 | 吴欢欢 | WBS-03~12, WBS-15~18 | 8h | 联调记录 | 核心闭环通过 |
| WBS-20 | 系统测试与缺陷修复 | 吴欢欢 | WBS-19 | 6h | 测试报告 | 关键用例通过 |

**预期结果与人工检查：**
- 任务名称以动作开头，不使用"处理一下"。
- 每项任务有唯一负责人。
- 验收标准可通过页面、接口、SQL 或测试证明。

**实际结果：**
- ✅ 20 项任务已拆分。
- ✅ 每项任务时长 2-8 小时。
- ✅ 依赖关系已标注。

**常见错误与定位方法：**
- 任务预计超过 8 小时应继续拆分。
- 任务边界不清时应重新定义输入输出。
