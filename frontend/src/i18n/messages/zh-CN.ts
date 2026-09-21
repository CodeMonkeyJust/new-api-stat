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
    tokenSecurity: '令牌安全',
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
    hour: '时间',
    userDetailsTitle: '{start}:00 - {end}:00 人员详情'
  },
  model: {
    title: '模型消耗统计',
    userDetailsTitle: '{model} 模型人员详情'
  },
  userDaily: {
    title: '人员统计',
    hourlyTitle: '{username} · 24小时统计',
    modelUsageTitle: '{username} · 模型使用统计',
    export: "导出",
    hideZeroCallHours: '隐藏调用次数为0'
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
  tokenSecurity: {
    title: '令牌来源与泄露预警',
    refresh: '刷新',
    generatedAt: '生成于 {time}',
    disclaimer: '以下为基于 logs.ip 与每分钟请求频率的启发式风险信号，不能单独证明令牌泄露；NAT、代理、移动网络和主动共享均可能造成误报。',
    tableTitle: '风险明细（{count}）',
    onlyRisky: '仅显示风险项',
    token: '令牌',
    unnamedToken: '未命名令牌',
    noTokenId: '无 token_id，按用户与令牌名归并',
    username: '用户',
    callCount: '调用次数',
    ipCount: 'IP 数',
    networkCount: '网段数',
    sharedWindows: '5 分钟并发窗口',
    newIpCount: '新 IP 数',
    minuteDetails: '每分钟请求统计',
    minuteDetailsHint: '仅列出请求量最高的 {count} 分钟及对应请求数',
    peakRpm: '峰值请求/分钟',
    peakMinute: '峰值时间',
    activeMinutes: '活跃分钟数',
    averageRpm: '活跃分钟均值',
    minute: '分钟',
    requests: '请求数',
    noMinuteData: '无每分钟数据',
    minuteRequestCount: '{count} 次',
    riskScore: '风险评分',
    reasons: '风险信号',
    noRisk: '未发现明显信号',
    ipDetails: 'IP 来源明细',
    ipDetailsTruncated: '仅展示调用次数最多的 {count} 个 IP',
    ipCoverage: {
      noLogsTitle: '所选周期没有消费日志',
      noLogsDescription: '所选时间范围内没有消费调用记录。',
      unavailableTitle: '未采集到 IP，来源风险分析不可用',
      unavailableDescription: '共 {total} 次调用，其中 {empty} 次未记录有效 IP，IP 来源风险无法评分；每分钟请求统计仍可正常使用。请在 new-api 的“个人设置 → 通知设置”中为相关用户启用“记录 IP 地址”；历史日志无法补录。',
      partialTitle: 'IP 数据不完整，分析结果可能遗漏风险',
      partialDescription: '共 {total} 次调用，其中 {empty} 次未记录有效 IP，当前覆盖率为 {percent}。缺失 IP 的调用不会参与来源风险评分。'
    },
    summary: {
      analyzedTokens: '分析令牌数',
      riskyTokens: '风险令牌数',
      highRiskTokens: '高风险令牌数',
      calls: '调用总数',
      distinctIps: '独立 IP 数'
    },
    ip: {
      ip: 'IP 地址',
      type: '来源类型',
      network: '网段',
      firstSeen: '首次出现',
      lastSeen: '最后出现',
      new: '新 IP'
    },
    ipType: {
      PUBLIC: '公网',
      PRIVATE: '内网',
      LOOPBACK: '回环',
      LINK_LOCAL: '链路本地',
      UNKNOWN: '未知'
    },
    risk: {
      NONE: '正常',
      LOW: '低',
      MEDIUM: '中',
      HIGH: '高'
    },
    reason: {
      CONCURRENT_MULTI_IP: '5 分钟内多 IP 并发',
      MULTIPLE_NETWORKS: '跨多个网段',
      NEW_IP_ACTIVITY: '相较上一周期出现新 IP',
      MANY_IPS: 'IP 数量异常',
      MIXED_NETWORK_SCOPE: '公网与内网混合',
      HIGH_RPM: '单令牌每分钟请求数达到 35 次及以上'
    }
  },
  myStats: {
    title: '个人统计',
    hourlyDetails: "时间段统计",
    modelDetails: '模型明细',
    hideZeroCallHours: '隐藏调用次数为0'
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
    logoutSuccess: '已退出登录',
    exportSuccess: "导出成功",
    exportFailed: "导出失败"
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
