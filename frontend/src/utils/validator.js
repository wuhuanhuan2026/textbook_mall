/**
 * 表单验证工具
 */

/**
 * 验证用户名：3-20位字母、数字、下划线
 */
export const validateUsername = (val) => /^[a-zA-Z0-9_]{3,20}$/.test(val)

/**
 * 验证密码：6-20位
 */
export const validatePassword = (val) => val.length >= 6 && val.length <= 20

/**
 * 验证手机号：中国大陆手机号
 */
export const validatePhone = (val) => /^1[3-9]\d{9}$/.test(val)

/**
 * 验证邮箱
 */
export const validateEmail = (val) => /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(val)

/**
 * 验证验证码：4位数字
 */
export const validateCaptcha = (val) => /^\d{4}$/.test(val)