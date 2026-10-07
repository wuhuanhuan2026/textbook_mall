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

// ============ 实训15：Mock 接口全部替换为真实接口 ============

/**
 * 获取教材列表（分页，真实接口）
 * 适配 Books.vue 中 `const data = await listBooks(params)` 的调用方式
 * 返回：{ records, total, pageNum, pageSize }
 */
export async function listBooks(params = {}) {
  const res = await request.get('/books', { params })
  if (res && res.code === 200 && res.data) {
    return {
      records: res.data.list || [],
      total: res.data.total || 0,
      pageNum: res.data.pageNum,
      pageSize: res.data.pageSize
    }
  }
  throw new Error(res?.message || '获取教材列表失败')
}

/**
 * 获取教材详情（真实接口）
 * 返回单个 book 对象，与实训11 的 Mock 保持一致
 */
export async function getBook(id) {
  const res = await request.get(`/books/${id}`)
  if (res && res.code === 200) {
    return res.data || null
  }
  throw new Error(res?.message || '获取教材详情失败')
}

/**
 * 新增教材（真实接口）
 * 管理端可调用，实训15 暂不重点验证
 */
export async function createBook(data) {
  const res = await request.post('/books', data)
  if (res && res.code === 200) {
    return res.data
  }
  throw new Error(res?.message || '新增教材失败')
}

/**
 * 修改教材（真实接口）
 */
export async function updateBook(id, data) {
  const res = await request.put(`/books/${id}`, data)
  if (res && res.code === 200) {
    return res.data
  }
  throw new Error(res?.message || '修改教材失败')
}

/**
 * 购买教材（真实接口）
 * 实训15 用加入购物车替代，此函数保留兼容
 */
export async function purchaseBook(id) {
  const res = await request.post(`/books/${id}/deduct-stock`, null, {
    params: { quantity: 1 }
  })
  if (res && res.code === 200) {
    return res.data
  }
  throw new Error(res?.message || '购买失败')
}

/**
 * 获取分类列表（真实接口）
 * 适配 Books.vue 中 `const res = await listCategories()` 的调用
 * 注意：Book 服务的分类接口走网关是 /api/categories
 */
export async function listCategories() {
  const res = await request.get('/categories')
  if (res && res.code === 200) {
    return res.data || []
  }
  throw new Error(res?.message || '获取分类失败')
}