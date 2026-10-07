<template>
  <div class="book-detail" v-loading="loading">
    <div v-if="book" class="detail-container">
      <!-- 面包屑 -->
      <div class="breadcrumb">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item :to="{ path: '/books' }">全部教材</el-breadcrumb-item>
          <el-breadcrumb-item>{{ book.bookName || '教材详情' }}</el-breadcrumb-item>
        </el-breadcrumb>
      </div>

      <!-- 主体 -->
      <div class="detail-main">
        <!-- 封面 -->
        <div class="detail-cover">
          <img :src="book.coverImage || '/default-cover.jpg'" :alt="book.bookName || '教材封面'" />
          <div class="cover-badges">
            <span class="badge discount" v-if="discount > 0 && discount < 10">
              {{ discount }}折
            </span>
            <span class="badge sold-out" v-if="book.stock === 0">已售罄</span>
          </div>
        </div>

        <!-- 信息 -->
        <div class="detail-info">
          <h1 class="book-title">{{ book.bookName || '未知书名' }}</h1>
          
          <div class="book-meta">
            <span>作者：{{ book.author || '未知' }}</span>
            <span>出版社：{{ book.publisher || '未知' }}</span>
            <span>ISBN：{{ book.isbn || '暂无' }}</span>
            <span>分类：{{ book.categoryName || '未分类' }}</span>
          </div>

          <div class="book-price-section">
            <div class="price-group">
              <span class="label">售价</span>
              <span class="price">¥{{ formatPrice(book.price) }}</span>
              <span class="original-price" v-if="book.originalPrice && Number(book.originalPrice) > Number(book.price)">
                原价 ¥{{ formatPrice(book.originalPrice) }}
              </span>
            </div>
            <div class="sales-group">
              <span>已售 <strong>{{ book.sales || 0 }}</strong> 件</span>
              <span>库存 <strong :class="{ 'low-stock': book.stock < 10 }">{{ book.stock || 0 }}</strong> 件</span>
            </div>
          </div>

          <div class="book-action">
            <el-input-number
              v-model="quantity"
              :min="1"
              :max="book.stock || 1"
              size="large"
              style="width: 120px; margin-right: 16px;"
            />
            <el-button 
              type="primary" 
              size="large" 
              :disabled="!canPurchase || submitting"
              :loading="submitting"
              @click="handleAddToCart"
            >
              {{ submitting ? '提交中...' : (canPurchase ? '加入购物车' : disabledReason) }}
            </el-button>
          </div>

          <div class="book-tips" v-if="book.stock > 0">
            <el-alert 
              :title="`库存充足，可放心购买`" 
              type="success" 
              :closable="false"
              v-if="book.stock > 20"
            />
            <el-alert 
              :title="`仅剩 ${book.stock} 件，欲购从速`" 
              type="warning" 
              :closable="false"
              v-else-if="book.stock > 0"
            />
          </div>
        </div>
      </div>

      <!-- 详情描述 -->
      <div class="detail-description">
        <h3>📖 教材简介</h3>
        <div class="description-content">
          {{ book.description || '暂无简介' }}
        </div>
      </div>

      <!-- 评价 -->
      <div class="detail-reviews">
        <h3>⭐ 用户评价</h3>
        <div v-if="reviews.length > 0">
          <div class="review-item" v-for="review in reviews" :key="review.reviewId">
            <div class="review-header">
              <span class="review-user">{{ review.username || '匿名用户' }}</span>
              <span class="review-rating">
                {{ '⭐'.repeat(review.rating || 0) }}
              </span>
              <span class="review-time">{{ review.createTime || '' }}</span>
            </div>
            <p class="review-content">{{ review.content || '暂无评价内容' }}</p>
          </div>
          <div class="review-more" v-if="reviewTotal > reviews.length">
            <el-link type="primary" @click="loadMoreReviews">加载更多评价</el-link>
          </div>
        </div>
        <el-empty v-else description="暂无评价，快来留下您的评价吧" :image-size="80" />
      </div>
    </div>
    
    <el-empty v-else-if="!loading" description="教材不存在" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useBookStore } from '@/stores/book'
