<template>
  <el-card class="daily-trend">
    <template #header>
      <div class="card-header">
        <span>每日消耗趋势</span>
      </div>
    </template>
    <DateUserSelector
      :loading="loading"
      @date-change="handleDateChange"
      @date-range-change="handleDateRangeChange"
      @user-mode-change="handleUserModeChange"
      @user-change="handleUserChange"
      ref="dateUserSelector"
    />
    <div ref="chartContainer" class="chart-container" v-loading="loading"></div>
    <UserDetailsTable
      v-if="selectedDate !== null"
      :title="`${selectedDate} 人员详情`"
      :data="userDetails"
      :loading="userDetailsLoading"
    />
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
import { getDaily } from '@/api/analyzer'
import type { DailyItem } from '@/api/analyzer'
import UserDetailsTable from '@/components/UserDetailsTable.vue'
import DateUserSelector from '@/components/DateUserSelector.vue'

const loading = ref(false)
const userDetailsLoading = ref(false)
const startDate = ref('')
const endDate = ref('')
const userMode = ref('all')
const specificUser = ref('')
const selectedDate = ref<string | null>(null)
const userDetails = ref<any[]>([])
const chartContainer = ref<HTMLElement>()
let chartInstance: echarts.ECharts | null = null

const handleDateChange = (date: string) => {
  startDate.value = date
  endDate.value = date
  selectedDate.value = null
  userDetails.value = []
  loadData()
}

const handleDateRangeChange = (start: string, end: string) => {
  startDate.value = start
  endDate.value = end
  selectedDate.value = null
  userDetails.value = []
  loadData()
}

const handleUserModeChange = (mode: string) => {
  userMode.value = mode
  selectedDate.value = null
  userDetails.value = []
  loadData()
}

const handleUserChange = (users: string[]) => {
  specificUser.value = users.join(',')
  selectedDate.value = null
  userDetails.value = []
  loadData()
}

const loadUserDetails = async (date: string) => {
  userDetailsLoading.value = true
  try {
    const params: QueryRequest = {
      startDate: date,
      endDate: date,
      dimension: 'user',
      rankType: 'total_cost',
      topN: 100
    }

    if (userMode.value === 'specific' && specificUser.value) {
      params.usernames = specificUser.value.split(',')
    }

    const response = await getDaily(params)
    userDetails.value = response.data
  } catch (error) {
    ElMessage.error('加载人员详情失败')
  } finally {
    userDetailsLoading.value = false
  }
}

const loadData = async () => {
  if (!startDate.value || !endDate.value) return

  loading.value = true
  try {
    const params: QueryRequest = {
      startDate: startDate.value,
      endDate: endDate.value,
      dimension: 'user',
      rankType: 'daily',
      topN: 100
    }

    if (userMode.value === 'specific' && specificUser.value) {
      params.usernames = specificUser.value.split(',')
    }

    const response = await getDaily(params)
    renderChart(response.data)
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const renderChart = (data: DailyItem[]) => {
  if (!chartContainer.value) return

  if (!chartInstance) {
    chartInstance = echarts.init(chartContainer.value)
  }

  const dates = data.map(item => item.date)
  const promptTokens = data.map(item => item.promptTokens)
  const completionTokens = data.map(item => item.completionTokens)
  const costs = data.map(item => item.cost)

  const selectedLegend = readLegendState('dailyTrendLegend', ['输入Token', '输出Token', '费用(美元)'])

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'none'
      },
      formatter: (params: any) => {
        let result = params[0].name + '<br/>'
        params.forEach((param: any) => {
          result += `${param.marker} ${param.seriesName}: ${formatNumber(param.value)}<br/>`
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
      data: dates,
      axisLabel: {
        rotate: 45
      }
    },
    yAxis: [
      {
        type: 'value',
        name: 'Token数',
        position: 'left',
        axisLabel: {
          formatter: (value: number) => formatNumber(value)
        }
      },
      {
        type: 'value',
        name: '费用(美元)',
        position: 'right',
        axisLabel: {
          formatter: (value: number) => value.toFixed(2)
        }
      }
    ],
    series: [
      {
        name: '输入Token',
        type: 'line',
        data: promptTokens,
        label: {
          show: true,
          position: 'top',
          formatter: (params: any) => formatNumber(params.value)
        }
      },
      {
        name: '输出Token',
        type: 'line',
        data: completionTokens,
        label: {
          show: true,
          position: 'top',
          formatter: (params: any) => formatNumber(params.value)
        }
      },
      {
        name: '费用(美元)',
        type: 'line',
        yAxisIndex: 1,
        data: costs,
        label: {
          show: true,
          position: 'top',
          formatter: (params: any) => params.value.toFixed(2)
        }
      }
    ]
  }

  chartInstance.setOption(option)

  chartInstance.off('click')
  chartInstance.off('legendselectchanged')
  chartInstance.on('legendselectchanged', (params: any) => {
    localStorage.setItem('dailyTrendLegend', JSON.stringify(params.selected))
  })
  chartInstance.on('click', (params: any) => {
    selectedDate.value = params.name
    loadUserDetails(params.name)
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
.daily-trend {
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
  height: 500px;
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
    height: 350px;
    padding: 12px;
    margin-top: 16px;
  }
}
</style>
