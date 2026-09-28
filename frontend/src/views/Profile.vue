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
      
      <!-- 右侧信息区域 -->
      <div class="right-panel">
        <!-- 基本信息 -->
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
        
        <!-- 修改密码 - 重新设计 -->
        <div class="info-section password-section">
          <h3>🔒 修改密码</h3>
          
          <div class="password-form">
            <!-- 当前密码 -->
            <div class="password-field">
              <label class="password-label">
                <span class="label-icon">🔐</span>
                当前密码
              </label>
              <div class="password-input-wrapper">
                <el-input 
                  v-model="pwdForm.oldPassword" 
                  type="password" 
                  placeholder="请输入当前密码" 
                  show-password 
                  size="large"
                />
              </div>
            </div>

            <!-- 新密码 -->
            <div class="password-field">
              <label class="password-label">
                <span class="label-icon">✨</span>
                新密码
              </label>
              <div class="password-input-wrapper">
                <el-input 
                  v-model="pwdForm.newPassword" 
                  type="password" 
                  placeholder="请输入新密码（6-20位）" 
                  show-password 
                  size="large"
                />
              </div>
              <div class="password-strength" v-if="pwdForm.newPassword">
                <span class="strength-label">密码强度：</span>
                <span class="strength-bar">
                  <span class="strength-level" :class="passwordStrength"></span>
                </span>
                <span class="strength-text">{{ strengthText }}</span>
              </div>
            </div>

            <!-- 确认新密码 -->
            <div class="password-field">
              <label class="password-label">
                <span class="label-icon">✅</span>
                确认新密码
              </label>
              <div class="password-input-wrapper">
                <el-input 
                  v-model="pwdForm.confirmPassword" 
                  type="password" 
                  placeholder="请再次输入新密码" 
                  show-password 
                  size="large"
                  :class="{ 'is-error': pwdForm.confirmPassword && pwdForm.confirmPassword !== pwdForm.newPassword }"
                />
              </div>
              <div class="password-match" v-if="pwdForm.confirmPassword">
                <span v-if="pwdForm.confirmPassword === pwdForm.newPassword" class="match-success">
                  ✅ 密码匹配
                </span>
                <span v-else class="match-error">
                  ❌ 密码不一致
                </span>
              </div>
            </div>

            <!-- 提交按钮 -->
            <div class="password-submit">
              <el-button 
                type="primary" 
                @click="updatePassword" 
                :loading="pwdLoading" 
                size="large"
                :disabled="!pwdForm.oldPassword || !pwdForm.newPassword || !pwdForm.confirmPassword"
              >
                <span v-if="!pwdLoading">🔑 修改密码</span>
                <span v-else>提交中...</span>
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { userApi } from '@/api/user'

const userStore = useUserStore()
const userInfo = ref(null)
const saving = ref(false)
const pwdLoading = ref(false)
const formRef = ref()

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

// 密码强度计算
const passwordStrength = computed(() => {
  const pwd = pwdForm.newPassword
  if (!pwd || pwd.length < 6) return 'weak'
  let score = 0
  if (pwd.length >= 8) score++
  if (/[a-z]/.test(pwd) && /[A-Z]/.test(pwd)) score++
  if (/\d/.test(pwd)) score++
  if (/[^a-zA-Z0-9]/.test(pwd)) score++
  if (pwd.length >= 12) score++
  if (score >= 4) return 'strong'
  if (score >= 2) return 'medium'
  return 'weak'
})

const strengthText = computed(() => {
  const map = { weak: '弱', medium: '中等', strong: '强' }
  return map[passwordStrength.value] || ''
})

const rules = {
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
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
        email: form.email
      })
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
  // 前端验证
  if (!pwdForm.oldPassword) {
    ElMessage.warning('请输入当前密码')
    return
  }
  if (pwdForm.newPassword.length < 6 || pwdForm.newPassword.length > 20) {
    ElMessage.warning('新密码长度需为6-20位')
    return
  }
  if (pwdForm.newPassword !== pwdForm.confirmPassword) {
    ElMessage.warning('两次输入的密码不一致')
    return
  }
  
  pwdLoading.value = true
  try {
    await userApi.updatePassword({
      oldPassword: pwdForm.oldPassword,
      newPassword: pwdForm.newPassword
    })
    ElMessage.success('密码修改成功，请重新登录')
    // 清空密码表单
    pwdForm.oldPassword = ''
    pwdForm.newPassword = ''
    pwdForm.confirmPassword = ''
    await userStore.logout()
    setTimeout(() => {
      window.location.href = '/login'
    }, 1500)
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '密码修改失败')
  } finally {
    pwdLoading.value = false
  }
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
  grid-template-columns: 300px 1fr;
  gap: 24px;
}

