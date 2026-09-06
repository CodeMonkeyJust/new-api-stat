<template>
  <el-card class="user-daily-view">
    <template #header>
      <div class="card-header">
        <span>{{ t('userDaily.title') }}</span>
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
import { ref, watch, onMounted, onUnmounted, nextTick } from 'vue'
import type { QueryRequest } from '@/api/analyzer'
import { ElMessage } from 'element-plus'
import type * as echarts from 'echarts'
import { useI18n } from 'vue-i18n'
import { useAppLocale } from '@/composables/useAppLocale'
import { initChart } from '@/utils/chart'
import { getUserDaily } from '@/api/analyzer'
import type { UserDailyItem } from '@/api/analyzer'
import DateUserSelector from '@/components/DateUserSelector.vue'

const { t } = useI18n({ useScope: 'global' })
const { locale } = useAppLocale()

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

const loading = ref(false)
const startDate = ref('')
const endDate = ref('')
const userMode = ref('all')
const specificUser = ref('')
const chartContainer = ref<HTMLElement>()
const chartData = ref<UserDailyItem[]>([])
const hasRendered = ref(false)
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
    chartData.value = response.data
    renderChart()
  } catch (error) {
    console.error('Load data error:', error)
    ElMessage.error(t('msg.loadFailed'))
  } finally {
    loading.value = false
  }
}

const renderChart = () => {
  if (!chartContainer.value) return

  if (chartInstance) {
    chartInstance.dispose()
  }
  chartInstance = initChart(chartContainer.value, locale.value)
  hasRendered.value = true

  const inputName = t('metric.inputToken')
  const outputName = t('metric.outputToken')
  const costName = t('metric.costUsd')
  const valueAxisName = t('metric.valueAxis')

  // 按用户聚合数据
  const data = chartData.value
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

  // 读取当前语言保存的图例状态
  const selectedLegend = readLegendState('userDailyLegend:' + locale.value, null)

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
          if (param.seriesName === costName) {
            result += `${param.marker} ${param.seriesName}: $${param.value.toFixed(4)}<br/>`
          } else {
            result += `${param.marker} ${param.seriesName}: ${formatNumber(param.value)}<br/>`
          }
        })
        return result
      }
    },
    legend: {
      data: [inputName, outputName, costName],
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
      name: valueAxisName,
      axisLabel: {
        formatter: (value: number) => formatNumber(value)
      }
    },
    series: [
      {
        name: inputName,
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
        name: outputName,
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
        name: costName,
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

  // 监听图例选择变化并保存（按语言分别保存）
  chartInstance.off('legendselectchanged')
  chartInstance.on('legendselectchanged', (params: any) => {
    localStorage.setItem('userDailyLegend:' + locale.value, JSON.stringify(params.selected))
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

watch(locale, () => {
  if (chartContainer.value && hasRendered.value) {
    renderChart()
  }
})

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