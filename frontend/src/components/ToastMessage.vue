<template>
  <div></div>
</template>

<script setup>
import { ElMessage } from 'element-plus'

export function showSuccess(message) { ElMessage.success(message) }
export function showError(message) { ElMessage.error(message) }
export function showWarning(message) { ElMessage.warning(message) }
export function showInfo(message) { ElMessage.info(message) }
</script>
