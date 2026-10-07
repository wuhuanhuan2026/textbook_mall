<template>
  <div class="loading-state">
    <el-skeleton :rows="4" animated />
    <p class="loading-text">加载中...</p>
  </div>
</template>

<style scoped>
.loading-state { padding: 40px 20px; text-align: center; }
.loading-text { margin-top: 16px; color: #909399; font-size: 14px; }
</style>
