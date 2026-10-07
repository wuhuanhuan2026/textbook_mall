<template>
  <div class="books-page">
    <!-- 筛选栏 -->
    <div class="filter-bar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索教材名称、作者..."
        prefix-icon="Search"
        style="width: 280px"
        clearable
        @keyup.enter="handleSearch"
        @clear="handleSearch"
      />
      
      <el-select 
        v-model="filters.categoryId" 
        placeholder="全部分类" 
        clearable 
        @change="handleFilter"
        style="width: 140px"
      >
        <el-option 
          v-for="cat in categories" 
          :key="cat.categoryId" 
          :label="cat.categoryName" 
          :value="cat.categoryId" 
        />
      </el-select>
      
      <el-select 
        v-model="filters.sortBy" 
        placeholder="排序方式" 
        @change="handleFilter"
        style="width: 140px"
      >
        <el-option label="综合排序" value="default" />
        <el-option label="价格低→高" value="price_asc" />
        <el-option label="价格高→低" value="price_desc" />
        <el-option label="销量最高" value="sales" />
      </el-select>
    </div>

    <!-- 结果统计 -->
    <div class="result-stats" v-if="total > 0 && !loading && !error">
      共找到 <strong>{{ total }}</strong> 本教材
    </div>

    <!-- 状态分支 -->
    <LoadingState v-if="loading" />
    <ErrorState v-else-if="error" :message="error" @retry="fetchBooks" />
    <EmptyState
      v-else-if="bookList.length === 0"
      :message="searchKeyword ? '未找到匹配的教材' : '暂无教材数据'"
      :action-text="searchKeyword ? '清空筛选' : ''"
      @action="resetQuery"
    />
    <el-row v-else :gutter="20">
      <el-col :span="6" v-for="book in bookList" :key="book.bookId">
        <BookCard :book="book" @click="goToDetail(book.bookId)" />
      </el-col>
    </el-row>

    <!-- ✅ 修复分页：使用 current-page + jumper -->
    <div class="pagination-wrapper" v-if="total > 0 && !loading && !error">
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[8, 16, 24, 32]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="onSizeChange"
        @current-change="onCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { listBooks, listCategories } from '@/api/book'
import BookCard from '@/components/BookCard.vue'
import LoadingState from '@/components/LoadingState.vue'
import EmptyState from '@/components/EmptyState.vue'
import ErrorState from '@/components/ErrorState.vue'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const error = ref('')
const bookList = ref([])
const categories = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(8)
const searchKeyword = ref('')

const filters = reactive({
  categoryId: null,
  sortBy: 'default'
})

const fetchCategories = async () => {
  try {
    const res = await listCategories()
    if (Array.isArray(res)) {
      categories.value = res
    } else if (res && res.code === 200 && Array.isArray(res.data)) {
      categories.value = res.data
    } else {
      categories.value = []
    }
  } catch (error) {
    console.error('获取分类失败:', error)
    categories.value = []
  }
}

const fetchBooks = async () => {
  loading.value = true
  error.value = ''
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
    if (searchKeyword.value) params.keyword = searchKeyword.value
    if (filters.categoryId) params.categoryId = filters.categoryId

    const data = await listBooks(params)
    bookList.value = data.records || []
    total.value = data.total || 0
  } catch (error) {
    console.error('获取教材列表失败:', error)
    error.value = error.message || '加载失败'
    bookList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// ✅ 每页条数变化
const onSizeChange = (val) => {
  pageSize.value = val
  pageNum.value = 1
  fetchBooks()
}

// ✅ 页码变化（点击页码 + "前往"跳转）
const onCurrentChange = (val) => {
  pageNum.value = val
  fetchBooks()
}

const handleSearch = () => {
  pageNum.value = 1
  fetchBooks()
}

const handleFilter = () => {
  pageNum.value = 1
  fetchBooks()
}

const resetQuery = () => {
  searchKeyword.value = ''
  filters.categoryId = null
  filters.sortBy = 'default'
  pageNum.value = 1
  fetchBooks()
}

const goToDetail = (bookId) => router.push(`/book/${bookId}`)

watch(() => route.query.category, (val) => {
  if (val) {
    filters.categoryId = Number(val)
    fetchBooks()
  }
})

onMounted(async () => {
  await fetchCategories()
  if (route.query.category) {
    filters.categoryId = Number(route.query.category)
  }
  fetchBooks()
})
</script>

<style scoped>
.books-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.filter-bar {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
  flex-wrap: wrap;
  background: #ffffff;
  padding: 16px 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.result-stats {
  font-size: 14px;
  color: #909399;
  margin-bottom: 16px;
  padding: 0 4px;
}

.result-stats strong {
  color: #409EFF;
}

.pagination-wrapper {
  margin-top: 24px;
  display: flex;
  justify-content: center;
  padding: 16px 0;
  background: #ffffff;
  border-radius: 8px;
}
</style>