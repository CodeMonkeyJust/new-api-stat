import langZH from 'echarts/lib/i18n/langZH'
import langEN from 'echarts/lib/i18n/langEN'
import langFR from 'echarts/lib/i18n/langFR'
import langJA from 'echarts/lib/i18n/langJA'
import langRU from 'echarts/lib/i18n/langRU'
import langVI from 'echarts/lib/i18n/langVI'

export type AppLocale = 'zh-CN' | 'en' | 'fr' | 'ru' | 'ja' | 'vi' | 'zh-TW'

export const LOCALE_ZH_CN: AppLocale = 'zh-CN'
export const LOCALE_EN: AppLocale = 'en'
export const LOCALE_FR: AppLocale = 'fr'
export const LOCALE_RU: AppLocale = 'ru'
export const LOCALE_JA: AppLocale = 'ja'
export const LOCALE_VI: AppLocale = 'vi'
export const LOCALE_ZH_TW: AppLocale = 'zh-TW'

export const LOCALE_STORAGE_KEY = 'app-locale'

export interface AppLanguage {
  code: AppLocale
  label: string
  /** Element Plus locale module name, e.g. `zh-cn`. */
  ep: string
}

/**
 * Supported languages. Order and native names mirror new-api:
 * zh-CN / en / fr / ru / ja / vi / zh-TW.
 */
export const APP_LANGUAGES: AppLanguage[] = [
  { code: LOCALE_ZH_CN, label: '简体中文', ep: 'zh-cn' },
  { code: LOCALE_EN, label: 'English', ep: 'en' },
  { code: LOCALE_FR, label: 'Français', ep: 'fr' },
  { code: LOCALE_RU, label: 'Русский', ep: 'ru' },
  { code: LOCALE_JA, label: '日本語', ep: 'ja' },
  { code: LOCALE_VI, label: 'Tiếng Việt', ep: 'vi' },
  { code: LOCALE_ZH_TW, label: '繁體中文', ep: 'zh-tw' }
]

// ECharts has no Traditional Chinese pack; zh-TW falls back to zh-CN (langZH).
const ECHARTS_LOCALES: Record<AppLocale, any> = {
  [LOCALE_ZH_CN]: langZH,
  [LOCALE_ZH_TW]: langZH,
  [LOCALE_EN]: langEN,
  [LOCALE_FR]: langFR,
  [LOCALE_RU]: langRU,
  [LOCALE_JA]: langJA,
  [LOCALE_VI]: langVI
}

export const echartsLocaleFor = (code: AppLocale): any => ECHARTS_LOCALES[code] ?? langEN

/**
 * Normalize an arbitrary locale string into one of the supported AppLocale codes.
 * Mirrors new-api's logic for Chinese variants; unknown languages fall back to en.
 */
export const normalizeLanguage = (raw?: string | null): AppLocale => {
  if (!raw) return LOCALE_EN
  const code = raw.trim().replace(/_/g, '-')
  const lower = code.toLowerCase()
  if (lower === 'zh' || lower === 'zh-cn' || lower === 'zh-hans' || lower === 'zh-sg') {
    return LOCALE_ZH_CN
  }
  if (lower === 'zh-tw' || lower === 'zh-hk' || lower === 'zh-mo' || lower === 'zh-hant') {
    return LOCALE_ZH_TW
  }
  const primary = lower.split('-')[0]
  const matched = APP_LANGUAGES.find(
    (item) => item.code.toLowerCase() === lower || item.code.toLowerCase() === primary
  )
  return matched ? matched.code : LOCALE_EN
}

/** Initial language: localStorage -> browser language -> en. */
export const resolveInitialLanguage = (): AppLocale => {
  try {
    const stored = localStorage.getItem(LOCALE_STORAGE_KEY)
    if (stored) return normalizeLanguage(stored)
  } catch {
    // ignore storage errors
  }
  if (typeof navigator !== 'undefined' && navigator.language) {
    return normalizeLanguage(navigator.language)
  }
  return LOCALE_EN
}