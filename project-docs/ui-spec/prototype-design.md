**原型路由片段：**

```javascript
const routes = [
  { path: '/', component: () => import('./views/Home.vue') },
  { path: '/books', component: () => import('./views/Books.vue') },
  { path: '/book/:id', component: () => import('./views/BookDetail.vue') },
  { path: '/cart', component: () => import('./views/Cart.vue') },
  { path: '/orders/create', component: () => import('./views/OrderCreate.vue') },
  { path: '/orders', component: () => import('./views/Order.vue') },
  { path: '/order/:id', component: () => import('./views/OrderDetail.vue') },
  { path: '/profile', component: () => import('./views/Profile.vue') },
  { path: '/address', component: () => import('./views/Address.vue') },
  { path: '/login', component: () => import('./views/UserLogin.vue') },
  { path: '/register', component: () => import('./views/Register.vue') },
  { path: '/admin/login', component: () => import('./views/AdminLogin.vue') },
  { path: '/admin', component: () => import('./views/admin/AdminLayout.vue'),
    children: [
      { path: 'dashboard', component: () => import('./views/admin/AdminDashboard.vue') },
      { path: 'books', component: () => import('./views/admin/AdminBooks.vue') },
      { path: 'categories', component: () => import('./views/admin/AdminCategories.vue') },
      { path: 'orders', component: () => import('./views/admin/AdminOrders.vue') },
      { path: 'users', component: () => import('./views/admin/AdminUsers.vue') },
      { path: 'members', component: () => import('./views/admin/AdminMembers.vue') },
      { path: 'profile', component: () => import('./views/admin/AdminProfile.vue') }
    ]
  }
]
```

**公共布局结构：**

```
AppLayout（学生端）
├── Header
│   ├── Logo
│   ├── 导航菜单（教材大厅、购物车、我的订单）
│   └── 用户区域（登录/注册 或 用户菜单）
├── Main（<RouterView />）
└── Footer

AdminLayout（管理端）
├── Aside（左侧菜单）
├── Header（面包屑、管理员菜单）
└── Main（<RouterView />）
```

**预期结果与人工检查：**
- 导航点击后URL和页面标题同时变化。
- 刷新任一路由不出现空白页。
- 原型数据集中存放，不散落在多个组件。

**实际结果：**
- ✅ 公共布局结构已定义。
- ✅ 22 个路由已配置。
- ✅ Mock 数据集中存放。

**常见错误与定位方法：**
- 原型与正式工程混在一起时用Git分支或明确目录隔离。
- 路由空白时检查App.vue是否包含RouterView。

---