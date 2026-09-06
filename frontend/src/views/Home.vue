<template>
  <div class="home">
    <!-- 轮播图 -->
    <div class="banner">
      <el-carousel height="400px" indicator-position="outside">
        <el-carousel-item v-for="(item, index) in banners" :key="index">
          <div class="banner-item" :style="{ background: item.bg }">
            <h2>{{ item.title }}</h2>
            <p>{{ item.subtitle }}</p>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>

    <!-- 分类导航 -->
    <div class="categories" v-if="categories.length > 0">
      <div class="section-title">
        <h2>📂 教材分类</h2>
      </div>
      <el-row :gutter="16">
        <el-col :span="3" v-for="cat in categories" :key="cat.categoryId">
          <div class="category-item" @click="goToCategory(cat.categoryId)">
            <div class="category-icon">📖</div>
            <span>{{ cat.categoryName }}</span>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 热门教材 -->
    <div class="section" v-if="hotBooks.length > 0">
      <div class="section-header">
        <h2>🔥 热门教材</h2>
        <el-link type="primary" @click="goToBooks">查看更多 →</el-link>
      </div>
      <el-row :gutter="20">
        <el-col :span="6" v-for="book in hotBooks" :key="book.bookId">
          <BookCard :book="book" @click="goToDetail(book.bookId)" />
        </el-col>
      </el-row>
    </div>

    <!-- 新品推荐 -->
    <div class="section" v-if="newBooks.length > 0">
      <div class="section-header">
        <h2>✨ 新品推荐</h2>
        <el-link type="primary" @click="goToBooks">查看更多 →</el-link>
      </div>
      <el-row :gutter="20">
        <el-col :span="6" v-for="book in newBooks" :key="book.bookId">
          <BookCard :book="book" @click="goToDetail(book.bookId)" />
        </el-col>
      </el-row>
    </div>
    
    <!-- 空状态 -->
    <el-empty 
      v-if="!loading && hotBooks.length === 0 && newBooks.length === 0" 
      description="暂无教材数据" 
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { bookApi } from '@/api/book'
import { categoryApi } from '@/api/category'
import BookCard from '@/components/BookCard.vue'

const router = useRouter()
const hotBooks = ref([])
const newBooks = ref([])
const categories = ref([])
const loading = ref(false)

const banners = [
  { 
    title: '📚 教材大促', 
    subtitle: '全场低至5折起，限时抢购', 
    bg: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' 
  },
  { 
    title: '🎓 新学期新教材', 
    subtitle: '精选教材，助力学习进步', 
    bg: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)' 
  },
  { 
    title: '📖 经典教材推荐', 
    subtitle: '名师推荐，品质保证', 
    bg: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)' 
  }
]

const fetchData = async () => {
  loading.value = true
  try {
    const [hotRes, newRes, categoryRes] = await Promise.all([
      bookApi.getHotBooks(8),
      bookApi.getNewBooks(8),
      categoryApi.getCategories()
    ])
    
    // 处理热门图书
    if (hotRes && hotRes.code === 200) {
      hotBooks.value = hotRes.data || []
    } else if (Array.isArray(hotRes)) {
      hotBooks.value = hotRes
    } else {
      hotBooks.value = []
    }
    
    // 处理新品图书
    if (newRes && newRes.code === 200) {
      newBooks.value = newRes.data || []
    } else if (Array.isArray(newRes)) {
      newBooks.value = newRes
    } else {
      newBooks.value = []
    }
    
    // 处理分类
    if (categoryRes && categoryRes.code === 200) {
      categories.value = categoryRes.data || []
    } else if (Array.isArray(categoryRes)) {
      categories.value = categoryRes
    } else {
      categories.value = []
    }
    
    console.log('分类数据:', categories.value)
    console.log('热门图书:', hotBooks.value)
    console.log('新品图书:', newBooks.value)
  } catch (error) {
    console.error('加载首页数据失败:', error)
    hotBooks.value = []
    newBooks.value = []
    categories.value = []
  } finally {
    loading.value = false
  }
}

const goToDetail = (bookId) => router.push(`/book/${bookId}`)
const goToCategory = (categoryId) => router.push(`/books?category=${categoryId}`)
const goToBooks = () => router.push('/books')

onMounted(fetchData)
</script>

<style scoped>
.home {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px 20px 0;
}

.banner-item {
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: #ffffff;
  border-radius: 12px;
}

.banner-item h2 {
  font-size: 44px;
  font-weight: 700;
  margin: 0;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.banner-item p {
  font-size: 18px;
  margin-top: 12px;
  opacity: 0.95;
  text-shadow: 0 1px 4px rgba(0, 0, 0, 0.15);
}

/* 分类 */
.categories {
  margin: 30px 0 40px;
  padding: 24px 20px;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.section-title {
  margin-bottom: 16px;
}

.section-title h2 {
  font-size: 20px;
  color: #303133;
}

.category-item {
  text-align: center;
  cursor: pointer;
  padding: 12px 8px;
  border-radius: 8px;
  transition: all 0.3s;
}

.category-item:hover {
  background: #f0f7ff;
  transform: translateY(-2px);
}

.category-icon {
  font-size: 28px;
  margin-bottom: 6px;
}

.category-item span {
  display: block;
  font-size: 13px;
  color: #606266;
}

/* 教材区 */
.section {
  margin: 40px 0;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h2 {
  font-size: 22px;
  color: #303133;
  font-weight: 600;
}
</style>