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
    userDetailsTitle: 'User details for {start}:00 - {end}:00'
  },
  model: {
    title: 'Model Consumption',
    userDetailsTitle: 'Model user details for {model}'
  },
  userDaily: {
    title: 'User Statistics'
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
  myStats: {
    title: 'My Statistics',
    modelDetails: 'Model Details'
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
    logoutSuccess: 'Signed out successfully'
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