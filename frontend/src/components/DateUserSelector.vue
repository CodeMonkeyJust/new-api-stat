<template>
  <div class="date-user-selector">
    <div class="date-mode-selector">
      <el-radio-group v-model="dateMode" size="small" @change="handleDateModeChange">
        <el-radio-button value="single">{{ t('selector.singleDay') }}</el-radio-button>
        <el-radio-button value="range">{{ t('selector.dateRange') }}</el-radio-button>
      </el-radio-group>
    </div>
    <div class="date-selector" v-if="dateMode === 'single'">
      <el-button @click="prevDay" :disabled="loading" size="small">{{ t('selector.previousDay') }}</el-button>
      <el-date-picker
        v-model="selectedDate"
        type="date"
        :placeholder="t('selector.selectDate')"
        style="width: 200px; margin: 0 10px"
        value-format="YYYY-MM-DD"
        size="small"
        :disabled-date="disableFutureDate"
        @change="handleDateChange"
      />
      <el-button @click="nextDay" :disabled="loading" size="small">{{ t('selector.nextDay') }}</el-button>
    </div>
    <div class="date-range-selector" v-if="dateMode === 'range'">
      <el-date-picker
        v-model="dateRange"
        type="daterange"
        :range-separator="t('selector.rangeSeparator')"
        :start-placeholder="t('selector.startPlaceholder')"
        :end-placeholder="t('selector.endPlaceholder')"
        style="width: 280px; margin: 0 10px"
        value-format="YYYY-MM-DD"
        size="small"
        :disabled-date="disableFutureDate"
        @change="handleDateRangeChange"
      />
      <el-button-group>
        <el-button @click="selectRecentDays(7)" :disabled="loading" size="small">{{ t('selector.recentNDays', { days: 7 }) }}</el-button>
        <el-button @click="selectRecentDays(30)" :disabled="loading" size="small">{{ t('selector.recentNDays', { days: 30 }) }}</el-button>
      </el-button-group>
    </div>
    <div class="user-selector" v-if="showUserSelector">
      <el-select v-model="userMode" :placeholder="t('selector.selectUser')" size="small" style="width: 150px" @change="handleUserModeChange">
        <el-option :label="t('selector.allUsers')" value="all" />
        <el-option :label="t('selector.specificUser')" value="specific" />
      </el-select>
      <el-select
        v-if="userMode === 'specific'"
        v-model="selectedUsers"
        :loading="loadingUsers"
        :disabled="loadingUsers"
        :placeholder="t('selector.selectUser')"
        size="small"
        style="width: 400px; margin-left: 10px"
        multiple
        filterable
        collapse-tags
        :max-collapse-tags="2"
        collapse-tags-tooltip
        clearable
        @clear="handleUserClear"
        popper-class="user-select-dropdown"
      >
        <el-option
          v-for="user in userList"
          :key="user.id"
          :label="user.displayName || user.username"
          :value="user.username"
        >
          <span style="float: left">{{ user.displayName || user.username }}</span>
          <span style="float: right; color: #8492a6; font-size: 13px">{{ user.username }}</span>
        </el-option>
      </el-select>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getUsers } from '@/api/analyzer'
import { useI18n } from 'vue-i18n'
import type { UserItem } from '@/api/analyzer'

interface Props {
  showUserSelector?: boolean
  loading?: boolean
  defaultDateMode?: 'single' | 'range'
  dateModeStorageKey?: string
}

interface Emits {
  (e: 'date-change', date: string): void
  (e: 'date-range-change', startDate: string, endDate: string): void
  (e: 'user-mode-change', mode: string): void
  (e: 'user-change', users: string[]): void
}

const props = withDefaults(defineProps<Props>(), {
  loading: false,
  defaultDateMode: 'range',
  showUserSelector: true
})

const { t } = useI18n({ useScope: 'global' })

const emit = defineEmits<Emits>()

