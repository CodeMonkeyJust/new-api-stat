<template>
  <el-card class="dashboard">
    <template #header>
      <div class="card-header">
        <span>仪表盘</span>
        <el-button type="primary" size="small" @click="loadData" :loading="loading">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </template>

    <el-row :gutter="20" class="summary-row">
      <el-col :span="8">
        <el-card class="summary-card total">
          <template #header>
            <div class="card-title">汇总统计</div>
          </template>
          <div class="summary-content">
            <div class="summary-item">
              <span class="label">请求数</span>
              <span class="value">{{ formatNumber(data.total?.totalCount || 0) }}</span>
            </div>
            <div class="summary-item">
              <span class="label">输入Token</span>
              <span class="value">{{ formatNumber(data.total?.promptTokens || 0) }}</span>
            </div>
            <div class="summary-item">
              <span class="label">输出Token</span>
              <span class="value">{{ formatNumber(data.total?.completionTokens || 0) }}</span>
            </div>
            <div class="summary-item">
              <span class="label">花费(美元)</span>
              <span class="value">{{ (data.total?.totalCost || 0).toFixed(2) }}</span>
            </div>
            <div class="summary-item">
              <span class="label">平均耗时(ms)</span>
              <span class="value">{{ (data.total?.avgTime || 0).toFixed(2) }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="summary-card today">
          <template #header>
            <div class="card-title">今日统计</div>
          </template>
          <div class="summary-content">
            <div class="summary-item">
              <span class="label">请求数</span>
              <span class="value">{{ formatNumber(data.today?.totalCount || 0) }}</span>
            </div>
            <div class="summary-item">
              <span class="label">输入Token</span>
              <span class="value">{{ formatNumber(data.today?.promptTokens || 0) }}</span>
            </div>
            <div class="summary-item">
              <span class="label">输出Token</span>
              <span class="value">{{ formatNumber(data.today?.completionTokens || 0) }}</span>
            </div>
            <div class="summary-item">
              <span class="label">花费(美元)</span>
              <span class="value">{{ (data.today?.totalCost || 0).toFixed(2) }}</span>
            </div>
            <div class="summary-item">
              <span class="label">平均耗时(ms)</span>
              <span class="value">{{ (data.today?.avgTime || 0).toFixed(2) }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="summary-card yesterday">
          <template #header>
            <div class="card-title">昨日统计</div>
          </template>
          <div class="summary-content">
            <div class="summary-item">
              <span class="label">请求数</span>
              <span class="value">{{ formatNumber(data.yesterday?.totalCount || 0) }}</span>
            </div>
            <div class="summary-item">
              <span class="label">输入Token</span>
              <span class="value">{{ formatNumber(data.yesterday?.promptTokens || 0) }}</span>
            </div>
            <div class="summary-item">
              <span class="label">输出Token</span>
              <span class="value">{{ formatNumber(data.yesterday?.completionTokens || 0) }}</span>
            </div>
            <div class="summary-item">
              <span class="label">花费(美元)</span>
              <span class="value">{{ (data.yesterday?.totalCost || 0).toFixed(2) }}</span>
            </div>
            <div class="summary-item">
              <span class="label">平均耗时(ms)</span>
              <span class="value">{{ (data.yesterday?.avgTime || 0).toFixed(2) }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="top-users-card">
      <template #header>
        <div class="card-title">最近7天每日Top用户</div>
      </template>
      <div ref="chartContainer" class="chart-container"></div>
    </el-card>
  </el-card>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { getDashboard } from '@/api/analyzer'
import type { DashboardResponse } from '@/api/analyzer'

const loading = ref(false)
const data = ref<DashboardResponse>({
  total: { totalCount: 0, totalTokens: 0, promptTokens: 0, completionTokens: 0, totalCost: 0, avgTime: 0 },
  today: { totalCount: 0, totalTokens: 0, promptTokens: 0, completionTokens: 0, totalCost: 0, avgTime: 0 },
  yesterday: { totalCount: 0, totalTokens: 0, promptTokens: 0, completionTokens: 0, totalCost: 0, avgTime: 0 },
  last7DaysTopUsers: []
})
const chartContainer = ref<HTMLElement>()
let chartInstance: echarts.ECharts | null = null

const loadData = async () => {
  loading.value = true
  try {
    const response = await getDashboard()
    data.value = response.data
    await nextTick()
    renderChart()
  } catch (error) {
    console.error('Load data error:', error)
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const formatNumber = (num: number): string => {
  if (num >= 1000000) {
    return (num / 1000000).toFixed(2) + 'M'
  } else if (num >= 1000) {
    return (num / 1000).toFixed(2) + 'K'
  }
  return num.toString()
}

const renderChart = () => {
  if (!chartContainer.value) return

  if (chartInstance) {
    chartInstance.dispose()
  }

  chartInstance = echarts.init(chartContainer.value)

  const dates = data.value.last7DaysTopUsers.map(item => item.date)
  const costData = data.value.last7DaysTopUsers.map(item => ({
    value: item.topCostValue,
    name: item.topCostUser
  }))
  const promptTokensData = data.value.last7DaysTopUsers.map(item => ({
    value: item.topPromptTokensValue,
    name: item.topPromptTokensUser
  }))
  const completionTokensData = data.value.last7DaysTopUsers.map(item => ({
    value: item.topCompletionTokensValue,
    name: item.topCompletionTokensUser
  }))

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'none'
      },
      formatter: (params: any) => {
        let result = `<div style="font-weight: bold; margin-bottom: 5px;">${params[0].axisValue}</div>`
        params.forEach((param: any) => {
          const data = param.data
          const name = data.name
          const val = data.value
          const formattedVal = param.seriesName === '花费' ? `$${val.toFixed(2)}` : formatNumber(val)
          result += `<div style="display: flex; justify-content: space-between; min-width: 200px;">
            <span style="color: ${param.color}; margin-right: 10px;">${param.seriesName}:</span>
            <span style="font-weight: 500;">${name}</span>
            <span style="font-weight: bold; color: #67C23A;">${formattedVal}</span>
          </div>`
        })
        return result
      }
    },
    legend: {
      data: ['花费', '输入Token', '输出Token'],
      top: 0
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '15%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: dates,
      axisLabel: {
        rotate: 45
      }
    },
    yAxis: [
      {
        type: 'value',
        name: '花费(美元)',
        position: 'left',
        axisLabel: {
          formatter: '${value}'
        }
      },
      {
        type: 'value',
        name: 'Token数',
        position: 'right',
        axisLabel: {
          formatter: (value: number) => formatNumber(value)
        }
      }
    ],
    series: [
      {
        name: '花费',
        type: 'bar',
        data: costData,
        itemStyle: {
          color: '#F56C6C'
        },
        label: {
          show: true,
          position: 'top',
          formatter: (params: any) => {
            const username = params.data.name.length > 10 ? params.data.name.substring(0, 10) + '...' : params.data.name
            return `${username}\n$${params.data.value.toFixed(2)}`
          }
        }
      },
      {
        name: '输入Token',
        type: 'bar',
        yAxisIndex: 1,
        data: promptTokensData,
        itemStyle: {
          color: '#409EFF'
        },
        label: {
          show: true,
          position: 'top',
          formatter: (params: any) => {
            const username = params.data.name.length > 10 ? params.data.name.substring(0, 10) + '...' : params.data.name
            return `${username}\n${formatNumber(params.data.value)}`
          }
        }
      },
      {
        name: '输出Token',
        type: 'bar',
        yAxisIndex: 1,
        data: completionTokensData,
        itemStyle: {
          color: '#67C23A'
        },
        label: {
          show: true,
          position: 'top',
          formatter: (params: any) => {
            const username = params.data.name.length > 10 ? params.data.name.substring(0, 10) + '...' : params.data.name
            return `${username}\n${formatNumber(params.data.value)}`
          }
        }
      }
    ]
  }

  chartInstance.setOption(option)
}

const handleResize = () => {
  chartInstance?.resize()
}

onMounted(() => {
  loadData()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  if (chartInstance) {
    chartInstance.dispose()
  }
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.dashboard {
  height: 100%;
  background: transparent;
  border: none;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0;
}

.card-header span {
  font-size: 24px;
  font-weight: 700;
  background: linear-gradient(135deg, #2563EB 0%, #7C3AED 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.summary-row {
  margin-bottom: 24px;
}

.summary-card {
  border-radius: 16px;
  border: none;
  overflow: hidden;
  transition: all 0.3s ease;
  cursor: pointer;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
}

.summary-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
}

.summary-card.total {
  border-left: none;
  position: relative;
}

.summary-card.total::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 4px;
  background: linear-gradient(180deg, #2563EB 0%, #3B82F6 100%);
  border-radius: 16px 0 0 16px;
}

.summary-card.today {
  border-left: none;
  position: relative;
}

.summary-card.today::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 4px;
  background: linear-gradient(180deg, #10b981 0%, #059669 100%);
  border-radius: 16px 0 0 16px;
}

.summary-card.yesterday {
  border-left: none;
  position: relative;
}

.summary-card.yesterday::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 4px;
  background: linear-gradient(180deg, #F59E0B 0%, #D97706 100%);
  border-radius: 16px 0 0 16px;
}

.card-title {
  font-weight: 700;
  font-size: 18px;
  color: #1E293B;
  letter-spacing: -0.01em;
}

.summary-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 4px 0;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-radius: 10px;
  background: linear-gradient(135deg, rgba(248, 250, 252, 0.8) 0%, rgba(241, 245, 249, 0.8) 100%);
  border: 1px solid rgba(226, 232, 240, 0.6);
  transition: all 0.2s ease;
}

.summary-item:hover {
  background: linear-gradient(135deg, rgba(241, 245, 249, 1) 0%, rgba(226, 232, 240, 0.9) 100%);
  transform: translateX(4px);
}

.summary-item:last-child {
  border-bottom: 1px solid rgba(226, 232, 240, 0.6);
}

.summary-item .label {
  color: #64748B;
  font-size: 14px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 8px;
}

.summary-item .label::before {
  content: '';
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: linear-gradient(135deg, #2563EB 0%, #7C3AED 100%);
}

.summary-item .value {
  color: #0F172A;
  font-weight: 700;
  font-size: 20px;
  letter-spacing: -0.02em;
}

.top-users-card {
  margin-top: 24px;
  border-radius: 16px;
  border: none;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
}

.chart-container {
  width: 100%;
  height: 450px;
  padding: 12px 0;
}

.user-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  padding: 8px 12px;
  border-radius: 8px;
  background: rgba(248, 250, 252, 0.5);
  transition: all 0.2s ease;
}

.user-info:hover {
  background: rgba(241, 245, 249, 1);
}

.user-info .username {
  color: #2563EB;
  font-weight: 600;
  font-size: 14px;
}

.user-info .value {
  color: #10b981;
  font-weight: 700;
  font-size: 15px;
}

/* Responsive Design */
@media (max-width: 768px) {
  .summary-row {
    margin-bottom: 16px;
  }

  .card-header span {
    font-size: 20px;
  }

  .card-title {
    font-size: 16px;
  }

  .summary-item {
    padding: 10px 12px;
  }

  .summary-item .value {
    font-size: 18px;
  }

  .chart-container {
    height: 350px;
  }
}

/* Accessibility */
@media (prefers-reduced-motion: reduce) {
  .summary-card,
  .summary-item,
  .user-info {
    transition: none;
  }

  .summary-card:hover,
  .summary-item:hover {
    transform: none;
  }
}
</style>
