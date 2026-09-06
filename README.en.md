# new-api-stat

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![CI](https://github.com/CodeMonkeyJust/new-api-stat/actions/workflows/ci.yml/badge.svg)](https://github.com/CodeMonkeyJust/new-api-stat/actions/workflows/ci.yml)

<p align="center">
  <a href="./README.md">简体中文</a> |
  <strong>English</strong> |
  <a href="./README.fr.md">Français</a> |
  <a href="./README.ru.md">Русский</a> |
  <a href="./README.ja.md">日本語</a> |
  <a href="./README.vi.md">Tiếng Việt</a> |
  <a href="./README.zh-TW.md">繁體中文</a>
</p>

A Token consumption and call statistics tool for [new-api](https://github.com/Calcium-Ion/new-api). It provides dashboard, trend, model, hourly, user and balance views, and the backend exposes an Excel export API; the frontend does not yet integrate an export button.

> The project is in an early stage. Please verify database compatibility and statistics accuracy in a test environment before using it in production.

## Features

- User login (reuses new-api's `users` table)
- Overview dashboard: totals, today, yesterday and top users of the last 7 days
- Statistics by user, model and group for Token usage, call counts and cost
- Daily trend, 24-hour distribution, per-model daily details and per-user daily details
- User balance lookup
- Backend Excel (`.xlsx`) export API for statistics (the frontend entry will be completed in a later release)
- Supports PostgreSQL and MySQL databases (switch via `DB_URL`, see the configuration section)

## Tech Stack

- Backend: Java 17, Spring Boot 3, Spring Data JPA; database support for PostgreSQL and MySQL
- Frontend: Vue 3, TypeScript, Vite, Element Plus, ECharts

## Project Structure

```text
backend/   Spring Boot API service
db/        Reference SQL for the database structure
frontend/  Vue frontend
```

## Quick Start

### 1. Prepare the Database

This project reads new-api's `logs` and `users` tables directly for read-only statistics. Both PostgreSQL and MySQL are supported; switch via `DB_URL` (PostgreSQL is the default). First make sure the database account has read-only query privileges, and verify SQL and field version compatibility in a test environment.

`db/MySQL.sql` is a reference file for the table structure of the target new-api MySQL instance, used to explain new-api's database structure (this project mainly reads the `logs` and `users` tables). It is currently verified against new-api **v1.0.0-rc.32**. The file is for reading/reference only and is not a migration script; do not execute it against any database or overwrite existing tables.

### 2. Start the Backend

Requirements: Java 17, Maven 3.9+. The backend is based on Spring Boot 3.5 (see `backend/pom.xml` for the exact version).

```bash
cd backend
mvn clean package -DskipTests
java -jar target/newapi-stat-api-1.0.0.jar
```

The backend listens on `http://localhost:8082` by default with the context path `/newapi-stat-api`. `DB_USERNAME` and `DB_PASSWORD` must be set before the first start; the application never creates or modifies new-api tables. Swagger UI and OpenAPI JSON are disabled by default; set `SWAGGER_ENABLED=true` for development and debugging. API documentation:

- Swagger UI: `http://localhost:8082/newapi-stat-api/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8082/newapi-stat-api/v3/api-docs`

### 3. Start the Frontend

Requirements: Node.js 20.19+, npm 10+.

```bash
cd frontend
npm ci
npm run dev
```

The dev server listens on `http://localhost:3001` by default. The dev proxy forwards to `http://localhost:8082` by default; if the backend runs elsewhere, configure it through an environment variable:

```powershell
$env:VITE_API_PROXY_TARGET = "http://localhost:8082"
npm run dev
```

If you change `SERVER_CONTEXT_PATH`, adjust `VITE_API_BASE_URL`, the Vite proxy path and the reverse-proxy rules accordingly.

Production build:

```bash
npm run build
```

The build output is located in `frontend/dist/`. Serve it with a web server and reverse-proxy `/newapi-stat-api` to the backend.

## Configuration

The backend prefers environment variables to avoid committing database credentials to Git:

| Environment variable | Default | Description |
| --- | --- | --- |
| `DB_URL` | `jdbc:postgresql://localhost:5432/new-api` | JDBC URL; both PostgreSQL and MySQL work (e.g. `jdbc:mysql://host:3306/new_api?...`). The driver and dialect are auto-detected from the URL |
| `DB_USERNAME` | none | Database username; must be set explicitly. A read-only account is recommended |
| `DB_PASSWORD` | none | Database password; must be set explicitly |
| `DB_CONNECTION_INIT_SQL` | PostgreSQL timezone statement | Initialization SQL executed after each connection is established, used to unify the timezone of `created_at` (epoch seconds) statistics. For MySQL, change it to a fixed offset matching `APP_TIME_ZONE`, e.g. `SET time_zone = '+08:00'` |
| `SERVER_PORT` | `8082` | Server port |
| `SERVER_CONTEXT_PATH` | `/newapi-stat-api` | Server context path |
| `APP_TIME_ZONE` | `Asia/Shanghai` | Timezone used for the statistics date and hour |
| `APP_ANALYTICS_ROLES` | `10,100` | new-api role values allowed to access the statistics APIs; default is administrator/root user |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:3001,http://localhost:5173` | Allowed frontend origins, comma-separated |
| `SESSION_COOKIE_SECURE` | `false` | Set to `true` for HTTPS deployments |
| `SESSION_COOKIE_SAME_SITE` | `lax` | SameSite attribute of the session cookie |
| `SWAGGER_ENABLED` | `false` | Whether to enable Swagger/OpenAPI |

Example:

```bash
export DB_URL='jdbc:postgresql://db.example.com:5432/new-api'
export DB_USERNAME='newapi_stat_readonly'
export DB_PASSWORD='replace-with-a-secret'
export APP_TIME_ZONE='Asia/Shanghai'
export CORS_ALLOWED_ORIGINS='https://stat.example.com'
export SESSION_COOKIE_SECURE='true'
export SWAGGER_ENABLED='false'
```

MySQL example (note that the `DB_CONNECTION_INIT_SQL` syntax differs from PostgreSQL):

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

### Credentials and Key Management

Sensitive values such as database passwords are injected only through environment variables or platform Secrets; no real credentials exist in this repository. `.env*`, key and certificate files are excluded by `.gitignore`; only `*.example` sample files are kept. CI runs a gitleaks secret scan, and any hard-coded secret fails the check. If sensitive information was ever committed, rotate the credential immediately and clean up the Git history.

### Roles and Access Control

By default, only the new-api roles listed in `APP_ANALYTICS_ROLES` can access the statistics API, user balance, user list, data export and database-status endpoints; the default is `10,100`. Role values may differ between new-api versions; refer to the target version's source code/database conventions and adjust them through the environment variable. Regular logged-in users cannot access these endpoints.

## Docker

The current Dockerfile is a runtime image and does not include the Maven build. Build the backend JAR first, then build the image:

```bash
cd backend
mvn clean package -DskipTests
copy target\newapi-stat-api-1.0.0.jar app.jar  # Windows PowerShell can use Copy-Item
# Linux/macOS: cp target/newapi-stat-api-1.0.0.jar app.jar
docker build -t newapi-stat-api .
docker run --rm -p 8082:8082 \
  -e DB_URL='jdbc:postgresql://host.docker.internal:5432/new-api' \
  -e DB_USERNAME='newapi_stat_readonly' \
  -e DB_PASSWORD='replace-with-a-secret' \
  --name newapi-stat-api \
  newapi-stat-api
```

MySQL example:

```bash
docker run --rm -p 8082:8082 \
  -e DB_URL='jdbc:mysql://host.docker.internal:3306/new_api?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true' \
  -e DB_USERNAME='newapi_stat_readonly' \
  -e DB_PASSWORD='replace-with-a-secret' \
  -e DB_CONNECTION_INIT_SQL="SET time_zone = '+08:00'" \
  --name newapi-stat-api \
  newapi-stat-api
```

Never commit real passwords, internal IPs or `app.jar`. In production, inject credentials through Secrets, environment variables or platform configuration.

## Security Notes

- A read-only database account is recommended.
- CORS allows only local development addresses by default; set the real frontend domain in production.
- Statistics endpoints rely on session login; by default only new-api administrator/root roles can access statistics, balance, user list, export and database-status endpoints. The backend enables a cookie-based CSRF token; use HTTPS and restrict access sources at the reverse-proxy layer.
- Query date ranges are limited to at most 366 days and 100 users per query; export follows the same query limits.
- The application never accepts or tests arbitrary database connection URLs submitted by users; it only connects to the datasource configured at startup.
- In production, disable Swagger and consider login rate limiting, audit logging and stricter role authorization.
- This project does not back up, migrate or upgrade the new-api database; back it up and run regression tests before upgrading.
- The application currently uses single-node in-memory sessions; for multi-instance production deployments, configure shared sessions (e.g. Spring Session + Redis) or keep a single instance.
- The current frontend production build generates a relatively large JavaScript bundle, which is acceptable for the initial Alpha release; route-level lazy loading is recommended later.

## License

This project is licensed under the [MIT License](LICENSE).

## Related Documents

The following documents are currently available in Chinese only:

- [Contribution Guide](CONTRIBUTING.md)
- [Changelog](CHANGELOG.md)
- [Security Policy](SECURITY.md)
