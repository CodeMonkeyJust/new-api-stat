<template>
  <el-card class="token-security-view">
    <template #header>
      <div class="card-header">
        <div>
          <span class="page-title">{{ t('tokenSecurity.title') }}</span>
          <span v-if="generatedAt" class="generated-at">
            {{ t('tokenSecurity.generatedAt', { time: formatTime(generatedAt) }) }}
          </span>
        </div>
        <el-button type="primary" size="small" :loading="loading" @click="loadData">
          <el-icon><Refresh /></el-icon>
          {{ t('tokenSecurity.refresh') }}
        </el-button>
      </div>
    </template>

    <DateUserSelector
      :loading="loading"
      date-mode-storage-key="dateMode:tokenSecurity"
      default-date-mode="range"
      @date-change="handleDateChange"
      @date-range-change="handleDateRangeChange"
      @user-mode-change="handleUserModeChange"
      @user-change="handleUserChange"
      ref="dateUserSelector"
    />

    <div v-if="summary" class="summary-grid" v-loading="loading">
      <div class="summary-card">
        <div class="summary-label">{{ t('tokenSecurity.summary.analyzedTokens') }}</div>
        <div class="summary-value">{{ formatNumber(summary.analyzedTokenCount) }}</div>
      </div>
      <div class="summary-card warning-card">
        <div class="summary-label">{{ t('tokenSecurity.summary.riskyTokens') }}</div>
        <div class="summary-value">{{ formatNumber(summary.riskyTokenCount) }}</div>
      </div>
      <div class="summary-card danger-card">
        <div class="summary-label">{{ t('tokenSecurity.summary.highRiskTokens') }}</div>
        <div class="summary-value">{{ formatNumber(summary.highRiskTokenCount) }}</div>
      </div>
      <div class="summary-card">
        <div class="summary-label">{{ t('tokenSecurity.summary.calls') }}</div>
        <div class="summary-value">{{ formatNumber(summary.callCount) }}</div>
      </div>
      <div class="summary-card">
        <div class="summary-label">{{ t('tokenSecurity.summary.distinctIps') }}</div>
        <div class="summary-value">{{ formatNumber(summary.distinctIpCount) }}</div>
      </div>
    </div>

    <el-alert
      v-if="summary && summary.totalLogCount === 0"
      class="ip-coverage-alert"
      type="info"
      :closable="false"
      show-icon
      :title="t('tokenSecurity.ipCoverage.noLogsTitle')"
      :description="t('tokenSecurity.ipCoverage.noLogsDescription')"
    />
    <el-alert
      v-else-if="summary && !summary.ipDataAvailable"
      class="ip-coverage-alert"
      type="error"
      :closable="false"
      show-icon
      :title="t('tokenSecurity.ipCoverage.unavailableTitle')"
      :description="t('tokenSecurity.ipCoverage.unavailableDescription', {
        total: formatNumber(summary.totalLogCount),
        empty: formatNumber(summary.emptyIpLogCount)
      })"
    />
    <el-alert
      v-else-if="summary && summary.ipCoveragePercent < 100"
      class="ip-coverage-alert"
      type="warning"
      :closable="false"
      show-icon
      :title="t('tokenSecurity.ipCoverage.partialTitle')"
      :description="t('tokenSecurity.ipCoverage.partialDescription', {
        total: formatNumber(summary.totalLogCount),
        empty: formatNumber(summary.emptyIpLogCount),
        percent: formatPercent(summary.ipCoveragePercent)
      })"
    />

    <el-alert
      class="disclaimer"
      type="warning"
      :closable="false"
      show-icon
      :title="t('tokenSecurity.disclaimer')"
    />

    <template v-if="hasData">
      <div class="table-toolbar">
        <div class="table-title">{{ t('tokenSecurity.tableTitle', { count: visibleTokens.length }) }}</div>
        <el-switch
          v-model="onlyRisky"
          :active-text="t('tokenSecurity.onlyRisky')"
          size="small"
        />
      </div>

      <el-table
        :data="visibleTokens"
        row-key="tokenRowKey"
        stripe
        border
        style="width: 100%"
        v-loading="loading"
      >
      <el-table-column type="expand">
        <template #default="{ row }">
          <div class="token-details">
            <section class="detail-section">
              <div class="detail-title">
                {{ t('tokenSecurity.minuteDetails') }}
                <span class="truncated-hint">
                  {{ t('tokenSecurity.minuteDetailsHint', { count: 10 }) }}
                </span>
              </div>
              <div class="minute-summary">
                <div class="minute-stat">
                  <span class="minute-stat-label">{{ t('tokenSecurity.peakRpm') }}</span>
                  <span class="minute-stat-value" :class="{ 'danger-text': row.peakRequestsPerMinute >= highRpmThreshold }">{{ formatNumber(row.peakRequestsPerMinute) }}</span>
                </div>
                <div class="minute-stat">
                  <span class="minute-stat-label">{{ t('tokenSecurity.peakMinute') }}</span>
                  <span class="minute-stat-value time">{{ formatTime(row.peakMinute) }}</span>
                </div>
                <div class="minute-stat">
                  <span class="minute-stat-label">{{ t('tokenSecurity.activeMinutes') }}</span>
                  <span class="minute-stat-value">{{ formatNumber(row.activeMinuteCount) }}</span>
                </div>
                <div class="minute-stat">
                  <span class="minute-stat-label">{{ t('tokenSecurity.averageRpm') }}</span>
                  <span class="minute-stat-value">{{ formatAverage(row.averageRequestsPerMinute) }}</span>
                </div>
              </div>
              <el-table
                :data="row.busiestMinutes"
                size="small"
                border
                :empty-text="t('tokenSecurity.noMinuteData')"
              >
                <el-table-column :label="t('tokenSecurity.minute')" min-width="300">
                  <template #default="scope">
                    <span class="mono">{{ formatTime(scope.row.minuteStart) }}</span>
                    <span
                      class="minute-request-count"
                      :class="{ 'danger-text': scope.row.requestCount >= highRpmThreshold }"
                    >
                      {{ t('tokenSecurity.minuteRequestCount', { count: formatNumber(scope.row.requestCount) }) }}
                    </span>
                  </template>
                </el-table-column>
              </el-table>
            </section>

            <section v-if="row.ips.length" class="detail-section">
              <div class="detail-title">
                {{ t('tokenSecurity.ipDetails') }}
                <span v-if="row.ipDetailsTruncated" class="truncated-hint">
                  {{ t('tokenSecurity.ipDetailsTruncated', { count: 100 }) }}
                </span>
              </div>
              <el-table :data="row.ips" size="small" border>
                <el-table-column prop="ip" :label="t('tokenSecurity.ip.ip')" min-width="180">
                  <template #default="scope">
                    <span class="mono">{{ scope.row.ip }}</span>
                    <el-tag v-if="scope.row.newIp" class="new-ip-tag" type="warning" size="small">
                      {{ t('tokenSecurity.ip.new') }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column :label="t('tokenSecurity.ip.type')" width="110">
                  <template #default="scope">
                    <el-tag :type="ipTypeTagType(scope.row.ipType)" size="small">
                      {{ t(`tokenSecurity.ipType.${scope.row.ipType}`) }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="network" :label="t('tokenSecurity.ip.network')" min-width="150">
                  <template #default="scope"><span class="mono">{{ scope.row.network }}</span></template>
                </el-table-column>
                <el-table-column prop="callCount" :label="t('tokenSecurity.callCount')" width="100" align="right">
                  <template #default="scope">{{ formatNumber(scope.row.callCount) }}</template>
                </el-table-column>
                <el-table-column prop="totalTokens" :label="t('metric.totalToken')" width="120" align="right">
                  <template #default="scope">{{ formatNumber(scope.row.totalTokens) }}</template>
                </el-table-column>
                <el-table-column prop="cost" :label="t('metric.costUsd')" width="120" align="right">
                  <template #default="scope">${{ formatCost(scope.row.cost) }}</template>
                </el-table-column>
                <el-table-column :label="t('tokenSecurity.ip.firstSeen')" width="170">
                  <template #default="scope">{{ formatTime(scope.row.firstSeen) }}</template>
                </el-table-column>
                <el-table-column :label="t('tokenSecurity.ip.lastSeen')" width="170">
                  <template #default="scope">{{ formatTime(scope.row.lastSeen) }}</template>
                </el-table-column>
              </el-table>
            </section>
          </div>
        </template>
      </el-table-column>

      <el-table-column :label="t('tokenSecurity.token')" min-width="190">
        <template #default="{ row }">
          <div class="token-name">{{ row.tokenName || t('tokenSecurity.unnamedToken') }}</div>
          <div class="muted">{{ row.tokenId ? `ID: ${row.tokenId}` : t('tokenSecurity.noTokenId') }}</div>
        </template>
      </el-table-column>
      <el-table-column prop="username" :label="t('tokenSecurity.username')" width="130" />
      <el-table-column prop="callCount" :label="t('tokenSecurity.callCount')" width="105" sortable align="right">
        <template #default="{ row }">{{ formatNumber(row.callCount) }}</template>
      </el-table-column>
      <el-table-column prop="totalTokens" :label="t('metric.totalToken')" width="130" sortable align="right">
        <template #default="{ row }">{{ formatNumber(row.totalTokens) }}</template>
      </el-table-column>
      <el-table-column prop="cost" :label="t('metric.costUsd')" width="125" sortable align="right">
        <template #default="{ row }">${{ formatCost(row.cost) }}</template>
      </el-table-column>
      <el-table-column prop="distinctIpCount" :label="t('tokenSecurity.ipCount')" width="100" sortable align="right" />
      <el-table-column prop="distinctNetworkCount" :label="t('tokenSecurity.networkCount')" width="110" sortable align="right" />
      <el-table-column prop="sharedFiveMinuteWindows" :label="t('tokenSecurity.sharedWindows')" width="125" sortable align="right">
        <template #default="{ row }">
          <span :class="{ 'danger-text': row.sharedFiveMinuteWindows > 0 }">
            {{ formatNumber(row.sharedFiveMinuteWindows) }}
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="newIpCount" :label="t('tokenSecurity.newIpCount')" width="100" sortable align="right" />
      <el-table-column prop="peakRequestsPerMinute" :label="t('tokenSecurity.peakRpm')" width="130" sortable align="right">
        <template #default="{ row }">
          <span :class="{ 'danger-text': row.peakRequestsPerMinute >= highRpmThreshold }">
            {{ formatNumber(row.peakRequestsPerMinute) }}
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="riskScore" :label="t('tokenSecurity.riskScore')" width="110" sortable align="right">
        <template #default="{ row }">
          <el-tag :type="riskTagType(row.riskLevel)" effect="dark" size="small">
            {{ row.riskScore }} · {{ t(`tokenSecurity.risk.${row.riskLevel}`) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column :label="t('tokenSecurity.reasons')" min-width="250">
        <template #default="{ row }">
          <template v-if="row.riskReasons.length">
            <el-tag
              v-for="reason in row.riskReasons"
              :key="reason"
              class="reason-tag"
              :type="riskTagType(row.riskLevel)"
              size="small"
            >
              {{ t(`tokenSecurity.reason.${reason}`) }}
            </el-tag>
          </template>
          <span v-else class="muted">{{ t('tokenSecurity.noRisk') }}</span>
        </template>
      </el-table-column>
      </el-table>
    </template>
  </el-card>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import { useI18n } from 'vue-i18n'
import DateUserSelector from '@/components/DateUserSelector.vue'
import { getTokenSecurity } from '@/api/analyzer'
import type { TokenRiskItem, TokenSecurityRequest, TokenSecuritySummary } from '@/api/analyzer'
import { useAppLocale } from '@/composables/useAppLocale'

const { t } = useI18n({ useScope: 'global' })
const { locale } = useAppLocale()

const loading = ref(false)
const startDate = ref('')
const endDate = ref('')
const selectedUsers = ref<string[]>([])
const onlyRisky = ref(true)
const summary = ref<TokenSecuritySummary | null>(null)
const tokens = ref<TokenRiskItem[]>([])
const generatedAt = ref(0)
const highRpmThreshold = 35

const hasData = computed(() => (summary.value?.totalLogCount ?? 0) > 0)

const visibleTokens = computed(() =>
  onlyRisky.value ? tokens.value.filter(token => token.riskLevel !== 'NONE') : tokens.value
)

const loadData = async () => {
  if (!startDate.value || !endDate.value) return

  loading.value = true
  try {
    const params: TokenSecurityRequest = {
      startDate: startDate.value,
      endDate: endDate.value,
      topN: 500
    }
    if (selectedUsers.value.length > 0) {
      params.usernames = selectedUsers.value
    }

    const response = await getTokenSecurity(params)
    summary.value = response.data.summary
    const hasRiskSignals = response.data.tokens.some(token => token.riskLevel !== 'NONE')
    if (!response.data.summary.ipDataAvailable && !hasRiskSignals) {
      onlyRisky.value = false
    }
    tokens.value = response.data.tokens.map(token => ({
      ...token,
      tokenRowKey: `${token.tokenId ?? 'name'}:${token.username}:${token.tokenName}`
    }))
    generatedAt.value = response.data.generatedAt
  } catch (error) {
    console.error('Load token security data error:', error)
    ElMessage.error(t('msg.loadFailed'))
  } finally {
    loading.value = false
  }
}

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

const handleUserModeChange = () => {
  selectedUsers.value = []
  loadData()
}

const handleUserChange = (users: string[]) => {
  selectedUsers.value = users
  loadData()
}

const formatNumber = (value: number): string => {
  return new Intl.NumberFormat(locale.value, { notation: value >= 10000 ? 'compact' : 'standard' }).format(value || 0)
}

const formatPercent = (value: number): string => {
  return `${new Intl.NumberFormat(locale.value, { maximumFractionDigits: 1 }).format(value || 0)}%`
}

const formatCost = (value: number): string => {
  const amount = value || 0
  return amount >= 1 ? amount.toFixed(2) : amount.toFixed(4)
}

const formatAverage = (value: number): string => {
  return new Intl.NumberFormat(locale.value, { maximumFractionDigits: 2 }).format(value || 0)
}

const formatTime = (timestamp?: number | null): string => {
  if (!timestamp) return '-'
  return new Date(timestamp * 1000).toLocaleString(locale.value)
}

const riskTagType = (level: TokenRiskItem['riskLevel']) => {
  switch (level) {
    case 'HIGH': return 'danger'
    case 'MEDIUM': return 'warning'
    case 'LOW': return 'info'
    default: return 'success'
  }
}

const ipTypeTagType = (ipType: string) => {
  switch (ipType) {
    case 'PUBLIC': return 'warning'
    case 'PRIVATE': return 'success'
    case 'LOOPBACK':
    case 'LINK_LOCAL': return 'info'
    default: return 'danger'
  }
}
</script>

<style scoped>
.token-security-view {
  min-height: 100%;
  background: transparent;
  border: none;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

.card-header > div:first-child {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.page-title {
  font-size: 24px;
  font-weight: 700;
  background: linear-gradient(135deg, #2563EB 0%, #7C3AED 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.generated-at {
  color: #64748b;
  font-size: 12px;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 12px;
  margin: 20px 0;
}

.summary-card {
  padding: 16px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.76);
  border: 1px solid rgba(203, 213, 225, 0.72);
}

.warning-card {
  border-color: rgba(245, 158, 11, 0.45);
  background: rgba(255, 251, 235, 0.9);
}

.danger-card {
  border-color: rgba(239, 68, 68, 0.4);
  background: rgba(254, 242, 242, 0.9);
}

.summary-label {
  color: #64748b;
  font-size: 13px;
}

.summary-value {
  margin-top: 8px;
  color: #0f172a;
  font-size: 24px;
  font-weight: 700;
}

.ip-coverage-alert {
  margin-bottom: 14px;
}

.disclaimer {
  margin-bottom: 18px;
}

.table-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}

.table-title {
  color: #334155;
  font-size: 16px;
  font-weight: 600;
}

.token-details {
  display: flex;
  flex-direction: column;
  gap: 18px;
  padding: 16px 24px 20px;
  background: rgba(248, 250, 252, 0.86);
}

.detail-section {
  min-width: 0;
}

.detail-title {
  margin-bottom: 10px;
  color: #334155;
  font-weight: 600;
}

.minute-summary {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 10px;
  margin-bottom: 12px;
}

.minute-stat {
  display: flex;
  flex-direction: column;
  gap: 5px;
  padding: 10px 12px;
  border: 1px solid rgba(203, 213, 225, 0.78);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.82);
}

.minute-stat-label {
  color: #64748b;
  font-size: 12px;
}

.minute-stat-value {
  color: #0f172a;
  font-size: 18px;
  font-weight: 700;
}

.minute-request-count {
  margin-left: 8px;
  font-weight: 600;
  font-variant-numeric: tabular-nums;
}

.minute-stat-value.time {
  font-size: 14px;
}

.truncated-hint {
  margin-left: 8px;
  color: #d97706;
  font-size: 12px;
  font-weight: 400;
}

.token-name {
  color: #0f172a;
  font-weight: 600;
}

.muted {
  color: #94a3b8;
  font-size: 12px;
}

.mono {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
}

.new-ip-tag {
  margin-left: 8px;
}

.reason-tag {
  margin: 2px 6px 2px 0;
}

.danger-text {
  color: #dc2626;
  font-weight: 700;
}

@media (max-width: 1100px) {
  .summary-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 900px) {
  .minute-summary {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .page-title {
    font-size: 20px;
  }

  .summary-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .table-toolbar {
    align-items: flex-start;
    gap: 10px;
  }
}
</style>
