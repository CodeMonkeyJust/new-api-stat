<template>
  <el-card class="hourly-statistics">
    <template #header>
      <div class="card-header">
        <span>{{ t('hourly.title') }}</span>
      </div>
    </template>
    <DateUserSelector
      :loading="loading"
      date-mode-storage-key="dateMode:hourlyStatistics"
      default-date-mode="single"
      @date-change="handleDateChange"
      @date-range-change="handleDateRangeChange"
      @user-mode-change="handleUserModeChange"
      @user-change="handleUserChange"
      ref="dateUserSelector"
    />
    <div ref="chartContainer" class="chart-container" v-loading="loading"></div>
    <UserDetailsTable
      v-if="selectedHour !== null"
      :title="t('hourly.userDetailsTitle', { start: selectedHour, end: selectedHour + 1 })"
      :data="userDetails"
      :loading="userDetailsLoading"
    />
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
import { getHourly, getHourlyUsers } from '@/api/analyzer'
import type { HourlyItem, HourlyUserItem } from '@/api/analyzer'
import UserDetailsTable from '@/components/UserDetailsTable.vue'
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
const userDetailsLoading = ref(false)
const selectedDate = ref('')
const startDate = ref('')
const endDate = ref('')
const userMode = ref('all')
const specificUser = ref('')
const selectedHour = ref<number | null>(null)
const userDetails = ref<HourlyUserItem[]>([])
const chartContainer = ref<HTMLElement>()
const chartData = ref<HourlyItem[]>([])
const hasRendered = ref(false)
let chartInstance: echarts.ECharts | null = null

const handleDateChange = (date: string) => {
  selectedDate.value = date
  startDate.value = date
  endDate.value = date
  selectedHour.value = null
  userDetails.value = []
  loadData()
}

const handleDateRangeChange = (start: string, end: string) => {
  startDate.value = start
  endDate.value = end
  selectedDate.value = start
  selectedHour.value = null
  userDetails.value = []
  loadData()
}

const handleUserModeChange = (mode: string) => {
  userMode.value = mode
  selectedHour.value = null
  userDetails.value = []
  loadData()
}

const handleUserChange = (users: string[]) => {
  specificUser.value = users.join(',')
  selectedHour.value = null
  userDetails.value = []
  loadData()
}

const loadUserDetails = async (hour: number) => {
  userDetailsLoading.value = true
  try {
    const params: QueryRequest = {
      startDate: startDate.value,
      endDate: endDate.value,
      dimension: 'user',
      rankType: 'hourly',
      topN: 24
    }

    if (userMode.value === 'specific' && specificUser.value) {
      params.usernames = specificUser.value.split(',')
    }

    const response = await getHourlyUsers(params, hour)
    userDetails.value = response.data
  } catch (error) {
    console.error('Load user details error:', error)
    ElMessage.error(t('msg.loadUserDetailsFailed'))
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
      rankType: 'hourly',
      topN: 24
    }

    if (userMode.value === 'specific' && specificUser.value) {
      params.usernames = specificUser.value.split(',')
    }

    const response = await getHourly(params)
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
  const tokenAxisName = t('metric.tokenAxis')

  const data = chartData.value
  const hours = Array.from({ length: 24 }, (_, i) => `${i}:00`)
  const promptTokenData = new Array(24).fill(0)
  const completionTokenData = new Array(24).fill(0)
  const costData = new Array(24).fill(0)

  data.forEach(item => {
    promptTokenData[item.hour] = item.promptTokens
    completionTokenData[item.hour] = item.completionTokens
    costData[item.hour] = item.cost
  })

  const selectedLegend = readLegendState('hourlyLegend:' + locale.value, null)

  const option = {
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
            result += `${param.marker} ${param.seriesName}: $${param.value.toFixed(2)}<br/>`
          } else {
            result += `${param.marker} ${param.seriesName}: ${formatNumber(param.value)}<br/>`
          }
        })
        return result
      }
    },
    legend: {
      data: [inputName, outputName, costName],
      selected: selectedLegend
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category' as const,
      data: hours,
      axisLabel: {
        rotate: 45
      }
    },
    yAxis: [
      {
        type: 'value' as const,
        name: tokenAxisName,
        position: 'left'
      },
      {
        type: 'value' as const,
        name: costName,
        position: 'right'
      }
    ],
    series: [
      {
        name: inputName,
        type: 'bar' as const,
        data: promptTokenData,
        itemStyle: {
          color: '#409EFF'
        },
        label: {
          show: true,
          position: 'top',
          fontSize: 10,
          formatter: (params: any) => {
            if (params.value === 0) return ''
            return formatNumber(params.value)
          }
        }
      },
      {
        name: outputName,
        type: 'bar' as const,
        data: completionTokenData,
        itemStyle: {
          color: '#67C23A'
        },
        label: {
          show: true,
          position: 'top',
          fontSize: 10,
          formatter: (params: any) => {
            if (params.value === 0) return ''
            return formatNumber(params.value)
          }
        }
      },
      {
        name: costName,
        type: 'bar' as const,
        yAxisIndex: 1,
        data: costData,
        itemStyle: {
          color: '#F56C6C'
        },
        label: {
          show: true,
          position: 'top',
          fontSize: 10,
          formatter: (params: any) => {
            if (params.value === 0) return ''
            return '$' + params.value.toFixed(2)
          }
        }
      }
    ]
  }

  chartInstance.setOption(option)

  chartInstance.off('click')
  chartInstance.off('legendselectchanged')
  chartInstance.on('legendselectchanged', (params: any) => {
    localStorage.setItem('hourlyLegend:' + locale.value, JSON.stringify(params.selected))
  })
  chartInstance.on('click', (params: any) => {
    if (params.componentType === 'series') {
      const hour = params.dataIndex
      selectedHour.value = hour
      loadUserDetails(hour)
    }
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
  loadData()
  nextTick(() => {
    window.addEventListener('resize', handleResize)
  })
})

onUnmounted(() => {
  if (chartInstance) {
    chartInstance.dispose()
    chartInstance = null
  }
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.hourly-statistics {
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

.date-selector {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 12px 0;
}

.user-selector {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 8px 0;
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