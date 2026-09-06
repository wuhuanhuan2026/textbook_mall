<template>
  <div class="order-detail" v-loading="loading">
    <div v-if="order" class="detail-container">
      <!-- 面包屑 -->
      <div class="breadcrumb">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item :to="{ path: '/orders' }">我的订单</el-breadcrumb-item>
          <el-breadcrumb-item>订单详情</el-breadcrumb-item>
        </el-breadcrumb>
      </div>
      
      <!-- 订单状态 -->
      <div class="order-status-bar" :class="getStatusClass(order.orderStatus)">
        <div class="status-icon">{{ getStatusIcon(order.orderStatus) }}</div>
        <div class="status-info">
          <h2>{{ getStatusText(order.orderStatus) }}</h2>
          <p v-if="order.orderStatus === 0">请尽快完成支付，超时订单将自动取消</p>
          <p v-else-if="order.orderStatus === 1">商家正在准备发货，请耐心等待</p>
          <p v-else-if="order.orderStatus === 2">商品已发货，请注意查收</p>
          <p v-else-if="order.orderStatus === 3">订单已完成，感谢您的购买</p>
          <p v-else-if="order.orderStatus === 4">订单已取消</p>
        </div>
        <div class="status-actions" v-if="order.orderStatus === 0">
          <el-button type="primary" @click="handlePay">立即支付</el-button>
          <el-button @click="handleCancel">取消订单</el-button>
        </div>
        <div class="status-actions" v-if="order.orderStatus === 2">
          <el-button type="success" @click="handleReceive">确认收货</el-button>
        </div>
      </div>
      
      <!-- 订单信息 -->
      <div class="detail-section">
        <h3>📋 订单信息</h3>
        <div class="info-grid">
          <div class="info-item">
            <span class="label">订单号</span>
            <span class="value">{{ order.orderId || '-' }}</span>
          </div>
          <div class="info-item">
            <span class="label">下单时间</span>
            <span class="value">{{ order.createTime || '-' }}</span>
          </div>
          <div class="info-item">
            <span class="label">支付方式</span>
            <span class="value">{{ order.payType === 0 ? '余额支付' : '其他' }}</span>
          </div>
          <div class="info-item">
            <span class="label">支付时间</span>
            <span class="value">{{ order.payTime || '未支付' }}</span>
          </div>
        </div>
      </div>
      
      <!-- 收货信息 -->
      <div class="detail-section">
        <h3>📍 收货信息</h3>
        <div class="address-info">
          <p><strong>{{ order.receiverName || '-' }}</strong> {{ order.receiverPhone || '-' }}</p>
          <p>{{ order.receiverAddress || '-' }}</p>
          <p v-if="order.remark">备注：{{ order.remark }}</p>
        </div>
      </div>
      
      <!-- 商品列表 -->
      <div class="detail-section">
        <h3>📦 商品列表</h3>
        <div class="product-list">
          <div class="product-item" v-for="item in (order.items || [])" :key="item.itemId">
            <img :src="item.bookCover || '/default-cover.jpg'" :alt="item.bookName" />
            <div class="product-detail">
              <span class="product-name">{{ item.bookName || '未知商品' }}</span>
              <span class="product-price">¥{{ safeToFixed(item.price) }}</span>
            </div>
            <span class="product-quantity">×{{ item.quantity || 0 }}</span>
            <span class="product-total">¥{{ safeToFixed(item.totalPrice) }}</span>
          </div>
        </div>
        
        <div class="order-total-summary">
          <div class="summary-row">
            <span>商品金额</span>
            <span>¥{{ safeToFixed(order.totalAmount) }}</span>
          </div>
          <div class="summary-row">
            <span>运费</span>
            <span>¥{{ safeToFixed(order.shippingFee || 0) }}</span>
          </div>
          <div class="summary-row total">
            <span>实付款</span>
            <span>¥{{ safeToFixed(order.totalAmount) }}</span>
          </div>
        </div>
      </div>
      
      <!-- 评价按钮 -->
      <div class="detail-actions" v-if="order.orderStatus === 3">
        <el-button type="primary" @click="goToReview">去评价</el-button>
      </div>
    </div>
    
    <el-empty v-else-if="!loading" description="订单不存在" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { orderApi } from '@/api/order'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const order = ref(null)

const statusMap = {
  0: { text: '待付款', class: 'pending', icon: '⏳' },
  1: { text: '待发货', class: 'paid', icon: '📦' },
  2: { text: '待收货', class: 'shipped', icon: '🚚' },
  3: { text: '已完成', class: 'completed', icon: '✅' },
  4: { text: '已取消', class: 'cancelled', icon: '❌' }
}

