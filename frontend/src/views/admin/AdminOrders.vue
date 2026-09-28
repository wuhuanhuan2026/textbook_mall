<template>
  <div class="admin-orders">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-row">
      <el-col :span="4" v-for="stat in orderStats" :key="stat.label">
        <el-statistic :value="stat.value" :title="stat.label">
          <template #suffix>
            <span style="font-size:14px;color:#909399">单</span>
          </template>
        </el-statistic>
      </el-col>
    </el-row>

    <!-- 搜索区域 -->
    <el-card shadow="hover" class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="订单号">
          <el-input v-model="searchForm.orderId" placeholder="输入订单号" clearable />
        </el-form-item>
        <el-form-item label="用户ID">
          <el-input-number v-model="searchForm.userId" :min="1" placeholder="用户ID" style="width:140px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部状态" clearable>
            <el-option label="待付款" :value="0" />
            <el-option label="待发货" :value="1" />
            <el-option label="待收货" :value="2" />
            <el-option label="已完成" :value="3" />
            <el-option label="已取消" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchOrders">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
          <el-button type="success" @click="exportOrders">导出数据</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格 -->
    <el-card shadow="hover" class="table-card">
      <div class="table-toolbar">
        <div class="toolbar-left">
          <el-button type="success" plain :disabled="!selectedIds.length" @click="batchDeliver">
            批量发货
          </el-button>
          <el-button type="danger" plain :disabled="!selectedIds.length" @click="batchCancel">
            批量取消
          </el-button>
        </div>
        <div class="toolbar-right">
          <span class="total-info">共 {{ total }} 笔订单</span>
        </div>
      </div>

      <el-table
        :data="orderList"
        style="width: 100%"
        v-loading="loading"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="40" />
        <el-table-column prop="orderId" label="订单号" width="160" />
        <el-table-column prop="userId" label="用户ID" width="80" />
        <el-table-column prop="totalAmount" label="金额" width="100">
          <template #default="{ row }">
            ¥{{ row.totalAmount?.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="orderStatus" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.orderStatus)" size="small">
              {{ getStatusText(row.orderStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="receiverName" label="收货人" width="80" />
        <el-table-column prop="receiverPhone" label="电话" width="120" />
        <el-table-column prop="createTime" label="下单时间" width="170" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="viewOrder(row.orderId)">
              详情
            </el-button>
            <el-button 
              v-if="row.orderStatus === 1" 
              type="success" 
              link 
              size="small" 
              @click="handleDeliver(row.orderId)"
            >
              发货
            </el-button>
            <el-button 
              v-if="row.orderStatus === 0 || row.orderStatus === 1" 
              type="danger" 
              link 
              size="small" 
              @click="handleCancel(row.orderId)"
            >
              取消
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:page-num="pageNum"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchOrders"
          @current-change="fetchOrders"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminApi } from '@/api/admin'

const router = useRouter()
const loading = ref(false)
const orderList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const selectedIds = ref([])

const searchForm = reactive({
  orderId: '',
  userId: null,
  status: null,
  dateRange: null
})

const statusMap = {
  0: { text: '待付款', type: 'warning' },
  1: { text: '待发货', type: 'info' },
  2: { text: '待收货', type: 'primary' },
  3: { text: '已完成', type: 'success' },
  4: { text: '已取消', type: 'danger' }
}

const getStatusText = (status) => statusMap[status]?.text || '未知'
const getStatusType = (status) => statusMap[status]?.type || 'info'

// 统计信息
const orderStats = ref([
  { label: '总订单', value: 0 },
  { label: '待付款', value: 0 },
  { label: '待发货', value: 0 },
  { label: '待收货', value: 0 },
  { label: '已完成', value: 0 },
  { label: '已取消', value: 0 }
])

const fetchOrders = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      userId: searchForm.userId || undefined,
      status: searchForm.status !== null ? searchForm.status : undefined,
      startTime: searchForm.dateRange?.[0] || undefined,
      endTime: searchForm.dateRange?.[1] || undefined
    }
    // 移除空值
    Object.keys(params).forEach(key => {
      if (params[key] === undefined || params[key] === null || params[key] === '') {
        delete params[key]
      }
    })
    
    const res = await adminApi.getOrders(params)
    if (res && res.code === 200) {
      const pageData = res.data
      orderList.value = pageData?.list || []
      total.value = pageData?.total || 0
      updateStats()
    }
  } catch (error) {
    console.error('获取订单列表失败:', error)
  } finally {
    loading.value = false
  }
}

const updateStats = () => {
  const list = orderList.value
  orderStats.value = [
    { label: '总订单', value: total.value },
    { label: '待付款', value: list.filter(o => o.orderStatus === 0).length },
    { label: '待发货', value: list.filter(o => o.orderStatus === 1).length },
    { label: '待收货', value: list.filter(o => o.orderStatus === 2).length },
    { label: '已完成', value: list.filter(o => o.orderStatus === 3).length },
    { label: '已取消', value: list.filter(o => o.orderStatus === 4).length }
  ]
}

