<template>
  <div class="admin-dashboard">
    <div class="dashboard-header">
      <h1>📊 管理后台</h1>
      <el-button type="danger" @click="handleLogout">退出登录</el-button>
    </div>
    
    <div class="dashboard-stats">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-number">{{ stats.users }}</div>
            <div class="stat-label">用户总数</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-number">{{ stats.orders }}</div>
            <div class="stat-label">订单总数</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-number">{{ stats.books }}</div>
            <div class="stat-label">教材总数</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-number">¥{{ stats.revenue }}</div>
            <div class="stat-label">总营收</div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <div class="dashboard-content">
      <el-card shadow="hover">
        <template #header>
          <span>欢迎回来，管理员！</span>
        </template>
        <p>这里是教材商城管理后台，您可以在这里管理用户、订单、教材等数据。</p>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()

const stats = ref({
  users: 0,
  orders: 0,
  books: 0,
  revenue: 0
})

const handleLogout = () => {
  localStorage.removeItem('admin_token')
  localStorage.removeItem('admin_info')
  ElMessage.success('退出成功')
  router.push('/admin/login')
}

onMounted(() => {
  // TODO: 从 API 获取统计数据
  stats.value = {
    users: 156,
    orders: 328,
    books: 30,
    revenue: 45678
  }
})
</script>

<style scoped>
.admin-dashboard {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.dashboard-header h1 {
  font-size: 24px;
  color: #303133;
  margin: 0;
}

.stat-card {
  text-align: center;
  padding: 20px 0;
}

.stat-number {
  font-size: 32px;
  font-weight: 700;
  color: #409EFF;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 8px;
}

.dashboard-content {
  margin-top: 20px;
}
</style>