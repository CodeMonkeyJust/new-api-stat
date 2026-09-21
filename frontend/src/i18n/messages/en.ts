export default {
  brand: {
    appTitle: 'Model Usage Analytics',
    loginTitle: 'NewAPI Token Usage Analytics',
    docTitle: 'new-api Analytics',
    badge: 'Analytics'
  },
  menu: {
    myStatistics: 'My Statistics',
    dashboard: 'Dashboard',
    dailyTrend: 'Daily Trend',
    modelConsumption: 'Model Consumption',
    hourly: 'Hourly Statistics',
    userDaily: 'User Statistics',
    userBalance: 'User Balance',
    tokenSecurity: 'Token Security',
    logout: 'Sign Out'
  },
  header: {
    userFallback: 'User'
  },
  login: {
    subtitle: 'User Sign In',
    username: 'Username',
    password: 'Password',
    usernamePlaceholder: 'Enter username or email',
    passwordPlaceholder: 'Enter your password',
    submit: 'Sign In',
    success: 'Signed in successfully',
    failedNetwork: 'Sign-in failed, please check your network connection'
  },
  selector: {
    singleDay: 'Single Day',
    dateRange: 'Date Range',
    previousDay: 'Previous Day',
    nextDay: 'Next Day',
    selectDate: 'Select date',
    rangeSeparator: 'to',
    startPlaceholder: 'Start date',
    endPlaceholder: 'End date',
    recentNDays: 'Last {days} days',
    selectUser: 'Select user',
    allUsers: 'All Users',
    specificUser: 'Specific Users'
  },
  metric: {
    inputToken: 'Input Tokens',
    outputToken: 'Output Tokens',
    totalToken: 'Total Tokens',
    totalTokenCount: 'Total Tokens',
    cost: 'Cost',
    costUsd: 'Cost (USD)',
    costUsdAxis: 'Cost (USD)',
    tokenAxis: 'Tokens',
    valueAxis: 'Value',
    callCount: 'Calls',
    requestCount: 'Requests',
    userCount: 'Users',
    model: 'Model'
  },
  summary: {
    totalTitle: 'Total Summary',
    todayTitle: "Today's Summary",
    yesterdayTitle: "Yesterday's Summary",
    requestCount: 'Requests',
    avgTime: 'Avg Time (ms)',
    topUsers7Days: 'Daily Top Users in Last 7 Days'
  },
  dashboard: {
    title: 'Dashboard',
    refresh: 'Refresh'
  },
  daily: {
    title: 'Daily Consumption Trend',
    userDetailsTitle: 'User details for {date}'
  },
  hourly: {
    title: 'Hourly Statistics',
    hour: 'Time',
    userDetailsTitle: 'User details for {start}:00 - {end}:00'
  },
  model: {
    title: 'Model Consumption',
    userDetailsTitle: 'Model user details for {model}'
  },
  userDaily: {
    title: 'User Statistics',
    hourlyTitle: '{username} · 24-hour Statistics',
    modelUsageTitle: '{username} · Model Usage',
    export: "Export",
    hideZeroCallHours: 'Hide hours with no calls'
  },
  userBalance: {
    title: 'User Balance',
    refresh: 'Refresh',
    username: 'Username',
    displayName: 'Display Name',
    email: 'Email',
    remainingBalance: 'Remaining Balance (USD)',
    spentBalance: 'Spent (USD)',
    remainingQuota: 'Remaining Quota',
    usedQuota: 'Used Quota',
    requestCount: 'Requests',
    group: 'Group',
    status: 'Status',
    normal: 'Normal',
    disabled: 'Disabled'
  },
  tokenSecurity: {
    title: 'Token Source & Leak Signals',
    refresh: 'Refresh',
    generatedAt: 'Generated at {time}',
    disclaimer: 'These are heuristic risk signals based on logs.ip and per-minute request rates and cannot prove token leakage. NAT, proxies, mobile networks, and intentional sharing can cause false positives.',
    tableTitle: 'Risk Details ({count})',
    onlyRisky: 'Show risky tokens only',
    token: 'Token',
    unnamedToken: 'Unnamed token',
    noTokenId: 'No token_id; grouped by user and token name',
    username: 'User',
    callCount: 'Calls',
    ipCount: 'IPs',
    networkCount: 'Networks',
    sharedWindows: '5-min shared windows',
    newIpCount: 'New IPs',
    minuteDetails: 'Requests per Minute',
    minuteDetailsHint: 'Showing the top {count} minutes by requests and their request counts',
    peakRpm: 'Peak requests/min',
    peakMinute: 'Peak time',
    activeMinutes: 'Active minutes',
    averageRpm: 'Avg per active minute',
    minute: 'Minute',
    requests: 'Requests',
    noMinuteData: 'No per-minute data',
    minuteRequestCount: '{count} requests',
    riskScore: 'Risk score',
    reasons: 'Signals',
    noRisk: 'No obvious signal',
    ipDetails: 'IP source details',
    ipDetailsTruncated: 'Showing the {count} most active IPs only',
    ipCoverage: {
      noLogsTitle: 'No consumption logs in the selected period',
      noLogsDescription: 'There are no consumption calls in the selected date range.',
      unavailableTitle: 'No IP data collected; IP source-risk analysis is unavailable',
      unavailableDescription: '{empty} of {total} calls have no usable IP and cannot be scored for IP source risk; per-minute request analysis is still available. Enable "Record IP Address" for the affected users in new-api under Profile → Notification Settings. Historical logs cannot be backfilled.',
      partialTitle: 'IP coverage is incomplete; some risks may be missed',
      partialDescription: '{empty} of {total} calls have no usable IP; current coverage is {percent}. Calls without IP data are excluded from source-risk scoring.'
    },
    summary: {
      analyzedTokens: 'Analyzed Tokens',
      riskyTokens: 'Risky Tokens',
      highRiskTokens: 'High-risk Tokens',
      calls: 'Total Calls',
      distinctIps: 'Distinct IPs'
    },
    ip: {
      ip: 'IP address',
      type: 'Source type',
      network: 'Network',
      firstSeen: 'First seen',
      lastSeen: 'Last seen',
      new: 'New IP'
    },
    ipType: {
      PUBLIC: 'Public',
      PRIVATE: 'Private',
      LOOPBACK: 'Loopback',
      LINK_LOCAL: 'Link local',
      UNKNOWN: 'Unknown'
    },
    risk: {
      NONE: 'Normal',
      LOW: 'Low',
      MEDIUM: 'Medium',
      HIGH: 'High'
    },
    reason: {
      CONCURRENT_MULTI_IP: 'Multi-IP activity within 5 minutes',
      MULTIPLE_NETWORKS: 'Activity across multiple networks',
      NEW_IP_ACTIVITY: 'New IP versus previous period',
      MANY_IPS: 'Unusually high IP count',
      MIXED_NETWORK_SCOPE: 'Public and private IPs mixed',
      HIGH_RPM: 'Single-token request rate reached 35 per minute or more'
    }
  },
  myStats: {
    title: 'My Statistics',
    hourlyDetails: "Hourly Statistics",
    modelDetails: 'Model Details',
    hideZeroCallHours: 'Hide hours with no calls'
  },
  userDetails: {
    username: 'User'
  },
  validation: {
    usernameRequired: 'Please enter username or email',
    passwordRequired: 'Please enter your password'
  },
  confirm: {
    title: 'Notice',
    logoutMessage: 'Are you sure you want to sign out?',
    confirm: 'Confirm',
    cancel: 'Cancel'
  },
  msg: {
    requestFailed: 'Request failed',
    networkError: 'Network error',
    noAccess: 'Your account does not have access',
    loadFailed: 'Failed to load data',
    loadUserDetailsFailed: 'Failed to load user details',
    loadUsersFailed: 'Failed to load user list',
    logoutSuccess: 'Signed out successfully',
    exportSuccess: "Export successful",
    exportFailed: "Export failed"
  },
  server: {
    pleaseLoginFirst: 'Please sign in first',
    sessionInvalid: 'Invalid session, please sign in again',
    loginExpired: 'Session expired, please sign in again',
    forbidden: 'Only administrators or root users can access statistics',
    internalError: 'Internal server error',
    invalidParams: 'Invalid request parameters',
    missingRequestParam: 'Missing required parameter: {param}',
    timezoneInvalid: 'Invalid APP_TIME_ZONE: {param}',
    currentUserNotFound: 'Current user does not exist',
    wrongCredentials: 'Incorrect username or password',
    loginSuccess: 'Signed in successfully',
    startDateRequired: 'Start date is required',
    endDateRequired: 'End date is required',
    startDateFormat: 'Start date must be in YYYY-MM-DD format',
    endDateFormat: 'End date must be in YYYY-MM-DD format',
    dateRangeInvalid: 'Start date cannot be later than end date, and the date range cannot exceed 366 days',
    dimensionRequired: 'Statistics dimension is required',
    dimensionInvalid: 'Invalid statistics dimension',
    rankTypeRequired: 'Rank type is required',
    rankTypeInvalid: 'Invalid rank type',
    topNRequired: 'topN is required',
    topNPositive: 'topN must be greater than 0',
    topNMax: 'topN cannot be greater than 1000',
    usernameRequired: 'Username is required',
    usernameTooLong: 'Username cannot exceed 128 characters',
    passwordRequired: 'Password is required',
    passwordTooLong: 'Password cannot exceed 256 characters',
    max100Users: 'At most 100 users can be selected at a time',
    dateFormat: 'Date must be in YYYY-MM-DD format'
  }
}
