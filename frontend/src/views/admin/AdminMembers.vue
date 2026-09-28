<template>
  <div class="admin-members">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>👥 普通用户管理</span>
          <span class="total-info">共 {{ total }} 位用户</span>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="用户名">
          <el-input
            v-model="searchForm.keyword"
            placeholder="搜索用户名/手机号/邮箱"
            clearable
            style="width: 220px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table :data="userList" v-loading="loading" style="width: 100%">
        <el-table-column prop="userId" label="ID" width="80" />
        <el-table-column label="头像" width="80">
          <template #default="{ row }">
            <el-avatar :size="40" :src="row.avatar || ''">
              {{ (row.username || 'U').charAt(0).toUpperCase() }}
            </el-avatar>
          </template>
        </el-table-column>
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column prop="phone" label="手机号" width="140" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="balance" label="余额" width="120">
          <template #default="{ row }">
            <span class="balance">¥{{ formatMoney(row.balance) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="180" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 0"
              type="success"
              link
              size="small"
              @click="handleStatus(row.userId, 1)"
            >
              启用
            </el-button>
            <el-button
              v-else
              type="warning"
              link
              size="small"
              @click="handleStatus(row.userId, 0)"
            >
              禁用
            </el-button>
            <el-button
              type="danger"
              link
              size="small"
              @click="handleDelete(row.userId, row.username)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminApi } from '@/api/admin'

const loading = ref(false)
const userList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const searchForm = reactive({
  keyword: '',
  status: null
})

// 格式化金额
const formatMoney = (val) => {
  if (val === undefined || val === null || isNaN(val)) return '0.00'
  return Number(val).toFixed(2)
}

// 获取用户列表
const fetchUsers = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
    if (searchForm.keyword) params.keyword = searchForm.keyword
    if (searchForm.status !== null) params.status = searchForm.status

    const res = await adminApi.getMemberList(params)
    if (res && res.code === 200) {
      userList.value = res.data?.list || []
      total.value = res.data?.total || 0
    }
  } catch (error) {
    console.error('获取用户列表失败:', error)
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pageNum.value = 1
  fetchUsers()
}

// 重置
const resetSearch = () => {
  searchForm.keyword = ''
  searchForm.status = null
  pageNum.value = 1
  fetchUsers()
}

// 分页
const handleSizeChange = (val) => {
  pageSize.value = val
  pageNum.value = 1
  fetchUsers()
}

const handleCurrentChange = (val) => {
  pageNum.value = val
  fetchUsers()
}

// 启用/禁用
const handleStatus = async (userId, status) => {
  const tip = status === 1 ? '启用' : '禁用'
  try {
    await ElMessageBox.confirm(`确定要${tip}该用户吗？`, '提示', { type: 'warning' })
    await adminApi.updateMemberStatus(userId, status)
    ElMessage.success(`${tip}成功`)
    fetchUsers()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(`${tip}失败`)
    }
  }
}

// 删除
const handleDelete = async (userId, username) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除用户「${username}」吗？此操作不可恢复！`,
      '危险操作',
      { type: 'error' }
    )
    await adminApi.deleteMember(userId)
    ElMessage.success('删除成功')
    fetchUsers()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(fetchUsers)
</script>

<style scoped>
.admin-members {
  max-width: 1400px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.total-info {
  font-size: 14px;
  color: #909399;
}

.search-form {
  margin-bottom: 16px;
}

.balance {
  color: #f56c6c;
  font-weight: 600;
}

.pagination-wrapper {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>