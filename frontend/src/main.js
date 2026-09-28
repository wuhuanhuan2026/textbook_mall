// frontend/src/main.js
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import App from './App.vue'
import router from './router'
import './style.css'

// 导入 ECharts（用于仪表盘图表）
import * as echarts from 'echarts'

const app = createApp(App)
const pinia = createPinia()

// 注册所有 Element Plus 图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 全局挂载 echarts
app.config.globalProperties.$echarts = echarts

app.use(pinia)
app.use(router)
app.use(ElementPlus)

app.mount('#app')