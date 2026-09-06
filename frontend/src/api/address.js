import request from './request'

/**
 * 收货地址相关 API
 */
export const addressApi = {
  /**
   * 获取地址列表
   */
  getAddressList() {
    return request.get('/address')
  },

  /**
   * 获取地址详情
   * @param {number} addressId
   */
  getAddressById(addressId) {
    return request.get(`/address/${addressId}`)
  },

  /**
   * 新增地址
   * @param {Object} data - { receiverName, receiverPhone, province, city, district, detailAddress, isDefault }
   */
  addAddress(data) {
    return request.post('/address', data)
  },

  /**
   * 修改地址
   * @param {number} addressId
   * @param {Object} data
   */
  updateAddress(addressId, data) {
    return request.put(`/address/${addressId}`, data)
  },

  /**
   * 删除地址
   * @param {number} addressId
   */
  deleteAddress(addressId) {
    return request.delete(`/address/${addressId}`)
  },

  /**
   * 设置默认地址
   * @param {number} addressId
   */
  setDefaultAddress(addressId) {
    return request.put(`/address/${addressId}/default`)
  },

  /**
   * 获取默认地址
   */
  getDefaultAddress() {
    return request.get('/address/default')
  }
}