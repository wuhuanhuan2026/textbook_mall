<template>
  <div class="order-page">
    <h1 class="page-title">📦 我的订单</h1>
    
    <!-- 状态筛选 -->
    <div class="order-tabs">
      <el-tabs v-model="activeStatus" @tab-change="handleTabChange">
        <el-tab-pane label="全部订单" name="all" />
        <el-tab-pane label="待付款" name="0" />
        <el-tab-pane label="待发货" name="1" />
        <el-tab-pane label="待收货" name="2" />
        <el-tab-pane label="已完成" name="3" />
        <el-tab-pane label="已取消" name="4" />
      </el-tabs>
    </div>
    
    <!-- 订单列表 -->
    <div v-loading="loading">
      <div v-if="orderList.length > 0">
        <div class="order-item" v-for="order in orderList" :key="order.orderId">
          <div class="order-header">
            <span class="order-id">订单号：{{ order.orderId }}</span>
            <span class="order-status" :class="getStatusClass(order.orderStatus)">
              {{ getStatusText(order.orderStatus) }}
            </span>
          </div>
          
          <div class="order-body">
            <div class="order-items">
              <div class="order-product" v-for="item in (order.items || [])" :key="item.itemId">
                <img :src="item.bookCover || '/default-cover.jpg'" :alt="item.bookName" />
                <div class="product-info">
                  <span class="product-name">{{ item.bookName }}</span>
                  <span class="product-price">¥{{ safeToFixed(item.price) }} × {{ item.quantity }}</span>
                </div>
              </div>
            </div>
            
            <div class="order-summary">
              <span>共 {{ order.items?.length || 0 }} 件商品</span>
              <span class="order-total">实付：¥{{ safeToFixed(order.totalAmount) }}</span>
            </div>
          </div>
          
          <div class="order-footer">
            <span class="order-time">下单时间：{{ order.createTime || '-' }}</span>
            <div class="order-actions">
              <el-button size="small" @click="viewDetail(order.orderId)">查看详情</el-button>
              <el-button 
                v-if="order.orderStatus === 0" 
                type="primary" 
                size="small" 
                @click="handlePay(order.orderId)"
              >
                立即支付
              </el-button>
              <el-button 
                v-if="order.orderStatus === 0" 
                size="small" 
                @click="handleCancel(order.orderId)"
              >
                取消订单
              </el-button>
              <el-button 
                v-if="order.orderStatus === 2" 
                type="success" 
                size="small" 
                @click="handleReceive(order.orderId)"
              >
                确认收货
              </el-button>
            </div>
          </div>
        </div>
      </div>
      
      <el-empty v-else description="暂无订单" />
    </div>
    
    <!-- 分页 -->
    <div class="pagination-wrapper" v-if="total > 0">
      <el-pagination
        v-model:page-num="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[5, 10, 20]"
        layout="total, sizes, prev, pager, next"
        @size-change="fetchOrders"
        @current-change="fetchOrders"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { orderApi } from '@/api/order'

const router = useRouter()
const route = useRoute()

const loading = ref(false)
const orderList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(5)

// ✅ 从 URL 参数读取状态，默认为 'all'
const activeStatus = ref(route.query.status || 'all')

const statusMap = {
  0: { text: '待付款', class: 'pending' },
  1: { text: '待发货', class: 'paid' },
  2: { text: '待收货', class: 'shipped' },
  3: { text: '已完成', class: 'completed' },
  4: { text: '已取消', class: 'cancelled' }
}

const getStatusText = (status) => statusMap[status]?.text || '未知'
const getStatusClass = (status) => statusMap[status]?.class || ''

/**
 * ✅ 安全格式化数字
 */
const safeToFixed = (value, digits = 2) => {
  if (value === undefined || value === null || isNaN(value)) {
    return '0.00'
  }
  const num = Number(value)
  if (isNaN(num)) {
    return '0.00'
  }
  return num.toFixed(digits)
}

