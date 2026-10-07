

功能追踪矩阵：

功能	角色	页面	按钮	接口	数据	验收条件
F-003 浏览教材	游客	/books	搜索	GET /api/books	Book	列表显示教材
F-005 查看详情	游客	/book/:id	-	GET /api/books/:id	Book	详情显示正确
F-006 加入购物车	学生	/book/:id	加入购物车	POST /api/cart	Cart	购物车新增记录
F-007 提交订单	学生	/orders/create	提交订单	POST /api/order	Order	订单创建成功
F-008 余额支付	学生	/order/:id	支付	POST /api/order/:id/pay	Order, User	订单状态变更，余额扣减
F-009 我的订单	学生	/orders	查看详情	GET /api/order	Order	列表显示订单
F-001 教材管理	管理员	/admin/books	新增/编辑	POST/PUT /api/books	Book	教材保存成功
F-002 上架教材	管理员	/admin/books	上架	PUT /api/books/:id/on-sale	Book	状态变为 PUBLISHED
F-010 订单管理	管理员	/admin/orders	发货	GET /api/admin/orders	Order	列表显示所有订单
F-011 发货	管理员	/admin/orders	发货	PUT /api/admin/orders/:id/deliver	Order	状态变为 SHIPPED


预期结果与人工检查：

每项 MVP 功能至少对应一个页面和一种证据。

按钮操作可以追踪到接口或前端状态。

不存在孤立页面和无消费者接口。



实际结果：

✅ 10 项核心功能已追踪。

✅ 功能→页面→接口→数据→验收条件全链路完整。



常见错误与定位方法：

只做功能列表会在开发时反复补页面和字段。

把截图当作唯一证据会遗漏数据库和接口结果。

