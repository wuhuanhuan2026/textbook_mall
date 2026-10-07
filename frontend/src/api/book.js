import request from './request'
import { books, categories } from '@/mocks/books'
import { mockRequest, paginate } from '@/mocks/mockApi'

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

// ============ 实训11 新增：Mock 接口（不依赖后端） ============

/**
 * 获取教材列表（分页，Mock）
 * @param {Object} params - { pageNum, pageSize, keyword, status, categoryId }
 */
export async function listBooks(params = {}) {
  return mockRequest(() => {
    const keyword = params.keyword?.trim() || ''
    const status = params.status || ''
    const categoryId = params.categoryId || null

    const records = books.filter(item => {
      if (keyword && !item.bookName.includes(keyword) && !item.author.includes(keyword)) {
        return false
      }
      if (status && item.status !== status) return false
      if (categoryId && item.categoryId !== Number(categoryId)) return false
      return true
    })

    return paginate(records, params.pageNum || 1, params.pageSize || 8)
  }, { delay: 500 })
}

/**
 * 获取教材详情（Mock）
 */
export async function getBook(id) {
  return mockRequest(() => {
    return books.find(item => item.bookId === Number(id)) || null
  }, { delay: 500 })
}

/**
 * 新增教材（Mock）
 */
export async function createBook(data) {
  return mockRequest(() => {
    const newBook = {
      bookId: books.length + 1,
      ...data,
      sales: 0
    }
    books.push(newBook)
    return newBook
  }, { delay: 500 })
}

/**
 * 修改教材（Mock）
 */
export async function updateBook(id, data) {
  return mockRequest(() => {
    const book = books.find(item => item.bookId === Number(id))
    if (!book) throw new Error('教材不存在')
    Object.assign(book, data)
    return book
  }, { delay: 500 })
}

/**
 * 购买教材（Mock）
 */
export async function purchaseBook(id) {
  return mockRequest(() => {
    const book = books.find(item => item.bookId === Number(id))
    if (!book) throw new Error('教材不存在')
    if (book.stock <= 0) throw new Error('库存不足')
    book.stock -= 1
    book.sales += 1
    return book
  }, { delay: 500 })
}

/**
 * 获取分类列表（Mock）
 */
export async function listCategories() {
  return mockRequest(() => categories, { delay: 300 })
}