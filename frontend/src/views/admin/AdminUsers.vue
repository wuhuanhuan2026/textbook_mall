<template>
  <div class="admin-users">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>👤 管理员管理</span>
          <el-button type="primary" @click="showCreateDialog">+ 新增管理员</el-button>
        </div>
      </template>

      <el-table :data="adminList" v-loading="loading">
        <el-table-column prop="adminId" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="realName" label="姓名" />
        <el-table-column prop="role" label="角色" width="120">
          <template #default="{ row }">
            {{ getRoleText(row.role) }}
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastLoginTime" label="最后登录" width="170" />
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="editAdmin(row)">
              编辑
            </el-button>
            <el-button 
              v-if="row.status === 0" 
              type="success" 
              link 
              size="small" 
              @click="handleEnable(row.adminId)"
            >
              启用
            </el-button>
            <el-button 
              v-else 
              type="danger" 
              link 
              size="small" 
              @click="handleDisable(row.adminId)"
            >
              禁用
            </el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row.adminId)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 创建/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="480px"
      @close="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" placeholder="请选择角色" style="width:100%">
            <el-option label="超级管理员" value="super_admin" />
            <el-option label="管理员" value="admin" />
            <el-option label="运营人员" value="operator" />
            <el-option label="查看者" value="viewer" />
          </el-select>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="头像URL" prop="avatar">
          <el-input v-model="form.avatar" placeholder="请输入头像图片URL" />
        </el-form-item>
        <el-form-item v-if="!isEdit" label="初始密码">
          <el-alert title="默认密码为 123456，请管理员登录后修改" type="info" :closable="false" />
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminApi } from '@/api/admin'

const loading = ref(false)
const submitting = ref(false)
const adminList = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()

const form = reactive({
  adminId: null,
  username: '',
  realName: '',
  role: 'admin',
  phone: '',
  email: '',
  avatar: ''
})

const dialogTitle = computed(() => isEdit.value ? '编辑管理员' : '新增管理员')

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

const getRoleText = (role) => {
  const map = {
    super_admin: '超级管理员',
    admin: '管理员',
    operator: '运营人员',
    viewer: '查看者'
  }
  return map[role] || role
}

const fetchAdmins = async () => {
  loading.value = true
  try {
    const res = await adminApi.getAdminList()
    if (res && res.code === 200) {
      adminList.value = res.data || []
    }
  } catch (error) {
    console.error('获取管理员列表失败:', error)
  } finally {
    loading.value = false
  }
}

const showCreateDialog = () => {
  isEdit.value = false
  form.adminId = null
  resetForm()
  dialogVisible.value = true
}

const editAdmin = (row) => {
  isEdit.value = true
  form.adminId = row.adminId
  form.username = row.username
  form.realName = row.realName || ''
  form.role = row.role
  form.phone = row.phone || ''
  form.email = row.email || ''
  form.avatar = row.avatar || ''
  dialogVisible.value = true
}

const resetForm = () => {
  form.username = ''
  form.realName = ''
  form.role = 'admin'
  form.phone = ''
  form.email = ''
  form.avatar = ''
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
      const data = {
        username: form.username,
        realName: form.realName,
        role: form.role,
        phone: form.phone,
        email: form.email,
        avatar: form.avatar
      }
      
      if (isEdit.value) {
        await adminApi.updateAdmin(form.adminId, data)
        ElMessage.success('更新成功')
      } else {
        await adminApi.createAdmin(data)
        ElMessage.success('添加成功，默认密码为 123456')
      }
      dialogVisible.value = false
      fetchAdmins()
    } catch (error) {
      ElMessage.error(isEdit.value ? '更新失败' : '添加失败')
    } finally {
      submitting.value = false
    }
  })
}

const handleEnable = async (adminId) => {
  try {
    await ElMessageBox.confirm('确定要启用该管理员吗？', '提示', { type: 'info' })
    await adminApi.enableAdmin(adminId)
    ElMessage.success('启用成功')
    fetchAdmins()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('启用失败')
    }
  }
}

const handleDisable = async (adminId) => {
  try {
    await ElMessageBox.confirm('确定要禁用该管理员吗？', '提示', { type: 'warning' })
    await adminApi.disableAdmin(adminId)
    ElMessage.success('禁用成功')
    fetchAdmins()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('禁用失败')
    }
  }
}

const handleDelete = async (adminId) => {
  try {
    await ElMessageBox.confirm('确定要删除该管理员吗？此操作不可恢复！', '危险操作', { type: 'error' })
    await adminApi.deleteAdmin(adminId)
    ElMessage.success('删除成功')
    fetchAdmins()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(fetchAdmins)
</script>

<style scoped>
.admin-users {
  max-width: 1200px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>