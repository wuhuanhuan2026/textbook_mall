import request from './request'

/**
 * 用户相关 API
 */
export const userApi = {
  /**
   * 用户登录
   * @param {Object} data - { username, password }
   */
  login(data) {
    return request.post('/user/login', data)
  },

  /**
   * 用户注册
   * @param {Object} data - { username, password, phone, email, captcha }
   */
  register(data) {
    return request.post('/user/register', data)
  },

  /**
   * 用户退出登录
   */
  logout() {
    return request.post('/user/logout')
  },

  /**
   * 获取当前用户信息
   */
  getUserInfo() {
    return request.get('/user/info')
  },

  /**
   * 获取指定用户信息（内部调用）
   * @param {number} userId
   */
  getUserInfoById(userId) {
    return request.get(`/user/info/${userId}`)
  },

  /**
   * 修改密码
   * @param {Object} data - { oldPassword, newPassword }
   */
  updatePassword(data) {
    return request.put('/user/password', data)
  },

  /**
   * 修改用户信息
   * @param {Object} data - { phone, email, avatar }
   */
  updateUserInfo(data) {
    return request.put('/user/info', data)
  },

  /**
   * 检查用户名是否可用
   * @param {string} username
   */
  checkUsername(username) {
    return request.get('/user/check-username', { params: { username } })
  },

  /**
   * 检查用户是否存在（内部调用）
   * @param {number} userId
   */
  checkUserExists(userId) {
    return request.get(`/user/exists/${userId}`)
  }
}