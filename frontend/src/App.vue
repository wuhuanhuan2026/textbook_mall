<template>
  <el-config-provider :locale="zhCn">
    <div id="app">
      <Header v-if="showHeader" />
      <router-view />
      <Footer v-if="showFooter" />
    </div>
  </el-config-provider>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { ElConfigProvider } from 'element-plus'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import Header from '@/components/Header.vue'
import Footer from '@/components/Footer.vue'

const route = useRoute()

/**
 * 判断当前页面是否应该隐藏前台 Header / Footer
 * 隐藏规则：
 *   1. 登录、注册页面
 *   2. 所有后台路由（以 /admin 开头）
 */
const hideLayout = computed(() => {
  // 登录、注册页不显示
  const authPages = ['Login', 'Register']
  if (authPages.includes(route.name)) return true

  // 后台路由不显示（/admin 开头）
  if (route.path.startsWith('/admin')) return true

  return false
})

const showHeader = computed(() => !hideLayout.value)
const showFooter = computed(() => !hideLayout.value)
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

#app {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Microsoft YaHei', sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  min-height: 100vh;
  background: #f5f7fa;
}

a {
  text-decoration: none;
  color: inherit;
}

/* 滚动条美化 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
</style>