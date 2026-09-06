<template>
  <div class="address-page">
    <div class="page-header">
      <h1 class="page-title">📍 地址管理</h1>
      <el-button type="primary" @click="showAddDialog">新增地址</el-button>
    </div>
    
    <div class="address-list" v-loading="loading">
      <div 
        class="address-item" 
        v-for="addr in addressList" 
        :key="addr.addressId"
        :class="{ 'is-default': addr.isDefault === 1 }"
      >
        <div class="address-info">
          <div class="address-header">
            <span class="receiver-name">{{ addr.receiverName }}</span>
            <span class="receiver-phone">{{ addr.receiverPhone }}</span>
            <span class="default-badge" v-if="addr.isDefault === 1">默认</span>
          </div>
          <p class="address-detail">
            {{ addr.province }} {{ addr.city }} {{ addr.district }} {{ addr.detailAddress }}
          </p>
        </div>
        <div class="address-actions">
          <el-button 
            v-if="addr.isDefault !== 1" 
            size="small" 
            @click="setDefault(addr.addressId)"
          >
            设为默认
          </el-button>
          <el-button size="small" @click="showEditDialog(addr)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteAddress(addr.addressId)">
            删除
          </el-button>
        </div>
      </div>
      
      <el-empty v-if="!loading && addressList.length === 0" description="暂无地址" />
    </div>
    
    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="520px"
      @close="resetForm"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item label="收货人" prop="receiverName">
          <el-input v-model="form.receiverName" placeholder="请输入收货人姓名" />
        </el-form-item>
        
        <el-form-item label="手机号" prop="receiverPhone">
          <el-input v-model="form.receiverPhone" placeholder="请输入收货人手机号" />
        </el-form-item>
        
        <el-form-item label="省份" prop="province">
          <el-input v-model="form.province" placeholder="请输入省份" />
        </el-form-item>
        
        <el-form-item label="城市" prop="city">
          <el-input v-model="form.city" placeholder="请输入城市" />
        </el-form-item>
        
        <el-form-item label="区/县" prop="district">
          <el-input v-model="form.district" placeholder="请输入区/县" />
        </el-form-item>
        
        <el-form-item label="详细地址" prop="detailAddress">
          <el-input v-model="form.detailAddress" type="textarea" :rows="2" placeholder="请输入详细地址" />
        </el-form-item>
        
        <el-form-item label="设为默认">
          <el-switch v-model="form.isDefault" :active-value="1" :inactive-value="0" />
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
import { addressApi } from '@/api/address'

const loading = ref(false)
const submitting = ref(false)
const addressList = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const editingId = ref(null)
const formRef = ref()

const form = reactive({
  receiverName: '',
  receiverPhone: '',
  province: '',
  city: '',
  district: '',
  detailAddress: '',
  isDefault: 0
})

const dialogTitle = computed(() => isEdit.value ? '编辑地址' : '新增地址')

const rules = {
  receiverName: [
    { required: true, message: '请输入收货人姓名', trigger: 'blur' },
    { max: 20, message: '姓名不能超过20个字符', trigger: 'blur' }
  ],
  receiverPhone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  province: [
    { required: true, message: '请输入省份', trigger: 'blur' }
  ],
  city: [
    { required: true, message: '请输入城市', trigger: 'blur' }
  ],
  district: [
    { required: true, message: '请输入区/县', trigger: 'blur' }
  ],
  detailAddress: [
    { required: true, message: '请输入详细地址', trigger: 'blur' },
    { max: 200, message: '地址不能超过200个字符', trigger: 'blur' }
  ]
}

const fetchAddressList = async () => {
  loading.value = true
  try {
    const result = await addressApi.getAddressList()
    // 处理不同响应格式
    if (Array.isArray(result)) {
      addressList.value = result
    } else if (result && result.code === 200) {
      addressList.value = result.data || []
    } else {
      addressList.value = []
    }
  } catch (error) {
    console.error('获取地址列表失败:', error)
    addressList.value = []
  } finally {
    loading.value = false
  }
}

const showAddDialog = () => {
  isEdit.value = false
  editingId.value = null
  resetForm()
  dialogVisible.value = true
}

const showEditDialog = (addr) => {
  isEdit.value = true
  editingId.value = addr.addressId
  Object.assign(form, {
    receiverName: addr.receiverName,
    receiverPhone: addr.receiverPhone,
    province: addr.province || '',
    city: addr.city || '',
    district: addr.district || '',
    detailAddress: addr.detailAddress,
    isDefault: addr.isDefault || 0
  })
  dialogVisible.value = true
}

const resetForm = () => {
  form.receiverName = ''
  form.receiverPhone = ''
  form.province = ''
  form.city = ''
  form.district = ''
  form.detailAddress = ''
  form.isDefault = 0
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
      if (isEdit.value) {
        if (!editingId.value) {
          ElMessage.error('地址ID无效')
          return
        }
        await addressApi.updateAddress(editingId.value, data)
        ElMessage.success('地址修改成功')
      } else {
        await addressApi.addAddress(data)
        ElMessage.success('地址添加成功')
      }
      dialogVisible.value = false
      await fetchAddressList()
    } catch (error) {
      console.error('提交地址失败:', error)
      ElMessage.error(isEdit.value ? '修改失败' : '添加失败')
    } finally {
      submitting.value = false
    }
  })
}

const setDefault = async (addressId) => {
  // 参数校验
  if (!addressId || addressId === 'undefined' || isNaN(addressId)) {
    ElMessage.error('地址ID无效')
    return
  }
  try {
    await addressApi.setDefaultAddress(addressId)
    ElMessage.success('设置成功')
    await fetchAddressList()
  } catch (error) {
    console.error('设置默认地址失败:', error)
    ElMessage.error('设置失败')
  }
}

const deleteAddress = async (addressId) => {
  // 参数校验 - 防止传递 undefined
  if (!addressId || addressId === 'undefined' || isNaN(addressId)) {
    ElMessage.error('地址ID无效，无法删除')
    return
  }
  
  try {
    await ElMessageBox.confirm('确定要删除该地址吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await addressApi.deleteAddress(addressId)
    ElMessage.success('删除成功')
    await fetchAddressList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除地址失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  fetchAddressList()
})
</script>

<style scoped>
.address-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  font-size: 24px;
  color: #303133;
  margin: 0;
}

.address-list {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.address-item {
  background: #ffffff;
  border-radius: 8px;
  padding: 16px 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  border: 1px solid #ebeef5;
  transition: all 0.3s;
}

.address-item.is-default {
  border-color: #409EFF;
  background: #ecf5ff;
}

.address-item:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

.address-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 4px;
}

.receiver-name {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

.receiver-phone {
  font-size: 14px;
  color: #606266;
}

.default-badge {
  font-size: 12px;
  color: #409EFF;
  background: #d9ecff;
  padding: 2px 10px;
  border-radius: 12px;
  font-weight: 500;
}

.address-detail {
  font-size: 14px;
  color: #606266;
  margin: 0 0 8px 0;
}

.address-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

@media (max-width: 768px) {
  .address-list {
    grid-template-columns: 1fr;
  }
}
</style>