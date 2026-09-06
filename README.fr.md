# new-api-stat

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![CI](https://github.com/CodeMonkeyJust/new-api-stat/actions/workflows/ci.yml/badge.svg)](https://github.com/CodeMonkeyJust/new-api-stat/actions/workflows/ci.yml)

<p align="center">
  <a href="./README.md">简体中文</a> |
  <a href="./README.en.md">English</a> |
  <strong>Français</strong> |
  <a href="./README.ru.md">Русский</a> |
  <a href="./README.ja.md">日本語</a> |
  <a href="./README.vi.md">Tiếng Việt</a> |
  <a href="./README.zh-TW.md">繁體中文</a>
</p>

Outil d'analyse statistique de la consommation de jetons (Token) et des appels pour [new-api](https://github.com/Calcium-Ion/new-api). Il fournit des vues de tableau de bord, de tendances, de modèles, de tranches horaires, d'utilisateurs et de solde ; le backend expose une API d'export Excel, mais le frontend n'intègre pas encore de bouton d'export.

> Le projet en est à un stade précoce. Vérifiez d'abord la compatibilité de la base de données et la fiabilité des statistiques dans un environnement de test avant une utilisation en production.

## Fonctionnalités

- Connexion utilisateur (réutilise la table `users` de new-api)
- Tableau de bord de synthèse : totaux, aujourd'hui, hier et principaux utilisateurs des 7 derniers jours
- Statistiques par utilisateur, modèle et groupe : consommation de jetons, nombre d'appels et coût
- Tendance quotidienne, répartition sur 24 heures, détails quotidiens par modèle et par utilisateur
- Consultation du solde des utilisateurs
- API d'export Excel (`.xlsx`) des statistiques côté backend (l'entrée côté frontend sera complétée dans une prochaine version)
- Prise en charge des bases PostgreSQL et MySQL (bascule via `DB_URL`, voir la section configuration)

## Technologies

- Backend : Java 17, Spring Boot 3, Spring Data JPA ; bases de données PostgreSQL et MySQL
- Frontend : Vue 3, TypeScript, Vite, Element Plus, ECharts

## Structure du projet

```text
backend/   Service API Spring Boot
db/        SQL de référence pour la structure de la base de données
frontend/  Frontend Vue
```

## Démarrage rapide

### 1. Préparer la base de données

Ce projet lit directement les tables `logs` et `users` de new-api pour des statistiques en lecture seule. PostgreSQL et MySQL sont pris en charge ; basculez via `DB_URL` (PostgreSQL par défaut). Assurez-vous d'abord que le compte de base de données dispose des droits de lecture seule, puis vérifiez la compatibilité du SQL et des versions de colonnes dans un environnement de test.

`db/MySQL.sql` est un fichier de référence de la structure des tables de l'instance MySQL cible de new-api, servant à expliquer la structure de la base de données de new-api (ce projet lit principalement les tables `logs` et `users`). Il est actuellement vérifié avec new-api **v1.0.0-rc.32**. Ce fichier est fourni uniquement pour lecture/référence et n'est pas un script de migration ; ne l'exécutez sur aucune base de données et ne remplacez aucune table existante.

### 2. Démarrer le backend

Prérequis : Java 17, Maven 3.9+. Le backend est basé sur Spring Boot 3.5 (voir `backend/pom.xml` pour la version exacte).

```bash
cd backend
mvn clean package -DskipTests
java -jar target/newapi-stat-api-1.0.0.jar
```

Le backend écoute par défaut sur `http://localhost:8082`, avec le chemin de contexte `/newapi-stat-api`. `DB_USERNAME` et `DB_PASSWORD` doivent être définis avant le premier démarrage ; l'application ne crée ni ne modifie jamais les tables de new-api. Swagger UI et le JSON OpenAPI sont désactivés par défaut ; définissez `SWAGGER_ENABLED=true` pour le développement et le débogage. Documentation de l'API :

- Swagger UI : `http://localhost:8082/newapi-stat-api/swagger-ui.html`
- JSON OpenAPI : `http://localhost:8082/newapi-stat-api/v3/api-docs`

### 3. Démarrer le frontend

Prérequis : Node.js 20.19+, npm 10+.

```bash
cd frontend
npm ci
npm run dev
```