/**
 * 安全格式化数字
 * @param {number|string|undefined} value - 要格式化的值
 * @param {number} digits - 小数位数
 * @returns {string} 格式化后的字符串
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

const getStatusText = (status) => statusMap[status]?.text || '未知'
const getStatusClass = (status) => statusMap[status]?.class || ''
const getStatusIcon = (status) => statusMap[status]?.icon || '📋'

const fetchOrderDetail = async () => {
  const orderId = route.params.id
  if (!orderId) {
    ElMessage.error('参数错误')
    router.push('/orders')
    return
  }
  
  loading.value = true
  try {
    const result = await orderApi.getOrderDetail(orderId)
    // 处理不同响应格式
    if (result && result.code === 200) {
      order.value = result.data || null
    } else if (result && result.orderId) {
      order.value = result
    } else {
      order.value = null
    }
  } catch (error) {
    console.error('获取订单详情失败:', error)
    ElMessage.error(error.message || '获取订单详情失败')
    order.value = null
  } finally {
    loading.value = false
  }
}

const handlePay = async () => {
  if (!order.value) return
  try {
    await ElMessageBox.confirm('确定要支付该订单吗？', '提示', {
      confirmButtonText: '确定支付',
      cancelButtonText: '取消',
      type: 'info'
    })
    await orderApi.payOrder(order.value.orderId)
    ElMessage.success('支付成功')
    await fetchOrderDetail()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('支付失败:', error)
      ElMessage.error(error.message || '支付失败')
    }
  }
}

const handleCancel = async () => {
  if (!order.value) return
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？', '提示', {
      confirmButtonText: '确定取消',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await orderApi.cancelOrder(order.value.orderId)
    ElMessage.success('订单已取消')
    await fetchOrderDetail()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消失败:', error)
      ElMessage.error(error.message || '取消失败')
    }
  }
}

const handleReceive = async () => {
  if (!order.value) return
  try {
    await ElMessageBox.confirm('确认已收到商品？', '提示', {
      confirmButtonText: '确认收货',
      cancelButtonText: '取消',
      type: 'info'
    })
    await orderApi.confirmReceive(order.value.orderId)
    ElMessage.success('确认收货成功')
    await fetchOrderDetail()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('确认收货失败:', error)
      ElMessage.error(error.message || '确认收货失败')
    }
  }
}

const goToReview = () => {
  if (!order.value) return
  router.push(`/review/create?orderId=${order.value.orderId}`)
}

onMounted(fetchOrderDetail)
</script>

<style scoped>
.order-detail {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.detail-container {
  background: #ffffff;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.breadcrumb {
  margin-bottom: 20px;
}

.order-status-bar {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 20px 24px;
  border-radius: 8px;
  margin-bottom: 24px;
}

.order-status-bar.pending { background: #fdf6ec; border-left: 4px solid #E6A23C; }
.order-status-bar.paid { background: #ecf5ff; border-left: 4px solid #409EFF; }
.order-status-bar.shipped { background: #f0f9eb; border-left: 4px solid #67C23A; }
.order-status-bar.completed { background: #f4f4f5; border-left: 4px solid #909399; }
.order-status-bar.cancelled { background: #fef0f0; border-left: 4px solid #F56C6C; }

.status-icon {
  font-size: 32px;
}

.status-info h2 {
  font-size: 18px;
  margin: 0 0 4px 0;
  color: #303133;
}

.status-info p {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

.status-actions {
  margin-left: auto;
  display: flex;
  gap: 8px;
}

.detail-section {
  margin: 24px 0;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.detail-section:first-of-type {
  border-top: none;
  padding-top: 0;
}

.detail-section h3 {
  font-size: 16px;
  color: #303133;
  margin-bottom: 16px;
}

.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.info-item {
  display: flex;
  gap: 8px;
}

.info-item .label {
  color: #909399;
  font-size: 14px;
  min-width: 70px;
}

.info-item .value {
  color: #303133;
  font-size: 14px;
}

.address-info {
  font-size: 14px;
  color: #606266;
  line-height: 1.8;
}

.address-info strong {
  color: #303133;
}

.product-list {
  border: 1px solid #ebeef5;
  border-radius: 4px;
  overflow: hidden;
}

.product-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px 16px;
  border-bottom: 1px solid #ebeef5;
}

.product-item:last-child {
  border-bottom: none;
}

.product-item img {
  width: 50px;
  height: 70px;
  object-fit: cover;
  border-radius: 4px;
  background: #f5f7fa;
}

.product-detail {
  flex: 1;
  display: flex;
  justify-content: space-between;
}

.product-name {
  font-size: 14px;
  color: #303133;
}

.product-price {
  font-size: 14px;
  color: #606266;
}

.product-quantity {
  font-size: 14px;
  color: #909399;
  min-width: 60px;
  text-align: center;
}

.product-total {
  font-size: 14px;
  color: #f56c6c;
  font-weight: 600;
  min-width: 100px;
  text-align: right;
}

.order-total-summary {
  margin-top: 16px;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8px;
}

.summary-row {
  display: flex;
  gap: 40px;
  font-size: 14px;
  color: #606266;
}

.summary-row.total {
  font-size: 18px;
  font-weight: 600;
  color: #f56c6c;
}

.detail-actions {
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
  text-align: center;
}
</style>