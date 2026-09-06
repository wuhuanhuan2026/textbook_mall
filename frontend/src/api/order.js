import request from './request'

/**
 * 订单相关 API
 */
export const orderApi = {
  /**
   * 创建订单
   * @param {Object} data - { addressId, remark }
   */
  createOrder(data) {
    return request.post('/order', data)
  },

  /**
   * 获取订单列表
   * @param {Object} params - { pageNum, pageSize, status }
   */
  getOrderList(params) {
    return request.get('/order', { params })
  },

  /**
   * 获取订单详情
   * @param {string} orderId
   */
  getOrderDetail(orderId) {
    return request.get(`/order/${orderId}`)
  },

  /**
   * 取消订单
   * @param {string} orderId
   */
  cancelOrder(orderId) {
    return request.put(`/order/${orderId}/cancel`)
  },

  /**
   * 支付订单
   * @param {string} orderId
   */
  payOrder(orderId) {
    return request.post(`/order/${orderId}/pay`)
  },

  /**
   * 确认收货
   * @param {string} orderId
   */
  confirmReceive(orderId) {
    return request.put(`/order/${orderId}/receive`)
  }
}