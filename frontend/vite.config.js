import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'
import { fileURLToPath } from 'url'

// 获取 __dirname（ES Module 方式）
const __dirname = path.dirname(fileURLToPath(import.meta.url))

export default defineConfig({
  plugins: [vue()],

  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src')
    }
  },

  // ✅ 优化依赖配置 - 强制重新构建
  optimizeDeps: {
    force: true,  // 强制重新优化依赖，解决缓存问题
    include: [
      'vue',
      'vue-router',
      'pinia',
      'element-plus',
      'axios',
      'echarts'          // ✅ 新增：仪表盘用到 echarts，预构建加快首次加载
    ]
  },

  server: {
    port: 8979,
    host: '0.0.0.0',     // ✅ 新增：允许局域网访问（手机/其他电脑调试）
    open: false,         // ✅ 新增：启动后不自动打开浏览器（可选）
    // ✅ 强制开启 HMR，确保热更新生效
    hmr: {
      overlay: true
    },
    // ✅ 开发服务器配置
    watch: {
      usePolling: true,  // 使用轮询检测文件变化（适用于某些文件系统）
      interval: 100      // 轮询间隔
    },
    proxy: {
      // API 请求代理到 Gateway
      '/api': {
        target: 'http://127.0.0.1:8090',   // ✅ 改为 127.0.0.1，与后端统一
        changeOrigin: true,
        rewrite: (path) => path            // 保留 /api 路径
      },
      // ✅ 图片请求也走 Gateway（推荐，统一入口）
      '/images': {
        target: 'http://127.0.0.1:8090',   // ✅ 改为网关地址，不再直连 8083
        changeOrigin: true
      }
    }
  }
})