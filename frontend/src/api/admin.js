// src/api/admin.js
import request from './request'

export const adminApi = {
  // ========== 认证 ==========
  login(data) {
    return request.post('/admin/login', data)
  },
  
  logout() {
    return request.post('/admin/logout')
  },
  
  getAdminInfo() {
    return request.get('/admin/profile')
  },

  // ========== 个人信息 ==========
  updateProfile(data) {
    return request.put('/admin/profile', data)
  },
  
  updatePassword(data) {
    return request.put('/admin/profile/password', data)
  },

  // ========== 管理员管理 ==========
  getAdminList() {
    return request.get('/admin/users')
  },
  
  getActiveAdmins() {
    return request.get('/admin/users/active')
  },
  
  getAdminById(adminId) {
    return request.get(`/admin/users/${adminId}`)
  },
  
  createAdmin(data) {
    return request.post('/admin/users', data)
  },
  
  updateAdmin(adminId, data) {
    return request.put(`/admin/users/${adminId}`, data)
  },
  
  enableAdmin(adminId) {
    return request.put(`/admin/users/${adminId}/enable`)
  },
  
  disableAdmin(adminId) {
    return request.put(`/admin/users/${adminId}/disable`)
  },
  
  deleteAdmin(adminId) {
    return request.delete(`/admin/users/${adminId}`)
  },
  
  batchDeleteAdmins(adminIds) {
    return request.delete('/admin/users/batch', { data: adminIds })
  },

  // ========== ✅ 新增：普通用户管理 ==========
  getMemberList(params) {
    return request.get('/admin/users/members', { params })
  },

  getMemberDetail(userId) {
    return request.get(`/admin/users/members/${userId}`)
  },

  updateMemberStatus(userId, status) {
    return request.put(`/admin/users/members/${userId}/status`, null, { params: { status } })
  },

  getMemberBalance(userId) {
    return request.get(`/admin/users/members/${userId}/balance`)
  },

  deleteMember(userId) {
    return request.delete(`/admin/users/members/${userId}`)
  },

  // ========== 仪表盘 ==========
  getDashboardStatistics() {
    return request.get('/admin/dashboard/statistics')
  },
  
  getDashboardTrend(type = 'day') {
    return request.get('/admin/dashboard/trend', { params: { type } })
  },

  // ========== 教材管理 ==========
  getBooks(params) {
    return request.get('/admin/books', { params })
  },
  
  getBookDetail(bookId) {
    return request.get(`/admin/books/${bookId}`)
  },
  
  createBook(data) {
    return request.post('/admin/books', data)
  },
  
  updateBook(bookId, data) {
    return request.put(`/admin/books/${bookId}`, data)
  },
  
  deleteBook(bookId) {
    return request.delete(`/admin/books/${bookId}`)
  },
  
  batchDeleteBooks(bookIds) {
    return request.delete('/admin/books/batch', { data: bookIds })
  },
  
  onSale(bookId) {
    return request.put(`/admin/books/${bookId}/on-sale`)
  },
  
  offSale(bookId) {
    return request.put(`/admin/books/${bookId}/off-sale`)
  },
  
  batchOnSale(bookIds) {
    return request.put('/admin/books/batch/on-sale', bookIds)
  },
  
  batchOffSale(bookIds) {
    return request.put('/admin/books/batch/off-sale', bookIds)
  },
  
  updateStock(bookId, stock) {
    return request.put(`/admin/books/${bookId}/stock`, null, { params: { stock } })
  },
  
  getBookStatistics() {
    return request.get('/admin/books/statistics')
  },
  
  getHotBooks(limit = 10) {
    return request.get('/admin/books/hot', { params: { limit } })
  },
  
  getNewBooks(limit = 10) {
    return request.get('/admin/books/new', { params: { limit } })
  },

  // ========== 分类管理 ==========
  getCategories() {
    return request.get('/admin/categories')
  },
  
  getCategoryById(categoryId) {
    return request.get(`/admin/categories/${categoryId}`)
  },
  
  createCategory(data) {
    return request.post('/admin/categories', data)
  },
  
  updateCategory(categoryId, data) {
    return request.put(`/admin/categories/${categoryId}`, data)
  },
  
  deleteCategory(categoryId) {
    return request.delete(`/admin/categories/${categoryId}`)
  },
  
  batchDeleteCategories(categoryIds) {
    return request.delete('/admin/categories/batch', { data: categoryIds })
  },
  
  updateCategorySort(categoryId, sortOrder) {
    return request.put(`/admin/categories/${categoryId}/sort`, null, { params: { sortOrder } })
  },
  
  getCategoryBookCount(categoryId) {
    return request.get(`/admin/categories/${categoryId}/book-count`)
  },

  // ========== 订单管理 ==========
  getOrders(params) {
    return request.get('/admin/orders', { params })
  },
  
  getOrderDetail(orderId) {
    return request.get(`/admin/orders/${orderId}`)
  },
  
  deliverOrder(orderId) {
    return request.put(`/admin/orders/${orderId}/deliver`)
  },
  
  batchDeliverOrders(orderIds) {
    return request.put('/admin/orders/batch/deliver', orderIds)
  },
  
  cancelOrderByAdmin(orderId) {
    return request.put(`/admin/orders/${orderId}/cancel`)
  },
  
  batchCancelOrders(orderIds) {
    return request.put('/admin/orders/batch/cancel', orderIds)
  },
  
  updateOrderRemark(orderId, remark) {
    return request.put(`/admin/orders/${orderId}/remark`, null, { params: { remark } })
  },
  
  updateReceiverInfo(orderId, data) {
    return request.put(`/admin/orders/${orderId}/receiver`, data)
  },
  
  getOrderStatistics() {
    return request.get('/admin/orders/statistics')
  },
  
  getOrderStatusCount() {
    return request.get('/admin/orders/statistics/status')
  },
  
  getTodayOrderStatistics() {
    return request.get('/admin/orders/statistics/today')
  },
  
  exportOrders(params) {
    return request.get('/admin/orders/export', { params, responseType: 'blob' })
  }
}