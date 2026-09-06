<template>
  <el-card class="data-table">
    <template #header>
      <div class="table-header">
        <span>详细数据</span>
        <el-button type="primary" size="small" @click="handleExport">导出</el-button>
      </div>
    </template>
    <el-table :data="tableData" stripe border style="width: 100%">
      <el-table-column prop="name" label="名称" width="180" v-if="rankType !== 'daily' && rankType !== 'hourly'" />
      <el-table-column prop="date" label="日期" width="120" v-if="rankType === 'daily'" />
      <el-table-column prop="hour" label="小时" width="80" v-if="rankType === 'hourly'" />
      <el-table-column prop="promptTokens" label="输入Token" width="120" sortable align="right">
        <template #default="{ row }">
          {{ formatNumber(row.promptTokens) }}
        </template>
      </el-table-column>
      <el-table-column prop="completionTokens" label="输出Token" width="120" sortable align="right">
        <template #default="{ row }">
          {{ formatNumber(row.completionTokens) }}
        </template>
      </el-table-column>
      <el-table-column label="总Token" width="120" align="right">
        <template #default="{ row }">
          {{ formatNumber((row.promptTokens || 0) + (row.completionTokens || 0)) }}
        </template>
      </el-table-column>
      <el-table-column prop="cost" label="花费(美元)" width="120" sortable align="right">
        <template #default="{ row }">
          {{ row.cost?.toFixed(2) || '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="count" label="调用次数" width="120" sortable align="right">
        <template #default="{ row }">
          {{ row.count?.toLocaleString() || '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="users" label="用户数" width="100" v-if="rankType === 'hourly'" />
    </el-table>
  </el-card>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import type { QueryDimension, QueryRankType } from '@/api/analyzer'
import { ElMessage } from 'element-plus'
import { exportData } from '@/api/export'

const tableData = ref<any[]>([])
const rankType = ref<QueryRankType>('total_cost')
const dimension = ref<QueryDimension>('user')
const startDate = ref('')
const endDate = ref('')

const updateTable = (data: any[], type: QueryRankType, query?: { startDate?: string; endDate?: string; dimension?: QueryDimension }) => {
  tableData.value = data
  rankType.value = type
  if (query?.startDate) startDate.value = query.startDate
  if (query?.endDate) endDate.value = query.endDate
  if (query?.dimension) dimension.value = query.dimension
}

const formatNumber = (num: number): string => {
  const n = num || 0
  if (n >= 1000000) {
    return (n / 1000000).toFixed(2) + 'M'
  } else if (n >= 1000) {
    return (n / 1000).toFixed(2) + 'K'
  }
  return n.toString()
}

const handleExport = async () => {
  try {
    const response = await exportData({
      startDate: startDate.value,
      endDate: endDate.value,
      dimension: dimension.value,
      rankType: rankType.value,
      topN: Math.min(Math.max(tableData.value.length, 1), 1000)
    })

    const blob = response.data
    const url = window.URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = `export_${Date.now()}.xlsx`
    a.click()
    window.URL.revokeObjectURL(url)

    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

defineExpose({
  updateTable
})
</script>

<style scoped>
.data-table {
  height: 100%;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
