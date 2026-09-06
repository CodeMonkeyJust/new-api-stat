<template>
  <el-card class="user-daily-view">
    <template #header>
      <div class="card-header">
        <span>人员统计</span>
      </div>
    </template>
    <DateUserSelector
      :loading="loading"
      default-date-mode="range"
      @date-change="handleDateChange"
      @date-range-change="handleDateRangeChange"
      @user-mode-change="handleUserModeChange"
      @user-change="handleUserChange"
      ref="dateUserSelector"
    />
    <div ref="chartContainer" class="chart-container" v-loading="loading"></div>
  </el-card>
</template>

<script setup lang="ts">

const readLegendState = (key: string, fallback: Record<string, boolean> | string[] | null = null) => {
  try {
    const value = localStorage.getItem(key)
    if (!value) return fallback
    const parsed = JSON.parse(value)
    return parsed && typeof parsed === 'object' ? parsed : fallback
  } catch {
    return fallback
  }
}

import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import type { QueryRequest } from '@/api/analyzer'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { getUserDaily } from '@/api/analyzer'
import type { UserDailyItem } from '@/api/analyzer'
import DateUserSelector from '@/components/DateUserSelector.vue'

const loading = ref(false)
const startDate = ref('')
const endDate = ref('')
const userMode = ref('all')
const specificUser = ref('')
const chartContainer = ref<HTMLElement>()
let chartInstance: echarts.ECharts | null = null

const handleDateChange = (date: string) => {
  startDate.value = date
  endDate.value = date
  loadData()
}

const handleDateRangeChange = (start: string, end: string) => {
  startDate.value = start
  endDate.value = end
  loadData()
}

const handleUserModeChange = (mode: string) => {
  userMode.value = mode
  loadData()
}

const handleUserChange = (users: string[]) => {
  specificUser.value = users.join(',')
  loadData()
}

const loadData = async () => {
  if (!startDate.value || !endDate.value) return

  loading.value = true
  try {
    const params: QueryRequest = {
      startDate: startDate.value,
      endDate: endDate.value,
      dimension: 'user',
      rankType: 'total_cost',
      topN: 20
    }

    if (userMode.value === 'specific' && specificUser.value) {
      params.usernames = specificUser.value.split(',')
    }

    const response = await getUserDaily(params)
    renderChart(response.data)
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const renderChart = (data: UserDailyItem[]) => {
  if (!chartContainer.value) return

  if (!chartInstance) {
    chartInstance = echarts.init(chartContainer.value)
  }

  // 按用户聚合数据
  const userMap = new Map<string, { promptTokens: number, completionTokens: number, cost: number }>()

  data.forEach(item => {
    if (!userMap.has(item.username)) {
      userMap.set(item.username, { promptTokens: 0, completionTokens: 0, cost: 0 })
    }
    const userData = userMap.get(item.username)!
    userData.promptTokens += item.promptTokens
    userData.completionTokens += item.completionTokens
    userData.cost += item.cost
  })

  // 按花费从大到小排序，取前20
  const users = Array.from(userMap.entries())
    .sort((a, b) => b[1].cost - a[1].cost)
    .slice(0, 20)
    .map(entry => entry[0])

  const promptTokenData = users.map(username => userMap.get(username)!.promptTokens)
  const completionTokenData = users.map(username => userMap.get(username)!.completionTokens)
  const costData = users.map(username => userMap.get(username)!.cost)

  // 读取保存的图例状态
  const selectedLegend = readLegendState('userDailyLegend', null)

  const option: echarts.EChartsOption = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'none'
      },
      formatter: (params: any) => {
        if (!params || params.length === 0) return ''
        let result = params[0].name + '<br/>'
        params.forEach((param: any) => {
          if (param.seriesName.includes('费用')) {
            result += `${param.marker} ${param.seriesName}: $${param.value.toFixed(4)}<br/>`
          } else {
            result += `${param.marker} ${param.seriesName}: ${formatNumber(param.value)}<br/>`
          }
        })
        return result
      }
    },
    legend: {
      data: ['输入Token', '输出Token', '费用(美元)'],
      selected: selectedLegend,
      top: 10
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
      data: users,
      axisLabel: {
        rotate: 45,
        interval: 0
      }
    },
    yAxis: {
      type: 'value',
      name: '数值',
      axisLabel: {
        formatter: (value: number) => formatNumber(value)
      }
    },
    series: [
      {
        name: '输入Token',
        type: 'bar',
        data: promptTokenData,
        label: {
          show: true,
          position: 'top',
          formatter: (params: any) => formatNumber(params.value),
          fontSize: 10
        }
      },
      {
        name: '输出Token',
        type: 'bar',
        data: completionTokenData,
        label: {
          show: true,
          position: 'top',
          formatter: (params: any) => formatNumber(params.value),
          fontSize: 10
        }
      },
      {
        name: '费用(美元)',
        type: 'bar',
        data: costData,
        label: {
          show: true,
          position: 'top',
          formatter: (params: any) => '$' + params.value.toFixed(2),
          fontSize: 10
        }
      }
    ]
  }

  chartInstance.setOption(option, { notMerge: true })

  // 监听图例选择变化并保存
  chartInstance.off('legendselectchanged')
  chartInstance.on('legendselectchanged', (params: any) => {
    localStorage.setItem('userDailyLegend', JSON.stringify(params.selected))
  })
}

const formatNumber = (num: number): string => {
  if (num >= 1000000) {
    return (num / 1000000).toFixed(2) + 'M'
  } else if (num >= 1000) {
    return (num / 1000).toFixed(2) + 'K'
  }
  return num.toString()
}

const handleResize = () => {
  if (chartInstance) {
    chartInstance.resize()
  }
}

onMounted(() => {
  window.addEventListener('resize', handleResize)
  nextTick(() => {
    loadData()
  })
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  if (chartInstance) {
    chartInstance.dispose()
    chartInstance = null
  }
})
</script>

<style scoped>
.user-daily-view {
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

.chart-container {
  width: 100%;
  height: 550px;
  margin-top: 24px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(5px);
  border-radius: 12px;
  border: 1px solid rgba(226, 232, 240, 0.8);
}

/* Responsive Design */
@media (max-width: 768px) {
  .card-header span {
    font-size: 20px;
  }

  .chart-container {
    height: 400px;
    padding: 12px;
    margin-top: 16px;
  }
}
</style>