const readStoredDateMode = (): 'single' | 'range' => {
  if (!props.dateModeStorageKey) return props.defaultDateMode

  try {
    const storedMode = localStorage.getItem(props.dateModeStorageKey)
    return storedMode === 'single' || storedMode === 'range'
      ? storedMode
      : props.defaultDateMode
  } catch {
    return props.defaultDateMode
  }
}

const dateMode = ref<'single' | 'range'>(readStoredDateMode())

const saveDateMode = () => {
  if (!props.dateModeStorageKey) return

  try {
    localStorage.setItem(props.dateModeStorageKey, dateMode.value)
  } catch {
    // Ignore unavailable storage and keep the selector functional.
  }
}
const selectedDate = ref('')
const dateRange = ref<[string, string] | null>(null)
const userMode = ref('all')
const selectedUsers = ref<string[]>([])
const userList = ref<UserItem[]>([])
const loadingUsers = ref(false)

const setToday = () => {
  const today = new Date()
  const year = today.getFullYear()
  const month = String(today.getMonth() + 1).padStart(2, '0')
  const day = String(today.getDate()).padStart(2, '0')
  selectedDate.value = `${year}-${month}-${day}`
  emit('date-change', selectedDate.value)
}

const prevDay = () => {
  const date = new Date(selectedDate.value)
  date.setDate(date.getDate() - 1)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  selectedDate.value = `${year}-${month}-${day}`
  emit('date-change', selectedDate.value)
}

const nextDay = () => {
  const date = new Date(selectedDate.value)
  date.setDate(date.getDate() + 1)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  selectedDate.value = `${year}-${month}-${day}`
  emit('date-change', selectedDate.value)
}

const disableFutureDate = (date: Date) => date.getTime() > Date.now()

const handleDateModeChange = () => {
  saveDateMode()
  if (dateMode.value === 'single') {
    setToday()
  } else {
    selectRecentDays(7)
  }
}

const handleDateChange = () => {
  emit('date-change', selectedDate.value)
}

const handleDateRangeChange = () => {
  if (dateRange.value && dateRange.value.length === 2) {
    emit('date-range-change', dateRange.value[0], dateRange.value[1])
  }
}

const selectRecentDays = (days: number) => {
  const endDate = new Date()
  const startDate = new Date()
  startDate.setDate(startDate.getDate() - days + 1)

  const formatDate = (date: Date) => {
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    return `${year}-${month}-${day}`
  }

  dateRange.value = [formatDate(startDate), formatDate(endDate)]
  emit('date-range-change', dateRange.value[0], dateRange.value[1])
}

const handleUserModeChange = () => {
  emit('user-mode-change', userMode.value)
  if (userMode.value === 'specific' && userList.value.length === 0) {
    loadUsers()
  }
}

const handleUserClear = () => {
  selectedUsers.value = []
  emit('user-change', [])
}

watch(selectedUsers, (newValue: string[]) => {
  emit('user-change', newValue)
})

const loadUsers = async () => {
  loadingUsers.value = true
  try {
    const response = await getUsers()
    userList.value = response.data
  } catch (error) {
    ElMessage.error(t('msg.loadUsersFailed'))
  } finally {
    loadingUsers.value = false
  }
}

onMounted(() => {
  if (dateMode.value === 'single') {
    setToday()
  } else {
    selectRecentDays(7)
  }
})

defineExpose({
  setToday
})
</script>

<style scoped>
.date-user-selector {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.date-mode-selector {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 5px 0;
}

.date-selector {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 5px 0;
}

.date-range-selector {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 5px 0;
}

.user-selector {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 5px 0;
}
</style>

<style>
.user-select-dropdown {
  max-width: 600px;
}

.user-select-dropdown .el-select-dropdown__item {
  height: auto;
  padding: 8px 20px;
  line-height: 1.5;
}

.user-select-dropdown .el-select-dropdown__item:hover {
  background-color: #f5f7fa;
}
</style>
