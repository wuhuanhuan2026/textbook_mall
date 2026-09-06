<template>
  <div class="book-card" @click="$emit('click')">
    <!-- 封面 -->
    <div class="book-cover">
      <img 
        :src="book.coverImage || '/default-cover.jpg'" 
        :alt="book.bookName"
        loading="lazy"
      />
      <span class="discount" v-if="discount > 0 && discount < 10">
        {{ discount }}折
      </span>
      <span class="sold-out" v-if="book.stock === 0">已售罄</span>
    </div>
    
    <!-- 信息 -->
    <div class="book-info">
      <h3 class="book-name" :title="book.bookName">{{ book.bookName }}</h3>
      <p class="book-author">{{ book.author || '未知作者' }}</p>
      
      <div class="book-price">
        <span class="price">¥{{ book.price.toFixed(2) }}</span>
        <span class="original-price" v-if="book.originalPrice && book.originalPrice > book.price">
          ¥{{ book.originalPrice.toFixed(2) }}
        </span>
      </div>
      
      <div class="book-stats">
        <span>📈 销量 {{ book.sales || 0 }}</span>
        <span>📦 库存 {{ book.stock || 0 }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  book: {
    type: Object,
    required: true,
    validator: (value) => {
      return value && typeof value.bookId !== 'undefined'
    }
  }
})

defineEmits(['click'])

const discount = computed(() => {
  if (!props.book.originalPrice || props.book.originalPrice === 0) return 0
  return Math.round((props.book.price / props.book.originalPrice) * 10)
})
</script>

<style scoped>
.book-card {
  background: #ffffff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  cursor: pointer;
  transition: all 0.3s ease;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.book-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.book-cover {
  position: relative;
  padding-top: 100%;
  background: #f5f7fa;
  overflow: hidden;
  flex-shrink: 0;
}

.book-cover img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.book-card:hover .book-cover img {
  transform: scale(1.05);
}

.discount {
  position: absolute;
  top: 8px;
  right: 8px;
  background: #f56c6c;
  color: #ffffff;
  padding: 2px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.sold-out {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: rgba(0, 0, 0, 0.7);
  color: #ffffff;
  padding: 8px 16px;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 600;
}

.book-info {
  padding: 12px 14px 14px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.book-name {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin: 0 0 4px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.4;
  min-height: 38px;
}

.book-author {
  font-size: 12px;
  color: #909399;
  margin: 0 0 8px 0;
}

.book-price {
  margin-bottom: 8px;
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.price {
  font-size: 20px;
  font-weight: 700;
  color: #f56c6c;
}

.original-price {
  font-size: 13px;
  color: #c0c4cc;
  text-decoration: line-through;
}

.book-stats {
  font-size: 12px;
  color: #909399;
  display: flex;
  gap: 16px;
  margin-top: auto;
  border-top: 1px solid #ebeef5;
  padding-top: 8px;
}
</style>