import request from './request'

/**
 * 评价相关 API
 */
export const reviewApi = {
  /**
   * 创建评价
   * @param {Object} data - { bookId, orderId, rating, content }
   */
  createReview(data) {
    return request.post('/review', data)
  },

  /**
   * 获取教材评价列表
   * @param {number} bookId
   * @param {number} pageNum
   * @param {number} pageSize
   */
  getBookReviews(bookId, pageNum = 1, pageSize = 10) {
    return request.get(`/review/book/${bookId}`, { params: { pageNum, pageSize } })
  },

  /**
   * 获取用户评价列表
   */
  getUserReviews(pageNum = 1, pageSize = 10) {
    return request.get('/review/user', { params: { pageNum, pageSize } })
  },

  /**
   * 获取评价详情
   * @param {number} reviewId
   */
  getReviewDetail(reviewId) {
    return request.get(`/review/${reviewId}`)
  },

  /**
   * 删除评价
   * @param {number} reviewId
   */
  deleteReview(reviewId) {
    return request.delete(`/review/${reviewId}`)
  }
}