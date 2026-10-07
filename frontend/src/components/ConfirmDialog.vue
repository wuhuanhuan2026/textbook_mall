<template>
  <el-dialog v-model="visible" :title="title" width="400px" :close-on-click-modal="false">
    <p>{{ message }}</p>
    <template #footer>
      <el-button @click="handleCancel">取消</el-button>
      <el-button type="primary" @click="handleConfirm" :loading="loading">确认</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'
const props = defineProps({
  modelValue: { type: Boolean, default: false },
  title: { type: String, default: '提示' },
  message: { type: String, default: '' },
  loading: { type: Boolean, default: false }
})
const emit = defineEmits(['update:modelValue', 'confirm', 'cancel'])
const visible = ref(props.modelValue)
watch(() => props.modelValue, val => { visible.value = val })
watch(visible, val => { emit('update:modelValue', val) })
function handleConfirm() { emit('confirm') }
function handleCancel() { visible.value = false; emit('cancel') }
</script>
