<template>
  <el-container class="admin-layout">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '220px'" class="admin-aside">
      <div class="logo-container">
        <span class="logo-icon">📚</span>
        <span v-show="!isCollapse" class="logo-text">管理后台</span>
      </div>
      
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :collapse-transition="false"
        router
        background-color="#001529"
        text-color="#b8c7ce"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/admin/dashboard">
          <el-icon><DataLine /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>
        
        <el-menu-item index="/admin/books">
          <el-icon><Notebook /></el-icon>
          <span>教材管理</span>
        </el-menu-item>
        
        <el-menu-item index="/admin/categories">
          <el-icon><Menu /></el-icon>
          <span>分类管理</span>
        </el-menu-item>
        
        <el-menu-item index="/admin/orders">
          <el-icon><List /></el-icon>
          <span>订单管理</span>
        </el-menu-item>
        
        <el-menu-item index="/admin/users">
          <el-icon><User /></el-icon>
          <span>管理员管理</span>
        </el-menu-item>
        
        <!-- ✅ 新增：用户管理 -->
        <el-menu-item index="/admin/members">
          <el-icon><UserFilled /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        
        <el-menu-item index="/admin/profile">
          <el-icon><Setting /></el-icon>
          <span>个人信息</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 主内容 -->
    <el-container>
      <!-- 顶部导航 -->
      <el-header class="admin-header">
        <div class="header-left">
          <el-button 
            :icon="isCollapse ? 'Expand' : 'Fold'" 
            text 
            @click="toggleCollapse"
          />
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="currentRoute">{{ currentRoute.meta?.title || currentRoute.name }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <div class="admin-info">
              <el-avatar :size="32" :src="adminInfo?.avatar || ''">
                {{ adminInfo?.username?.charAt(0)?.toUpperCase() || 'A' }}
              </el-avatar>
              <span class="admin-name">{{ adminInfo?.realName || adminInfo?.username || '管理员' }}</span>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人信息</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 内容区 -->
      <el-main class="admin-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
// ✅ 新增：导入 UserFilled 图标
import { DataLine, Notebook, Menu, List, User, UserFilled, Setting, ArrowDown } from '@element-plus/icons-vue'
import { adminApi } from '@/api/admin'

const route = useRoute()
const router = useRouter()
const isCollapse = ref(false)
const adminInfo = ref(null)

const activeMenu = computed(() => route.path)
const currentRoute = computed(() => route)

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

const fetchAdminInfo = async () => {
  try {
    const res = await adminApi.getAdminInfo()
    if (res && res.code === 200) {
      adminInfo.value = res.data
    }
  } catch (error) {
    console.error('获取管理员信息失败:', error)
  }
}

const handleCommand = (command) => {
  if (command === 'logout') {
    localStorage.removeItem('admin_token')
    localStorage.removeItem('admin_info')
    ElMessage.success('退出成功')
    router.push('/admin/login')
  } else if (command === 'profile') {
    router.push('/admin/profile')
  }
}

onMounted(() => {
  fetchAdminInfo()
  // 从 localStorage 读取管理员信息作为备用
  const info = localStorage.getItem('admin_info')
  if (info) {
    try {
      adminInfo.value = JSON.parse(info)
    } catch (e) {
      console.error('解析管理员信息失败:', e)
    }
  }
})
</script>

<style scoped>
.admin-layout {
  height: 100vh;
  overflow: hidden;
}

.admin-aside {
  background: #001529;
  overflow: hidden;
  transition: width 0.3s ease;
  flex-shrink: 0;
}

.logo-container {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  background: #002140;
  border-bottom: 1px solid #0a1a2a;
  overflow: hidden;
}

.logo-icon {
  font-size: 28px;
  flex-shrink: 0;
}

.logo-text {
  font-size: 18px;
  font-weight: 600;
  color: #ffffff;
  white-space: nowrap;
}

.el-menu {
  border-right: none;
  height: calc(100vh - 64px);
  overflow-y: auto;
}

.el-menu::-webkit-scrollbar {
  width: 4px;
}
.el-menu::-webkit-scrollbar-thumb {
  background: #1a2a3a;
  border-radius: 2px;
}

/* 顶部导航 */
.admin-header {
  background: #ffffff;
  border-bottom: 1px solid #e8ecf1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  height: 64px;
  flex-shrink: 0;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.admin-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 12px;
  border-radius: 6px;
  transition: background 0.2s;
}

.admin-info:hover {
  background: #f0f2f5;
}

.admin-name {
  font-size: 14px;
  color: #303133;
}

.admin-main {
  background: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
  height: calc(100vh - 64px);
}

/* 菜单项图标间距 */
.el-menu-item .el-icon {
  margin-right: 8px;
  font-size: 18px;
}

/* 折叠状态 */
.el-menu--collapse .el-menu-item .el-icon {
  margin-right: 0;
}

/* 响应式 */
@media (max-width: 768px) {
  .admin-aside {
    position: fixed;
    left: 0;
    top: 0;
    bottom: 0;
    z-index: 1000;
  }
  
  .admin-header {
    padding: 0 12px;
  }
  
  .admin-main {
    padding: 12px;
  }
}
</style>