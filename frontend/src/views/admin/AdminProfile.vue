<template>
  <div class="admin-profile">
    <el-row :gutter="24">
      <!-- 左侧：个人信息 -->
      <el-col :span="14">
        <el-card shadow="hover">
          <template #header>
            <span>📋 个人信息</span>
          </template>
          
          <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
            <el-form-item label="用户名">
              <el-input v-model="form.username" disabled />
            </el-form-item>
            
            <el-form-item label="姓名" prop="realName">
              <el-input v-model="form.realName" placeholder="请输入真实姓名" />
            </el-form-item>
            
            <el-form-item label="角色">
              <el-input v-model="form.roleDesc" disabled />
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
            
            <el-form-item>
              <el-button type="primary" @click="saveProfile" :loading="saving">
                保存修改
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <!-- 右侧：修改密码 -->
      <el-col :span="10">
        <el-card shadow="hover">
          <template #header>
            <span>🔒 修改密码</span>
          </template>
          
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
              <el-button type="primary" @click="updatePassword" :loading="pwdLoading">
                修改密码
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { adminApi } from '@/api/admin'

const router = useRouter()
const saving = ref(false)
const pwdLoading = ref(false)
const formRef = ref()
const pwdFormRef = ref()

const form = reactive({
  adminId: null,
  username: '',
  realName: '',
  role: '',
  roleDesc: '',
  phone: '',
  email: '',
  avatar: '',
  status: 1
})

const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const rules = {
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }],
  email: [{ type: 'email', message: '邮箱格式不正确', trigger: 'blur' }]
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

const loadProfile = async () => {
  try {
    const res = await adminApi.getAdminInfo()
    if (res && res.code === 200) {
      const data = res.data
      form.adminId = data.adminId
      form.username = data.username
      form.realName = data.realName || ''
      form.role = data.role
      form.roleDesc = data.roleDesc || data.role
      form.phone = data.phone || ''
      form.email = data.email || ''
      form.avatar = data.avatar || ''
      form.status = data.status
    }
  } catch (error) {
    console.error('获取个人信息失败:', error)
  }
}

const saveProfile = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    saving.value = true
    try {
      const data = {
        realName: form.realName,
        phone: form.phone,
        email: form.email,
        avatar: form.avatar
      }
      await adminApi.updateProfile(data)
      ElMessage.success('保存成功')
      // 更新本地存储
      const info = localStorage.getItem('admin_info')
      if (info) {
        try {
          const parsed = JSON.parse(info)
          parsed.realName = form.realName
          parsed.phone = form.phone
          parsed.email = form.email
          parsed.avatar = form.avatar
          localStorage.setItem('admin_info', JSON.stringify(parsed))
        } catch (e) {
          console.error('更新本地存储失败:', e)
        }
      }
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
      const data = {
        oldPassword: pwdForm.oldPassword,
        newPassword: pwdForm.newPassword
      }
      await adminApi.updatePassword(data)
      ElMessage.success('密码修改成功，请重新登录')
      // 清除管理员 token 并跳转到登录页
      localStorage.removeItem('admin_token')
      localStorage.removeItem('admin_info')
      setTimeout(() => {
        router.push('/admin/login')
      }, 1500)
    } catch (error) {
      ElMessage.error('密码修改失败')
    } finally {
      pwdLoading.value = false
    }
  })
}

onMounted(loadProfile)
</script>

<style scoped>
.admin-profile {
  max-width: 1200px;
  margin: 0 auto;
}

@media (max-width: 768px) {
  .admin-profile .el-col {
    margin-bottom: 16px;
  }
}
</style>