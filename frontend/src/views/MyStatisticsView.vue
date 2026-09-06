<template>
  <el-card class="my-statistics">
    <template #header>
      <div class="card-header">
        <span>{{ t('myStats.title') }}</span>
        <span class="user-tag" v-if="displayName">{{ displayName }}</span>
      </div>
    </template>
    <DateUserSelector
      :loading="loading"
      :show-user-selector="false"
      default-date-mode="range"
      @date-change="handleDateChange"
      @date-range-change="handleDateRangeChange"
    />
    <div class="summary-grid" v-loading="loading">
      <div
        v-for="card in summaryCards"
        :key="card.label"
        class="summary-card"
        :class="{ cost: card.cost }"
      >
        <div class="summary-card-title">{{ card.label }}</div>
        <div class="summary-card-value">{{ card.value }}</div>
      </div>
    </div>
    <div ref="chartContainer" class="chart-container" v-loading="loading"></div>
    <div class="table-section">
      <h3>{{ t('myStats.modelDetails') }}</h3>
      <el-table :data="models" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="model" :label="t('metric.model')" min-width="220" show-overflow-tooltip />
        <el-table-column prop="promptTokens" :label="t('metric.inputToken')" width="130" align="right">
          <template #default="{ row }">{{ formatNumber(row.promptTokens) }}</template>
        </el-table-column>
        <el-table-column prop="completionTokens" :label="t('metric.outputToken')" width="130" align="right">
          <template #default="{ row }">{{ formatNumber(row.completionTokens) }}</template>
        </el-table-column>
        <el-table-column prop="totalTokens" :label="t('metric.totalToken')" width="130" align="right">
          <template #default="{ row }">{{ formatNumber(row.totalTokens) }}</template>
        </el-table-column>
        <el-table-column prop="cost" :label="t('metric.costUsd')" width="130" align="right">
          <template #default="{ row }">{{ '$' + Number(row.cost || 0).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="callCount" :label="t('metric.callCount')" width="110" align="right">
          <template #default="{ row }">{{ row.callCount.toLocaleString() }}</template>
        </el-table-column>
      </el-table>
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onUnmounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import type * as echarts from 'echarts'
import { useI18n } from 'vue-i18n'
import { useAppLocale } from '@/composables/useAppLocale'
import { initChart } from '@/utils/chart'
import { getPersonalStats } from '@/api/analyzer'
import type { PersonalModelItem } from '@/api/analyzer'
import type { UserDTO } from '@/api/auth'
import DateUserSelector from '@/components/DateUserSelector.vue'

const { t } = useI18n({ useScope: 'global' })
const { locale } = useAppLocale()

const loading = ref(false)
const startDate = ref('')
const endDate = ref('')
const models = ref<PersonalModelItem[]>([])
const chartContainer = ref<HTMLElement>()
const hasRendered = ref(false)
let chartInstance: echarts.ECharts | null = null

const currentUser = ref<UserDTO | null>(null)
try {
  const stored = localStorage.getItem('user')
  if (stored) currentUser.value = JSON.parse(stored) as UserDTO
} catch {
  currentUser.value = null
}

const displayName = computed(() => currentUser.value?.displayName || currentUser.value?.username || '')

const summary = ref({
  totalCount: 0,
  promptTokens: 0,
  completionTokens: 0,
  totalTokens: 0,
  totalCost: 0
})

const formatNumber = (num: number): string => {
  const value = Number(num) || 0
  if (value >= 1000000) {
    return (value / 1000000).toFixed(2) + 'M'
  } else if (value >= 1000) {
    return (value / 1000).toFixed(2) + 'K'
  }
  return value.toString()
}

const summaryCards = computed(() => [
  { label: t('metric.callCount'), value: summary.value.totalCount.toLocaleString(), cost: false },
  { label: t('metric.inputToken'), value: formatNumber(summary.value.promptTokens), cost: false },
  { label: t('metric.outputToken'), value: formatNumber(summary.value.completionTokens), cost: false },
  { label: t('metric.totalToken'), value: formatNumber(summary.value.totalTokens), cost: false },
  { label: t('metric.costUsd'), value: '$' + (summary.value.totalCost || 0).toFixed(2), cost: true }
])

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

