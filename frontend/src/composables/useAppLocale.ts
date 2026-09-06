import { computed, ref } from 'vue'
import i18n from '@/i18n'
import {
  APP_LANGUAGES,
  LOCALE_STORAGE_KEY,
  echartsLocaleFor,
  resolveInitialLanguage
} from '@/i18n/languages'
import type { AppLocale } from '@/i18n/languages'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import zhTw from 'element-plus/es/locale/lang/zh-tw'
import en from 'element-plus/es/locale/lang/en'
import fr from 'element-plus/es/locale/lang/fr'
import ja from 'element-plus/es/locale/lang/ja'
import ru from 'element-plus/es/locale/lang/ru'
import vi from 'element-plus/es/locale/lang/vi'

const EP_LOCALES: Record<AppLocale, any> = {
  'zh-CN': zhCn,
  'zh-TW': zhTw,
  en,
  fr,
  ja,
  ru,
  vi
}

/**
 * Module-level reactive singleton so the active locale is shared across all
 * components (header, login page, chart views, ...).
 */
const locale = ref<AppLocale>(resolveInitialLanguage())

const syncDocumentMeta = (code: AppLocale) => {
  document.documentElement.lang = code
  document.title = i18n.global.t('brand.docTitle')
}

export const setLocale = (code: AppLocale) => {
  locale.value = code
  i18n.global.locale.value = code
  try {
    localStorage.setItem(LOCALE_STORAGE_KEY, code)
  } catch {
    // storage may be unavailable (private mode / disabled cookies)
  }
  syncDocumentMeta(code)
}

/** Apply the current locale to the document (called once after mount). */
export const applyLocaleMeta = () => {
  syncDocumentMeta(locale.value)
}

const epLocale = computed(() => EP_LOCALES[locale.value])
const echartsLocale = computed(() => echartsLocaleFor(locale.value))
const options = APP_LANGUAGES

export function useAppLocale() {
  return { locale, setLocale, applyLocaleMeta, options, epLocale, echartsLocale }
}

export default useAppLocale