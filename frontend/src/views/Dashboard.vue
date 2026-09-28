<template>
  <div class="admin-dashboard">
    <div class="dashboard-header">
      <h1>📊 管理后台</h1>
      <el-button type="danger" @click="handleLogout">退出登录</el-button>
    </div>

    <!-- 统计卡片 -->
    <div class="dashboard-stats">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-number">{{ stats.totalUsers }}</div>
            <div class="stat-label">用户总数</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-number">{{ stats.totalOrders }}</div>
            <div class="stat-label">订单总数</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-number">{{ stats.totalBooks }}</div>
            <div class="stat-label">教材总数</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-number">¥{{ stats.totalSales }}</div>
            <div class="stat-label">总销售额</div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 销售趋势图 -->
    <div class="dashboard-content">
      <el-row :gutter="20">
        <el-col :span="16">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span>📈 销售趋势</span>
                <el-radio-group v-model="trendType" size="small" @change="fetchTrend">
                  <el-radio-button label="day">日</el-radio-button>
                  <el-radio-button label="week">周</el-radio-button>
                  <el-radio-button label="month">月</el-radio-button>
                </el-radio-group>
              </div>
            </template>
            <div class="chart-container" ref="trendChartRef"></div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="hover">
            <template #header>
              <span>📊 订单分布</span>
            </template>
            <div class="chart-container" ref="pieChartRef"></div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import request from '@/utils/request'

const router = useRouter()

// 统计数据
const stats = ref({
  totalUsers: 0,
  totalOrders: 0,
  totalBooks: 0,
  totalSales: 0
})

// 趋势图
const trendType = ref('day')
const trendChartRef = ref(null)
const pieChartRef = ref(null)
let trendChart = null
let pieChart = null

// 退出登录
const handleLogout = () => {
  localStorage.removeItem('admin_token')
  localStorage.removeItem('admin_info')
  ElMessage.success('退出成功')
  router.push('/admin/login')
}

// 获取统计数据
const fetchStatistics = async () => {
  try {
    const res = await request.get('/admin/dashboard/statistics')
    const data = res.data
    stats.value = {
      totalUsers: data.totalUsers,
      totalOrders: data.totalOrders,
      totalBooks: data.totalBooks,
      totalSales: data.totalSales
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
    ElMessage.error('获取统计数据失败')
  }
}

// 获取趋势数据
const fetchTrend = async () => {
  try {
    const res = await request.get('/admin/dashboard/trend', {
      params: { type: trendType.value }
    })
    const data = res.data
    renderTrendChart(data)
  } catch (error) {
    console.error('获取趋势数据失败:', error)
  }
}

// 渲染趋势图
const renderTrendChart = (data) => {
  if (!trendChartRef.value) return
  if (!trendChart) {
    trendChart = echarts.init(trendChartRef.value)
  }
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['订单数', '销售额', '新增用户']
    },
    xAxis: {
      type: 'category',
      data: data.dates || []
    },
    yAxis: [
      {
        type: 'value',
        name: '订单数/用户数'
      },
      {
        type: 'value',
        name: '销售额(元)'
      }
    ],
    series: [
      {
        name: '订单数',
        type: 'bar',
        data: data.orderCounts || [],
        itemStyle: { color: '#67C23A' }
      },
      {
        name: '销售额',
        type: 'line',
        yAxisIndex: 1,
        data: data.salesAmounts || [],
        itemStyle: { color: '#409EFF' },
        smooth: true
      },
      {
        name: '新增用户',
        type: 'line',
        data: data.userRegistrations || [],
        itemStyle: { color: '#E6A23C' },
        smooth: true
      }
    ]
  }
  trendChart.setOption(option)
}

// 渲染订单分布饼图（暂时用统计数据模拟）
const renderPieChart = () => {
  if (!pieChartRef.value) return
  if (!pieChart) {
    pieChart = echarts.init(pieChartRef.value)
  }
  const option = {
    tooltip: {
      trigger: 'item'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '订单分布',
        type: 'pie',
        radius: '50%',
        data: [
          { value: stats.value.totalOrders, name: '总订单' },
          { value: stats.value.totalUsers, name: '总用户' },
          { value: stats.value.totalBooks, name: '总教材' }
        ],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }
  pieChart.setOption(option)
}

// 窗口大小变化时重绘图表
const handleResize = () => {
  trendChart?.resize()
  pieChart?.resize()
}

onMounted(async () => {
  await fetchStatistics()
  await nextTick()
  renderPieChart()
  await fetchTrend()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  trendChart?.dispose()
  pieChart?.dispose()
})
</script>

<style scoped>
.admin-dashboard {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.dashboard-header h1 {
  font-size: 24px;
  color: #303133;
  margin: 0;
}

.stat-card {
  text-align: center;
  padding: 20px 0;
}

.stat-number {
  font-size: 32px;
  font-weight: 700;
  color: #409EFF;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 8px;
}

.dashboard-content {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-container {
  width: 100%;
  height: 350px;
}
</style>