const loadData = async () => {
  if (!startDate.value || !endDate.value) return

  loading.value = true
  try {
    const response = await getPersonalStats({
      startDate: startDate.value,
      endDate: endDate.value
    })
    const data = response.data
    summary.value = {
      totalCount: data.summary?.totalCount ?? 0,
      promptTokens: data.summary?.promptTokens ?? 0,
      completionTokens: data.summary?.completionTokens ?? 0,
      totalTokens: data.summary?.totalTokens ?? 0,
      totalCost: data.summary?.totalCost ?? 0
    }
    models.value = data.models ?? []
    renderChart()
  } catch (error) {
    console.error('Load personal stats error:', error)
    ElMessage.error(t('msg.loadFailed'))
  } finally {
    loading.value = false
  }
}

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

  const modelNames = models.value.map(item => item.model)
  const promptTokenData = models.value.map(item => item.promptTokens)
  const completionTokenData = models.value.map(item => item.completionTokens)
  const costData = models.value.map(item => item.cost)

  const selectedLegend = readLegendState('myStatsLegend:' + locale.value, null)

  const option = {
    tooltip: {
      trigger: 'axis' as const,
      axisPointer: {
        type: 'none'
      },
      formatter: (params: any) => {
        if (!params || params.length === 0) return ''
        let result = params[0].name + '<br/>'
        params.forEach((param: any) => {
          if (param.seriesName === costName) {
            result += `${param.marker} ${param.seriesName}: $${Number(param.value || 0).toFixed(2)}<br/>`
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
      type: 'category' as const,
      data: modelNames,
      axisLabel: {
        rotate: 45,
        interval: 0
      }
    },
    yAxis: [
      {
        type: 'value' as const,
        name: tokenAxisName,
        position: 'left',
        axisLabel: {
          formatter: (value: number) => formatNumber(value)
        }
      },
      {
        type: 'value' as const,
        name: costName,
        position: 'right',
        axisLabel: {
          formatter: (value: number) => value.toFixed(2)
        }
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
            if (!params.value) return ''
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
            if (!params.value) return ''
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
            if (!params.value) return ''
            return '$' + Number(params.value).toFixed(2)
          }
        }
      }
    ]
  }

  chartInstance.setOption(option, true)

  chartInstance.off('legendselectchanged')
  chartInstance.on('legendselectchanged', (params: any) => {
    localStorage.setItem('myStatsLegend:' + locale.value, JSON.stringify(params.selected))
  })
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
    if (!startDate.value) {
      loadData()
    }
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
.my-statistics {
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

.card-header .user-tag {
  font-size: 14px;
  font-weight: 500;
  color: #64748b;
  background: rgba(255, 255, 255, 0.9);
  -webkit-text-fill-color: #64748b;
  padding: 6px 14px;
  border-radius: 16px;
  border: 1px solid rgba(226, 232, 240, 0.8);
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 16px;
  margin-top: 20px;
}

.summary-card {
  text-align: center;
  padding: 20px 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  color: white;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.25);
}

.summary-card-title {
  font-size: 14px;
  margin-bottom: 10px;
  opacity: 0.9;
}

.summary-card-value {
  font-size: 22px;
  font-weight: bold;
  word-break: break-all;
}

.summary-card.cost .summary-card-value {
  color: #ff6b6b;
}

.chart-container {
  width: 100%;
  height: 480px;
  margin-top: 20px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(5px);
  border-radius: 12px;
  border: 1px solid rgba(226, 232, 240, 0.8);
}

.table-section {
  margin-top: 20px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(5px);
  border-radius: 12px;
  border: 1px solid rgba(226, 232, 240, 0.8);
}

.table-section h3 {
  margin: 0 0 12px 0;
  font-size: 16px;
  font-weight: 500;
  color: #334155;
}

@media (max-width: 1200px) {
  .summary-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .card-header span {
    font-size: 20px;
  }

  .summary-grid {
    grid-template-columns: repeat(1, 1fr);
  }

  .chart-container {
    height: 350px;
    padding: 12px;
    margin-top: 16px;
  }
}
</style>