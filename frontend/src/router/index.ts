import { createRouter, createWebHashHistory, RouteRecordRaw } from 'vue-router'
import MainView from '@/views/MainView.vue'
import LoginView from '@/views/LoginView.vue'
import Dashboard from '@/views/Dashboard.vue'
import ModelDailyView from '@/views/ModelDailyView.vue'
import DailyTrend from '@/views/DailyTrend.vue'
import HourlyStatistics from '@/views/HourlyStatistics.vue'
import UserDailyView from '@/views/UserDailyView.vue'
import UserBalance from '@/views/UserBalance.vue'
import MyStatisticsView from '@/views/MyStatisticsView.vue'
import { getCurrentUser } from '@/api/auth'
import type { UserDTO } from '@/api/auth'
import { isPrivilegedRole } from '@/utils/role'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: LoginView
  },
  {
    path: '/',
    name: 'Main',
    component: MainView,
    redirect: () => {
      const stored = localStorage.getItem('user')
      if (!stored) return '/my-statistics'
      try {
        const user = JSON.parse(stored) as UserDTO
        return isPrivilegedRole(user.role) ? '/dashboard' : '/my-statistics'
      } catch {
        return '/my-statistics'
      }
    },
    children: [
      {
        path: 'my-statistics',
        name: 'MyStatistics',
        component: MyStatisticsView
      },
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: Dashboard,
        meta: { privilegeRequired: true }
      },
      {
        path: 'daily-trend',
        name: 'DailyTrend',
        component: DailyTrend,
        meta: { privilegeRequired: true }
      },
      {
        path: 'model-daily',
        name: 'ModelDaily',
        component: ModelDailyView,
        meta: { privilegeRequired: true }
      },
      {
        path: 'hourly',
        name: 'Hourly',
        component: HourlyStatistics,
        meta: { privilegeRequired: true }
      },
      {
        path: 'user-daily',
        name: 'UserDaily',
        component: UserDailyView,
        meta: { privilegeRequired: true }
      },
      {
        path: 'user-balance',
        name: 'UserBalance',
        component: UserBalance,
        meta: { privilegeRequired: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

let sessionChecked = false
let sessionCheckPromise: Promise<unknown> | null = null

const readStoredUser = (): UserDTO | null => {
  const stored = localStorage.getItem('user')
  if (!stored) return null
  try {
    return JSON.parse(stored) as UserDTO
  } catch {
    return null
  }
}

router.beforeEach(async (to) => {
  if (!sessionChecked) {
    sessionCheckPromise ??= getCurrentUser().catch(() => null)
    const currentUser = await sessionCheckPromise
    sessionChecked = true
    if (currentUser) {
      localStorage.setItem('user', JSON.stringify(currentUser))
    } else {
      localStorage.removeItem('user')
    }
  }

  const currentUser = readStoredUser()

  if (to.path === '/login') {
    return currentUser ? '/' : true
  }
  if (!currentUser) {
    return '/login'
  }

  const privilegeRequired = to.matched.some(record => record.meta.privilegeRequired === true)
  if (privilegeRequired && !isPrivilegedRole(currentUser.role)) {
    return '/my-statistics'
  }
  return true
})

export default router
