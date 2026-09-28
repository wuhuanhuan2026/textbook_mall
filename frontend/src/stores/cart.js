import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { cartApi } from '@/api/cart'

/**
 * 购物车状态管理
 *
 * ⚠️ 注意：Store 层不负责 UI 提示（ElMessage），
 * 由调用它的页面组件（如 BookDetail.vue、Cart.vue）自己处理提示，
 * 避免同一操作出现重复提示。
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
   * ✅ 只负责数据操作，不弹提示
   * 调用方（如 BookDetail.vue）自己弹 ElMessage
   */
  const addToCart = async (bookId, quantity = 1) => {
    const res = await cartApi.addToCart({ bookId, quantity })
    console.log('addToCart 响应:', res)

    if (res && res.code === 200) {
      await fetchCart()
      return res.data
    }
    throw new Error(res?.message || '添加失败')
  }

  /**
   * 更新商品数量
   * ✅ 只负责数据操作，不弹成功提示
   * （数量修改很频繁，弹提示会打扰用户）
   */
  const updateQuantity = async (cartId, quantity) => {
    const res = await cartApi.updateCartItem(cartId, { quantity })
    if (res && res.code === 200) {
      await fetchCart()
      return res.data
    }
    throw new Error(res?.message || '更新失败')
  }

  /**
   * 删除购物车项
   * ✅ 只负责数据操作，不弹提示
   */
  const removeItem = async (cartId) => {
    const res = await cartApi.deleteCartItem(cartId)
    if (res && res.code === 200) {
      await fetchCart()
      return true
    }
    throw new Error(res?.message || '删除失败')
  }

  /**
   * 清空购物车
   * ✅ 只负责数据操作，不弹提示
   */
  const clearCart = async () => {
    const res = await cartApi.clearCart()
    if (res && res.code === 200) {
      cartItems.value = []
      return true
    }
    throw new Error(res?.message || '清空失败')
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