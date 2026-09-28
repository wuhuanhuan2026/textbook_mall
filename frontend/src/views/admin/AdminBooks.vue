<template>
  <div class="admin-books">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-row">
      <el-col :span="6" v-for="stat in bookStats" :key="stat.label">
        <el-statistic :value="stat.value" :title="stat.label">
          <template #suffix>
            <span style="font-size:14px;color:#909399">{{ stat.suffix }}</span>
          </template>
        </el-statistic>
      </el-col>
    </el-row>

    <!-- 搜索区域 -->
    <el-card shadow="hover" class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="书名/作者/出版社" clearable />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="searchForm.categoryId" placeholder="全部分类" clearable>
            <el-option 
              v-for="cat in categories" 
              :key="cat.categoryId" 
              :label="cat.categoryName" 
              :value="cat.categoryId" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部状态" clearable>
            <el-option label="上架" :value="1" />
            <el-option label="下架" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="价格区间">
          <el-input-number v-model="searchForm.minPrice" :min="0" placeholder="最低价" style="width:120px" />
          <span style="margin:0 8px">-</span>
          <el-input-number v-model="searchForm.maxPrice" :min="0" placeholder="最高价" style="width:120px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchBooks">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
          <el-button type="success" @click="showCreateDialog">+ 新增教材</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格 -->
    <el-card shadow="hover" class="table-card">
      <div class="table-toolbar">
        <div class="toolbar-left">
          <el-button type="danger" plain :disabled="!selectedIds.length" @click="batchDelete">
            批量删除
          </el-button>
          <el-button type="success" plain :disabled="!selectedIds.length" @click="batchOnSale">
            批量上架
          </el-button>
          <el-button type="warning" plain :disabled="!selectedIds.length" @click="batchOffSale">
            批量下架
          </el-button>
        </div>
        <div class="toolbar-right">
          <span class="total-info">共 {{ total }} 本教材</span>
        </div>
      </div>

      <el-table
        :data="bookList"
        style="width: 100%"
        v-loading="loading"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="40" />
        <el-table-column label="封面" width="70">
          <template #default="{ row }">
            <el-avatar :size="50" :src="row.coverImage || '/default-cover.jpg'" shape="square" />
          </template>
        </el-table-column>
        <el-table-column prop="bookName" label="书名" min-width="160" />
        <el-table-column prop="author" label="作者" width="120" />
        <el-table-column prop="categoryName" label="分类" width="100" />
        <el-table-column prop="price" label="售价" width="100">
          <template #default="{ row }">
            ¥{{ row.price?.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="80" />
        <el-table-column prop="sales" label="销量" width="80" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="viewBook(row.bookId)">
              查看
            </el-button>
            <el-button type="warning" link size="small" @click="editBook(row)">
              编辑
            </el-button>
            <el-button 
              v-if="row.status === 0" 
              type="success" 
              link 
              size="small" 
              @click="handleOnSale(row.bookId)"
            >
              上架
            </el-button>
            <el-button 
              v-else 
              type="danger" 
              link 
              size="small" 
              @click="handleOffSale(row.bookId)"
            >
              下架
            </el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row.bookId)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- ✅ 分页：修复"前往第几页"跳转 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 创建/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="resetForm"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="90px"
        label-position="right"
      >
        <el-form-item label="教材名称" prop="bookName">
          <el-input v-model="form.bookName" placeholder="请输入教材名称" />
        </el-form-item>
        
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="作者" prop="author">
              <el-input v-model="form.author" placeholder="请输入作者" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="出版社" prop="publisher">
              <el-input v-model="form.publisher" placeholder="请输入出版社" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="ISBN" prop="isbn">
              <el-input v-model="form.isbn" placeholder="请输入ISBN" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分类" prop="categoryId">
              <el-select v-model="form.categoryId" placeholder="请选择分类" style="width:100%">
                <el-option 
                  v-for="cat in categories" 
                  :key="cat.categoryId" 
                  :label="cat.categoryName" 
                  :value="cat.categoryId" 
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="售价" prop="price">
              <el-input-number v-model="form.price" :min="0.01" :precision="2" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="原价" prop="originalPrice">
              <el-input-number v-model="form.originalPrice" :min="0" :precision="2" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="库存" prop="stock">
              <el-input-number v-model="form.stock" :min="0" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择状态" style="width:100%">
                <el-option label="上架" :value="1" />
                <el-option label="下架" :value="0" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="封面URL" prop="coverImage">
          <el-input v-model="form.coverImage" placeholder="请输入封面图片URL" />
        </el-form-item>

        <el-form-item label="简介" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入教材简介" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitting">
          {{ isEdit ? '保存修改' : '确认添加' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminApi } from '@/api/admin'

const router = useRouter()
const loading = ref(false)
const submitting = ref(false)
const bookList = ref([])
const categories = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const selectedIds = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()

const searchForm = reactive({
  keyword: '',
  categoryId: null,
  status: null,
  minPrice: null,
  maxPrice: null
})

const form = reactive({
  bookId: null,
  bookName: '',
  author: '',
  publisher: '',
  isbn: '',
  categoryId: null,
  price: null,
  originalPrice: null,
  stock: 0,
  coverImage: '',
  description: '',
  status: 1
})

const dialogTitle = computed(() => isEdit.value ? '编辑教材' : '新增教材')

const rules = {
  bookName: [{ required: true, message: '请输入教材名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入售价', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

// 统计信息
const bookStats = ref([
  { label: '教材总数', value: 0, suffix: '本' },
  { label: '上架教材', value: 0, suffix: '本' },
  { label: '总库存', value: 0, suffix: '件' },
  { label: '总销量', value: 0, suffix: '件' }
])

// 获取分类列表
const fetchCategories = async () => {
  try {
    const res = await adminApi.getCategories()
    if (res && res.code === 200) {
      categories.value = res.data || []
    }
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

// 获取教材列表
const fetchBooks = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      ...searchForm
    }
    // 移除空值
    Object.keys(params).forEach(key => {
      if (params[key] === null || params[key] === '') {
        delete params[key]
      }
    })
    
    const res = await adminApi.getBooks(params)
    if (res && res.code === 200) {
      const pageData = res.data
      bookList.value = pageData?.list || []
      total.value = pageData?.total || 0
      updateStats()
    }
  } catch (error) {
    console.error('获取教材列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 更新统计
const updateStats = () => {
  const list = bookList.value
  bookStats.value = [
    { label: '教材总数', value: total.value, suffix: '本' },
    { label: '上架教材', value: list.filter(b => b.status === 1).length, suffix: '本' },
    { label: '总库存', value: list.reduce((sum, b) => sum + (b.stock || 0), 0), suffix: '件' },
    { label: '总销量', value: list.reduce((sum, b) => sum + (b.sales || 0), 0), suffix: '件' }
  ]
}

const resetSearch = () => {
  Object.assign(searchForm, {
    keyword: '',
    categoryId: null,
    status: null,
    minPrice: null,
    maxPrice: null
  })
  pageNum.value = 1
  fetchBooks()
}

const handleSelectionChange = (rows) => {
  selectedIds.value = rows.map(r => r.bookId)
}

// ✅ 分页事件处理
const handleSizeChange = (val) => {
  pageSize.value = val
  pageNum.value = 1
  fetchBooks()
}

const handleCurrentChange = (val) => {
  pageNum.value = val
  fetchBooks()
}

// ========== 上架/下架 ==========
const handleOnSale = async (bookId) => {
  try {
    await ElMessageBox.confirm('确定要上架该教材吗？', '提示', { type: 'info' })
    await adminApi.onSale(bookId)
    ElMessage.success('上架成功')
    fetchBooks()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('上架失败')
    }
  }
}

const handleOffSale = async (bookId) => {
  try {
    await ElMessageBox.confirm('确定要下架该教材吗？', '提示', { type: 'warning' })
    await adminApi.offSale(bookId)
    ElMessage.success('下架成功')
    fetchBooks()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('下架失败')
    }
  }
}

// ========== 批量操作 ==========
const batchOnSale = async () => {
  if (!selectedIds.value.length) return
  try {
    await ElMessageBox.confirm(`确定要批量上架 ${selectedIds.value.length} 本教材吗？`, '提示', { type: 'info' })
    await adminApi.batchOnSale(selectedIds.value)
    ElMessage.success('批量上架成功')
    selectedIds.value = []
    fetchBooks()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量上架失败')
    }
  }
}

const batchOffSale = async () => {
  if (!selectedIds.value.length) return
  try {
    await ElMessageBox.confirm(`确定要批量下架 ${selectedIds.value.length} 本教材吗？`, '提示', { type: 'warning' })
    await adminApi.batchOffSale(selectedIds.value)
    ElMessage.success('批量下架成功')
    selectedIds.value = []
    fetchBooks()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量下架失败')
    }
  }
}

const batchDelete = async () => {
  if (!selectedIds.value.length) return
  try {
    await ElMessageBox.confirm(`确定要删除 ${selectedIds.value.length} 本教材吗？此操作不可恢复！`, '危险操作', { type: 'error' })
    await adminApi.batchDeleteBooks(selectedIds.value)
    ElMessage.success('批量删除成功')
    selectedIds.value = []
    fetchBooks()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败')
    }
  }
}

// ========== 删除 ==========
const handleDelete = async (bookId) => {
  try {
    await ElMessageBox.confirm('确定要删除该教材吗？此操作不可恢复！', '危险操作', { type: 'error' })
    await adminApi.deleteBook(bookId)
    ElMessage.success('删除成功')
    fetchBooks()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// ========== 查看 ==========
// ✅ 修改：新标签页打开前台详情，不跳出管理后台
const viewBook = (bookId) => {
  window.open(`/book/${bookId}`, '_blank')
}

// ========== 创建/编辑 ==========
const showCreateDialog = () => {
  isEdit.value = false
  form.bookId = null
  resetForm()
  dialogVisible.value = true
}

const editBook = (row) => {
  isEdit.value = true
  Object.assign(form, {
    bookId: row.bookId,
    bookName: row.bookName,
    author: row.author || '',
    publisher: row.publisher || '',
    isbn: row.isbn || '',
    categoryId: row.categoryId,
    price: row.price,
    originalPrice: row.originalPrice || null,
    stock: row.stock || 0,
    coverImage: row.coverImage || '',
    description: row.description || '',
    status: row.status
  })
  dialogVisible.value = true
}

const resetForm = () => {
  form.bookId = null
  form.bookName = ''
  form.author = ''
  form.publisher = ''
  form.isbn = ''
  form.categoryId = null
  form.price = null
  form.originalPrice = null
  form.stock = 0
  form.coverImage = ''
  form.description = ''
  form.status = 1
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

const submitForm = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitting.value = true
    try {
      const data = { ...form }
      delete data.bookId
      
      if (isEdit.value) {
        await adminApi.updateBook(form.bookId, data)
        ElMessage.success('更新成功')
      } else {
        await adminApi.createBook(data)
        ElMessage.success('添加成功')
      }
      dialogVisible.value = false
      fetchBooks()
    } catch (error) {
      ElMessage.error(isEdit.value ? '更新失败' : '添加失败')
    } finally {
      submitting.value = false
    }
  })
}

onMounted(async () => {
  await fetchCategories()
  fetchBooks()
})
</script>

<style scoped>
.admin-books {
  max-width: 1400px;
  margin: 0 auto;
}

.stat-row {
  margin-bottom: 20px;
}

.stat-row .el-col {
  margin-bottom: 12px;
}

.search-card {
  margin-bottom: 20px;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
}

.search-form .el-form-item {
  margin-bottom: 0;
}

.table-card {
  overflow: hidden;
}

.table-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  flex-wrap: wrap;
  gap: 8px;
}

.toolbar-left {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.toolbar-right {
  font-size: 14px;
  color: #909399;
}

.total-info {
  font-weight: 500;
  color: #303133;
}

.pagination-wrapper {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

@media (max-width: 768px) {
  .search-form .el-form-item {
    width: 100%;
  }
  .search-form .el-form-item .el-input,
  .search-form .el-form-item .el-select {
    width: 100% !important;
  }
}
</style>