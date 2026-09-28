// frontend/src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  // ========== 前台路由 ==========
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/UserLogin.vue'),
    meta: { guest: true, title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { guest: true, title: '注册' }
  },
  {
    path: '/books',
    name: 'Books',
    component: () => import('@/views/Books.vue'),
    meta: { title: '全部教材' }
  },
  {
    path: '/book/:id',
    name: 'BookDetail',
    component: () => import('@/views/BookDetail.vue'),
    meta: { title: '教材详情' }
  },
  {
    path: '/cart',
    name: 'Cart',
    component: () => import('@/views/Cart.vue'),
    meta: { requiresAuth: true, title: '购物车' }
  },
  {
    path: '/orders',
    name: 'Orders',
    component: () => import('@/views/Order.vue'),
    meta: { requiresAuth: true, title: '我的订单' }
  },
  {
    path: '/orders/create',
    name: 'OrderCreate',
    component: () => import('@/views/OrderCreate.vue'),
    meta: { requiresAuth: true, title: '确认订单' }
  },
  {
    path: '/order/:id',
    name: 'OrderDetail',
    component: () => import('@/views/OrderDetail.vue'),
    meta: { requiresAuth: true, title: '订单详情' }
  },
  {
    path: '/review/create',
    name: 'ReviewCreate',
    component: () => import('@/views/ReviewCreate.vue'),
    meta: { requiresAuth: true, title: '发表评价' }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/Profile.vue'),
    meta: { requiresAuth: true, title: '个人中心' }
  },
  {
    path: '/address',
    name: 'Address',
    component: () => import('@/views/Address.vue'),
    meta: { requiresAuth: true, title: '地址管理' }
  },

  // ========== 管理员路由 ==========
  {
    path: '/admin/login',
    name: 'AdminLogin',
    component: () => import('@/views/AdminLogin.vue'),
    meta: { guest: true, title: '管理员登录' }
  },
  {
    path: '/admin',
    component: () => import('@/views/admin/AdminLayout.vue'),
    meta: { requiresAdmin: true, title: '管理后台' },
    children: [
      { path: '', redirect: '/admin/dashboard' },
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/AdminDashboard.vue'),
        meta: { title: '仪表盘' }
      },
      {
        path: 'books',
        name: 'AdminBooks',
        component: () => import('@/views/admin/AdminBooks.vue'),
        meta: { title: '教材管理' }
      },
      {
        path: 'categories',
        name: 'AdminCategories',
        component: () => import('@/views/admin/AdminCategories.vue'),
        meta: { title: '分类管理' }
      },
      {
        path: 'orders',
        name: 'AdminOrders',
        component: () => import('@/views/admin/AdminOrders.vue'),
        meta: { title: '订单管理' }
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('@/views/admin/AdminUsers.vue'),
        meta: { title: '管理员管理' }
      },
      // ✅ 新增：普通用户管理
      {
        path: 'members',
        name: 'AdminMembers',
        component: () => import('@/views/admin/AdminMembers.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: 'profile',
        name: 'AdminProfile',
        component: () => import('@/views/admin/AdminProfile.vue'),
        meta: { title: '个人信息' }
      }
    ]
  },
  
  // 404 重定向
  {
    path: '/:pathMatch(.*)*',
    redirect: '/'
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

/**
 * ✅ 安全解码 JWT payload（支持 URL-safe Base64）
 */
function decodeJwtPayload(token) {
  if (!token) return null
  
  try {
    const parts = token.split('.')
    if (parts.length !== 3) {
      console.error('❌ JWT 格式无效，段数:', parts.length)
      return null
    }
    
    // 获取 payload 部分
    let base64 = parts[1]
    
    // 1. 替换 URL-safe 字符为标准 Base64 字符
    base64 = base64.replace(/-/g, '+').replace(/_/g, '/')
    
    // 2. 补齐 padding（Base64 要求长度为 4 的倍数）
    while (base64.length % 4 !== 0) {
      base64 += '='
    }
    
    // 3. 解码
    const jsonStr = atob(base64)
    return JSON.parse(jsonStr)
  } catch (error) {
    console.error('❌ JWT 解码失败:', error.message)
    return null
  }
}

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  document.title = to.meta.title ? `${to.meta.title} - 教材商城` : '教材商城'

  // ===== 管理员权限检查 =====
  if (to.meta.requiresAdmin) {
    const adminToken = localStorage.getItem('admin_token')
    
    console.log('=== 管理员路由守卫 ===')
    console.log('目标路径:', to.path)
    console.log('adminToken:', adminToken ? '存在 (长度: ' + adminToken.length + ')' : '不存在')
    
    if (!adminToken) {
      console.log('❌ 无 token，跳转登录')
      next({ path: '/admin/login', query: { redirect: to.fullPath } })
      return
    }
    
    // ✅ 使用安全解码函数
    const payload = decodeJwtPayload(adminToken)
    
    if (!payload) {
      console.log('❌ token 解码失败，跳转登录')
      // 不清除 token，保留用于调试
      next({ path: '/admin/login', query: { redirect: to.fullPath } })
      return
    }
    
    console.log('✅ payload 解码成功:', payload)
    console.log('exp:', payload.exp)
    console.log('过期时间:', new Date(payload.exp * 1000).toLocaleString())
    console.log('当前时间:', new Date().toLocaleString())
    
    if (payload.exp && payload.exp * 1000 < Date.now()) {
      console.log('❌ token 已过期')
      localStorage.removeItem('admin_token')
      localStorage.removeItem('admin_info')
      next({ path: '/admin/login', query: { redirect: to.fullPath } })
      return
    }
    
    console.log('✅ token 验证通过，放行')
    next()
    return
  }

  // ===== 普通用户权限检查 =====
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    console.log('❌ 需要登录，跳转到登录页')
    next({ path: '/login', query: { redirect: to.fullPath } })
    return
  }
  
  if (to.meta.guest && userStore.isLoggedIn) {
    const redirect = from.query.redirect || '/'
    console.log('✅ 已登录，从登录页跳转到:', redirect)
    next(redirect)
    return
  }
  
  next()
})

export default router