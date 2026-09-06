<template>
  <div class="order-create" v-loading="loading">
    <div class="page-header">
      <h1 class="page-title">📝 确认订单</h1>
      <el-button type="text" @click="goBack">
        <el-icon><ArrowLeft /></el-icon> 返回购物车
      </el-button>
    </div>
    
    <div v-if="cartItems.length > 0" class="order-container">
      <!-- 收货地址 -->
      <div class="section address-section">
        <div class="section-header">
          <h3>📍 收货地址</h3>
          <el-button type="primary" link @click="goToAddress">管理地址</el-button>
        </div>
        
        <div v-if="addresses.length > 0" class="address-list">
          <div 
            class="address-item" 
            v-for="addr in addresses" 
            :key="addr.addressId"
            :class="{ active: selectedAddress === addr.addressId }"
            @click="selectedAddress = addr.addressId"
          >
            <div class="address-radio">
              <el-radio :model-value="selectedAddress" :label="addr.addressId">
                {{ '' }}
              </el-radio>
            </div>
            <div class="address-info">
              <div class="address-name">
                <span class="receiver">{{ addr.receiverName }}</span>
                <span class="phone">{{ addr.receiverPhone }}</span>
                <el-tag v-if="addr.isDefault === 1" size="small" type="success" class="default-tag">默认</el-tag>
              </div>
              <div class="address-detail">{{ addr.fullAddress || addr.province + addr.city + addr.district + addr.detailAddress }}</div>
            </div>
          </div>
        </div>
        
        <div v-else class="empty-address">
          <el-empty description="暂无收货地址，请先添加" :image-size="80">
            <el-button type="primary" @click="goToAddress">添加地址</el-button>
          </el-empty>
        </div>
      </div>

      <!-- 商品列表 -->
      <div class="section goods-section">
        <h3>📦 商品列表</h3>
        <div class="goods-list">
          <div class="goods-item" v-for="item in cartItems" :key="item.cartId">
            <div class="goods-image">
              <img :src="item.bookCover || '/default-cover.jpg'" :alt="item.bookName" />
            </div>
            <div class="goods-info">
              <div class="goods-name">{{ item.bookName }}</div>
              <div class="goods-meta">
                <span class="goods-price">¥{{ Number(item.price).toFixed(2) }}</span>
                <span class="goods-quantity">× {{ item.quantity }}</span>
              </div>
            </div>
            <div class="goods-subtotal">
              ¥{{ (Number(item.price) * item.quantity).toFixed(2) }}
            </div>
          </div>
        </div>
      </div>

      <!-- 订单备注 -->
      <div class="section remark-section">
        <h3>📝 订单备注</h3>
        <el-input 
          v-model="remark" 
          type="textarea" 
          :rows="2" 
          placeholder="选填，如有特殊要求请备注"
          maxlength="200"
          show-word-limit
        />
      </div>

      <!-- 结算信息 -->
      <div class="section summary-section">
        <div class="summary-content">
          <div class="summary-row">
            <span>商品金额</span>
            <span>¥{{ totalPrice.toFixed(2) }}</span>
          </div>
          <div class="summary-row">
            <span>运费</span>
            <span>¥0.00</span>
          </div>
          <div class="summary-row total">
            <span>实付款</span>
            <span class="total-price">¥{{ totalPrice.toFixed(2) }}</span>
          </div>
        </div>
        
        <div class="summary-actions">
          <el-button size="large" @click="goBack">返回购物车</el-button>
          <el-button 
            type="primary" 
            size="large" 
            :loading="submitting"
            :disabled="!selectedAddress || addresses.length === 0"
            @click="submitOrder"
          >
            {{ submitting ? '提交中...' : '提交订单' }}
          </el-button>
        </div>
      </div>
    </div>
    
    <el-empty v-else description="购物车是空的" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import { useCartStore } from '@/stores/cart'
import { useUserStore } from '@/stores/user' // ✅ 新增：引入用户状态
import { addressApi } from '@/api/address'
import { orderApi } from '@/api/order'

const router = useRouter()
const cartStore = useCartStore()
const userStore = useUserStore() // ✅ 新增：用户状态

const loading = ref(false)
const submitting = ref(false)
const addresses = ref([])
const selectedAddress = ref(null)
const remark = ref('')

// 记录当前用户ID，用于检测切换账号
const currentUserId = ref(null)

// 从购物车获取商品列表
const cartItems = computed(() => cartStore.cartItems || [])
const totalPrice = computed(() => cartStore.totalPrice || 0)

