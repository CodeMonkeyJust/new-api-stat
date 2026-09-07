<template>
  <el-card class="model-daily">
    <template #header>
      <div class="card-header">
        <span>{{ t('model.title') }}</span>
      </div>
    </template>
    <DateUserSelector
      :loading="loading"
      date-mode-storage-key="dateMode:modelDaily"
      default-date-mode="single"
      @date-change="handleDateChange"
      @date-range-change="handleDateRangeChange"
      @user-mode-change="handleUserModeChange"
      @user-change="handleUserChange"
      ref="dateUserSelector"
    />
    <div ref="chartContainer" class="chart-container" v-loading="loading"></div>
    <UserDetailsTable
      v-if="selectedModel !== null"
      :title="t('model.userDetailsTitle', { model: selectedModel })"
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
import { getModelDaily, getModelUsers } from '@/api/analyzer'
import type { ModelDailyItem, ModelUserItem } from '@/api/analyzer'
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
const selectedModel = ref<string | null>(null)
const userDetails = ref<ModelUserItem[]>([])
const chartContainer = ref<HTMLElement>()
const chartData = ref<ModelDailyItem[]>([])
const hasRendered = ref(false)
let chartInstance: echarts.ECharts | null = null

const handleDateChange = (date: string) => {
  selectedDate.value = date
  startDate.value = date
  endDate.value = date
  selectedModel.value = null
  userDetails.value = []
  loadData()
}

const handleDateRangeChange = (start: string, end: string) => {
  startDate.value = start
  endDate.value = end
  selectedDate.value = start
  selectedModel.value = null
  userDetails.value = []
  loadData()
}

const handleUserModeChange = (mode: string) => {
  userMode.value = mode
  selectedModel.value = null
  userDetails.value = []
  loadData()
}

const handleUserChange = (users: string[]) => {
  specificUser.value = users.join(',')
  selectedModel.value = null
  userDetails.value = []
  loadData()
}

const loadUserDetails = async (model: string) => {
  userDetailsLoading.value = true
  try {
    const params: QueryRequest = {
      startDate: startDate.value,
      endDate: endDate.value,
      dimension: 'model',
      rankType: 'total_cost',
      topN: 100
    }

    if (userMode.value === 'specific' && specificUser.value) {
      params.usernames = specificUser.value.split(',')
    }

    const response = await getModelUsers(params, model)
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
      dimension: 'model',
      rankType: 'total_cost',
      topN: 100
    }

    if (userMode.value === 'specific' && specificUser.value) {
      params.usernames = specificUser.value.split(',')
    }

    const response = await getModelDaily(params)
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
  const models = data.map(item => item.model)
  const promptTokenData = data.map(item => item.promptTokens)
  const completionTokenData = data.map(item => item.completionTokens)
  const costData = data.map(item => item.cost)

  const selectedLegend = readLegendState('modelDailyLegend:' + locale.value, null)

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
      data: models,
      axisLabel: {
        rotate: 45,
        interval: 0
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
    localStorage.setItem('modelDailyLegend:' + locale.value, JSON.stringify(params.selected))
  })
  chartInstance.on('click', (params: any) => {
    if (params.componentType === 'series') {
      const model = models[params.dataIndex]
      selectedModel.value = model
      loadUserDetails(model)
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
.model-daily {
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