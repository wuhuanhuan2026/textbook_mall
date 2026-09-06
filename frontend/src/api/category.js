import request from './request'

/**
 * 分类相关 API
 */
export const categoryApi = {
  /**
   * 获取所有分类
   */
  getCategories() {
    return request.get('/categories')
  },

  /**
   * 获取分类详情
   * @param {number} categoryId
   */
  getCategoryById(categoryId) {
    return request.get(`/categories/${categoryId}`)
  }
}