Le serveur de développement écoute par défaut sur `http://localhost:3001`. Le proxy de développement transmet par défaut vers `http://localhost:8082` ; si le backend utilise une autre adresse, configurez-la via une variable d'environnement :

```powershell
$env:VITE_API_PROXY_TARGET = "http://localhost:8082"
npm run dev
```

Si vous modifiez `SERVER_CONTEXT_PATH`, ajustez en conséquence `VITE_API_BASE_URL`, le chemin du proxy Vite et les règles du proxy inverse.

Compilation de production :

```bash
npm run build
```

Les artefacts de compilation se trouvent dans `frontend/dist/`. Servez-les avec un serveur web et configurez un proxy inverse de `/newapi-stat-api` vers le backend.

## Configuration

Le backend privilégie les variables d'environnement afin d'éviter de commettre les identifiants de base de données dans Git :

| Variable d'environnement | Valeur par défaut | Description |
| --- | --- | --- |
| `DB_URL` | `jdbc:postgresql://localhost:5432/new-api` | URL JDBC ; PostgreSQL ou MySQL fonctionnent tous deux (par ex. `jdbc:mysql://host:3306/new_api?...`). Le pilote et le dialecte sont détectés automatiquement à partir de l'URL |
| `DB_USERNAME` | aucune | Nom d'utilisateur de la base de données ; doit être défini explicitement. Un compte en lecture seule est recommandé |
| `DB_PASSWORD` | aucune | Mot de passe de la base de données ; doit être défini explicitement |
| `DB_CONNECTION_INIT_SQL` | instruction de fuseau horaire PostgreSQL | SQL d'initialisation exécuté après l'établissement de chaque connexion, utilisé pour unifier le fuseau horaire des statistiques de `created_at` (secondes epoch). Pour MySQL, remplacez-le par un décalage fixe correspondant à `APP_TIME_ZONE`, par ex. `SET time_zone = '+08:00'` |
| `SERVER_PORT` | `8082` | Port du serveur |
| `SERVER_CONTEXT_PATH` | `/newapi-stat-api` | Chemin de contexte du serveur |
| `APP_TIME_ZONE` | `Asia/Shanghai` | Fuseau horaire utilisé pour les dates et heures des statistiques |
| `APP_ANALYTICS_ROLES` | `10,100` | Valeurs de rôle new-api autorisées à accéder aux API de statistiques ; par défaut administrateur/utilisateur racine |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:3001,http://localhost:5173` | Origines frontend autorisées, séparées par des virgules |
| `SESSION_COOKIE_SECURE` | `false` | À définir sur `true` pour les déploiements HTTPS |
| `SESSION_COOKIE_SAME_SITE` | `lax` | Attribut SameSite du cookie de session |
| `SWAGGER_ENABLED` | `false` | Activation ou non de Swagger/OpenAPI |

Exemple :

```bash
export DB_URL='jdbc:postgresql://db.example.com:5432/new-api'
export DB_USERNAME='newapi_stat_readonly'
export DB_PASSWORD='replace-with-a-secret'
export APP_TIME_ZONE='Asia/Shanghai'
export CORS_ALLOWED_ORIGINS='https://stat.example.com'
export SESSION_COOKIE_SECURE='true'
export SWAGGER_ENABLED='false'
```

Exemple MySQL (notez que la syntaxe de `DB_CONNECTION_INIT_SQL` diffère de PostgreSQL) :

```bash
export DB_URL='jdbc:mysql://db.example.com:3306/new_api?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true'
export DB_USERNAME='newapi_stat_readonly'
export DB_PASSWORD='replace-with-a-secret'
export DB_CONNECTION_INIT_SQL="SET time_zone = '+08:00'"
export APP_TIME_ZONE='Asia/Shanghai'
export CORS_ALLOWED_ORIGINS='https://stat.example.com'
export SESSION_COOKIE_SECURE='true'
export SWAGGER_ENABLED='false'
```

### Gestion des identifiants et des clés

Les valeurs sensibles telles que les mots de passe de base de données sont injectées uniquement via des variables d'environnement ou des Secrets de la plateforme ; aucun identifiant réel n'existe dans ce dépôt. Les fichiers `.env*`, clés et certificats sont exclus par `.gitignore` ; seuls les fichiers d'exemple `*.example` sont conservés. Le CI exécute une analyse de secrets gitleaks et tout secret codé en dur fait échouer la vérification. Si des informations sensibles ont été commitées par erreur, faites immédiatement pivoter l'identifiant et nettoyez l'historique Git.

