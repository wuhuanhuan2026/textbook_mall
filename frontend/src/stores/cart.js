import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { cartApi } from '@/api/cart'
import { ElMessage } from 'element-plus'

/**
 * 购物车状态管理
 */
export const useCartStore = defineStore('cart', () => {
  // ========== State ==========
  const cartItems = ref([])
  const loading = ref(false)

  // ========== Getters ==========
  const totalCount = computed(() => {
    if (!cartItems.value || !Array.isArray(cartItems.value)) {
      return 0
    }
    return cartItems.value.reduce((sum, item) => sum + (item.quantity || 0), 0)
  })

  const totalPrice = computed(() => {
    if (!cartItems.value || !Array.isArray(cartItems.value)) {
      return 0
    }
    return cartItems.value.reduce((sum, item) => {
      const price = Number(item.price) || 0
      const quantity = Number(item.quantity) || 0
      return sum + price * quantity
    }, 0)
  })

  // ========== Actions ==========
  /**
   * 获取购物车列表
   */
  const fetchCart = async () => {
    loading.value = true
    try {
      const res = await cartApi.getCartList()
      if (res && res.code === 200) {
        cartItems.value = Array.isArray(res.data) ? res.data : []
      } else if (Array.isArray(res)) {
        cartItems.value = res
      } else {
        cartItems.value = []
      }
    } catch (error) {
      console.error('获取购物车失败:', error)
      cartItems.value = []
    } finally {
      loading.value = false
    }
  }

  /**
   * 添加到购物车
   * ✅ 修复：传递对象 { bookId, quantity }
   */
  const addToCart = async (bookId, quantity = 1) => {
    try {
      // ✅ 关键修复：传递对象，而不是两个独立参数
      const res = await cartApi.addToCart({ bookId, quantity })
      console.log('addToCart 响应:', res)
      
      if (res && res.code === 200) {
        await fetchCart()
        ElMessage.success('已加入购物车')
        return res.data
      }
      throw new Error(res?.message || '添加失败')
    } catch (error) {
      console.error('加入购物车失败:', error)
      ElMessage.error(error.message || '添加失败')
      throw error
    }
  }

  /**
   * 更新商品数量
   */
  const updateQuantity = async (cartId, quantity) => {
    try {
      const res = await cartApi.updateCartItem(cartId, { quantity })
      if (res && res.code === 200) {
        await fetchCart()
        return res.data
      }
      throw new Error(res?.message || '更新失败')
    } catch (error) {
      ElMessage.error(error.message || '更新失败')
      throw error
    }
  }

  /**
   * 删除购物车项
   */
  const removeItem = async (cartId) => {
    try {
      const res = await cartApi.deleteCartItem(cartId)
      if (res && res.code === 200) {
        await fetchCart()
        ElMessage.success('已删除')
        return true
      }
      throw new Error(res?.message || '删除失败')
    } catch (error) {
      ElMessage.error(error.message || '删除失败')
      throw error
    }
  }

  /**
   * 清空购物车
   */
  const clearCart = async () => {
    try {
      const res = await cartApi.clearCart()
      if (res && res.code === 200) {
        cartItems.value = []
        ElMessage.success('已清空购物车')
        return true
      }
      throw new Error(res?.message || '清空失败')
    } catch (error) {
      ElMessage.error(error.message || '清空失败')
      throw error
    }
  }

  return {
    cartItems,
    loading,
    totalCount,
    totalPrice,
    fetchCart,
    addToCart,
    updateQuantity,
    removeItem,
    clearCart
  }
})