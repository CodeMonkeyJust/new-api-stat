export default {
  brand: {
    appTitle: '模型应用统计',
    loginTitle: 'NewAPI Token消耗分析工具',
    docTitle: 'new-api 统计分析',
    badge: '统计分析'
  },
  menu: {
    myStatistics: '个人统计',
    dashboard: '仪表盘',
    dailyTrend: '每日趋势',
    modelConsumption: '模型消耗统计',
    hourly: '时段统计',
    userDaily: '人员统计',
    userBalance: '用户余额',
    logout: '退出登录'
  },
  header: {
    userFallback: '用户'
  },
  login: {
    subtitle: '用户登录',
    username: '用户名',
    password: '密码',
    usernamePlaceholder: '请输入用户名或邮箱',
    passwordPlaceholder: '请输入密码',
    submit: '登录',
    success: '登录成功',
    failedNetwork: '登录失败，请检查网络连接'
  },
  selector: {
    singleDay: '单日',
    dateRange: '日期范围',
    previousDay: '前一天',
    nextDay: '后一天',
    selectDate: '选择日期',
    rangeSeparator: '至',
    startPlaceholder: '开始日期',
    endPlaceholder: '结束日期',
    recentNDays: '最近{days}天',
    selectUser: '选择人员',
    allUsers: '全部人员',
    specificUser: '指定人员'
  },
  metric: {
    inputToken: '输入Token',
    outputToken: '输出Token',
    totalToken: '总Token',
    totalTokenCount: '总Token数',
    cost: '花费',
    costUsd: '费用(美元)',
    costUsdAxis: '花费(美元)',
    tokenAxis: 'Token数',
    valueAxis: '数值',
    callCount: '调用次数',
    requestCount: '请求次数',
    userCount: '用户数',
    model: '模型'
  },
  summary: {
    totalTitle: '汇总统计',
    todayTitle: '今日统计',
    yesterdayTitle: '昨日统计',
    requestCount: '请求数',
    avgTime: '平均耗时(ms)',
    topUsers7Days: '最近7天每日Top用户'
  },
  dashboard: {
    title: '仪表盘',
    refresh: '刷新'
  },
  daily: {
    title: '每日消耗趋势',
    userDetailsTitle: '{date} 人员详情'
  },
  hourly: {
    title: '时段统计',
    userDetailsTitle: '{start}:00 - {end}:00 人员详情'
  },
  model: {
    title: '模型消耗统计',
    userDetailsTitle: '{model} 模型人员详情'
  },
  userDaily: {
    title: '人员统计'
  },
  userBalance: {
    title: '用户余额表',
    refresh: '刷新',
    username: '用户名',
    displayName: '显示名称',
    email: '邮箱',
    remainingBalance: '剩余费用(美元)',
    spentBalance: '已花费(美元)',
    remainingQuota: '剩余Quota',
    usedQuota: '已用Quota',
    requestCount: '请求次数',
    group: '用户组',
    status: '状态',
    normal: '正常',
    disabled: '禁用'
  },
  myStats: {
    title: '个人统计',
    modelDetails: '模型明细'
  },
  userDetails: {
    username: '人员名称'
  },
  validation: {
    usernameRequired: '请输入用户名或邮箱',
    passwordRequired: '请输入密码'
  },
  confirm: {
    title: '提示',
    logoutMessage: '确定要退出登录吗？',
    confirm: '确定',
    cancel: '取消'
  },
  msg: {
    requestFailed: '请求失败',
    networkError: '网络错误',
    noAccess: '当前账号没有访问权限',
    loadFailed: '加载数据失败',
    loadUserDetailsFailed: '加载人员详情失败',
    loadUsersFailed: '加载用户列表失败',
    logoutSuccess: '已退出登录'
  },
  server: {
    pleaseLoginFirst: '请先登录',
    sessionInvalid: '登录状态无效，请重新登录',
    loginExpired: '登录已失效，请重新登录',
    forbidden: '仅管理员或根用户可以访问统计数据',
    internalError: '服务器内部错误',
    invalidParams: '请求参数无效',
    missingRequestParam: '缺少请求参数: {param}',
    timezoneInvalid: 'APP_TIME_ZONE 无效: {param}',
    currentUserNotFound: '当前登录用户不存在',
    wrongCredentials: '用户名或密码错误',
    loginSuccess: '登录成功',
    startDateRequired: '开始日期不能为空',
    endDateRequired: '结束日期不能为空',
    startDateFormat: '开始日期格式必须为 YYYY-MM-DD',
    endDateFormat: '结束日期格式必须为 YYYY-MM-DD',
    dateRangeInvalid: '开始日期不能晚于结束日期，且日期范围不能超过 366 天',
    dimensionRequired: '统计维度不能为空',
    dimensionInvalid: '统计维度无效',
    rankTypeRequired: '排行类型不能为空',
    rankTypeInvalid: '排行类型无效',
    topNRequired: 'topN 不能为空',
    topNPositive: 'topN 必须大于 0',
    topNMax: 'topN 不能大于 1000',
    usernameRequired: '用户名不能为空',
    usernameTooLong: '用户名不能超过 128 个字符',
    passwordRequired: '密码不能为空',
    passwordTooLong: '密码不能超过 256 个字符',
    max100Users: '一次最多选择 100 个用户',
    dateFormat: '日期格式必须为 YYYY-MM-DD'
  }
}