import { useCartStore } from '@/stores/cart'
import { useUserStore } from '@/stores/user'
// ✅ 实训11：注释掉 reviewApi，改用 Mock 评价
// import { reviewApi } from '@/api/review'

const route = useRoute()
const router = useRouter()
const bookStore = useBookStore()
const cartStore = useCartStore()
const userStore = useUserStore()

const quantity = ref(1)
const reviews = ref([])
const reviewPage = ref(1)
const reviewTotal = ref(0)
const loading = ref(false)
const book = ref(null)

// ============ 实训11 新增：按钮状态与防重复提交 ============
const submitting = ref(false)

const disabledReason = computed(() => {
  if (!userStore.isLoggedIn) return '请先登录'
  if (book.value?.status !== 'PUBLISHED') return '教材未上架'
  if (book.value?.stock <= 0) return '库存不足'
  return ''
})

const canPurchase = computed(() => !disabledReason.value)
// ========================================================

// 安全格式化价格
const formatPrice = (value) => {
  if (value === undefined || value === null || isNaN(value)) {
    return '0.00'
  }
  return Number(value).toFixed(2)
}

const discount = computed(() => {
  if (!book.value?.originalPrice || book.value.originalPrice === 0) return 0
  const price = Number(book.value.price) || 0
  const originalPrice = Number(book.value.originalPrice) || 0
  if (price === 0 || originalPrice === 0) return 0
  return Math.round((price / originalPrice) * 10)
})

const fetchBookDetail = async () => {
  const bookId = Number(route.params.id)
  if (!bookId) {
    ElMessage.error('参数错误')
    router.push('/books')
    return
  }
  
  loading.value = true
  try {
    const result = await bookStore.getBookDetail(bookId)
    console.log('图书详情响应:', result)
    
    if (result && result.code === 200) {
      book.value = result.data || null
    } else if (result && result.bookId) {
      book.value = result
    } else {
      book.value = null
    }
    
    // 确保价格是数字
    if (book.value) {
      if (book.value.price !== undefined) {
        book.value.price = Number(book.value.price) || 0
      }
      if (book.value.originalPrice !== undefined) {
        book.value.originalPrice = Number(book.value.originalPrice) || 0
      }
    }
  } catch (error) {
    console.error('获取图书详情失败:', error)
    book.value = null
  } finally {
    loading.value = false
  }
}

/**
 * ✅ 实训11：改用 Mock 评价数据，不依赖后端
 */
const fetchReviews = async (append = false) => {
  if (!book.value) return
  try {
    // 模拟延迟
    await new Promise(resolve => setTimeout(resolve, 300))

    const mockReviews = [
      {
        reviewId: 1,
        username: '张三',
        rating: 5,
        content: '这本书讲得非常详细，强烈推荐！',
        createTime: '2026-09-15 10:00:00'
      },
      {
        reviewId: 2,
        username: '李四',
        rating: 4,
        content: '内容很全面，适合入门。',
        createTime: '2026-09-18 14:30:00'
      }
    ]

    if (append) {
      reviews.value = [...reviews.value, ...mockReviews]
    } else {
      reviews.value = mockReviews
    }
    reviewTotal.value = mockReviews.length
  } catch (error) {
    console.error('获取评价失败:', error)
    if (!append) reviews.value = []
  }
}

const loadMoreReviews = () => {
  reviewPage.value++
  fetchReviews(true)
}

/**
 * ✅ 加入购物车（唯一提示源）
 * Store 层不弹提示，只负责数据操作
 * 实训11：加 canPurchase 判断 + 防重复提交
 */
