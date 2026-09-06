import * as echarts from 'echarts'
import type { AppLocale } from '@/i18n/languages'
import { echartsLocaleFor } from '@/i18n/languages'

/**
 * Create an ECharts instance bound to the application locale so built-in UI
 * text (time pickers, tooltip defaults, ...) follows the selected language.
 */
export const initChart = (dom: HTMLElement, code: AppLocale): echarts.ECharts =>
  echarts.init(dom, null, { locale: echartsLocaleFor(code) })

export default initChart