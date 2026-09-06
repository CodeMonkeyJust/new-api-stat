export default {
  brand: {
    appTitle: "モデル利用統計",
    loginTitle: "NewAPI トークン消費分析ツール",
    docTitle: "new-api 統計分析",
    badge: "統計分析"
  },
  menu: {
    myStatistics: "マイ統計",
    dashboard: "ダッシュボード",
    dailyTrend: "日次トレンド",
    modelConsumption: "モデル消費統計",
    hourly: "時間帯統計",
    userDaily: "ユーザー統計",
    userBalance: "ユーザー残高",
    logout: "ログアウト"
  },
  header: {
    userFallback: "ユーザー"
  },
  login: {
    subtitle: "ユーザーログイン",
    username: "ユーザー名",
    password: "パスワード",
    usernamePlaceholder: "ユーザー名またはメールアドレスを入力してください",
    passwordPlaceholder: "パスワードを入力してください",
    submit: "ログイン",
    success: "ログインしました",
    failedNetwork: "ログインに失敗しました。ネットワーク接続を確認してください"
  },
  selector: {
    singleDay: "単日",
    dateRange: "期間指定",
    previousDay: "前日",
    nextDay: "翌日",
    selectDate: "日付を選択",
    rangeSeparator: "から",
    startPlaceholder: "開始日",
    endPlaceholder: "終了日",
    recentNDays: "直近{days}日",
    selectUser: "ユーザーを選択",
    allUsers: "全ユーザー",
    specificUser: "指定ユーザー"
  },
  metric: {
    inputToken: "入力トークン",
    outputToken: "出力トークン",
    totalToken: "総トークン",
    totalTokenCount: "総トークン数",
    cost: "費用",
    costUsd: "費用(USD)",
    costUsdAxis: "費用(USD)",
    tokenAxis: "トークン数",
    valueAxis: "値",
    callCount: "呼び出し回数",
    requestCount: "リクエスト数",
    userCount: "ユーザー数",
    model: "モデル"
  },
  summary: {
    totalTitle: "合計統計",
    todayTitle: "今日の統計",
    yesterdayTitle: "昨日の統計",
    requestCount: "リクエスト数",
    avgTime: "平均応答時間(ms)",
    topUsers7Days: "直近7日間の日別トップユーザー"
  },
  dashboard: {
    title: "ダッシュボード",
    refresh: "更新"
  },
  daily: {
    title: "日次消費トレンド",
    userDetailsTitle: "{date} のユーザー詳細"
  },
  hourly: {
    title: "時間帯統計",
    userDetailsTitle: "{start}:00 - {end}:00 のユーザー詳細"
  },
  model: {
    title: "モデル消費統計",
    userDetailsTitle: "{model} のモデルユーザー詳細"
  },
  userDaily: {
    title: "ユーザー統計"
  },
  userBalance: {
    title: "ユーザー残高一覧",
    refresh: "更新",
    username: "ユーザー名",
    displayName: "表示名",
    email: "メール",
    remainingBalance: "残高(USD)",
    spentBalance: "消費額(USD)",
    remainingQuota: "残りQuota",
    usedQuota: "使用済みQuota",
    requestCount: "リクエスト数",
    group: "ユーザーグループ",
    status: "ステータス",
    normal: "正常",
    disabled: "無効"
  },
  myStats: {
    title: "マイ統計",
    modelDetails: "モデル明細"
  },
  userDetails: {
    username: "ユーザー名"
  },
  validation: {
    usernameRequired: "ユーザー名またはメールアドレスを入力してください",
    passwordRequired: "パスワードを入力してください"
  },
  confirm: {
    title: "確認",
    logoutMessage: "ログアウトしますか？",
    confirm: "OK",
    cancel: "キャンセル"
  },
  msg: {
    requestFailed: "リクエストに失敗しました",
    networkError: "ネットワークエラー",
    noAccess: "このアカウントにはアクセス権限がありません",
    loadFailed: "データの読み込みに失敗しました",
    loadUserDetailsFailed: "ユーザー詳細の読み込みに失敗しました",
    loadUsersFailed: "ユーザー一覧の読み込みに失敗しました",
    logoutSuccess: "ログアウトしました"
  },
  server: {
    pleaseLoginFirst: "先にログインしてください",
    sessionInvalid: "セッションが無効です。再度ログインしてください",
    loginExpired: "ログインの有効期限が切れました。再度ログインしてください",
    forbidden: "統計データにアクセスできるのは管理者またはルートユーザーのみです",
    internalError: "サーバー内部エラー",
    invalidParams: "リクエストパラメータが無効です",
    missingRequestParam: "必須パラメータがありません: {param}",
    timezoneInvalid: "APP_TIME_ZONE が無効です: {param}",
    currentUserNotFound: "現在のログインユーザーが存在しません",
    wrongCredentials: "ユーザー名またはパスワードが正しくありません",
    loginSuccess: "ログインしました",
    startDateRequired: "開始日は必須です",
    endDateRequired: "終了日は必須です",
    startDateFormat: "開始日は YYYY-MM-DD 形式である必要があります",
    endDateFormat: "終了日は YYYY-MM-DD 形式である必要があります",
    dateRangeInvalid: "開始日は終了日より後にできず、期間は366日を超えられません",
    dimensionRequired: "統計ディメンションは必須です",
    dimensionInvalid: "統計ディメンションが無効です",
    rankTypeRequired: "ランキングタイプは必須です",
    rankTypeInvalid: "ランキングタイプが無効です",
    topNRequired: "topN は必須です",
    topNPositive: "topN は 0 より大きい必要があります",
    topNMax: "topN は 1000 を超えられません",
    usernameRequired: "ユーザー名は必須です",
    usernameTooLong: "ユーザー名は128文字以内である必要があります",
    passwordRequired: "パスワードは必須です",
    passwordTooLong: "パスワードは256文字以内である必要があります",
    max100Users: "一度に選択できるユーザーは最大100人です",
    dateFormat: "日付は YYYY-MM-DD 形式である必要があります"
  }
}