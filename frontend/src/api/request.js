// frontend/src/api/request.js
import axios from 'axios'
import { 
  getToken, 
  getAdminToken, 
  removeToken, 
  removeUser, 
  removeAdminToken, 
  removeAdminInfo,
  clearAll
} from '@/utils/storage'
import { ElMessage } from 'element-plus'

/**
 * Axios 实例配置
 * baseURL: /api，与 Gateway 路由匹配
 * Gateway 的 StripPrefix=1 会移除 /api 前缀，转发到后端服务
 */
const request = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

/**
 * 请求拦截器
 * 在请求发送前自动添加 Token
 * 优先使用管理员Token，其次使用用户Token
 */
request.interceptors.request.use(
  (config) => {
    // 1. 优先使用管理员Token
    let token = getAdminToken()
    
    // 2. 如果没有管理员Token，使用普通用户Token
    if (!token) {
      token = getToken()
    }
    
    // 3. 如果有Token，添加到请求头
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
      // ✅ 添加调试日志（开发环境）
      if (process.env.NODE_ENV === 'development') {
        console.log(`📤 [${config.method.toUpperCase()}] ${config.url} - 已携带Token`)
      }
    } else {
      // ✅ 添加调试日志（开发环境）
      if (process.env.NODE_ENV === 'development') {
        console.log(`📤 [${config.method.toUpperCase()}] ${config.url} - 无Token`)
      }
    }
    return config
  },
  (error) => {
    console.error('❌ 请求拦截器错误:', error)
    return Promise.reject(error)
  }
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
      return response.data
    }
    
    // Token 过期或未登录 (401 / 1010)
    if (code === 401 || code === 1010) {
      ElMessage.error('登录已过期，请重新登录')
      
      // 清除所有认证信息（普通用户 + 管理员）
      clearAll()
      
      // 根据当前路径跳转到对应登录页
      const currentPath = window.location.pathname
      if (currentPath.startsWith('/admin')) {
        window.location.href = '/admin/login'
      } else if (currentPath !== '/login' && currentPath !== '/register') {
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
    
    console.error('❌ HTTP错误:', status, message)
    
    if (status === 401) {
      ElMessage.error('登录已过期，请重新登录')
      
      // 清除所有认证信息
      clearAll()
      
      // 根据当前路径跳转到对应登录页
      const currentPath = window.location.pathname
      if (currentPath.startsWith('/admin')) {
        window.location.href = '/admin/login'
      } else if (currentPath !== '/login' && currentPath !== '/register') {
        window.location.href = '/login'
      }
      
    } else if (status === 403) {
      ElMessage.error('权限不足，请联系管理员')
    } else if (status === 404) {
      ElMessage.error('请求的资源不存在')
    } else if (status === 500) {
      ElMessage.error('服务器内部错误，请稍后重试')
    } else if (status === 502 || status === 503 || status === 504) {
      ElMessage.error('服务暂时不可用，请稍后重试')
    } else {
      ElMessage.error(message)
    }
    
    return Promise.reject(error)
  }
)

export default request