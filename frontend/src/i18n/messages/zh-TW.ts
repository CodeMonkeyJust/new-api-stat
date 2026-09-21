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
    hour: '時間',
    userDetailsTitle: '{start}:00 - {end}:00 人員詳情'
  },
  model: {
    title: '模型消耗統計',
    userDetailsTitle: '{model} 模型人員詳情'
  },
  userDaily: {
    title: '人員統計',
    hourlyTitle: '{username} · 24小時統計',
    modelUsageTitle: '{username} · 模型使用統計',
    export: "匯出",
    hideZeroCallHours: '隱藏調用次數為0'
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
  tokenSecurity: {
    title: '權杖來源與洩漏預警',
    refresh: '重新整理',
    generatedAt: '產生於 {time}',
    disclaimer: '以下為基於 logs.ip 與每分鐘請求頻率的啟發式風險訊號，不能單獨證明權杖洩漏；NAT、代理、行動網路和主動共享均可能造成誤報。',
    tableTitle: '風險明細（{count}）',
    onlyRisky: '僅顯示風險項目',
    token: '權杖',
    unnamedToken: '未命名權杖',
    noTokenId: '無 token_id，依使用者與權杖名稱合併',
    username: '使用者',
    callCount: '呼叫次數',
    ipCount: 'IP 數',
    networkCount: '網段數',
    sharedWindows: '5 分鐘並行視窗',
    newIpCount: '新 IP 數',
    minuteDetails: '每分鐘請求統計',
    minuteDetailsHint: '僅列出請求量最高的 {count} 分鐘及對應請求數',
    peakRpm: '峰值請求/分鐘',
    peakMinute: '峰值時間',
    activeMinutes: '活躍分鐘數',
    averageRpm: '活躍分鐘均值',
    minute: '分鐘',
    requests: '請求數',
    noMinuteData: '無每分鐘資料',
    minuteRequestCount: '{count} 次',
    riskScore: '風險評分',
    reasons: '風險訊號',
    noRisk: '未發現明顯訊號',
    ipDetails: 'IP 來源明細',
    ipDetailsTruncated: '僅顯示呼叫次數最多的 {count} 個 IP',
    ipCoverage: {
      noLogsTitle: '所選週期沒有消費日誌',
      noLogsDescription: '所選時間範圍內沒有消費呼叫記錄。',
      unavailableTitle: '未採集到 IP，來源風險分析不可用',
      unavailableDescription: '共 {total} 次呼叫，其中 {empty} 次未記錄有效 IP，無法進行 IP 來源風險評分；每分鐘請求統計仍可正常使用。請在 new-api 的「個人設定 → 通知設定」中為相關使用者啟用「記錄 IP 位址」；歷史日誌無法補登。',
      partialTitle: 'IP 資料不完整，分析結果可能遺漏風險',
      partialDescription: '共 {total} 次呼叫，其中 {empty} 次未記錄有效 IP，目前覆蓋率為 {percent}。缺少 IP 的呼叫不會參與來源風險評分。'
    },
    summary: {
      analyzedTokens: '分析權杖數',
      riskyTokens: '風險權杖數',
      highRiskTokens: '高風險權杖數',
      calls: '呼叫總數',
      distinctIps: '獨立 IP 數'
    },
    ip: {
      ip: 'IP 位址',
      type: '來源類型',
      network: '網段',
      firstSeen: '首次出現',
      lastSeen: '最後出現',
      new: '新 IP'
    },
    ipType: {
      PUBLIC: '公網',
      PRIVATE: '內網',
      LOOPBACK: '回送',
      LINK_LOCAL: '連結本機',
      UNKNOWN: '未知'
    },
    risk: {
      NONE: '正常',
      LOW: '低',
      MEDIUM: '中',
      HIGH: '高'
    },
    reason: {
      CONCURRENT_MULTI_IP: '5 分鐘內多 IP 並行',
      MULTIPLE_NETWORKS: '跨多個網段',
      NEW_IP_ACTIVITY: '相較上一週期出現新 IP',
      MANY_IPS: 'IP 數量異常',
      MIXED_NETWORK_SCOPE: '公網與內網混合',
      HIGH_RPM: '單一權杖每分鐘請求數達到 35 次以上'
    }
  },
  myStats: {
    title: '個人統計',
    hourlyDetails: "時段統計",
    modelDetails: '模型明細',
    hideZeroCallHours: '隱藏調用次數為0'
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
    logoutSuccess: '已退出登入',
    exportSuccess: "匯出成功",
    exportFailed: "匯出失敗"
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
