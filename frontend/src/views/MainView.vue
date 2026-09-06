<template>
  <div class="main-view">
    <el-container>
      <el-header class="app-header">
        <div class="header-content">
          <div class="logo-section">
            <svg class="logo-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M13 2L3 14h9l-1 8 10-12h-9l1-8z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            <h1 class="app-title">模型应用统计</h1>
          </div>
          <div class="header-meta">
            <span class="status-badge">统计分析</span>
            <el-dropdown @command="handleCommand">
              <span class="user-info">
                <el-icon class="user-icon"><User /></el-icon>
                <span class="username">{{ currentUser?.displayName || currentUser?.username || '用户' }}</span>
                <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="logout">
                    <el-icon><SwitchButton /></el-icon>
                    <span>退出登录</span>
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </el-header>
      <el-container class="main-container">
        <el-aside width="240px" class="sidebar">
          <el-menu v-if="currentUser"
            :default-active="activeMenu"
            class="sidebar-menu"
            router
          >
            <el-menu-item index="/my-statistics" class="menu-item">
              <el-icon class="menu-icon"><DataAnalysis /></el-icon>
              <span class="menu-text">个人统计</span>
            </el-menu-item>
            <template v-if="isPrivileged">
            <el-menu-item index="/dashboard" class="menu-item">
              <el-icon class="menu-icon"><Odometer /></el-icon>
              <span class="menu-text">仪表盘</span>
            </el-menu-item>
            <el-menu-item index="/daily-trend" class="menu-item">
              <el-icon class="menu-icon"><TrendCharts /></el-icon>
              <span class="menu-text">每日趋势</span>
            </el-menu-item>
            <el-menu-item index="/model-daily" class="menu-item">
              <el-icon class="menu-icon"><Document /></el-icon>
              <span class="menu-text">模型消耗统计</span>
            </el-menu-item>
            <el-menu-item index="/hourly" class="menu-item">
              <el-icon class="menu-icon"><Clock /></el-icon>
              <span class="menu-text">时段统计</span>
            </el-menu-item>
            <el-menu-item index="/user-daily" class="menu-item">
              <el-icon class="menu-icon"><User /></el-icon>
              <span class="menu-text">人员统计</span>
            </el-menu-item>
            <el-menu-item index="/user-balance" class="menu-item">
              <el-icon class="menu-icon"><Wallet /></el-icon>
              <span class="menu-text">用户余额</span>
            </el-menu-item>
            </template>
          </el-menu>
        </el-aside>
        <el-main class="content-area">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { DataAnalysis, Document, Clock, TrendCharts, User, Wallet, Odometer, ArrowDown, SwitchButton } from '@element-plus/icons-vue'
import { getCurrentUser, logout } from '@/api/auth'
import type { UserDTO } from '@/api/auth'
import { isPrivilegedRole } from '@/utils/role'

const route = useRoute()
const router = useRouter()
const activeMenu = ref(route.path)

const currentUser = ref<UserDTO | null>(null)

const isPrivileged = computed(() => isPrivilegedRole(currentUser.value?.role))

onMounted(async () => {
  try {
    const user = await getCurrentUser()
    currentUser.value = user
    localStorage.setItem('user', JSON.stringify(user))
  } catch {
    localStorage.removeItem('user')
    await router.replace('/login')
  }
})

watch(() => route.path, (newPath) => {
  activeMenu.value = newPath
})

const handleCommand = async (command: string) => {
  if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })

      await logout()
      localStorage.removeItem('user')
      currentUser.value = null
      ElMessage.success('已退出登录')
      router.push('/login')
    } catch (error) {
      if (error !== 'cancel') {
        localStorage.removeItem('user')
        currentUser.value = null
        await router.replace('/login')
      }
    }
  }
}
</script>

<style scoped>
.main-view {
  width: 100%;
  height: 100vh;
  background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 50%, #f0f9ff 100%);
  background-attachment: fixed;
}

/* Header Styles */
.app-header {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(226, 232, 240, 0.8);
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px 0 rgba(0, 0, 0, 0.06);
  padding: 0 32px;
  height: 64px;
  display: flex;
  align-items: center;
}

.header-content {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo-section {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-icon {
  width: 28px;
  height: 28px;
  color: #2563EB;
}

.app-title {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #1E293B;
  letter-spacing: -0.02em;
}

.header-meta {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.2s ease;
  border: 1px solid rgba(226, 232, 240, 0.8);
}

.user-info:hover {
  background: rgba(255, 255, 255, 1);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.user-icon {
  font-size: 18px;
  color: #64748b;
}

.username {
  font-size: 14px;
  font-weight: 500;
  color: #475569;
}

.dropdown-icon {
  font-size: 14px;
  color: #94a3b8;
}

.status-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: white;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
  box-shadow: 0 2px 4px rgba(16, 185, 129, 0.2);
}

.status-badge::before {
  content: '';
  width: 6px;
  height: 6px;
  background: white;
  border-radius: 50%;
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

/* Container Styles */
.main-container {
  height: calc(100vh - 64px);
}

/* Sidebar Styles */
.sidebar {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-right: 1px solid rgba(226, 232, 240, 0.8);
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.05);
}

.sidebar-menu {
  border-right: none;
  height: 100%;
  padding: 16px 12px;
  background: transparent;
}

.sidebar-menu .menu-item {
  margin-bottom: 8px;
  border-radius: 12px;
  height: 48px;
  transition: all 0.2s ease;
}

.sidebar-menu .menu-item:hover {
  background: linear-gradient(135deg, rgba(37, 99, 235, 0.1) 0%, rgba(59, 130, 246, 0.1) 100%);
  transform: translateX(4px);
}

.sidebar-menu .menu-item.is-active {
  background: linear-gradient(135deg, #2563EB 0%, #3B82F6 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);
}

.sidebar-menu .menu-item.is-active .menu-icon {
  color: white;
}

.sidebar-menu .menu-item.is-active .menu-text {
  color: white;
  font-weight: 600;
}

.menu-icon {
  font-size: 20px;
  color: #64748b;
  transition: color 0.2s ease;
}

.menu-text {
  font-size: 14px;
  font-weight: 500;
  color: #475569;
  transition: all 0.2s ease;
}

/* Content Area */
.content-area {
  background: transparent;
  padding: 24px;
  height: calc(100vh - 64px);
  overflow-y: auto;
}

/* Scrollbar Styles */
.content-area::-webkit-scrollbar {
  width: 8px;
}

.content-area::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.05);
  border-radius: 4px;
}

.content-area::-webkit-scrollbar-thumb {
  background: rgba(0, 0, 0, 0.2);
  border-radius: 4px;
}

.content-area::-webkit-scrollbar-thumb:hover {
  background: rgba(0, 0, 0, 0.3);
}

/* Responsive Design */
@media (max-width: 768px) {
  .app-header {
    padding: 0 16px;
  }

  .app-title {
    font-size: 16px;
  }

  .logo-icon {
    width: 24px;
    height: 24px;
  }

  .sidebar {
    width: 200px !important;
  }

  .content-area {
    padding: 16px;
  }
}

/* Accessibility */
@media (prefers-reduced-motion: reduce) {
  .sidebar-menu .menu-item,
  .menu-icon,
  .menu-text {
    transition: none;
  }

  .status-badge::before {
    animation: none;
  }
}
</style>
