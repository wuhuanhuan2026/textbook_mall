<template>
  <header class="header">
    <div class="header-container">
      <!-- Logo -->
      <div class="logo" @click="goHome">📚 教材商城</div>
      
      <!-- 导航菜单 -->
      <nav class="nav">
        <router-link to="/">首页</router-link>
        <router-link to="/books">全部教材</router-link>
        <router-link to="/orders" v-if="userStore.isLoggedIn">我的订单</router-link>
      </nav>

      <!-- 右侧操作区 -->
      <div class="header-right">
        <!-- 购物车 -->
        <router-link to="/cart" class="cart-link">
          <el-badge :value="cartStore.totalCount" :hidden="cartStore.totalCount === 0">
            <el-icon><ShoppingCart /></el-icon>
          </el-badge>
        </router-link>

        <!-- 用户信息 -->
        <div v-if="userStore.isLoggedIn" class="user-info">
          <el-dropdown @command="handleCommand">
            <span class="username">
              {{ userStore.userInfo?.username || '用户' }}
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="address">地址管理</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
        
        <!-- 未登录 -->
        <div v-else class="auth-buttons">
          <el-button type="primary" size="small" @click="goToLogin">登录</el-button>
          <el-button size="small" @click="goToRegister">注册</el-button>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ShoppingCart, ArrowDown } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { useCartStore } from '@/stores/cart'

const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()

const goHome = () => router.push('/')
const goToLogin = () => router.push('/login')
const goToRegister = () => router.push('/register')

const handleCommand = async (command) => {
  if (command === 'logout') {
    await userStore.logout()
    ElMessage.success('已退出登录')
    router.push('/login')
  } else if (command === 'profile') {
    router.push('/profile')
  } else if (command === 'address') {
    router.push('/address')
  }
}
</script>

<style scoped>
.header {
  background: #ffffff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo {
  font-size: 22px;
  font-weight: 700;
  color: #409EFF;
  cursor: pointer;
  user-select: none;
}

.logo:hover {
  opacity: 0.8;
}

.nav {
  display: flex;
  gap: 32px;
  flex: 1;
  margin-left: 48px;
}

.nav a {
  color: #606266;
  text-decoration: none;
  font-size: 15px;
  transition: color 0.3s;
  position: relative;
}

.nav a:hover {
  color: #409EFF;
}

.nav a.router-link-active {
  color: #409EFF;
}

.nav a.router-link-active::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  right: 0;
  height: 2px;
  background: #409EFF;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.cart-link {
  color: #606266;
  font-size: 20px;
  transition: color 0.3s;
  display: flex;
  align-items: center;
}

.cart-link:hover {
  color: #409EFF;
}

.user-info {
  cursor: pointer;
}

.username {
  color: #303133;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.auth-buttons {
  display: flex;
  gap: 8px;
}
</style>