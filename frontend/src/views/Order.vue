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
              <div class="order-product" v-for="item in order.items" :key="item.itemId">
                <img :src="item.bookCover || '/default-cover.jpg'" :alt="item.bookName" />
                <div class="product-info">
                  <span class="product-name">{{ item.bookName }}</span>
                  <span class="product-price">¥{{ item.price.toFixed(2) }} × {{ item.quantity }}</span>
                </div>
              </div>
            </div>
            
            <div class="order-summary">
              <span>共 {{ order.items?.length || 0 }} 件商品</span>
              <span class="order-total">实付：¥{{ order.totalAmount.toFixed(2) }}</span>
            </div>
          </div>
          
          <div class="order-footer">
            <span class="order-time">下单时间：{{ order.createTime }}</span>
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
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { orderApi } from '@/api/order'

const router = useRouter()
const loading = ref(false)
const orderList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(5)
const activeStatus = ref('all')

const statusMap = {
  0: { text: '待付款', class: 'pending' },
  1: { text: '待发货', class: 'paid' },
  2: { text: '待收货', class: 'shipped' },
  3: { text: '已完成', class: 'completed' },
  4: { text: '已取消', class: 'cancelled' }
}

const getStatusText = (status) => statusMap[status]?.text || '未知'
const getStatusClass = (status) => statusMap[status]?.class || ''

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
    
    const result = await orderApi.getOrderList(params)
    orderList.value = result.list || []
    total.value = result.total || 0
  } catch (error) {
    console.error('获取订单列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleTabChange = () => {
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
      ElMessage.error('支付失败')
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
      ElMessage.error('取消失败')
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
      ElMessage.error('确认收货失败')
    }
  }
}

onMounted(fetchOrders)
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