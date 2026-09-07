import axios from 'axios'
import { ElMessage } from 'element-plus'
import i18n from '@/i18n'
import { translateServerMessage } from '@/i18n/serverMessages'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/new-api-stat-api/api',
  timeout: 30000,
  maxContentLength: 10 * 1024 * 1024,
  maxBodyLength: 1 * 1024 * 1024,
  withCredentials: true,
  xsrfCookieName: 'XSRF-TOKEN',
  xsrfHeaderName: 'X-XSRF-TOKEN',
  withXSRFToken: true
})

request.interceptors.response.use(
  (response) => {
    if (response.config.responseType === 'blob') {
      return response
    }

    const res = response.data
    if (res && res.code !== undefined && res.code !== 200) {
      ElMessage.error(translateServerMessage(res.message) || i18n.global.t('msg.requestFailed'))
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res
  },
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('user')
      if (window.location.hash !== '#/login') {
        window.location.hash = '#/login'
      }
    }
    if (error.response?.status === 403) {
      ElMessage.error(i18n.global.t('msg.noAccess'))
    } else {
      ElMessage.error(translateServerMessage(error.response?.data?.message) || error.message || i18n.global.t('msg.networkError'))
    }
    return Promise.reject(error)
  }
)

export default request
