// src/api/admin.js
import request from './request'

export const adminApi = {
  login(data) {
    return request.post('/admin/login', data)
  },
  
  logout() {
    return request.post('/admin/logout')
  },
  
  getAdminInfo() {
    return request.get('/admin/info')
  }
}