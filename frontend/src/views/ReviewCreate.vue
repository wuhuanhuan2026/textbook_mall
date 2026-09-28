<template>
  <div class="review-create" v-loading="loading">
    <div class="page-header">
      <h1 class="page-title">✍️ 发表评价</h1>
      <el-button type="text" @click="goBack">
        <el-icon><ArrowLeft /></el-icon> 返回订单详情
      </el-button>
    </div>

    <div v-if="orderInfo" class="review-container">
      <!-- 订单信息 -->
      <div class="section order-section">
        <h3>📦 订单信息</h3>
        <div class="order-info">
          <span>订单号：{{ orderInfo.orderId }}</span>
          <span>下单时间：{{ orderInfo.createTime }}</span>
          <span>实付金额：¥{{ (orderInfo.totalAmount || 0).toFixed(2) }}</span>
        </div>
      </div>

      <!-- 商品列表 -->
      <div class="section book-section">
        <h3>📖 选择评价商品</h3>
        <div class="book-list">
          <div 
            class="book-item" 
            v-for="item in orderItems" 
            :key="item.bookId"
            :class="{ active: selectedBookId === item.bookId }"
            @click="selectBook(item)"
          >
            <img :src="item.bookCover || '/default-cover.jpg'" :alt="item.bookName" />
            <div class="book-info">
              <div class="book-name">{{ item.bookName }}</div>
              <div class="book-meta">
                <span>¥{{ (item.price || 0).toFixed(2) }} × {{ item.quantity }}</span>
                <el-tag 
                  v-if="hasReviewed(item.bookId)" 
                  size="small" 
                  type="success"
                >
                  已评价
                </el-tag>
              </div>
            </div>
            <el-radio 
              :model-value="selectedBookId" 
              :label="item.bookId"
              :disabled="hasReviewed(item.bookId)"
            >
              {{ '' }}
            </el-radio>
          </div>
        </div>
        <el-empty v-if="orderItems.length === 0" description="该订单暂无商品" />
      </div>

      <!-- 评价表单 -->
      <div class="section review-section" v-if="selectedBookId">
        <h3>⭐ 评价内容</h3>
        
        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-width="80px"
        >
          <el-form-item label="评分" prop="rating">
            <el-rate 
              v-model="form.rating" 
              :texts="ratingTexts"
              show-text
              :colors="ratingColors"
              size="large"
            />
          </el-form-item>

          <el-form-item label="评价内容" prop="content">
            <el-input
              v-model="form.content"
              type="textarea"
              :rows="4"
              placeholder="请分享您的使用感受，帮助其他同学选择教材..."
              maxlength="500"
              show-word-limit
            />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" size="large" :loading="submitting" @click="submitReview">
              提交评价
            </el-button>
            <el-button size="large" @click="resetForm">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 未选择商品 -->
      <el-empty v-else description="请选择要评价的商品" :image-size="80" />
    </div>

    <el-empty v-else-if="!loading" description="订单不存在" />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import { orderApi } from '@/api/order'
import { reviewApi } from '@/api/review'

const route = useRoute()
const router = useRouter()

// ============ 状态 ============
const loading = ref(false)
const submitting = ref(false)
const orderInfo = ref(null)
const orderItems = ref([])
const reviewedBooks = ref([])  // 已评价的书籍ID列表
const selectedBookId = ref(null)
const selectedBook = ref(null)
const formRef = ref()

// ============ 表单 ============
const form = reactive({
  rating: 5,
  content: ''
})

const rules = {
  rating: [
    { required: true, message: '请选择评分', trigger: 'change' }
  ],
  content: [
    { required: true, message: '请输入评价内容', trigger: 'blur' },
    { min: 5, message: '评价内容至少5个字', trigger: 'blur' },
    { max: 500, message: '评价内容不能超过500个字', trigger: 'blur' }
  ]
}

const ratingTexts = ['很差', '较差', '一般', '满意', '非常满意']
const ratingColors = ['#F56C6C', '#E6A23C', '#909399', '#67C23A', '#409EFF']

// ============ 方法 ============
const goBack = () => {
  const orderId = route.query.orderId
  if (orderId) {
    router.push(`/order/${orderId}`)
  } else {
    router.push('/orders')
  }
}

/**
 * 获取订单详情
 */
const fetchOrderDetail = async () => {
  const orderId = route.query.orderId
  if (!orderId) {
    ElMessage.error('参数错误，请从订单页面进入')
    router.push('/orders')
    return
  }

  loading.value = true
  try {
    const res = await orderApi.getOrderDetail(orderId)
    
    if (res && res.code === 200) {
      orderInfo.value = res.data
      orderItems.value = res.data?.items || []
      
      // 检查哪些商品已评价
      await checkReviewedBooks()
      
      // 默认选中第一个未评价的商品
      const firstUnreviewed = orderItems.value.find(
        item => !hasReviewed(item.bookId)
      )
      if (firstUnreviewed) {
        selectedBookId.value = firstUnreviewed.bookId
        selectedBook.value = firstUnreviewed
      }
    } else {
      ElMessage.error('获取订单详情失败')
      router.push('/orders')
    }
  } catch (error) {
    console.error('获取订单详情失败:', error)
    ElMessage.error(error.message || '获取订单详情失败')
    router.push('/orders')
  } finally {
    loading.value = false
  }
}

