import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getToken, setToken, removeToken, getUser, setUser, removeUser } from '@/utils/storage'
import { userApi } from '@/api/user'

/**
 * 用户状态管理
 */
export const useUserStore = defineStore('user', () => {
  // ========== State ==========
  const token = ref(getToken() || '')
  const userInfo = ref(getUser())
  
  // ========== Getters ==========
  const isLoggedIn = computed(() => !!token.value)

  // ========== Actions ==========
  /**
   * 设置认证信息
   */
  const setAuth = (data) => {
    token.value = data.token
    setToken(data.token)
    userInfo.value = {
      userId: data.userId,
      username: data.username,
      avatar: data.avatar || '',
      phone: data.phone || '',
      email: data.email || '',
      balance: data.balance || 0,
      status: data.status || 1,
      createTime: data.createTime || ''
    }
    setUser(userInfo.value)
  }

  /**
   * 清除认证信息
   */
  const clearAuth = () => {
    token.value = ''
    userInfo.value = null
    removeToken()
    removeUser()
  }

  /**
   * 登录
   */
  const login = async (params) => {
    const res = await userApi.login(params)
    // 检查响应是否成功
    if (res.code === 200 && res.data) {
      setAuth(res.data)
      return res
    } else {
      throw new Error(res.message || '登录失败')
    }
  }

  /**
   * 退出登录
   */
  const logout = async () => {
    try {
      await userApi.logout()
    } catch (error) {
      console.error('退出请求失败:', error)
    } finally {
      clearAuth()
    }
  }

  /**
   * 获取用户信息
   */
  const fetchUserInfo = async () => {
    try {
      const res = await userApi.getUserInfo()
      if (res.code === 200 && res.data) {
        userInfo.value = res.data
        setUser(res.data)
        return res.data
      }
      return null
    } catch (error) {
      console.error('获取用户信息失败:', error)
      throw error
    }
  }

  /**
   * 更新用户信息
   */
  const updateUserInfo = async (params) => {
    try {
      const res = await userApi.updateUserInfo(params)
      if (res.code === 200 && res.data) {
        userInfo.value = { ...userInfo.value, ...res.data }
        setUser(userInfo.value)
        return res.data
      }
      throw new Error(res.message || '更新失败')
    } catch (error) {
      console.error('更新用户信息失败:', error)
      throw error
    }
  }

  return {
    // State
    token,
    userInfo,
    // Getters
    isLoggedIn,
    // Actions
    login,
    logout,
    fetchUserInfo,
    updateUserInfo,
    clearAuth
  }
})