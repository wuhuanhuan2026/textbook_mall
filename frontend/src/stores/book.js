import { defineStore } from 'pinia'
import { ref } from 'vue'
import { bookApi } from '@/api/book'

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
   */
  const getBookDetail = async (bookId) => {
    loading.value = true
    try {
      const book = await bookApi.getBookDetail(bookId)
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