const goBack = () => {
  router.push('/cart')
}

const goToAddress = () => {
  router.push('/address')
}

// ✅ 重置页面数据（切换账号时调用）
const resetPageData = () => {
  addresses.value = []
  selectedAddress.value = null
  remark.value = ''
  // 不重置购物车，让 cartStore 自己管理
}

// ✅ 获取收货地址列表（增强版）
const fetchAddresses = async () => {
  try {
    // 获取当前用户ID
    const userId = userStore.userInfo?.userId || userStore.userId
    console.log('当前用户ID:', userId)
    
    // 如果用户ID发生变化，重置数据
    if (currentUserId.value && currentUserId.value !== userId) {
      console.log('检测到用户切换，重置数据')
      resetPageData()
    }
    currentUserId.value = userId
    
    const result = await addressApi.getAddressList()
    console.log('获取地址列表响应:', result)
    
    // 处理不同响应格式
    let addressList = []
    if (Array.isArray(result)) {
      addressList = result
    } else if (result && result.code === 200) {
      addressList = result.data || []
    } else {
      addressList = []
    }
    
    addresses.value = addressList
    
    // ✅ 默认选中默认地址或第一个地址（确保属于当前用户）
    if (addresses.value.length > 0) {
      // 优先选择默认地址
      let defaultAddr = addresses.value.find(a => a.isDefault === 1)
      // 如果没有默认地址，选择第一个
      if (!defaultAddr) {
        defaultAddr = addresses.value[0]
      }
      selectedAddress.value = defaultAddr.addressId
      console.log('选中的地址ID:', selectedAddress.value)
    } else {
      selectedAddress.value = null
      ElMessage.warning('暂无收货地址，请先添加')
    }
  } catch (error) {
    console.error('获取地址列表失败:', error)
    addresses.value = []
    selectedAddress.value = null
    ElMessage.error('获取地址列表失败，请刷新重试')
  }
}

// ✅ 提交订单（增强版）
const submitOrder = async () => {
  // 检查是否选中地址
  if (!selectedAddress.value) {
    ElMessage.warning('请选择收货地址')
    return
  }
  
  // ✅ 新增：校验选中的地址是否属于当前用户（防止缓存问题）
  const isValidAddress = addresses.value.some(addr => addr.addressId === selectedAddress.value)
  if (!isValidAddress) {
    ElMessage.error('所选地址无效，请重新选择')
    // 自动切换到第一个有效地址
    if (addresses.value.length > 0) {
      selectedAddress.value = addresses.value[0].addressId
      ElMessage.info('已自动切换到有效地址')
    } else {
      ElMessage.warning('暂无有效地址，请添加收货地址')
      return
    }
    return
  }
  
  if (cartItems.value.length === 0) {
    ElMessage.warning('购物车为空')
    return
  }
  
  // 确认提交
  try {
    await ElMessageBox.confirm('确认提交订单？', '提示', {
      confirmButtonText: '确认提交',
      cancelButtonText: '取消',
      type: 'info'
    })
  } catch {
    return
  }
  
  submitting.value = true
  try {
    const orderData = {
      addressId: selectedAddress.value,
      remark: remark.value
    }
    
    console.log('提交订单数据:', orderData) // ✅ 便于调试
    
    const result = await orderApi.createOrder(orderData)
    console.log('订单创建响应:', result) // ✅ 便于调试
    
    if (result && (result.code === 200 || result.code === undefined)) {
      ElMessage.success('订单创建成功')
      // 清空购物车数据
      await cartStore.fetchCart()
      // 跳转到订单详情或订单列表
      const orderId = result.data?.orderId
      if (orderId) {
        router.push(`/order/${orderId}`)
      } else {
        router.push('/orders')
      }
    } else {
      ElMessage.error(result?.message || '订单创建失败')
    }
  } catch (error) {
    console.error('提交订单失败:', error)
    // ✅ 优化错误信息显示
    const errorMsg = error?.response?.data?.message || error?.message || '订单创建失败，请稍后重试'
    ElMessage.error(errorMsg)
  } finally {
    submitting.value = false
  }
}

// ✅ 监听用户切换（如果用户Store支持响应式）
watch(
  () => userStore.userInfo?.userId || userStore.userId,
  (newUserId, oldUserId) => {
    if (newUserId && oldUserId && newUserId !== oldUserId) {
      console.log(`用户从 ${oldUserId} 切换到 ${newUserId}，刷新数据`)
      resetPageData()
      // 重新获取数据
      fetchAddresses()
      cartStore.fetchCart()
    }
  },
  { immediate: false }
)

