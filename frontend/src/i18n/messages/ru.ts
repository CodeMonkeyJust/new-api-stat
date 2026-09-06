export default {
  brand: {
    appTitle: "Аналитика использования моделей",
    loginTitle: "NewAPI Анализ расхода токенов",
    docTitle: "new-api Статистика",
    badge: "Статистика"
  },
  menu: {
    myStatistics: "Моя статистика",
    dashboard: "Панель управления",
    dailyTrend: "Дневная динамика",
    modelConsumption: "Расход по моделям",
    hourly: "Почасовая статистика",
    userDaily: "Статистика пользователей",
    userBalance: "Баланс пользователей",
    logout: "Выйти"
  },
  header: {
    userFallback: "Пользователь"
  },
  login: {
    subtitle: "Вход пользователя",
    username: "Имя пользователя",
    password: "Пароль",
    usernamePlaceholder: "Введите имя пользователя или email",
    passwordPlaceholder: "Введите пароль",
    submit: "Войти",
    success: "Вход выполнен успешно",
    failedNetwork: "Ошибка входа, проверьте сетевое подключение"
  },
  selector: {
    singleDay: "Один день",
    dateRange: "Диапазон дат",
    previousDay: "Предыдущий день",
    nextDay: "Следующий день",
    selectDate: "Выберите дату",
    rangeSeparator: "по",
    startPlaceholder: "Дата начала",
    endPlaceholder: "Дата окончания",
    recentNDays: "Последние {days} дн.",
    selectUser: "Выберите пользователя",
    allUsers: "Все пользователи",
    specificUser: "Отдельные пользователи"
  },
  metric: {
    inputToken: "Входные токены",
    outputToken: "Выходные токены",
    totalToken: "Всего токенов",
    totalTokenCount: "Всего токенов",
    cost: "Расход",
    costUsd: "Стоимость (USD)",
    costUsdAxis: "Расход (USD)",
    tokenAxis: "Токены",
    valueAxis: "Значение",
    callCount: "Вызовы",
    requestCount: "Запросы",
    userCount: "Пользователи",
    model: "Модель"
  },
  summary: {
    totalTitle: "Сводная статистика",
    todayTitle: "Статистика за сегодня",
    yesterdayTitle: "Статистика за вчера",
    requestCount: "Запросы",
    avgTime: "Среднее время (мс)",
    topUsers7Days: "Топ пользователей за последние 7 дней по дням"
  },
  dashboard: {
    title: "Панель управления",
    refresh: "Обновить"
  },
  daily: {
    title: "Динамика дневного расхода",
    userDetailsTitle: "Детали пользователей за {date}"
  },
  hourly: {
    title: "Почасовая статистика",
    userDetailsTitle: "Детали пользователей за {start}:00 - {end}:00"
  },
  model: {
    title: "Расход по моделям",
    userDetailsTitle: "Детали пользователей модели {model}"
  },
  userDaily: {
    title: "Статистика пользователей"
  },
  userBalance: {
    title: "Балансы пользователей",
    refresh: "Обновить",
    username: "Имя пользователя",
    displayName: "Отображаемое имя",
    email: "Email",
    remainingBalance: "Остаток (USD)",
    spentBalance: "Потрачено (USD)",
    remainingQuota: "Остаток квоты",
    usedQuota: "Использованная квота",
    requestCount: "Запросы",
    group: "Группа",
    status: "Статус",
    normal: "Нормальный",
    disabled: "Отключён"
  },
  myStats: {
    title: "Моя статистика",
    modelDetails: "Детализация по моделям"
  },
  userDetails: {
    username: "Пользователь"
  },
  validation: {
    usernameRequired: "Введите имя пользователя или email",
    passwordRequired: "Введите пароль"
  },
  confirm: {
    title: "Уведомление",
    logoutMessage: "Вы уверены, что хотите выйти?",
    confirm: "ОК",
    cancel: "Отмена"
  },
  msg: {
    requestFailed: "Ошибка запроса",
    networkError: "Ошибка сети",
    noAccess: "У вашей учётной записи нет доступа",
    loadFailed: "Не удалось загрузить данные",
    loadUserDetailsFailed: "Не удалось загрузить детали пользователей",
    loadUsersFailed: "Не удалось загрузить список пользователей",
    logoutSuccess: "Вы вышли из системы"
  },
  server: {
    pleaseLoginFirst: "Сначала войдите в систему",
    sessionInvalid: "Недействительная сессия, войдите снова",
    loginExpired: "Сессия истекла, войдите снова",
    forbidden: "Только администраторы или root-пользователи могут просматривать статистику",
    internalError: "Внутренняя ошибка сервера",
    invalidParams: "Недопустимые параметры запроса",
    missingRequestParam: "Отсутствует обязательный параметр: {param}",
    timezoneInvalid: "Недопустимый APP_TIME_ZONE: {param}",
    currentUserNotFound: "Текущий пользователь не найден",
    wrongCredentials: "Неверное имя пользователя или пароль",
    loginSuccess: "Вход выполнен успешно",
    startDateRequired: "Дата начала обязательна",
    endDateRequired: "Дата окончания обязательна",
    startDateFormat: "Дата начала должна быть в формате YYYY-MM-DD",
    endDateFormat: "Дата окончания должна быть в формате YYYY-MM-DD",
    dateRangeInvalid: "Дата начала не может быть позже даты окончания, а диапазон не может превышать 366 дней",
    dimensionRequired: "Измерение статистики обязательно",
    dimensionInvalid: "Недопустимое измерение статистики",
    rankTypeRequired: "Тип ранжирования обязателен",
    rankTypeInvalid: "Недопустимый тип ранжирования",
    topNRequired: "topN обязательно",
    topNPositive: "topN должно быть больше 0",
    topNMax: "topN не может превышать 1000",
    usernameRequired: "Имя пользователя обязательно",
    usernameTooLong: "Имя пользователя не может превышать 128 символов",
    passwordRequired: "Пароль обязателен",
    passwordTooLong: "Пароль не может превышать 256 символов",
    max100Users: "За один раз можно выбрать не более 100 пользователей",
    dateFormat: "Дата должна быть в формате YYYY-MM-DD"
  }
}