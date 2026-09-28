// frontend/src/utils/storage.js

// ============ 普通用户存储Key ============
const USER_TOKEN_KEY = 'book_mall_token'
const USER_INFO_KEY = 'book_mall_user'

// ============ 管理员存储Key ============
const ADMIN_TOKEN_KEY = 'admin_token'
const ADMIN_INFO_KEY = 'admin_info'

// ==================== 普通用户 ====================

/**
 * 获取用户 Token
 */
export const getToken = () => localStorage.getItem(USER_TOKEN_KEY)

/**
 * 设置用户 Token
 */
export const setToken = (token) => localStorage.setItem(USER_TOKEN_KEY, token)

/**
 * 删除用户 Token
 */
export const removeToken = () => localStorage.removeItem(USER_TOKEN_KEY)

/**
 * 获取用户信息
 */
export const getUser = () => {
  const data = localStorage.getItem(USER_INFO_KEY)
  return data ? JSON.parse(data) : null
}

/**
 * 设置用户信息
 */
export const setUser = (user) => localStorage.setItem(USER_INFO_KEY, JSON.stringify(user))

/**
 * 删除用户信息
 */
export const removeUser = () => localStorage.removeItem(USER_INFO_KEY)

// ==================== 管理员 ====================

/**
 * 获取管理员 Token
 */
export const getAdminToken = () => localStorage.getItem(ADMIN_TOKEN_KEY)

/**
 * 设置管理员 Token
 */
export const setAdminToken = (token) => localStorage.setItem(ADMIN_TOKEN_KEY, token)

/**
 * 删除管理员 Token
 */
export const removeAdminToken = () => localStorage.removeItem(ADMIN_TOKEN_KEY)

/**
 * 获取管理员信息
 */
export const getAdminInfo = () => {
  const data = localStorage.getItem(ADMIN_INFO_KEY)
  return data ? JSON.parse(data) : null
}

/**
 * 设置管理员信息
 */
export const setAdminInfo = (info) => localStorage.setItem(ADMIN_INFO_KEY, JSON.stringify(info))

/**
 * 删除管理员信息
 */
export const removeAdminInfo = () => localStorage.removeItem(ADMIN_INFO_KEY)

// ==================== 通用工具 ====================

/**
 * 清除所有用户相关存储（普通用户 + 管理员）
 */
export const clearAll = () => {
  localStorage.removeItem(USER_TOKEN_KEY)
  localStorage.removeItem(USER_INFO_KEY)
  localStorage.removeItem(ADMIN_TOKEN_KEY)
  localStorage.removeItem(ADMIN_INFO_KEY)
}

/**
 * 清除普通用户存储
 */
export const clearUser = () => {
  localStorage.removeItem(USER_TOKEN_KEY)
  localStorage.removeItem(USER_INFO_KEY)
}

/**
 * 清除管理员存储
 */
export const clearAdmin = () => {
  localStorage.removeItem(ADMIN_TOKEN_KEY)
  localStorage.removeItem(ADMIN_INFO_KEY)
}