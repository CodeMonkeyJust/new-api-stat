<template>
  <el-card class="data-chart">
    <template #header>
      <span>数据可视化</span>
    </template>
    <div ref="chartRef" style="width: 100%; height: 500px"></div>
  </el-card>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import type { RankData, HourlyData, DailyData } from '@/types'
import type { EChartsOption } from 'echarts'

const chartRef = ref<HTMLElement>()
let chartInstance: echarts.ECharts | null = null

const initChart = () => {
  if (!chartRef.value) return
  chartInstance = echarts.init(chartRef.value)
  window.addEventListener('resize', handleResize)
}

const handleResize = () => {
  chartInstance?.resize()
}

const updateChart = (data: any, type: string) => {
  if (!chartInstance) return

  let option: EChartsOption = {}

  switch (type) {
    case 'total_cost':
    case 'call_count':
      option = createBarChart(data)
      break
    case 'daily':
      option = createLineChart(data)
      break
    case 'hourly':
      option = createHeatmapChart(data)
      break
    default:
      option = createBarChart(data)
  }

  chartInstance.setOption(option, true)
}

const createBarChart = (data: RankData[]): EChartsOption => {
  const names = data.map(item => item.name)
  const values = data.map(item => item.value)
  const costs = data.map(item => item.cost)

  return {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    legend: {
      data: ['Token消耗', '花费(美元)']
    },
    grid: {
      bottom: 100
    },
    xAxis: {
      type: 'category' as const,
      data: names,
      axisLabel: {
        rotate: 45,
        interval: 0,
        fontSize: 12,
        margin: 15
      }
    },
    yAxis: [
      {
        type: 'value' as const,
        name: 'Token消耗',
        position: 'left'
      },
      {
        type: 'value' as const,
        name: '花费(美元)',
        position: 'right'
      }
    ],
    series: [
      {
        name: 'Token消耗',
        type: 'bar' as const,
        data: values,
        itemStyle: {
          color: '#409EFF'
        },
        label: {
          show: true,
          position: 'top'
        }
      },
      {
        name: '花费(美元)',
        type: 'bar' as const,
        yAxisIndex: 1,
        data: costs,
        itemStyle: {
          color: '#67C23A'
        },
        label: {
          show: true,
          position: 'top',
          formatter: (params: any) => params.value.toFixed(2)
        }
      }
    ]
  }
}

const createLineChart = (data: DailyData[]): EChartsOption => {
  const dates = data.map(item => item.date)
  const costs = data.map(item => item.cost)
  const promptTokens = data.map(item => item.promptTokens)
  const completionTokens = data.map(item => item.completionTokens)

  return {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['花费(美元)', '输入Token', '输出Token']
    },
    xAxis: {
      type: 'category' as const,
      data: dates
    },
    yAxis: {
      type: 'value' as const
    },
    series: [
      {
        name: '花费(美元)',
        type: 'line' as const,
        data: costs,
        itemStyle: {
          color: '#F56C6C'
        },
        label: {
          show: true,
          position: 'top',
          formatter: (params: any) => params.value.toFixed(2)
        }
      },
      {
        name: '输入Token',
        type: 'line' as const,
        data: promptTokens,
        itemStyle: {
          color: '#409EFF'
        },
        label: {
          show: true,
          position: 'top'
        }
      },
      {
        name: '输出Token',
        type: 'line' as const,
        data: completionTokens,
        itemStyle: {
          color: '#67C23A'
        },
        label: {
          show: true,
          position: 'top'
        }
      }
    ]
  }
}

const createHeatmapChart = (data: HourlyData[]): EChartsOption => {
  const hours = data.map(item => item.hour)
  const costs = data.map(item => item.cost)

  return {
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category' as const,
      data: hours,
      name: '小时'
    },
    yAxis: {
      type: 'value' as const,
      name: '花费(美元)'
    },
    series: [
      {
        type: 'bar' as const,
        data: costs,
        itemStyle: {
          color: (params: any) => {
            const colors = ['#91cc75', '#fac858', '#ee6666', '#73c0de', '#3ba272', '#fc8452', '#9a60b4', '#ea7ccc']
            return colors[params.dataIndex % colors.length]
          }
        }
      }
    ]
  }
}

onMounted(() => {
  initChart()
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  chartInstance?.dispose()
})

defineExpose({
  updateChart
})
</script>

<style scoped>
.data-chart {
  margin-bottom: 20px;
}
</style>
