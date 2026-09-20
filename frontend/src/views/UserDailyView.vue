<template>
  <el-card class="user-daily-view">
    <template #header>
      <div class="card-header">
        <span>{{ t('userDaily.title') }}</span>
      </div>
    </template>
    <DateUserSelector
      :loading="loading"
      date-mode-storage-key="dateMode:userDaily"
      default-date-mode="range"
      @date-change="handleDateChange"
      @date-range-change="handleDateRangeChange"
      @user-mode-change="handleUserModeChange"
      @user-change="handleUserChange"
      ref="dateUserSelector"
    />
    <div ref="chartContainer" class="chart-container" v-loading="loading"></div>
    <section v-if="selectedUser" class="hourly-section">
      <div class="table-header">
        <h3 class="hourly-title">{{ t('userDaily.hourlyTitle', { username: selectedUser }) }}</h3>
        <div class="table-actions">
          <el-checkbox v-model="hideZeroCallHours">
            {{ t('userDaily.hideZeroCallHours') }}
          </el-checkbox>
          <el-button type="primary" size="small" :loading="hourlyExporting" @click="handleHourlyExport">
            {{ t('userDaily.export') }}
          </el-button>
        </div>
      </div>
      <el-table
        :data="visibleHourlyTableData"
        v-loading="hourlyLoading"
        stripe
        border
        max-height="640"
        style="width: 100%"
      >
        <el-table-column prop="hour" :label="t('hourly.hour')" width="110" />
        <el-table-column prop="promptTokens" :label="t('metric.inputToken')" min-width="150" align="right">
          <template #default="{ row }">{{ formatNumber(row.promptTokens) }}</template>
        </el-table-column>
        <el-table-column prop="completionTokens" :label="t('metric.outputToken')" min-width="150" align="right">
          <template #default="{ row }">{{ formatNumber(row.completionTokens) }}</template>
        </el-table-column>
        <el-table-column prop="cost" :label="t('metric.costUsd')" min-width="150" align="right">
          <template #default="{ row }">{{ row.cost.toFixed(2) }}</template>
        </el-table-column>
      </el-table>
    </section>
    <section v-if="selectedUser" class="model-usage-section">
      <div class="table-header">
        <h3 class="hourly-title">{{ t('userDaily.modelUsageTitle', { username: selectedUser }) }}</h3>
        <el-button type="primary" size="small" :loading="modelUsageExporting" @click="handleModelUsageExport">
          {{ t('userDaily.export') }}
        </el-button>
      </div>
      <el-table
        :data="modelUsageData"
        v-loading="modelUsageLoading"
        stripe
        border
        max-height="640"
        style="width: 100%"
      >
        <el-table-column prop="model" :label="t('metric.model')" min-width="220" show-overflow-tooltip />
        <el-table-column prop="promptTokens" :label="t('metric.inputToken')" min-width="150" sortable align="right">
          <template #default="{ row }">{{ formatNumber(row.promptTokens) }}</template>
        </el-table-column>
        <el-table-column prop="completionTokens" :label="t('metric.outputToken')" min-width="150" sortable align="right">
          <template #default="{ row }">{{ formatNumber(row.completionTokens) }}</template>
        </el-table-column>
        <el-table-column prop="cost" :label="t('metric.costUsd')" min-width="150" sortable align="right">
          <template #default="{ row }">{{ row.cost.toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="callCount" :label="t('metric.callCount')" min-width="130" sortable align="right">
          <template #default="{ row }">{{ row.callCount.toLocaleString() }}</template>
        </el-table-column>
      </el-table>
    </section>
  </el-card>
</template>

<script setup lang="ts">
import { computed, ref, watch, onMounted, onUnmounted, nextTick } from 'vue'
import type { QueryRequest } from '@/api/analyzer'
import { ElMessage } from 'element-plus'
import type * as echarts from 'echarts'
import { useI18n } from 'vue-i18n'
import { useAppLocale } from '@/composables/useAppLocale'
import { initChart } from '@/utils/chart'
import { getHourly, getModelDaily, getUserDaily } from '@/api/analyzer'
import { exportData } from '@/api/export'
import type { HourlyItem, ModelDailyItem, UserDailyItem } from '@/api/analyzer'
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
const selectedUser = ref('')
const hourlyData = ref<HourlyItem[]>([])
const hourlyLoading = ref(false)
const hideZeroCallHours = ref(false)
const modelUsageData = ref<ModelDailyItem[]>([])
const modelUsageLoading = ref(false)
const hourlyExporting = ref(false)
const modelUsageExporting = ref(false)
const hasRendered = ref(false)
let chartInstance: echarts.ECharts | null = null
let hourlyRequestId = 0
let modelUsageRequestId = 0

interface HourlyTableItem {
  hour: string
  promptTokens: number
  completionTokens: number
  cost: number
  callCount: number
}

const clearHourlySelection = () => {
  hourlyRequestId += 1
  modelUsageRequestId += 1
  selectedUser.value = ''
  hourlyData.value = []
  hourlyLoading.value = false
  modelUsageData.value = []
  modelUsageLoading.value = false
}

const handleDateChange = (date: string) => {
  startDate.value = date
  endDate.value = date
  clearHourlySelection()
  loadData()
}

const handleDateRangeChange = (start: string, end: string) => {
  startDate.value = start
  endDate.value = end
  clearHourlySelection()
  loadData()
}

