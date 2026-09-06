<template>
  <el-dropdown trigger="click" @command="handleCommand">
    <span class="language-switcher">
      <span class="lang-label">{{ currentLabel }}</span>
      <el-icon class="lang-arrow"><ArrowDown /></el-icon>
    </span>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item
          v-for="lang in options"
          :key="lang.code"
          :command="lang.code"
          :class="{ 'is-active': lang.code === locale }"
        >
          <span class="lang-option-label">{{ lang.label }}</span>
          <el-icon v-if="lang.code === locale" class="lang-check"><Check /></el-icon>
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { ArrowDown, Check } from '@element-plus/icons-vue'
import { useAppLocale } from '@/composables/useAppLocale'
import type { AppLocale } from '@/i18n/languages'

const { locale, options, setLocale } = useAppLocale()

const currentLabel = computed(
  () => options.find((item) => item.code === locale.value)?.label ?? ''
)

const handleCommand = (command: string | number | object) => {
  setLocale(command as AppLocale)
}
</script>

<style scoped>
.language-switcher {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 14px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.8);
  border: 1px solid rgba(226, 232, 240, 0.8);
  cursor: pointer;
  color: #475569;
  font-size: 13px;
  font-weight: 500;
  transition: all 0.2s ease;
  outline: none;
}

.language-switcher:hover {
  background: #ffffff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.lang-arrow {
  font-size: 12px;
  color: #94a3b8;
}

.lang-option-label {
  margin-right: 8px;
}

.lang-check {
  color: #2563eb;
  font-size: 14px;
}

</style>

<style>
.el-dropdown-menu__item.is-active {
  color: #2563eb;
  font-weight: 600;
}
</style>