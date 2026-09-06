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
      'axios'
    ]
  },
  
  server: {
    port: 8979,
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
        target: 'http://localhost:8090',
        changeOrigin: true,
        rewrite: (path) => path  // 保留 /api 路径
      },
      // ✅ 图片请求直接代理到 Book 服务（绕过 Gateway）
      '/images': {
        target: 'http://localhost:8083',
        changeOrigin: true
      }
    }
  }
})