<template>
  <div class="admin-dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-row">
      <el-col :span="6" v-for="stat in statistics" :key="stat.label">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" :style="{ background: stat.color }">
            <el-icon><component :is="stat.icon" /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ stat.value }}</div>
            <div class="stat-label">{{ stat.label }}</div>
            <div class="stat-change" v-if="stat.change !== undefined">
              <span :class="stat.change >= 0 ? 'up' : 'down'">
                {{ stat.change >= 0 ? '↑' : '↓' }} {{ Math.abs(stat.change) }}%
              </span>
              较昨日
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="chart-header">
              <span>📈 销售趋势</span>
              <el-radio-group v-model="trendType" size="small" @change="fetchTrend">
                <el-radio-button value="day">日</el-radio-button>
                <el-radio-button value="week">周</el-radio-button>
                <el-radio-button value="month">月</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div ref="trendChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <span>📊 订单分布</span>
          </template>
          <div ref="pieChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最近订单 -->
    <el-card shadow="hover" class="table-card">
      <template #header>
        <div class="table-header">
          <span>📋 最近订单</span>
          <el-button type="primary" link @click="goToOrders">查看全部 →</el-button>
        </div>
      </template>
      <el-table :data="recentOrders" style="width: 100%" v-loading="orderLoading">
        <el-table-column prop="orderId" label="订单号" width="160" />
        <el-table-column prop="userId" label="用户ID" width="80" />
        <el-table-column prop="totalAmount" label="金额" width="120">
          <template #default="{ row }">
            ¥{{ row.totalAmount?.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="orderStatus" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.orderStatus)" size="small">
              {{ getStatusText(row.orderStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="180" />
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button type="primary" link @click="goToOrderDetail(row.orderId)">
              查看
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { adminApi } from '@/api/admin'

const router = useRouter()
const trendType = ref('day')
const trendChartRef = ref(null)
const pieChartRef = ref(null)
let trendChart = null
let pieChart = null

const orderLoading = ref(false)
const statistics = ref([])
const recentOrders = ref([])

// 状态映射
const statusMap = {
  0: { text: '待付款', type: 'warning' },
  1: { text: '待发货', type: 'info' },
  2: { text: '待收货', type: 'primary' },
  3: { text: '已完成', type: 'success' },
  4: { text: '已取消', type: 'danger' }
}

const getStatusText = (status) => statusMap[status]?.text || '未知'
const getStatusType = (status) => statusMap[status]?.type || 'info'

// 获取统计数据
const fetchStatistics = async () => {
  try {
    const res = await adminApi.getDashboardStatistics()
    if (res && res.code === 200) {
      const data = res.data
      statistics.value = [
        { 
          label: '用户总数', 
          value: data.totalUsers || 0, 
          icon: 'User', 
          color: '#409EFF',
          change: data.userGrowth
        },
        { 
          label: '教材总数', 
          value: data.totalBooks || 0, 
          icon: 'Notebook', 
          color: '#67C23A',
          change: data.bookGrowth
        },
        { 
          label: '订单总数', 
          value: data.totalOrders || 0, 
          icon: 'List', 
          color: '#E6A23C',
          change: data.orderGrowth
        },
        { 
          label: '总销售额', 
          value: `¥${(data.totalSales || 0).toFixed(2)}`, 
          icon: 'Money', 
          color: '#F56C6C',
          change: data.salesGrowth
        }
      ]
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

// 获取趋势数据
const fetchTrend = async () => {
  try {
    const res = await adminApi.getDashboardTrend(trendType.value)
    if (res && res.code === 200) {
      const data = res.data
      renderTrendChart(data)
    }
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
      trigger: 'axis',
      axisPointer: { type: 'cross' }
    },
    legend: {
      data: ['订单数', '销售额', '新增用户'],
      bottom: 0
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '20%',
      top: '5%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: data.dates || []
    },
    yAxis: [
      {
        type: 'value',
        name: '数量',
        splitLine: { lineStyle: { type: 'dashed' } }
      },
      {
        type: 'value',
        name: '金额 (元)',
        splitLine: { show: false }
      }
    ],
    series: [
      {
        name: '订单数',
        type: 'line',
        data: data.orderCounts || [],
        smooth: true,
        lineStyle: { color: '#409EFF' },
        areaStyle: { color: 'rgba(64, 158, 255, 0.1)' }
      },
      {
        name: '销售额',
        type: 'line',
        yAxisIndex: 1,
        data: data.salesAmounts || [],
        smooth: true,
        lineStyle: { color: '#F56C6C' },
        areaStyle: { color: 'rgba(245, 108, 108, 0.1)' }
      },
      {
        name: '新增用户',
        type: 'bar',
        data: data.userRegistrations || [],
        itemStyle: { color: '#67C23A' }
      }
    ]
  }
  
  trendChart.setOption(option)
  trendChart.resize()
}

// 获取订单分布
const fetchOrderDistribution = async () => {
  try {
    const res = await adminApi.getOrderStatusCount()
    if (res && res.code === 200) {
      const data = res.data || {}
      const pieData = Object.entries(data).map(([key, value]) => ({
        name: getStatusText(Number(key)),
        value: value
      }))
      renderPieChart(pieData)
    }
  } catch (error) {
    console.error('获取订单分布失败:', error)
  }
}

// 渲染饼图
const renderPieChart = (data) => {
  if (!pieChartRef.value) return
  
  if (!pieChart) {
    pieChart = echarts.init(pieChartRef.value)
  }
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: '5%',
      top: 'center',
      itemWidth: 12,
      itemHeight: 12
    },
    series: [
      {
        name: '订单状态',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 4,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 14,
            fontWeight: 'bold'
          }
        },
        data: data.length > 0 ? data : [{ name: '暂无数据', value: 1 }]
      }
    ]
  }
  
  pieChart.setOption(option)
  pieChart.resize()
}

// 获取最近订单
const fetchRecentOrders = async () => {
  orderLoading.value = true
  try {
    const res = await adminApi.getOrders({ pageNum: 1, pageSize: 5 })
    if (res && res.code === 200) {
      const pageData = res.data
      recentOrders.value = pageData?.list || []
    }
  } catch (error) {
    console.error('获取最近订单失败:', error)
  } finally {
    orderLoading.value = false
  }
}

const goToOrders = () => {
  router.push('/admin/orders')
}

const goToOrderDetail = (orderId) => {
  router.push(`/admin/orders?orderId=${orderId}`)
}

// 窗口变化自适应
const handleResize = () => {
  trendChart?.resize()
  pieChart?.resize()
}

onMounted(async () => {
  await fetchStatistics()
  await fetchRecentOrders()
  await fetchOrderDistribution()
  await nextTick()
  await fetchTrend()
  
  window.addEventListener('resize', handleResize)
})
</script>

<style scoped>
.admin-dashboard {
  max-width: 1400px;
  margin: 0 auto;
}

.stat-row {
  margin-bottom: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 8px 16px;
}

.stat-card :deep(.el-card__body) {
  display: flex;
  align-items: center;
  width: 100%;
  padding: 16px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  font-size: 24px;
  flex-shrink: 0;
}

.stat-content {
  margin-left: 16px;
  flex: 1;
}

.stat-number {
  font-size: 24px;
  font-weight: 700;
  color: #303133;
  line-height: 1.2;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 2px;
}

.stat-change {
  font-size: 12px;
  color: #909399;
  margin-top: 2px;
}

.stat-change .up {
  color: #67C23A;
}

.stat-change .down {
  color: #F56C6C;
}

.chart-card {
  margin-bottom: 20px;
}

.chart-card :deep(.el-card__header) {
  padding: 12px 20px;
  border-bottom: 1px solid #ebeef5;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-container {
  height: 300px;
  width: 100%;
}

.table-card :deep(.el-card__header) {
  padding: 12px 20px;
  border-bottom: 1px solid #ebeef5;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

@media (max-width: 1200px) {
  .stat-card :deep(.el-card__body) {
    flex-direction: column;
    text-align: center;
  }
  .stat-content {
    margin-left: 0;
    margin-top: 8px;
  }
}

@media (max-width: 768px) {
  .stat-row .el-col {
    margin-bottom: 12px;
  }
}
</style>