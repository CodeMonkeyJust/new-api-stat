import { createI18n } from 'vue-i18n'
import { resolveInitialLanguage } from './languages'
import zhCN from './messages/zh-CN'
import zhTW from './messages/zh-TW'
import en from './messages/en'
import fr from './messages/fr'
import ru from './messages/ru'
import ja from './messages/ja'
import vi from './messages/vi'

// Loose typing on purpose: strict message typing would slow down vue-tsc/build.
const messages: Record<string, any> = {
  'zh-CN': zhCN,
  'zh-TW': zhTW,
  en,
  fr,
  ru,
  ja,
  vi
}

const i18n = createI18n({
  legacy: false,
  globalInjection: true,
  locale: resolveInitialLanguage(),
  fallbackLocale: 'en',
  messages
})

export default i18n