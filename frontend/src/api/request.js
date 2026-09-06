import axios from 'axios'
import { getToken, removeToken, removeUser } from '@/utils/storage'
import { ElMessage } from 'element-plus'

/**
 * Axios 实例配置
 * baseURL: 从环境变量读取，默认 /api
 * timeout: 10秒超时
 */
const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

/**
 * 请求拦截器
 * 在请求发送前自动添加 Token
 */
request.interceptors.request.use(
  (config) => {
    const token = getToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

/**
 * 响应拦截器
 * 统一处理响应数据、错误提示、Token 过期
 */
request.interceptors.response.use(
  (response) => {
    const { code, message, data } = response.data
    
    // 成功响应
    if (code === 200) {
      // 返回完整响应数据（包含 data 字段）
      // 这样调用方可以通过 res.data 或 res.message 获取
      return response.data
    }
    
    // Token 过期或未登录 (401 / 1010)
    if (code === 401 || code === 1010) {
      ElMessage.error('登录已过期，请重新登录')
      removeToken()
      removeUser()
      // 跳转到登录页
      if (window.location.pathname !== '/login' && window.location.pathname !== '/admin/login') {
        window.location.href = '/login'
      }
      return Promise.reject(new Error('Unauthorized'))
    }
    
    // 业务错误
    ElMessage.error(message || '请求失败')
    return Promise.reject(new Error(message))
  },
  (error) => {
    // HTTP 错误
    const status = error.response?.status
    const message = error.response?.data?.message || error.message || '网络错误，请稍后重试'
    
    if (status === 401) {
      ElMessage.error('登录已过期，请重新登录')
      removeToken()
      removeUser()
      if (window.location.pathname !== '/login' && window.location.pathname !== '/admin/login') {
        window.location.href = '/login'
      }
    } else if (status === 403) {
      ElMessage.error('权限不足，请联系管理员')
    } else if (status === 404) {
      ElMessage.error('请求的资源不存在')
    } else if (status === 500) {
      ElMessage.error('服务器内部错误，请稍后重试')
    } else {
      ElMessage.error(message)
    }
    
    return Promise.reject(error)
  }
)

export default request