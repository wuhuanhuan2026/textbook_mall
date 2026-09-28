<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <h1>🔐 管理员登录</h1>
        <p>教材商城后台管理系统</p>
      </div>
      
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        @submit.prevent="handleLogin"
        size="large"
      >
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入管理员用户名"
            prefix-icon="User"
            clearable
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            show-password
            clearable
            @keyup.enter="handleLogin"
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            @click="handleLogin"
            style="width: 100%"
          >
            {{ loading ? '登录中...' : '登 录' }}
          </el-button>
        </el-form-item>

        <div class="login-footer">
          <span>返回 </span>
          <el-link type="primary" @click="goToUserLogin">用户登录</el-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { adminApi } from '@/api/admin'
// ✅ 导入存储工具
import { setAdminToken, setAdminInfo, getAdminToken } from '@/utils/storage'

const router = useRouter()
const route = useRoute()

const loading = ref(false)
const formRef = ref()

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入管理员用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  console.log('=== 🔐 管理员登录开始 ===')
  
  if (!formRef.value) {
    console.error('❌ formRef 为空')
    return
  }
  
  await formRef.value.validate(async (valid) => {
    console.log('表单验证结果:', valid)
    if (!valid) return
    
    console.log('📤 登录请求参数:', { username: form.username, password: '***' })
    
    loading.value = true
    try {
      const res = await adminApi.login(form)
      
      console.log('📥 登录响应:', res)
      console.log('📥 响应 code:', res?.code)
      console.log('📥 响应 data:', res?.data)
      console.log('📥 响应 token:', res?.data?.token)
      console.log('📥 响应 message:', res?.message)
      
      // 检查响应是否成功
      if (res.code === 200 && res.data) {
        // ✅ 检查 token 是否存在
        const token = res.data.token
        if (!token) {
          console.error('❌ token 为空！')
          ElMessage.error('登录失败：未获取到 token')
          loading.value = false
          return
        }
        
        console.log('✅ token 获取成功，长度:', token.length)
        
        // ✅ 使用统一的存储工具存储管理员信息
        setAdminToken(token)
        setAdminInfo(res.data)
        
        // ✅ 验证存储是否成功
        const savedToken = getAdminToken()
        console.log('💾 存储后读取 token:', savedToken)
        console.log('💾 token 是否匹配:', savedToken === token)
        
        if (!savedToken) {
          console.error('❌ token 存储失败！')
          ElMessage.error('登录状态保存失败，请重试')
          loading.value = false
          return
        }
        
        console.log('✅ 登录状态保存成功')
        ElMessage.success(res.message || '登录成功')
        
        // ✅ 延迟跳转，确保状态完全更新
        const redirect = route.query.redirect || '/admin/dashboard'
        console.log('🚀 准备跳转到:', redirect)
        
        setTimeout(() => {
          console.log('🚀 执行跳转:', redirect)
          router.push(redirect)
        }, 300)
        
      } else {
        console.warn('⚠️ 登录响应异常:', res)
        ElMessage.error(res.message || '登录失败')
      }
    } catch (error) {
      console.error('❌ 登录错误:', error)
      console.error('❌ 错误详情:', error.response?.data || error.message)
      ElMessage.error(error.message || '登录失败，请检查用户名和密码')
    } finally {
      loading.value = false
      console.log('=== 🔐 管理员登录结束 ===')
    }
  })
}

const goToUserLogin = () => {
  console.log('跳转到用户登录页')
  router.push('/login')
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
}

.login-card {
  width: 400px;
  padding: 40px 36px;
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.login-header h1 {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  margin-bottom: 8px;
}

.login-header p {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

.login-footer {
  text-align: center;
  font-size: 14px;
  color: #909399;
}

.login-footer .el-link {
  font-size: 14px;
}
</style>