/**
 * 检查哪些商品已评价
 */
const checkReviewedBooks = async () => {
  // 实际项目中应该调用 API 查询用户已评价的商品列表
  // 这里模拟：从本地缓存或 API 获取
  try {
    // 假设有一个 API 可以查询用户已评价的书籍
    // const res = await reviewApi.getUserReviewedBooks()
    // reviewedBooks.value = res.data || []
    
    // 暂时模拟：如果订单中有商品，假设第一个已评价（演示用）
    // 实际使用时请替换为真实 API 调用
    reviewedBooks.value = []
  } catch (error) {
    console.error('获取已评价列表失败:', error)
    reviewedBooks.value = []
  }
}

/**
 * 判断某本书是否已评价
 */
const hasReviewed = (bookId) => {
  return reviewedBooks.value.includes(bookId)
}

/**
 * 选择商品
 */
const selectBook = (item) => {
  if (hasReviewed(item.bookId)) {
    ElMessage.warning('该商品已评价，不可重复评价')
    return
  }
  selectedBookId.value = item.bookId
  selectedBook.value = item
  // 重置表单
  resetForm()
}

/**
 * 重置表单
 */
const resetForm = () => {
  form.rating = 5
  form.content = ''
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

/**
 * 提交评价
 */
const submitReview = async () => {
  if (!formRef.value) return
  
  // 检查是否选择了商品
  if (!selectedBookId.value) {
    ElMessage.warning('请选择要评价的商品')
    return
  }

  // 检查是否已评价
  if (hasReviewed(selectedBookId.value)) {
    ElMessage.warning('该商品已评价，不可重复评价')
    return
  }

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    // 二次确认
    try {
      await ElMessageBox.confirm(
        `确定要对「${selectedBook.value?.bookName}」发表评价吗？`,
        '确认提交',
        {
          confirmButtonText: '确定提交',
          cancelButtonText: '取消',
          type: 'info'
        }
      )
    } catch {
      return
    }

    submitting.value = true
    try {
      const data = {
        bookId: selectedBookId.value,
        orderId: orderInfo.value.orderId,
        rating: form.rating,
        content: form.content.trim()
      }

      const res = await reviewApi.createReview(data)
      
      if (res && res.code === 200) {
        ElMessage.success('评价提交成功！')
        
        // 标记已评价
        reviewedBooks.value.push(selectedBookId.value)
        
        // 自动选择下一个未评价的商品
        const nextUnreviewed = orderItems.value.find(
          item => !hasReviewed(item.bookId)
        )
        if (nextUnreviewed) {
          selectedBookId.value = nextUnreviewed.bookId
          selectedBook.value = nextUnreviewed
          resetForm()
          ElMessage.info('继续评价下一个商品')
        } else {
          // 所有商品都已评价
          ElMessage.success('该订单所有商品已评价完毕！')
          // 延迟跳转
          setTimeout(() => {
            router.push('/orders')
          }, 1500)
        }
      } else {
        ElMessage.error(res?.message || '评价提交失败')
      }
    } catch (error) {
      console.error('提交评价失败:', error)
      ElMessage.error(error.message || '评价提交失败')
    } finally {
      submitting.value = false
    }
  })
}

// ============ 生命周期 ============
onMounted(() => {
  fetchOrderDetail()
})
</script>

<style scoped>
.review-create {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-title {
  font-size: 24px;
  color: #303133;
  margin: 0;
}

.section {
  background: #ffffff;
  border-radius: 12px;
  padding: 20px 24px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.section h3 {
  font-size: 16px;
  color: #303133;
  margin: 0 0 16px 0;
}

/* ===== 订单信息 ===== */
.order-info {
  display: flex;
  gap: 32px;
  font-size: 14px;
  color: #606266;
}

.order-info span {
  padding: 4px 12px;
  background: #f5f7fa;
  border-radius: 4px;
}

/* ===== 商品列表 ===== */
.book-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.book-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  border: 2px solid #e4e7ed;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.book-item:hover {
  border-color: #c0c4cc;
}

.book-item.active {
  border-color: #409EFF;
  background: #ecf5ff;
}

.book-item img {
  width: 50px;
  height: 70px;
  object-fit: cover;
  border-radius: 4px;
  background: #f5f7fa;
  flex-shrink: 0;
}

.book-item .book-info {
  flex: 1;
  padding: 0 16px;
}

.book-item .book-name {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
}

.book-item .book-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 13px;
  color: #909399;
}

.book-item .el-radio {
  margin-left: auto;
}

/* ===== 评价表单 ===== */
.review-section .el-rate {
  margin-top: 4px;
}

.review-section .el-textarea {
  max-width: 600px;
}

.review-section .el-button--primary {
  min-width: 120px;
}

/* ===== 响应式 ===== */
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .order-info {
    flex-direction: column;
    gap: 8px;
  }

  .book-item {
    flex-wrap: wrap;
    gap: 8px;
  }

  .book-item .book-info {
    padding: 0 8px;
  }

  .book-item .el-radio {
    margin-left: 0;
  }

  .review-section .el-textarea {
    max-width: 100%;
  }
}
</style>