// ✅ 监听路由变化，确保每次进入页面都刷新数据
watch(
  () => router.currentRoute.value.path,
  () => {
    if (router.currentRoute.value.path === '/order/create') {
      console.log('重新进入订单确认页面，刷新数据')
      // 页面可见时刷新数据
      if (!loading.value) {
        fetchAddresses()
        cartStore.fetchCart()
      }
    }
  }
)

// ✅ 初始化（增强版）
onMounted(async () => {
  loading.value = true
  try {
    // 先重置数据，确保干净状态
    resetPageData()
    
    // 并行获取数据
    await Promise.all([
      cartStore.fetchCart(),
      fetchAddresses()
    ])
    
    console.log('订单页面初始化完成')
    console.log('购物车商品:', cartItems.value)
    console.log('地址列表:', addresses.value)
    console.log('选中的地址:', selectedAddress.value)
  } catch (error) {
    console.error('初始化订单页面失败:', error)
    ElMessage.error('页面初始化失败，请刷新重试')
  } finally {
    loading.value = false
  }
})

// ✅ 关键修复：暴露方法和数据给外部调用（用于调试和测试）
defineExpose({
  submitOrder,
  fetchAddresses,
  resetPageData,
  selectedAddress,
  addresses,
  cartItems,
  totalPrice,
  loading,
  submitting,
  remark,
  userStore,
  currentUserId,
  cartStore,
  goBack,
  goToAddress
})
</script>

<style scoped>
.order-create {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-title {
  font-size: 24px;
  color: #303133;
  margin: 0;
}

.section {
  background: #ffffff;
  border-radius: 12px;
  padding: 20px 24px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.section h3 {
  font-size: 16px;
  color: #303133;
  margin: 0 0 16px 0;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-header h3 {
  margin: 0;
}

/* 地址样式 */
.address-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.address-item {
  display: flex;
  align-items: flex-start;
  padding: 14px 16px;
  border: 2px solid #e4e7ed;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.address-item:hover {
  border-color: #c0c4cc;
}

.address-item.active {
  border-color: #409EFF;
  background: #ecf5ff;
}

.address-radio {
  padding-top: 2px;
  margin-right: 12px;
}

.address-info {
  flex: 1;
}

.address-name {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 4px;
}

.address-name .receiver {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.address-name .phone {
  font-size: 14px;
  color: #606266;
}

.default-tag {
  font-size: 12px;
}

.address-detail {
  font-size: 14px;
  color: #909399;
  line-height: 1.5;
}

.empty-address {
  padding: 10px 0;
}

/* 商品样式 */
.goods-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.goods-item {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.goods-item:last-child {
  border-bottom: none;
}

.goods-image {
  width: 70px;
  height: 90px;
  border-radius: 6px;
  overflow: hidden;
  flex-shrink: 0;
  background: #f5f7fa;
}

.goods-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.goods-info {
  flex: 1;
  padding: 0 16px;
}

.goods-name {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
  margin-bottom: 6px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.goods-meta {
  display: flex;
  align-items: center;
  gap: 12px;
}

.goods-price {
  font-size: 16px;
  font-weight: 600;
  color: #f56c6c;
}

.goods-quantity {
  font-size: 14px;
  color: #909399;
}

.goods-subtotal {
  font-size: 16px;
  font-weight: 600;
  color: #f56c6c;
  min-width: 100px;
  text-align: right;
}

/* 备注样式 */
.remark-section :deep(.el-textarea__inner) {
  min-height: 60px;
}

/* 结算样式 */
.summary-content {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8px;
  padding-bottom: 16px;
  border-bottom: 1px solid #ebeef5;
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
  color: #303133;
}

.summary-row .total-price {
  color: #f56c6c;
  font-size: 24px;
}

.summary-actions {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  padding-top: 16px;
}

.summary-actions .el-button--primary {
  min-width: 140px;
}

/* 响应式 */
@media (max-width: 768px) {
  .section {
    padding: 16px;
  }
  
  .address-item {
    flex-direction: column;
    gap: 8px;
  }
  
  .goods-item {
    flex-wrap: wrap;
  }
  
  .goods-info {
    padding: 0 12px;
  }
  
  .goods-subtotal {
    min-width: 80px;
    padding-left: 80px;
  }
  
  .summary-row {
    gap: 20px;
  }
  
  .summary-actions {
    flex-direction: column;
  }
  
  .summary-actions .el-button {
    width: 100%;
  }
}
</style>