/* ===== 用户信息卡片 ===== */
.profile-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  padding: 32px 24px;
  text-align: center;
  color: #ffffff;
  height: fit-content;
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.3);
}

.profile-card .avatar-wrapper {
  margin-bottom: 12px;
}

.profile-card .avatar-wrapper :deep(.el-avatar) {
  border: 3px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.profile-card .user-info h2 {
  font-size: 20px;
  color: #ffffff;
  margin: 0 0 4px 0;
}

.profile-card .user-balance {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.9);
  font-weight: 500;
  margin: 4px 0;
}

.profile-card .user-status {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.8);
  margin: 4px 0 0 0;
}

/* ===== 右侧面板 ===== */
.right-panel {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.info-section {
  background: #ffffff;
  border-radius: 12px;
  padding: 24px 28px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  border: 1px solid #f0f0f0;
}

.info-section h3 {
  font-size: 16px;
  color: #303133;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

/* ===== 密码表单 - 重新设计 ===== */
.password-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.password-field {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.password-label {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 6px;
}

.password-label .label-icon {
  font-size: 16px;
}

.password-input-wrapper :deep(.el-input__wrapper) {
  border-radius: 10px;
  padding: 0 16px;
  height: 44px;
  background-color: #f7f8fa;
  border: 2px solid transparent;
  transition: all 0.3s ease;
}

.password-input-wrapper :deep(.el-input__wrapper:hover) {
  background-color: #eef0f2;
}

.password-input-wrapper :deep(.el-input__wrapper.is-focus) {
  background-color: #ffffff;
  border-color: #667eea;
  box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
}

.password-input-wrapper :deep(.el-input__wrapper.is-error) {
  border-color: #f56c6c;
  background-color: #fef0f0;
}

.password-input-wrapper :deep(.el-input__inner) {
  font-size: 15px;
}

/* 密码强度 */
.password-strength {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 13px;
  margin-top: 2px;
}

.strength-label {
  color: #909399;
}

.strength-bar {
  width: 120px;
  height: 4px;
  background: #e4e7ed;
  border-radius: 2px;
  overflow: hidden;
}

.strength-level {
  display: block;
  height: 100%;
  border-radius: 2px;
  transition: width 0.4s ease, background 0.3s ease;
}

.strength-level.weak {
  width: 33%;
  background: #f56c6c;
}

.strength-level.medium {
  width: 66%;
  background: #e6a23c;
}

.strength-level.strong {
  width: 100%;
  background: #67c23a;
}

.strength-text {
  color: #909399;
  font-size: 13px;
  min-width: 20px;
}

/* 密码匹配 */
.password-match {
  font-size: 13px;
  margin-top: 2px;
}

.match-success {
  color: #67c23a;
}

.match-error {
  color: #f56c6c;
}

/* 提交按钮 */
.password-submit {
  margin-top: 8px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.password-submit .el-button {
  min-width: 160px;
  border-radius: 10px;
  font-weight: 500;
  height: 44px;
  font-size: 15px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s ease;
}

.password-submit .el-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.password-submit .el-button:active {
  transform: translateY(0);
}

.password-submit .el-button.is-disabled {
  background: #c0c4cc !important;
  transform: none !important;
  box-shadow: none !important;
}

/* ===== 响应式 ===== */
@media (max-width: 992px) {
  .profile-container {
    grid-template-columns: 1fr;
  }
  
  .profile-card {
    display: flex;
    align-items: center;
    text-align: left;
    gap: 24px;
    padding: 24px;
  }
  
  .profile-card .avatar-wrapper {
    margin-bottom: 0;
    flex-shrink: 0;
  }
}

@media (max-width: 768px) {
  .profile-page {
    padding: 12px;
  }
  
  .profile-card {
    flex-direction: column;
    text-align: center;
    padding: 24px 16px;
  }
  
  .profile-card .avatar-wrapper {
    margin-bottom: 12px;
  }
  
  .info-section {
    padding: 16px;
  }
  
  .password-submit .el-button {
    width: 100%;
  }
}
</style>