const handleUserModeChange = (mode: string) => {
  userMode.value = mode
  clearHourlySelection()
  loadData()
}

const handleUserChange = (users: string[]) => {
  specificUser.value = users.join(',')
  clearHourlySelection()
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

const loadHourlyData = async (username: string) => {
  const requestId = ++hourlyRequestId
  hourlyLoading.value = true

  try {
    const params: QueryRequest = {
      startDate: startDate.value,
      endDate: endDate.value,
      dimension: 'user',
      rankType: 'hourly',
      topN: 24,
      username
    }

    const response = await getHourly(params)
    if (requestId !== hourlyRequestId || selectedUser.value !== username) return

    hourlyData.value = response.data
  } catch (error) {
    if (requestId !== hourlyRequestId) return
    console.error('Load hourly data error:', error)
    ElMessage.error(t('msg.loadFailed'))
  } finally {
    if (requestId === hourlyRequestId) {
      hourlyLoading.value = false
    }
  }
}

const loadModelUsage = async (username: string) => {
  const requestId = ++modelUsageRequestId
  modelUsageLoading.value = true

  try {
    const params: QueryRequest = {
      startDate: startDate.value,
      endDate: endDate.value,
      dimension: 'model',
      rankType: 'total_cost',
      topN: 100,
      username
    }

    const response = await getModelDaily(params)
    if (requestId !== modelUsageRequestId || selectedUser.value !== username) return

    modelUsageData.value = response.data
  } catch (error) {
    if (requestId !== modelUsageRequestId) return
    console.error('Load model usage error:', error)
    ElMessage.error(t('msg.loadFailed'))
  } finally {
    if (requestId === modelUsageRequestId) {
      modelUsageLoading.value = false
    }
  }
}

const selectUser = (username: string) => {
  if (!username) return

  selectedUser.value = username
  hourlyData.value = []
  modelUsageData.value = []
  loadHourlyData(username)
  loadModelUsage(username)
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

  chartInstance.off('click')
  chartInstance.on('click', (params: any) => {
    if (params.componentType !== 'series') return
    const username = users[params.dataIndex]
    if (username) selectUser(username)
  })
}

const hourlyTableData = computed<HourlyTableItem[]>(() => {
  const rows = Array.from({ length: 24 }, (_, hour) => ({
    hour: `${hour}:00`,
    promptTokens: 0,
    completionTokens: 0,
    cost: 0,
    callCount: 0
  }))

  hourlyData.value.forEach(item => {
    if (item.hour < 0 || item.hour > 23) return
    rows[item.hour].promptTokens += item.promptTokens
    rows[item.hour].completionTokens += item.completionTokens
    rows[item.hour].cost += item.cost
    rows[item.hour].callCount += Number(item.count) || 0
  })

  return rows
})

const visibleHourlyTableData = computed(() =>
  hideZeroCallHours.value
    ? hourlyTableData.value.filter(item => item.callCount > 0)
    : hourlyTableData.value
)

const downloadBlob = (blob: Blob, filename: string) => {
  const url = window.URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = filename
  document.body.appendChild(link)
  link.click()
  link.remove()
  window.URL.revokeObjectURL(url)
}

const handleHourlyExport = async () => {
  const username = selectedUser.value
  if (!username) return

  hourlyExporting.value = true
  try {
    const response = await exportData({
      startDate: startDate.value,
      endDate: endDate.value,
      dimension: 'user',
      rankType: 'hourly',
      topN: 24,
      username
    }, 'user-hourly')

    downloadBlob(response.data, `user-hourly-${username}-${Date.now()}.xlsx`)
    ElMessage.success(t('msg.exportSuccess'))
  } catch (error) {
    console.error('Export hourly data error:', error)
    ElMessage.error(t('msg.exportFailed'))
  } finally {
    hourlyExporting.value = false
  }
}

const handleModelUsageExport = async () => {
  const username = selectedUser.value
  if (!username) return

  modelUsageExporting.value = true
  try {
    const response = await exportData({
      startDate: startDate.value,
      endDate: endDate.value,
      dimension: 'model',
      rankType: 'total_cost',
      topN: 100,
      username
    }, 'user-models')

    downloadBlob(response.data, `user-models-${username}-${Date.now()}.xlsx`)
    ElMessage.success(t('msg.exportSuccess'))
  } catch (error) {
    console.error('Export model usage data error:', error)
    ElMessage.error(t('msg.exportFailed'))
  } finally {
    modelUsageExporting.value = false
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
  hourlyRequestId += 1
  modelUsageRequestId += 1
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

.hourly-section {
  margin-top: 24px;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  margin-bottom: 12px;
}

.hourly-title {
  margin: 0;
  color: #1f2937;
  font-size: 18px;
  font-weight: 600;
}

.table-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-left: auto;
}

.hourly-section :deep(.el-table),
.model-usage-section :deep(.el-table) {
  overflow: hidden;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(5px);
}

.model-usage-section {
  margin-top: 24px;
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

  .hourly-section,
  .model-usage-section {
    margin-top: 16px;
  }

  .table-header {
    flex-wrap: wrap;
    align-items: flex-start;
  }

  .table-actions {
    width: 100%;
    justify-content: space-between;
    margin-left: 0;
  }
}
</style>