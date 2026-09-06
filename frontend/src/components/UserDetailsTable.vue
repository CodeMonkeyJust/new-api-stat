<template>
  <div v-if="data.length > 0" class="user-details">
    <h3>{{ title }}</h3>
    <el-table :data="data" stripe style="width: 100%" v-loading="loading">
      <el-table-column prop="username" :label="t('userDetails.username')" width="150" />
      <el-table-column prop="promptTokens" :label="t('metric.inputToken')" width="120" align="right">
        <template #default="{ row }">
          {{ formatNumber(row.promptTokens) }}
        </template>
      </el-table-column>
      <el-table-column prop="completionTokens" :label="t('metric.outputToken')" width="120" align="right">
        <template #default="{ row }">
          {{ formatNumber(row.completionTokens) }}
        </template>
      </el-table-column>
      <el-table-column prop="totalTokens" :label="t('metric.totalTokenCount')" width="120" align="right">
        <template #default="{ row }">
          {{ formatNumber(row.totalTokens) }}
        </template>
      </el-table-column>
      <el-table-column prop="cost" :label="t('metric.costUsd')" width="120" align="right">
        <template #default="{ row }">
          {{ row.cost.toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column prop="callCount" :label="t('metric.callCount')" width="100" align="right" />
    </el-table>
  </div>
</template>

<script setup lang="ts">
interface UserDetailItem {
  username: string
  promptTokens: number
  completionTokens: number
  totalTokens: number
  cost: number
  callCount: number
}

import { useI18n } from 'vue-i18n'

const { t } = useI18n({ useScope: 'global' })

interface Props {
  title: string
  data: UserDetailItem[]
  loading?: boolean
}

withDefaults(defineProps<Props>(), {
  loading: false
})

const formatNumber = (num: number): string => {
  if (num >= 1000000) {
    return (num / 1000000).toFixed(2) + 'M'
  } else if (num >= 1000) {
    return (num / 1000).toFixed(2) + 'K'
  }
  return num.toString()
}
</script>

<style scoped>
.user-details {
  margin-top: 20px;
}

.user-details h3 {
  margin-bottom: 15px;
  font-size: 16px;
  font-weight: 500;
}
</style>
