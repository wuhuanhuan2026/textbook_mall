<template>
  <div class="cart-page">
    <h1 class="page-title">🛒 我的购物车</h1>
    
    <div v-if="cartStore.cartItems.length > 0" class="cart-container">
      <!-- 购物车列表 -->
      <div class="cart-list">
        <div class="cart-header">
          <span>商品信息</span>
          <span>单价</span>
          <span>数量</span>
          <span>小计</span>
          <span>操作</span>
        </div>
        
        <div 
          class="cart-item" 
          v-for="item in cartStore.cartItems" 
          :key="item.cartId"
        >
          <div class="item-info" @click="goToDetail(item.bookId)">
            <img :src="item.bookCover || '/default-cover.jpg'" :alt="item.bookName" />
            <span class="item-name">{{ item.bookName }}</span>
          </div>
          <span class="item-price">¥{{ (item.price || 0).toFixed(2) }}</span>
          <div class="item-quantity">
            <el-input-number
              v-model="item.quantity"
              :min="1"
              :max="item.stock || 99"
              size="small"
              @change="updateQuantity(item.cartId, item.quantity)"
            />
          </div>
          <span class="item-total">¥{{ ((item.price || 0) * item.quantity).toFixed(2) }}</span>
          <el-button type="danger" link @click="removeItem(item.cartId)">删除</el-button>
        </div>
      </div>
      
      <!-- 购物车底部结算 -->
      <div class="cart-footer">
        <div class="cart-summary">
          <span>共 <strong>{{ cartStore.totalCount }}</strong> 件商品</span>
          <span>合计：<strong class="total-price">¥{{ cartStore.totalPrice.toFixed(2) }}</strong></span>
        </div>
        <div class="cart-actions">
          <el-button @click="clearCart">清空购物车</el-button>
          <el-button type="primary" size="large" @click="checkout">
            去结算
          </el-button>
        </div>
      </div>
    </div>
    
    <el-empty v-else description="购物车是空的，快去逛逛吧">
      <el-button type="primary" @click="goToBooks">去逛逛</el-button>
    </el-empty>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useCartStore } from '@/stores/cart'

const router = useRouter()
const cartStore = useCartStore()

const updateQuantity = async (cartId, quantity) => {
  await cartStore.updateQuantity(cartId, quantity)
}

const removeItem = async (cartId) => {
  try {
    await ElMessageBox.confirm('确定要删除该商品吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await cartStore.removeItem(cartId)
    ElMessage.success('已删除')
  } catch {
    // 取消删除
  }
}

const clearCart = async () => {
  try {
    await ElMessageBox.confirm('确定要清空购物车吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await cartStore.clearCart()
    ElMessage.success('已清空购物车')
  } catch {
    // 取消清空
  }
}

const checkout = () => {
  if (cartStore.cartItems.length === 0) {
    ElMessage.warning('购物车为空')
    return
  }
  router.push('/orders/create')
}

const goToDetail = (bookId) => {
  router.push(`/book/${bookId}`)
}

const goToBooks = () => {
  router.push('/books')
}

onMounted(() => {
  cartStore.fetchCart()
})
</script>

<style scoped>
.cart-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-title {
  font-size: 24px;
  color: #303133;
  margin-bottom: 20px;
}

.cart-container {
  background: #ffffff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  overflow: hidden;
}

.cart-header {
  display: grid;
  grid-template-columns: 4fr 1fr 1.5fr 1.5fr 1fr;
  padding: 12px 20px;
  background: #f5f7fa;
  font-size: 14px;
  color: #909399;
  font-weight: 500;
}

.cart-item {
  display: grid;
  grid-template-columns: 4fr 1fr 1.5fr 1.5fr 1fr;
  padding: 16px 20px;
  align-items: center;
  border-bottom: 1px solid #ebeef5;
}

.cart-item:last-child {
  border-bottom: none;
}

.item-info {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
}

.item-info img {
  width: 60px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
  background: #f5f7fa;
}

.item-name {
  font-size: 14px;
  color: #303133;
}

.item-price {
  font-size: 14px;
  color: #606266;
}

.item-quantity {
  display: flex;
  justify-content: center;
}

.item-total {
  font-size: 14px;
  color: #f56c6c;
  font-weight: 600;
}

.cart-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-top: 1px solid #ebeef5;
  background: #fafafa;
}

.cart-summary {
  display: flex;
  gap: 24px;
  font-size: 14px;
  color: #606266;
}

.cart-summary strong {
  color: #303133;
}

.total-price {
  font-size: 20px;
  color: #f56c6c !important;
}

.cart-actions {
  display: flex;
  gap: 12px;
}

@media (max-width: 768px) {
  .cart-header {
    display: none;
  }
  
  .cart-item {
    grid-template-columns: 1fr;
    gap: 8px;
    padding: 16px;
  }
  
  .item-info {
    flex-direction: column;
    text-align: center;
  }
  
  .item-quantity {
    justify-content: center;
  }
  
  .cart-footer {
    flex-direction: column;
    gap: 12px;
  }
  
  .cart-summary {
    flex-direction: column;
    align-items: center;
  }
}
</style>