const handleAddToCart = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  if (!book.value) {
    ElMessage.error('教材信息不存在')
    return
  }
  if (!canPurchase.value) {
    ElMessage.warning(disabledReason.value)
    return
  }
  if (submitting.value) return
  submitting.value = true
  try {
    await cartStore.addToCart(book.value.bookId, quantity.value)
    ElMessage.success('已加入购物车')  // ✅ 唯一提示源
  } catch (error) {
    ElMessage.error(error.message || '加入购物车失败')
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  await fetchBookDetail()
  if (book.value) {
    fetchReviews()
  }
})
</script>

<style scoped>
.book-detail {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.detail-container {
  background: #ffffff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.breadcrumb {
  margin-bottom: 24px;
}

.detail-main {
  display: flex;
  gap: 40px;
  margin-bottom: 32px;
}

.detail-cover {
  flex-shrink: 0;
  width: 340px;
  position: relative;
  border-radius: 8px;
  overflow: hidden;
  background: #f5f7fa;
}

.detail-cover img {
  width: 100%;
  height: auto;
  display: block;
}

.cover-badges {
  position: absolute;
  top: 12px;
  right: 12px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.badge {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 13px;
  font-weight: 600;
  color: #ffffff;
}

.badge.discount {
  background: #f56c6c;
}

.badge.sold-out {
  background: #909399;
}

.detail-info {
  flex: 1;
}

.book-title {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  margin: 0 0 16px 0;
}

.book-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 16px 32px;
  font-size: 14px;
  color: #909399;
  padding: 12px 0;
  border-top: 1px solid #ebeef5;
  border-bottom: 1px solid #ebeef5;
  margin-bottom: 20px;
}

.book-price-section {
  margin-bottom: 24px;
}

.price-group {
  display: flex;
  align-items: baseline;
  gap: 12px;
  margin-bottom: 8px;
}

.price-group .label {
  font-size: 14px;
  color: #909399;
}

.price-group .price {
  font-size: 32px;
  font-weight: 700;
  color: #f56c6c;
}

.price-group .original-price {
  font-size: 16px;
  color: #c0c4cc;
  text-decoration: line-through;
}

.sales-group {
  display: flex;
  gap: 24px;
  font-size: 14px;
  color: #909399;
}

.sales-group strong {
  color: #303133;
}

.sales-group .low-stock {
  color: #f56c6c;
}

.book-action {
  display: flex;
  align-items: center;
  margin: 20px 0 12px;
}

.book-tips {
  margin-top: 8px;
}

.detail-description {
  margin: 32px 0;
  padding-top: 24px;
  border-top: 1px solid #ebeef5;
}

.detail-description h3 {
  font-size: 18px;
  color: #303133;
  margin-bottom: 12px;
}

.description-content {
  font-size: 15px;
  color: #606266;
  line-height: 1.8;
  padding: 16px 20px;
  background: #f5f7fa;
  border-radius: 8px;
}

.detail-reviews {
  padding-top: 24px;
  border-top: 1px solid #ebeef5;
}

.detail-reviews h3 {
  font-size: 18px;
  color: #303133;
  margin-bottom: 16px;
}

.review-item {
  padding: 12px 16px;
  border-bottom: 1px solid #f0f0f0;
}

.review-item:last-child {
  border-bottom: none;
}

.review-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 6px;
}

.review-user {
  font-weight: 500;
  color: #303133;
  font-size: 14px;
}

.review-rating {
  font-size: 14px;
}

.review-time {
  font-size: 12px;
  color: #c0c4cc;
  margin-left: auto;
}

.review-content {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  margin: 0;
}

.review-more {
  text-align: center;
  padding: 12px 0;
}

@media (max-width: 768px) {
  .detail-main {
    flex-direction: column;
    gap: 20px;
  }
  
  .detail-cover {
    width: 100%;
    max-width: 300px;
    margin: 0 auto;
  }
  
  .book-title {
    font-size: 22px;
  }
  
  .price-group .price {
    font-size: 26px;
  }
}
</style>