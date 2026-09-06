/**
 * 本地存储工具
 * 用于存取 Token 和用户信息
 */

const TOKEN_KEY = 'book_mall_token'
const USER_KEY = 'book_mall_user'

/**
 * 获取 Token
 */
export const getToken = () => localStorage.getItem(TOKEN_KEY)

/**
 * 设置 Token
 */
export const setToken = (token) => localStorage.setItem(TOKEN_KEY, token)

/**
 * 删除 Token
 */
export const removeToken = () => localStorage.removeItem(TOKEN_KEY)

/**
 * 获取用户信息
 */
export const getUser = () => {
  const data = localStorage.getItem(USER_KEY)
  return data ? JSON.parse(data) : null
}

/**
 * 设置用户信息
 */
export const setUser = (user) => localStorage.setItem(USER_KEY, JSON.stringify(user))

/**
 * 删除用户信息
 */
export const removeUser = () => localStorage.removeItem(USER_KEY)