<template>
  <el-card class="user-balance">
    <template #header>
      <div class="card-header">
        <span>用户余额表</span>
        <el-button type="primary" size="small" @click="loadData" :loading="loading">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </template>
    <el-table :data="tableData" stripe style="width: 100%" v-loading="loading" :default-sort="{ prop: 'remainingBalance', order: 'ascending' }">
      <el-table-column prop="username" label="用户名" width="120" />
      <el-table-column prop="displayName" label="显示名称" width="150" />
      <el-table-column prop="email" label="邮箱" width="200" />
      <el-table-column prop="remainingBalance" label="剩余费用(美元)" width="140" sortable align="right">
        <template #default="{ row }">
          <span :class="{ 'low-balance': row.remainingBalance < 1 }">
            {{ row.remainingBalance.toFixed(2) }}
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="spentBalance" label="已花费(美元)" width="140" sortable align="right">
        <template #default="{ row }">
          {{ row.spentBalance.toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column prop="quota" label="剩余Quota" width="120" sortable align="right">
        <template #default="{ row }">
          {{ formatNumber(row.quota) }}
        </template>
      </el-table-column>
      <el-table-column prop="usedQuota" label="已用Quota" width="120" sortable align="right">
        <template #default="{ row }">
          {{ formatNumber(row.usedQuota) }}
        </template>
      </el-table-column>
      <el-table-column prop="requestCount" label="请求次数" width="120" sortable align="right">
        <template #default="{ row }">
          {{ formatNumber(row.requestCount) }}
        </template>
      </el-table-column>
      <el-table-column prop="group" label="用户组" width="100" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
            {{ row.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import { getUserBalances } from '@/api/analyzer'
import type { UserBalanceItem } from '@/api/analyzer'

const loading = ref(false)
const tableData = ref<UserBalanceItem[]>([])

const loadData = async () => {
  loading.value = true
  try {
    const response = await getUserBalances()
    tableData.value = response.data
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
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

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.user-balance {
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

.low-balance {
  color: #ef4444;
  font-weight: 700;
  padding: 4px 8px;
  background: rgba(239, 68, 68, 0.1);
  border-radius: 6px;
}

/* Table styles enhancement */
:deep(.el-table) {
  background: transparent;
}

:deep(.el-table th) {
  background: rgba(241, 245, 249, 0.8);
  color: #1E293B;
  font-weight: 600;
}

:deep(.el-table tr:hover > td) {
  background: rgba(248, 250, 252, 0.9) !important;
}

/* Responsive Design */
@media (max-width: 768px) {
  .card-header span {
    font-size: 20px;
  }
}
</style>
