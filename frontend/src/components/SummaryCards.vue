<template>
  <el-card class="summary-cards">
    <el-row :gutter="20">
      <el-col :span="6">
        <div class="summary-card">
          <div class="card-title">总请求数</div>
          <div class="card-value">{{ summary.totalCount.toLocaleString() }}</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="summary-card">
          <div class="card-title">总Token消耗</div>
          <div class="card-value">{{ summary.totalTokens.toLocaleString() }}</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="summary-card">
          <div class="card-title">总花费（美元）</div>
          <div class="card-value cost">{{ summary.totalCost.toFixed(2) }}</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="summary-card">
          <div class="card-title">平均耗时（ms）</div>
          <div class="card-value">{{ summary.avgTime.toFixed(0) }}</div>
        </div>
      </el-col>
    </el-row>
  </el-card>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import type { SummaryData } from '@/types'

const summary = reactive<SummaryData>({
  totalCount: 0,
  totalTokens: 0,
  totalCost: 0,
  avgTime: 0
})

const updateSummary = (data: SummaryData) => {
  Object.assign(summary, data)
}

defineExpose({
  updateSummary
})
</script>

<style scoped>
.summary-cards {
  margin-bottom: 20px;
}

.summary-card {
  text-align: center;
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px;
  color: white;
}

.card-title {
  font-size: 14px;
  margin-bottom: 10px;
  opacity: 0.9;
}

.card-value {
  font-size: 28px;
  font-weight: bold;
}

.card-value.cost {
  color: #ff6b6b;
}
</style>