const resetSearch = () => {
  searchForm.orderId = ''
  searchForm.userId = null
  searchForm.status = null
  searchForm.dateRange = null
  pageNum.value = 1
  fetchOrders()
}

const handleSelectionChange = (rows) => {
  selectedIds.value = rows.map(r => r.orderId)
}

// ========== 发货 ==========
const handleDeliver = async (orderId) => {
  try {
    await ElMessageBox.confirm('确定要发货该订单吗？', '提示', { type: 'info' })
    await adminApi.deliverOrder(orderId)
    ElMessage.success('发货成功')
    fetchOrders()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('发货失败')
    }
  }
}

/**
 * 批量发货 - 只筛选"待发货"状态的订单
 */
const batchDeliver = async () => {
  if (!selectedIds.value.length) return

  // ✅ 关键修复：只筛选状态为 1（待发货）的订单
  const deliverableIds = orderList.value
    .filter(row => selectedIds.value.includes(row.orderId) && row.orderStatus === 1)
    .map(row => row.orderId)

  if (!deliverableIds.length) {
    ElMessage.warning('选中的订单中没有可发货的（只有"待发货"状态的订单才能发货）')
    return
  }

  // 提示用户跳过的订单数量
  const skipped = selectedIds.value.length - deliverableIds.length
  const tip = skipped > 0
    ? `确定要批量发货 ${deliverableIds.length} 个订单吗？（跳过 ${skipped} 个状态不符的订单）`
    : `确定要批量发货 ${deliverableIds.length} 个订单吗？`

  try {
    await ElMessageBox.confirm(tip, '提示', { type: 'info' })
    await adminApi.batchDeliverOrders(deliverableIds)
    ElMessage.success('批量发货成功')
    selectedIds.value = []
    fetchOrders()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量发货失败')
    }
  }
}

// ========== 取消 ==========
const handleCancel = async (orderId) => {
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？', '提示', { type: 'warning' })
    await adminApi.cancelOrderByAdmin(orderId)
    ElMessage.success('取消成功')
    fetchOrders()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消失败')
    }
  }
}

/**
 * 批量取消 - 只筛选状态为 0（待付款）或 1（待发货）的订单
 */
const batchCancel = async () => {
  if (!selectedIds.value.length) return

  // ✅ 关键修复：只筛选可取消的订单（0-待付款 或 1-待发货）
  const cancellableIds = orderList.value
    .filter(row => selectedIds.value.includes(row.orderId) 
      && (row.orderStatus === 0 || row.orderStatus === 1))
    .map(row => row.orderId)

  if (!cancellableIds.length) {
    ElMessage.warning('选中的订单中没有可取消的（只有"待付款"或"待发货"状态的订单才能取消）')
    return
  }

  // 提示用户跳过的订单数量
  const skipped = selectedIds.value.length - cancellableIds.length
  const tip = skipped > 0
    ? `确定要批量取消 ${cancellableIds.length} 个订单吗？（跳过 ${skipped} 个状态不符的订单）`
    : `确定要批量取消 ${cancellableIds.length} 个订单吗？`

  try {
    await ElMessageBox.confirm(tip, '提示', { type: 'warning' })
    await adminApi.batchCancelOrders(cancellableIds)
    ElMessage.success('批量取消成功')
    selectedIds.value = []
    fetchOrders()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量取消失败')
    }
  }
}

// ========== 查看详情 ==========
const viewOrder = (orderId) => {
  router.push(`/order/${orderId}`)
}

// ========== 导出 ==========
const exportOrders = async () => {
  try {
    const params = {
      status: searchForm.status !== null ? searchForm.status : undefined,
      startTime: searchForm.dateRange?.[0] || undefined,
      endTime: searchForm.dateRange?.[1] || undefined,
      format: 'csv'
    }
    Object.keys(params).forEach(key => {
      if (params[key] === undefined || params[key] === null) {
        delete params[key]
      }
    })
    
    const res = await adminApi.exportOrders(params)
    // 创建下载链接
    const blob = new Blob([res], { type: 'text/csv;charset=utf-8' })
    const url = window.URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = `订单数据_${new Date().toISOString().slice(0,10)}.csv`
    a.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

onMounted(fetchOrders)
</script>

<style scoped>
.admin-orders {
  max-width: 1400px;
  margin: 0 auto;
}

.stat-row {
  margin-bottom: 20px;
}

.stat-row .el-col {
  margin-bottom: 12px;
}

.search-card {
  margin-bottom: 20px;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
}

.search-form .el-form-item {
  margin-bottom: 0;
}

.table-card {
  overflow: hidden;
}

.table-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  flex-wrap: wrap;
  gap: 8px;
}

.toolbar-left {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.toolbar-right {
  font-size: 14px;
  color: #909399;
}

.total-info {
  font-weight: 500;
  color: #303133;
}

.pagination-wrapper {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

@media (max-width: 768px) {
  .search-form .el-form-item {
    width: 100%;
  }
}
</style>