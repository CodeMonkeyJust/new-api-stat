export default {
  brand: {
    appTitle: "Statistiques d'utilisation des modèles",
    loginTitle: "NewAPI Analyse de la consommation de jetons",
    docTitle: "new-api Statistiques",
    badge: "Statistiques"
  },
  menu: {
    myStatistics: "Mes statistiques",
    dashboard: "Tableau de bord",
    dailyTrend: "Tendance quotidienne",
    modelConsumption: "Consommation par modèle",
    hourly: "Statistiques horaires",
    userDaily: "Statistiques utilisateurs",
    userBalance: "Solde utilisateur",
    logout: "Se déconnecter"
  },
  header: {
    userFallback: "Utilisateur"
  },
  login: {
    subtitle: "Connexion utilisateur",
    username: "Nom d'utilisateur",
    password: "Mot de passe",
    usernamePlaceholder: "Saisissez le nom d'utilisateur ou l'e-mail",
    passwordPlaceholder: "Saisissez votre mot de passe",
    submit: "Se connecter",
    success: "Connexion réussie",
    failedNetwork: "Échec de la connexion, vérifiez votre connexion réseau"
  },
  selector: {
    singleDay: "Un jour",
    dateRange: "Plage de dates",
    previousDay: "Jour précédent",
    nextDay: "Jour suivant",
    selectDate: "Sélectionner une date",
    rangeSeparator: "à",
    startPlaceholder: "Date de début",
    endPlaceholder: "Date de fin",
    recentNDays: "Derniers {days} jours",
    selectUser: "Sélectionner un utilisateur",
    allUsers: "Tous les utilisateurs",
    specificUser: "Utilisateurs spécifiques"
  },
  metric: {
    inputToken: "Jetons d'entrée",
    outputToken: "Jetons de sortie",
    totalToken: "Total de jetons",
    totalTokenCount: "Total de jetons",
    cost: "Coût",
    costUsd: "Coût (USD)",
    costUsdAxis: "Coût (USD)",
    tokenAxis: "Jetons",
    valueAxis: "Valeur",
    callCount: "Appels",
    requestCount: "Requêtes",
    userCount: "Utilisateurs",
    model: "Modèle"
  },
  summary: {
    totalTitle: "Résumé total",
    todayTitle: "Aujourd'hui",
    yesterdayTitle: "Hier",
    requestCount: "Requêtes",
    avgTime: "Temps moyen (ms)",
    topUsers7Days: "Meilleurs utilisateurs quotidiens des 7 derniers jours"
  },
  dashboard: {
    title: "Tableau de bord",
    refresh: "Actualiser"
  },
  daily: {
    title: "Tendance de consommation quotidienne",
    userDetailsTitle: "Détails des utilisateurs pour {date}"
  },
  hourly: {
    title: "Statistiques horaires",
    userDetailsTitle: "Détails des utilisateurs de {start}:00 à {end}:00"
  },
  model: {
    title: "Consommation par modèle",
    userDetailsTitle: "Détails des utilisateurs du modèle {model}"
  },
  userDaily: {
    title: "Statistiques utilisateurs"
  },
  userBalance: {
    title: "Soldes utilisateurs",
    refresh: "Actualiser",
    username: "Nom d'utilisateur",
    displayName: "Nom d'affichage",
    email: "E-mail",
    remainingBalance: "Solde restant (USD)",
    spentBalance: "Dépensé (USD)",
    remainingQuota: "Quota restant",
    usedQuota: "Quota utilisé",
    requestCount: "Requêtes",
    group: "Groupe",
    status: "Statut",
    normal: "Normal",
    disabled: "Désactivé"
  },
  myStats: {
    title: "Mes statistiques",
    modelDetails: "Détails des modèles"
  },
  userDetails: {
    username: "Utilisateur"
  },
  validation: {
    usernameRequired: "Saisissez le nom d'utilisateur ou l'e-mail",
    passwordRequired: "Saisissez votre mot de passe"
  },
  confirm: {
    title: "Confirmation",
    logoutMessage: "Voulez-vous vraiment vous déconnecter ?",
    confirm: "Confirmer",
    cancel: "Annuler"
  },
  msg: {
    requestFailed: "Échec de la requête",
    networkError: "Erreur réseau",
    noAccess: "Votre compte n'a pas accès à cette fonctionnalité",
    loadFailed: "Échec du chargement des données",
    loadUserDetailsFailed: "Échec du chargement des détails des utilisateurs",
    loadUsersFailed: "Échec du chargement de la liste des utilisateurs",
    logoutSuccess: "Vous êtes déconnecté"
  },
  server: {
    pleaseLoginFirst: "Veuillez d'abord vous connecter",
    sessionInvalid: "Session invalide, veuillez vous reconnecter",
    loginExpired: "Session expirée, veuillez vous reconnecter",
    forbidden: "Seuls les administrateurs ou les utilisateurs root peuvent accéder aux statistiques",
    internalError: "Erreur interne du serveur",
    invalidParams: "Paramètres de requête invalides",
    missingRequestParam: "Paramètre requis manquant : {param}",
    timezoneInvalid: "APP_TIME_ZONE invalide : {param}",
    currentUserNotFound: "L'utilisateur connecté n'existe pas",
    wrongCredentials: "Nom d'utilisateur ou mot de passe incorrect",
    loginSuccess: "Connexion réussie",
    startDateRequired: "La date de début est requise",
    endDateRequired: "La date de fin est requise",
    startDateFormat: "La date de début doit être au format YYYY-MM-DD",
    endDateFormat: "La date de fin doit être au format YYYY-MM-DD",
    dateRangeInvalid: "La date de début ne peut pas être postérieure à la date de fin et la plage ne peut pas dépasser 366 jours",
    dimensionRequired: "La dimension de statistiques est requise",
    dimensionInvalid: "Dimension de statistiques invalide",
    rankTypeRequired: "Le type de classement est requis",
    rankTypeInvalid: "Type de classement invalide",
    topNRequired: "topN est requis",
    topNPositive: "topN doit être supérieur à 0",
    topNMax: "topN ne peut pas dépasser 1000",
    usernameRequired: "Le nom d'utilisateur est requis",
    usernameTooLong: "Le nom d'utilisateur ne peut pas dépasser 128 caractères",
    passwordRequired: "Le mot de passe est requis",
    passwordTooLong: "Le mot de passe ne peut pas dépasser 256 caractères",
    max100Users: "Au maximum 100 utilisateurs peuvent être sélectionnés à la fois",
    dateFormat: "La date doit être au format YYYY-MM-DD"
  }
}