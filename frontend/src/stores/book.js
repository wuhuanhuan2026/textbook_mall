import { defineStore } from 'pinia'
import { ref } from 'vue'
import { bookApi, getBook } from '@/api/book'

/**
 * 教材状态管理
 */
export const useBookStore = defineStore('book', () => {
  // ========== State ==========
  const currentBook = ref(null)
  const loading = ref(false)

  // ========== Actions ==========
  /**
   * 获取教材详情
   *
   * 实训11：改用 Mock 接口 getBook，实现前端独立闭环
   * 原实现：bookApi.getBookDetail(bookId) —— 走真实 Axios，需要后端
   * 现实现：getBook(bookId) —— 走 Mock 数据，不依赖后端
   */
  const getBookDetail = async (bookId) => {
    loading.value = true
    try {
      // ✅ 实训11：改用 Mock 接口，实现前端独立于后端的演示闭环
      const book = await getBook(bookId)
      currentBook.value = book
      return book
    } finally {
      loading.value = false
    }
  }

  return {
    currentBook,
    loading,
    getBookDetail
  }
})