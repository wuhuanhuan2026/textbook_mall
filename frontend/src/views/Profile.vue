<template>
  <div class="profile-page">
    <h1 class="page-title">👤 个人中心</h1>
    
    <div class="profile-container">
      <!-- 用户信息卡片 -->
      <div class="profile-card">
        <div class="avatar-wrapper">
          <el-avatar :size="80" :src="userInfo?.avatar || ''">
            {{ userInfo?.username?.charAt(0)?.toUpperCase() || 'U' }}
          </el-avatar>
        </div>
        <div class="user-info">
          <h2>{{ userInfo?.username || '用户' }}</h2>
          <p class="user-balance">余额：¥{{ (userInfo?.balance || 0).toFixed(2) }}</p>
          <p class="user-status" :class="userInfo?.status === 1 ? 'active' : 'inactive'">
            {{ userInfo?.status === 1 ? '✅ 正常' : '❌ 已禁用' }}
          </p>
        </div>
      </div>
      
      <!-- 信息修改 -->
      <div class="info-section">
        <h3>📝 基本信息</h3>
        <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
          <el-form-item label="用户名">
            <el-input v-model="form.username" disabled />
          </el-form-item>
          
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="form.phone" placeholder="请输入手机号" />
          </el-form-item>
          
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="form.email" placeholder="请输入邮箱" />
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="saveProfile" :loading="saving">保存修改</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 修改密码 -->
      <div class="info-section">
        <h3>🔒 修改密码</h3>
        <el-form :model="pwdForm" :rules="pwdRules" ref="pwdFormRef" label-width="80px">
          <el-form-item label="旧密码" prop="oldPassword">
            <el-input v-model="pwdForm.oldPassword" type="password" placeholder="请输入旧密码" show-password />
          </el-form-item>
          
          <el-form-item label="新密码" prop="newPassword">
            <el-input v-model="pwdForm.newPassword" type="password" placeholder="请输入新密码" show-password />
          </el-form-item>
          
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input v-model="pwdForm.confirmPassword" type="password" placeholder="请再次输入新密码" show-password />
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="updatePassword" :loading="pwdLoading">修改密码</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { userApi } from '@/api/user'

const userStore = useUserStore()
const userInfo = ref(null)
const saving = ref(false)
const pwdLoading = ref(false)
const formRef = ref()
const pwdFormRef = ref()

const form = reactive({
  username: '',
  phone: '',
  email: ''
})

const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const rules = {
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ]
}

const validateConfirm = (rule, value, callback) => {
  if (value !== pwdForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const pwdRules = {
  oldPassword: [
    { required: true, message: '请输入旧密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20位', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirm, trigger: 'blur' }
  ]
}

const loadUserInfo = async () => {
  try {
    const info = await userStore.fetchUserInfo()
    userInfo.value = info
    form.username = info.username
    form.phone = info.phone || ''
    form.email = info.email || ''
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}

const saveProfile = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    saving.value = true
    try {
      await userApi.updateUserInfo({
        phone: form.phone,
        email: form.email      })
      await userStore.fetchUserInfo()
      ElMessage.success('保存成功')
    } catch (error) {
      ElMessage.error('保存失败')
    } finally {
      saving.value = false
    }
  })
}

const updatePassword = async () => {
  if (!pwdFormRef.value) return
  
  await pwdFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    pwdLoading.value = true
    try {
      await userApi.updatePassword({
        oldPassword: pwdForm.oldPassword,
        newPassword: pwdForm.newPassword
      })
      ElMessage.success('密码修改成功，请重新登录')
      await userStore.logout()
      // 跳转到登录页
      window.location.href = '/login'
    } catch (error) {
      ElMessage.error('密码修改失败')
    } finally {
      pwdLoading.value = false
    }
  })
}

onMounted(loadUserInfo)
</script>

<style scoped>
.profile-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-title {
  font-size: 24px;
  color: #303133;
  margin-bottom: 20px;
}

.profile-container {
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 24px;
}

.profile-card {
  background: #ffffff;
  border-radius: 8px;
  padding: 24px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  height: fit-content;
}

.avatar-wrapper {
  margin-bottom: 12px;
}

.user-info h2 {
  font-size: 18px;
  color: #303133;
  margin: 0 0 8px 0;
}

.user-balance {
  font-size: 16px;
  color: #f56c6c;
  font-weight: 600;
  margin: 4px 0;
}

.user-status {
  font-size: 13px;
  margin: 4px 0 0 0;
}

.user-status.active { color: #67C23A; }
.user-status.inactive { color: #F56C6C; }

.info-section {
  background: #ffffff;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.info-section h3 {
  font-size: 16px;
  color: #303133;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #ebeef5;
}

.info-section + .info-section {
  margin-top: 16px;
}

@media (max-width: 768px) {
  .profile-container {
    grid-template-columns: 1fr;
  }
}
</style>