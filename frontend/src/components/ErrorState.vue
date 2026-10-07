<template>
  <div class="error-state">
    <el-result icon="error" title="出错了" :sub-title="message || '加载失败'">
      <template #extra>
        <el-button type="primary" @click="$emit('retry')">重试</el-button>
      </template>
    </el-result>
  </div>
</template>

<script setup>
defineProps({ message: { type: String, default: '' } })
defineEmits(['retry'])
</script>

<style scoped>
.error-state { padding: 40px 20px; }
</style>
