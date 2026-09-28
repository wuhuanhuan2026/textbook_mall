<template>
  <div class="admin-categories">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>📂 分类管理</span>
          <div>
            <el-button type="primary" @click="showCreateDialog">+ 新增分类</el-button>
            <el-button 
              type="danger" 
              plain 
              :disabled="!selectedIds.length" 
              @click="batchDelete"
            >
              批量删除
            </el-button>
          </div>
        </div>
      </template>

      <el-table
        :data="categoryList"
        v-loading="loading"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="40" />
        <el-table-column prop="categoryId" label="ID" width="80" />
        <el-table-column prop="categoryName" label="分类名称" />
        <el-table-column prop="sortOrder" label="排序" width="100" />
        <el-table-column prop="bookCount" label="教材数量" width="100" />
        <el-table-column label="操作" width="250">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="editCategory(row)">
              编辑
            </el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row.categoryId)">
              删除
            </el-button>
            <el-button 
              type="warning" 
              link 
              size="small" 
              @click="showSortDialog(row)"
            >
              调整排序
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 创建/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="420px"
      @close="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="分类名称" prop="categoryName">
          <el-input v-model="form.categoryName" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" style="width:100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitting">
          {{ isEdit ? '保存修改' : '确认添加' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 排序对话框 -->
    <el-dialog v-model="sortDialogVisible" title="调整排序" width="400px">
      <el-form label-width="80px">
        <el-form-item label="分类名称">
          <span>{{ sortCategory?.categoryName }}</span>
        </el-form-item>
        <el-form-item label="当前排序">
          <span>{{ sortCategory?.sortOrder }}</span>
        </el-form-item>
        <el-form-item label="新排序">
          <el-input-number v-model="newSortOrder" :min="0" style="width:100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="sortDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitSort" :loading="sortLoading">
          确认修改
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminApi } from '@/api/admin'

const loading = ref(false)
const submitting = ref(false)
const sortLoading = ref(false)
const categoryList = ref([])
const selectedIds = ref([])
const dialogVisible = ref(false)
const sortDialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const sortCategory = ref(null)
const newSortOrder = ref(0)

const form = reactive({
  categoryId: null,
  categoryName: '',
  sortOrder: 0
})

const dialogTitle = computed(() => isEdit.value ? '编辑分类' : '新增分类')

const rules = {
  categoryName: [{ required: true, message: '请输入分类名称', trigger: 'blur' }],
  sortOrder: [{ required: true, message: '请输入排序', trigger: 'blur' }]
}

const fetchCategories = async () => {
  loading.value = true
  try {
    const res = await adminApi.getCategories()
    if (res && res.code === 200) {
      categoryList.value = res.data || []
    }
  } catch (error) {
    console.error('获取分类列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSelectionChange = (rows) => {
  selectedIds.value = rows.map(r => r.categoryId)
}

const showCreateDialog = () => {
  isEdit.value = false
  form.categoryId = null
  resetForm()
  dialogVisible.value = true
}

const editCategory = (row) => {
  isEdit.value = true
  form.categoryId = row.categoryId
  form.categoryName = row.categoryName
  form.sortOrder = row.sortOrder
  dialogVisible.value = true
}

const resetForm = () => {
  form.categoryName = ''
  form.sortOrder = 0
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
      const data = { categoryName: form.categoryName, sortOrder: form.sortOrder }
      if (isEdit.value) {
        await adminApi.updateCategory(form.categoryId, data)
        ElMessage.success('更新成功')
      } else {
        await adminApi.createCategory(data)
        ElMessage.success('添加成功')
      }
      dialogVisible.value = false
      fetchCategories()
    } catch (error) {
      ElMessage.error(isEdit.value ? '更新失败' : '添加失败')
    } finally {
      submitting.value = false
    }
  })
}

const showSortDialog = (row) => {
  sortCategory.value = row
  newSortOrder.value = row.sortOrder
  sortDialogVisible.value = true
}

const submitSort = async () => {
  if (!sortCategory.value) return
  
  sortLoading.value = true
  try {
    await adminApi.updateCategorySort(sortCategory.value.categoryId, newSortOrder.value)
    ElMessage.success('排序更新成功')
    sortDialogVisible.value = false
    fetchCategories()
  } catch (error) {
    ElMessage.error('排序更新失败')
  } finally {
    sortLoading.value = false
  }
}

const handleDelete = async (categoryId) => {
  try {
    await ElMessageBox.confirm('确定要删除该分类吗？如果分类下存在教材，将无法删除。', '提示', { type: 'warning' })
    await adminApi.deleteCategory(categoryId)
    ElMessage.success('删除成功')
    fetchCategories()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const batchDelete = async () => {
  if (!selectedIds.value.length) return
  try {
    await ElMessageBox.confirm(`确定要删除 ${selectedIds.value.length} 个分类吗？`, '提示', { type: 'warning' })
    await adminApi.batchDeleteCategories(selectedIds.value)
    ElMessage.success('批量删除成功')
    selectedIds.value = []
    fetchCategories()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败')
    }
  }
}

onMounted(fetchCategories)
</script>

<style scoped>
.admin-categories {
  max-width: 1200px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>