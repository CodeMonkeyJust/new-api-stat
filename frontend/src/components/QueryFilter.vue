<template>
  <el-card class="query-filter">
    <template #header>
      <span>查询条件</span>
    </template>
    <el-form label-width="70px" size="small">
      <el-form-item label="日期范围">
        <el-row :gutter="4" style="width: 100%">
          <el-col :span="6">
            <el-date-picker
              v-model="query.startDate"
              type="date"
              placeholder="开始日期"
              style="width: 100%"
              value-format="YYYY-MM-DD"
              size="small"
              :disabled-date="disableFutureDate"
            />
          </el-col>
          <el-col :span="6">
            <el-date-picker
              v-model="query.endDate"
              type="date"
              placeholder="结束日期"
              style="width: 100%"
              value-format="YYYY-MM-DD"
              size="small"
              :disabled-date="disableFutureDate"
            />
          </el-col>
          <el-col :span="2">
            <el-button @click="setToday()" size="small">今天</el-button>
          </el-col>
          <el-col :span="2">
            <el-button @click="setDateRange(7)" size="small">7天</el-button>
          </el-col>
          <el-col :span="2">
            <el-button @click="setDateRange(30)" size="small">30天</el-button>
          </el-col>
          <el-col :span="2">
            <el-button @click="setThisMonth()" size="small">本月</el-button>
          </el-col>
          <el-col :span="2">
            <el-button @click="setLastMonth()" size="small">上月</el-button>
          </el-col>
        </el-row>
      </el-form-item>
      <el-row :gutter="8">
        <el-col :span="12">
          <el-form-item label="排行类型" style="margin-bottom: 8px">
            <el-radio-group v-model="query.rankType" size="small">
              <el-radio label="total_cost">总花费</el-radio>
              <el-radio label="daily">每日</el-radio>
              <el-radio label="hourly">24小时</el-radio>
              <el-radio label="call_count">调用次数</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="维度选择" style="margin-bottom: 8px">
            <el-select v-model="query.dimension" style="width: 100%" size="small">
              <el-option label="用户维度" value="user" />
              <el-option label="模型维度" value="model" />
              <el-option label="分组维度" value="group" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="3">
          <el-form-item label="Top N" style="margin-bottom: 8px">
            <el-input-number v-model="query.topN" :min="5" :max="100" style="width: 100%" size="small" />
          </el-form-item>
        </el-col>
        <el-col :span="3">
          <el-form-item style="margin-bottom: 8px">
            <el-button type="primary" @click="handleQuery" :loading="loading" style="width: 100%" size="small">
              执行查询
            </el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </el-card>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import type { QueryParams } from '@/types'

const emit = defineEmits(['query'])

const loading = ref(false)

const query = reactive<QueryParams>({
  startDate: '',
  endDate: '',
  dimension: 'user',
  rankType: 'total_cost',
  topN: 20
})

const disableFutureDate = (date: Date) => date.getTime() > Date.now()

const setDateRange = (days: number) => {
  const end = new Date()
  const start = new Date()
  start.setDate(start.getDate() - days)
  query.endDate = formatDate(end)
  query.startDate = formatDate(start)
}

const setToday = () => {
  const today = new Date()
  query.startDate = formatDate(today)
  query.endDate = formatDate(today)
}

const setThisMonth = () => {
  const now = new Date()
  const start = new Date(now.getFullYear(), now.getMonth(), 1)
  const end = new Date(now.getFullYear(), now.getMonth() + 1, 0)
  query.startDate = formatDate(start)
  query.endDate = formatDate(end)
}

const setLastMonth = () => {
  const now = new Date()
  const start = new Date(now.getFullYear(), now.getMonth() - 1, 1)
  const end = new Date(now.getFullYear(), now.getMonth(), 0)
  query.startDate = formatDate(start)
  query.endDate = formatDate(end)
}

const formatDate = (date: Date): string => {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

const handleQuery = () => {
  emit('query', { ...query })
}

setDateRange(7)
</script>

<style scoped>
.query-filter :deep(.el-card__body) {
  padding: 12px;
}

.query-filter :deep(.el-form-item) {
  margin-bottom: 8px;
}
</style>
