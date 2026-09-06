<template>
  <teleport to="body">
    <div v-if="visible" class="toast-wrapper">
      <div class="toast" :class="type">
        <span class="toast-icon">{{ iconMap[type] }}</span>
        <span class="toast-message">{{ message }}</span>
      </div>
    </div>
  </teleport>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const props = defineProps({
  message: {
    type: String,
    required: true
  },
  type: {
    type: String,
    default: 'info',
    validator: (val) => ['info', 'success', 'warning', 'error'].includes(val)
  },
  duration: {
    type: Number,
    default: 3000
  }
})

const visible = ref(true)

const iconMap = {
  info: 'ℹ️',
  success: '✅',
  warning: '⚠️',
  error: '❌'
}

onMounted(() => {
  setTimeout(() => {
    visible.value = false
  }, props.duration)
})
</script>

<style scoped>
.toast-wrapper {
  position: fixed;
  top: 24px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 9999;
  animation: slideDown 0.3s ease;
}

.toast {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 24px;
  border-radius: 8px;
  color: #ffffff;
  font-size: 14px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  min-width: 200px;
  justify-content: center;
}

.toast.info {
  background: #409EFF;
}

.toast.success {
  background: #67C23A;
}

.toast.warning {
  background: #E6A23C;
}

.toast.error {
  background: #F56C6C;
}

.toast-icon {
  font-size: 18px;
}

.toast-message {
  font-weight: 500;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateX(-50%) translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(-50%) translateY(0);
  }
}
</style>