const fetchOrders = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
    if (activeStatus.value !== 'all') {
      params.status = Number(activeStatus.value)
    }
    
    // ✅ 添加调试日志
    console.log('📦 请求订单参数:', params)
    
    const result = await orderApi.getOrderList(params)
    
    // ✅ 添加调试日志
    console.log('📦 订单响应:', result)
    
    // ✅ 适配不同的响应格式
    if (result && result.list !== undefined) {
      orderList.value = result.list || []
      total.value = result.total || 0
    } else if (result && result.data && result.data.list !== undefined) {
      orderList.value = result.data.list || []
      total.value = result.data.total || 0
    } else {
      orderList.value = []
      total.value = 0
    }
    
    // ✅ 打印当前标签页和订单数量
    console.log(`📦 当前标签: ${activeStatus.value}, 订单数量: ${orderList.value.length}, 总数: ${total.value}`)
    
  } catch (error) {
    console.error('❌ 获取订单列表失败:', error)
    orderList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

const handleTabChange = () => {
  console.log(`📦 切换到标签: ${activeStatus.value}`)
  pageNum.value = 1
  fetchOrders()
}

const viewDetail = (orderId) => {
  router.push(`/order/${orderId}`)
}

const handlePay = async (orderId) => {
  try {
    await ElMessageBox.confirm('确定要支付该订单吗？', '提示', {
      confirmButtonText: '确定支付',
      cancelButtonText: '取消',
      type: 'info'
    })
    await orderApi.payOrder(orderId)
    ElMessage.success('支付成功')
    fetchOrders()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('❌ 支付失败:', error)
      ElMessage.error(error.message || '支付失败')
    }
  }
}

const handleCancel = async (orderId) => {
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？', '提示', {
      confirmButtonText: '确定取消',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await orderApi.cancelOrder(orderId)
    ElMessage.success('订单已取消')
    fetchOrders()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('❌ 取消失败:', error)
      ElMessage.error(error.message || '取消失败')
    }
  }
}

const handleReceive = async (orderId) => {
  try {
    await ElMessageBox.confirm('确认已收到商品？', '提示', {
      confirmButtonText: '确认收货',
      cancelButtonText: '取消',
      type: 'info'
    })
    await orderApi.confirmReceive(orderId)
    ElMessage.success('确认收货成功')
    fetchOrders()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('❌ 确认收货失败:', error)
      ElMessage.error(error.message || '确认收货失败')
    }
  }
}

// ✅ 监听路由参数变化（从其他页面跳转时携带 status 参数）
watch(
  () => route.query.status,
  (newStatus) => {
    if (newStatus !== undefined && newStatus !== null) {
      console.log(`📦 路由参数变化: status=${newStatus}`)
      activeStatus.value = newStatus
      fetchOrders()
    }
  },
  { immediate: false }
)

// ✅ 监听 activeStatus 变化，更新 URL（可选：实现浏览器前进/后退）
watch(
  activeStatus,
  (newStatus) => {
    // 不触发路由跳转，只更新查询参数（避免循环）
    const query = { ...route.query }
    if (newStatus === 'all') {
      delete query.status
    } else {
      query.status = newStatus
    }
    // 只在路径变化时更新
    if (route.query.status !== query.status) {
      router.replace({ query })
    }
  }
)

onMounted(() => {
  console.log('📦 订单页面初始化, status:', activeStatus.value)
  fetchOrders()
})
</script>

<style scoped>
.order-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-title {
  font-size: 24px;
  color: #303133;
  margin-bottom: 20px;
}

.order-tabs {
  background: #ffffff;
  border-radius: 8px;
  padding: 0 20px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.order-item {
  background: #ffffff;
  border-radius: 8px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  overflow: hidden;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  background: #fafafa;
  border-bottom: 1px solid #ebeef5;
}

.order-id {
  font-size: 13px;
  color: #909399;
}

.order-status {
  font-size: 14px;
  font-weight: 500;
}

.order-status.pending { color: #E6A23C; }
.order-status.paid { color: #409EFF; }
.order-status.shipped { color: #67C23A; }
.order-status.completed { color: #909399; }
.order-status.cancelled { color: #F56C6C; }

.order-body {
  padding: 16px 20px;
}

.order-items {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.order-product {
  display: flex;
  align-items: center;
  gap: 12px;
}

.order-product img {
  width: 50px;
  height: 70px;
  object-fit: cover;
  border-radius: 4px;
  background: #f5f7fa;
}

.product-info {
  display: flex;
  justify-content: space-between;
  flex: 1;
}

.product-name {
  font-size: 14px;
  color: #303133;
}

.product-price {
  font-size: 13px;
  color: #909399;
}

.order-summary {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 24px;
  padding-top: 12px;
  margin-top: 12px;
  border-top: 1px solid #ebeef5;
  font-size: 14px;
  color: #606266;
}

.order-total {
  font-size: 18px;
  font-weight: 600;
  color: #f56c6c;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  border-top: 1px solid #ebeef5;
}

.order-time {
  font-size: 12px;
  color: #c0c4cc;
}

.order-actions {
  display: flex;
  gap: 8px;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: center;
  background: #ffffff;
  padding: 16px;
  border-radius: 8px;
}
</style>