### Rôles et contrôle d'accès

Par défaut, seuls les rôles new-api listés dans `APP_ANALYTICS_ROLES` peuvent accéder à l'API de statistiques, au solde des utilisateurs, à la liste des utilisateurs, à l'export des données et aux points de terminaison d'état de la base de données ; la valeur par défaut est `10,100`. Les valeurs de rôle peuvent varier selon les versions de new-api ; référez-vous au code source/conventions de la base de données de la version cible et ajustez-les via la variable d'environnement. Les utilisateurs connectés ordinaires ne peuvent pas accéder à ces points de terminaison.

## Docker

Le Dockerfile actuel est une image d'exécution qui n'inclut pas le processus de compilation Maven. Compilez d'abord le JAR du backend, puis construisez l'image :

```bash
cd backend
mvn clean package -DskipTests
copy target\newapi-stat-api-1.0.0.jar app.jar  # Windows PowerShell peut utiliser Copy-Item
# Linux/macOS : cp target/newapi-stat-api-1.0.0.jar app.jar
docker build -t newapi-stat-api .
docker run --rm -p 8082:8082 \
  -e DB_URL='jdbc:postgresql://host.docker.internal:5432/new-api' \
  -e DB_USERNAME='newapi_stat_readonly' \
  -e DB_PASSWORD='replace-with-a-secret' \
  --name newapi-stat-api \
  newapi-stat-api
```

Exemple MySQL :

```bash
docker run --rm -p 8082:8082 \
  -e DB_URL='jdbc:mysql://host.docker.internal:3306/new_api?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true' \
  -e DB_USERNAME='newapi_stat_readonly' \
  -e DB_PASSWORD='replace-with-a-secret' \
  -e DB_CONNECTION_INIT_SQL="SET time_zone = '+08:00'" \
  --name newapi-stat-api \
  newapi-stat-api
```

Ne commettez jamais de mots de passe réels, d'adresses IP internes ou de `app.jar`. En production, injectez les identifiants via des Secrets, des variables d'environnement ou la configuration de la plateforme.

## Notes de sécurité

- Un compte de base de données en lecture seule est recommandé.
- CORS n'autorise par défaut que les adresses de développement locales ; en production, définissez le domaine réel du frontend.
- Les points de terminaison de statistiques reposent sur la connexion par session ; par défaut, seuls les rôles administrateur/racine de new-api peuvent accéder aux statistiques, au solde, à la liste des utilisateurs, à l'export et à l'état de la base de données. Le backend active un jeton CSRF basé sur un cookie ; utilisez HTTPS et limitez les sources d'accès au niveau du proxy inverse.
- Les plages de dates de requête sont limitées à 366 jours au maximum et à 100 utilisateurs par requête ; l'export suit les mêmes limites.
- L'application n'accepte ni ne teste jamais d'URL de connexion de base de données arbitraires soumises par les utilisateurs ; elle ne se connecte qu'à la source de données configurée au démarrage.
- En production, désactivez Swagger et envisagez une limitation du taux de connexion, des journaux d'audit et une autorisation par rôles plus stricte.
- Ce projet n'effectue ni sauvegarde, ni migration, ni mise à niveau de la base de données de new-api ; sauvegardez-la et exécutez des tests de régression avant toute mise à niveau.
- L'application utilise actuellement des sessions en mémoire sur un seul nœud ; pour les déploiements de production multi-instances, configurez des sessions partagées (par ex. Spring Session + Redis) ou conservez une instance unique.
- La compilation de production actuelle du frontend génère un bundle JavaScript relativement volumineux, acceptable pour la première version Alpha ; un chargement paresseux par route est recommandé ultérieurement.

## Licence

Ce projet est distribué sous la [licence MIT](LICENSE).

## Documents associés

Les documents suivants ne sont actuellement disponibles qu'en chinois :

- [Guide de contribution](CONTRIBUTING.md)
- [Journal des modifications](CHANGELOG.md)
- [Politique de sécurité](SECURITY.md)
