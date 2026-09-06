import request from './request'

/**
 * 教材相关 API
 */
export const bookApi = {
  /**
   * 获取教材列表（分页）
   * @param {Object} params - { pageNum, pageSize, keyword, categoryId, sortBy }
   */
  getBookList(params) {
    return request.get('/books', { params })
  },

  /**
   * 获取教材详情
   * @param {number} bookId
   */
  getBookDetail(bookId) {
    return request.get(`/books/${bookId}`)
  },

  /**
   * 获取热门教材
   * @param {number} limit - 数量限制
   */
  getHotBooks(limit = 10) {
    return request.get('/books/hot', { params: { limit } })
  },

  /**
   * 获取新品教材
   * @param {number} limit - 数量限制
   */
  getNewBooks(limit = 10) {
    return request.get('/books/new', { params: { limit } })
  },

  /**
   * 按分类获取教材
   * @param {number} categoryId
   * @param {Object} params - { pageNum, pageSize }
   */
  getBooksByCategory(categoryId, params) {
    return request.get(`/books/category/${categoryId}`, { params })
  },

  /**
   * 搜索教材
   * @param {string} keyword
   * @param {Object} params - { pageNum, pageSize }
   */
  searchBooks(keyword, params) {
    return request.get('/books/search', { params: { keyword, ...params } })
  }
}