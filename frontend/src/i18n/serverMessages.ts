import i18n from './index'

/**
 * Maps Chinese messages produced by the backend to i18n keys in the `server.*`
 * namespace. Exact strings are matched first; parameterized messages are matched
 * by prefix. Unknown messages are returned unchanged so nothing is ever lost.
 */

const exactMap: Record<string, string> = {
  '请先登录': 'server.pleaseLoginFirst',
  '登录状态无效，请重新登录': 'server.sessionInvalid',
  '登录已失效，请重新登录': 'server.loginExpired',
  '仅管理员或根用户可以访问统计数据': 'server.forbidden',
  '服务器内部错误': 'server.internalError',
  '请求参数无效': 'server.invalidParams',
  '当前登录用户不存在': 'server.currentUserNotFound',
  '用户名或密码错误': 'server.wrongCredentials',
  '登录成功': 'server.loginSuccess',
  '开始日期不能为空': 'server.startDateRequired',
  '开始日期格式必须为 YYYY-MM-DD': 'server.startDateFormat',
  '结束日期不能为空': 'server.endDateRequired',
  '结束日期格式必须为 YYYY-MM-DD': 'server.endDateFormat',
  '开始日期不能晚于结束日期，且日期范围不能超过 366 天': 'server.dateRangeInvalid',
  '统计维度不能为空': 'server.dimensionRequired',
  '统计维度无效': 'server.dimensionInvalid',
  '排行类型不能为空': 'server.rankTypeRequired',
  '排行类型无效': 'server.rankTypeInvalid',
  'topN 不能为空': 'server.topNRequired',
  'topN 必须大于 0': 'server.topNPositive',
  'topN 不能大于 1000': 'server.topNMax',
  '用户名不能为空': 'server.usernameRequired',
  '用户名不能超过 128 个字符': 'server.usernameTooLong',
  '密码不能为空': 'server.passwordRequired',
  '密码不能超过 256 个字符': 'server.passwordTooLong',
  '一次最多选择 100 个用户': 'server.max100Users',
  '日期格式必须为 YYYY-MM-DD': 'server.dateFormat'
}

interface PrefixRule {
  prefix: string
  key: string
  paramName: string
}

const prefixRules: PrefixRule[] = [
  { prefix: '缺少请求参数: ', key: 'server.missingRequestParam', paramName: 'param' },
  { prefix: 'APP_TIME_ZONE 无效: ', key: 'server.timezoneInvalid', paramName: 'param' }
]

export const translateServerMessage = (raw?: string | null): string => {
  if (!raw) return ''
  const key = exactMap[raw]
  if (key) return i18n.global.t(key)
  for (const rule of prefixRules) {
    if (raw.startsWith(rule.prefix)) {
      const param = raw.slice(rule.prefix.length)
      return i18n.global.t(rule.key, { [rule.paramName]: param })
    }
  }
  return raw
}

export default translateServerMessage