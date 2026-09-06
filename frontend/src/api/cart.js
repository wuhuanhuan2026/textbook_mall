import request from './request'

/**
 * 购物车相关 API
 */
export const cartApi = {
  /**
   * 获取购物车列表
   */
  getCartList() {
    return request.get('/cart')
  },

  /**
   * 添加到购物车
   * @param {Object} data - { bookId, quantity }
   */
  addToCart(data) {
    return request.post('/cart', data)  // POST /cart
  },

  /**
   * 更新购物车商品数量
   * @param {number} cartId
   * @param {Object} data - { quantity }
   */
  updateCartItem(cartId, data) {
    return request.put(`/cart/${cartId}`, data)
  },

  /**
   * 删除购物车项
   * @param {number} cartId
   */
  deleteCartItem(cartId) {
    return request.delete(`/cart/${cartId}`)
  },

  /**
   * 清空购物车
   */
  clearCart() {
    return request.delete('/cart/clear')
  },

  /**
   * 获取购物车商品数量
   */
  getCartCount() {
    return request.get('/cart/count')
  },

  /**
   * 获取购物车总金额
   */
  getCartTotal() {
    return request.get('/cart/total')
  },

  /**
   * 批量删除购物车项
   * @param {Array} cartIds
   */
  batchDelete(cartIds) {
    return request.delete('/cart/batch', { data: cartIds })
  }
}