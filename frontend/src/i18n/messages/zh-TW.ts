export default {
  brand: {
    appTitle: '模型應用統計',
    loginTitle: 'NewAPI Token消耗分析工具',
    docTitle: 'new-api 統計分析',
    badge: '統計分析'
  },
  menu: {
    myStatistics: '個人統計',
    dashboard: '儀表板',
    dailyTrend: '每日趨勢',
    modelConsumption: '模型消耗統計',
    hourly: '時段統計',
    userDaily: '人員統計',
    userBalance: '用戶餘額',
    logout: '退出登入'
  },
  header: {
    userFallback: '用戶'
  },
  login: {
    subtitle: '用戶登入',
    username: '用戶名',
    password: '密碼',
    usernamePlaceholder: '請輸入用戶名或郵箱',
    passwordPlaceholder: '請輸入密碼',
    submit: '登入',
    success: '登入成功',
    failedNetwork: '登入失敗，請檢查網路連線'
  },
  selector: {
    singleDay: '單日',
    dateRange: '日期範圍',
    previousDay: '前一天',
    nextDay: '後一天',
    selectDate: '選擇日期',
    rangeSeparator: '至',
    startPlaceholder: '開始日期',
    endPlaceholder: '結束日期',
    recentNDays: '最近{days}天',
    selectUser: '選擇人員',
    allUsers: '所有人員',
    specificUser: '指定人員'
  },
  metric: {
    inputToken: '輸入Token',
    outputToken: '輸出Token',
    totalToken: '總Token',
    totalTokenCount: '總Token數',
    cost: '花費',
    costUsd: '費用(美元)',
    costUsdAxis: '花費(美元)',
    tokenAxis: 'Token數',
    valueAxis: '數值',
    callCount: '調用次數',
    requestCount: '請求次數',
    userCount: '用戶數',
    model: '模型'
  },
  summary: {
    totalTitle: '匯總統計',
    todayTitle: '今日統計',
    yesterdayTitle: '昨日統計',
    requestCount: '請求數',
    avgTime: '平均耗時(ms)',
    topUsers7Days: '最近7天每日Top用戶'
  },
  dashboard: {
    title: '儀表板',
    refresh: '重新整理'
  },
  daily: {
    title: '每日消耗趨勢',
    userDetailsTitle: '{date} 人員詳情'
  },
  hourly: {
    title: '時段統計',
    userDetailsTitle: '{start}:00 - {end}:00 人員詳情'
  },
  model: {
    title: '模型消耗統計',
    userDetailsTitle: '{model} 模型人員詳情'
  },
  userDaily: {
    title: '人員統計'
  },
  userBalance: {
    title: '用戶餘額表',
    refresh: '重新整理',
    username: '用戶名',
    displayName: '顯示名稱',
    email: '信箱',
    remainingBalance: '剩餘費用(美元)',
    spentBalance: '已花費(美元)',
    remainingQuota: '剩餘Quota',
    usedQuota: '已用Quota',
    requestCount: '請求次數',
    group: '用戶組',
    status: '狀態',
    normal: '正常',
    disabled: '停用'
  },
  myStats: {
    title: '個人統計',
    modelDetails: '模型明細'
  },
  userDetails: {
    username: '人員名稱'
  },
  validation: {
    usernameRequired: '請輸入用戶名或郵箱',
    passwordRequired: '請輸入密碼'
  },
  confirm: {
    title: '提示',
    logoutMessage: '確定要退出登入嗎？',
    confirm: '確定',
    cancel: '取消'
  },
  msg: {
    requestFailed: '請求失敗',
    networkError: '網路錯誤',
    noAccess: '當前帳號沒有存取權限',
    loadFailed: '載入資料失敗',
    loadUserDetailsFailed: '載入人員詳情失敗',
    loadUsersFailed: '載入使用者清單失敗',
    logoutSuccess: '已退出登入'
  },
  server: {
    pleaseLoginFirst: '請先登入',
    sessionInvalid: '登入狀態無效，請重新登入',
    loginExpired: '登入已失效，請重新登入',
    forbidden: '僅管理員或根用戶可以存取統計資料',
    internalError: '伺服器內部錯誤',
    invalidParams: '請求參數無效',
    missingRequestParam: '缺少請求參數: {param}',
    timezoneInvalid: 'APP_TIME_ZONE 無效: {param}',
    currentUserNotFound: '目前登入用戶不存在',
    wrongCredentials: '用戶名或密碼錯誤',
    loginSuccess: '登入成功',
    startDateRequired: '開始日期不能為空',
    endDateRequired: '結束日期不能為空',
    startDateFormat: '開始日期格式必須為 YYYY-MM-DD',
    endDateFormat: '結束日期格式必須為 YYYY-MM-DD',
    dateRangeInvalid: '開始日期不能晚於結束日期，且日期範圍不能超過 366 天',
    dimensionRequired: '統計維度不能為空',
    dimensionInvalid: '統計維度無效',
    rankTypeRequired: '排行類型不能為空',
    rankTypeInvalid: '排行類型無效',
    topNRequired: 'topN 不能為空',
    topNPositive: 'topN 必須大於 0',
    topNMax: 'topN 不能大於 1000',
    usernameRequired: '用戶名不能為空',
    usernameTooLong: '用戶名不能超過 128 個字元',
    passwordRequired: '密碼不能為空',
    passwordTooLong: '密碼不能超過 256 個字元',
    max100Users: '一次最多選擇 100 個用戶',
    dateFormat: '日期格式必須為 YYYY-MM-DD'
  }
}