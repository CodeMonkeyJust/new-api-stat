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

This project reads new-api's `logs` and `users` tables directly for read-only statistics. Both PostgreSQL and MySQL are supported; switch via `DB_URL`. All examples below use MySQL. First make sure the database account has read-only query privileges, and verify SQL and field version compatibility in a test environment.

`db/MySQL.sql` is a reference file for the table structure of the target new-api MySQL instance, used to explain new-api's database structure (this project mainly reads the `logs` and `users` tables). It is currently verified against new-api **v1.0.0-rc.32**. The file is for reading/reference only and is not a migration script; do not execute it against any database or overwrite existing tables.

### 2. Start the Backend

Requirements: Java 17, Maven 3.9+. The backend is based on Spring Boot 3.5 (see `backend/pom.xml` for the exact version).

```bash
cd backend
mvn clean package -DskipTests
java -jar target/new-api-stat-api-1.0.0.jar
```

The backend listens on `http://localhost:8082` by default with the context path `/new-api-stat-api`. `DB_USERNAME` and `DB_PASSWORD` must be set before the first start; the application never creates or modifies new-api tables. Swagger UI and OpenAPI JSON are disabled by default; set `SWAGGER_ENABLED=true` for development and debugging. API documentation:

- Swagger UI: `http://localhost:8082/new-api-stat-api/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8082/new-api-stat-api/v3/api-docs`

### 3. Start the Frontend

Requirements: Node.js 20.19+, npm 10+.

```bash
cd frontend
npm ci
npm run dev
```

The dev server listens on `http://localhost:3001` by default, and the app page path defaults to `/new-api-stat/` (visiting `http://localhost:3001` automatically redirects there). The dev proxy forwards to `http://localhost:8082` by default; if the backend runs elsewhere, configure it through an environment variable:

```powershell
$env:VITE_API_PROXY_TARGET = "http://localhost:8082"
npm run dev
```

If you change `SERVER_CONTEXT_PATH`, adjust `VITE_API_BASE_URL`, the Vite proxy path and the reverse-proxy rules accordingly.

Production build:

```bash
npm run build
```

The build output is located in `frontend/new-api-stat/` and the page path defaults to `/new-api-stat/` (e.g. `https://<your-domain>/new-api-stat/`). Map `/new-api-stat/` to `frontend/new-api-stat/` and reverse-proxy `/new-api-stat-api` to the backend. To deploy at the site root or under another path, set `VITE_BASE_PATH` before building (e.g. `VITE_BASE_PATH=/` or `VITE_BASE_PATH=/stat/`).

## Configuration

The backend prefers environment variables to avoid committing database credentials to Git:

> Note: set the database environment variables explicitly when deploying; if `DB_URL` is unset, the application falls back to a local PostgreSQL connection. All examples in this document (including the MySQL sample values in the table below) use MySQL.

| Environment variable | Default | Description |
| --- | --- | --- |
| `DB_URL` | `jdbc:mysql://db.example.com:3306/new_api?...` | JDBC URL; both PostgreSQL and MySQL work. The driver and dialect are auto-detected from the URL |
| `DB_USERNAME` | none | Database username; must be set explicitly. A read-only account is recommended |
| `DB_PASSWORD` | none | Database password; must be set explicitly |
| `DB_CONNECTION_INIT_SQL` | `SET time_zone = '+08:00'` | Initialization SQL executed after each connection is established, used to unify the timezone of `created_at` (epoch seconds) statistics. MySQL requires a fixed offset matching `APP_TIME_ZONE`; PostgreSQL uses a different syntax (`SET TIME ZONE '<APP_TIME_ZONE>'`). Set it according to the database in use |
| `SERVER_PORT` | `8082` | Server port |
| `SERVER_CONTEXT_PATH` | `/new-api-stat-api` | Server context path |
| `APP_TIME_ZONE` | `Asia/Shanghai` | Timezone used for the statistics date and hour |
| `APP_ANALYTICS_ROLES` | `10,100` | new-api role values allowed to access the statistics APIs; default is administrator/root user |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:3001,http://localhost:5173` | Allowed frontend origins, comma-separated |
| `SESSION_COOKIE_SECURE` | `false` | Set to `true` for HTTPS deployments |
| `SESSION_COOKIE_SAME_SITE` | `lax` | SameSite attribute of the session cookie |
| `SWAGGER_ENABLED` | `false` | Whether to enable Swagger/OpenAPI |

Example (MySQL):

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
copy target\new-api-stat-api-1.0.0.jar app.jar  # Windows PowerShell can use Copy-Item
# Linux/macOS: cp target/new-api-stat-api-1.0.0.jar app.jar
docker build -t new-api-stat-api .
docker run -d --restart unless-stopped -p 8082:8082 \
  -e DB_URL='jdbc:mysql://host.docker.internal:3306/new_api?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true' \
  -e DB_USERNAME='newapi_stat_readonly' \
  -e DB_PASSWORD='replace-with-a-secret' \
  -e DB_CONNECTION_INIT_SQL="SET time_zone = '+08:00'" \
  --name new-api-stat-api \
  new-api-stat-api
```

Never commit real passwords, internal IPs or `app.jar`. In production, inject credentials through Secrets, environment variables or platform configuration.

### Docker Compose Deployment

Compose starts the nginx frontend and the backend with a single command and builds both images automatically via multi-stage Dockerfiles: the backend uses `backend/Dockerfile.compose` (Maven package → JRE runtime) and the frontend uses `frontend/Dockerfile` (Node build → nginx hosting). No JDK, Maven or Node.js is required on the build machine.

1. Copy `.env.example` to `.env` and fill in the connection to an existing new-api database (this tool only reads the `logs`/`users` tables read-only; it never creates or alters tables, so a read-only account is recommended):

```powershell
Copy-Item .env.example .env
```

On Linux/macOS: `cp .env.example .env`

2. Build and start:

```bash
docker compose up -d --build
```

3. Open `http://<host>:8080/new-api-stat/` (change the host port via `WEB_PORT` in `.env`). nginx reverse-proxies `/new-api-stat-api` to the backend container, so the page and API share the same origin and no extra CORS setup is needed; stop with `docker compose down`.

- The examples use MySQL and reach a new-api database on the host machine via `host.docker.internal` (compose adds the `host-gateway` mapping automatically on Linux). If the database runs in another Docker network, attach this stack to that network as shown in the `compose.yaml` comments and change the host in `DB_URL` to the corresponding service name.
- `compose.yaml` provides the connection initialization SQL for MySQL by default (`SET time_zone = '+08:00'`, matching the default `APP_TIME_ZONE=Asia/Shanghai`); to adjust the timezone or switch to PostgreSQL, override `DB_CONNECTION_INIT_SQL` in `.env` (PostgreSQL syntax example: `SET TIME ZONE 'Asia/Shanghai'`).
- The frontend defaults to `VITE_BASE_PATH=/new-api-stat/` and `VITE_API_BASE_URL=/new-api-stat-api/api`, matching the `location` prefixes in `frontend/nginx.conf`; to deploy under another sub-path, update both the compose `VITE_BASE_PATH` and `frontend/nginx.conf` accordingly.
- Put real credentials only in `.env` (ignored by .gitignore) or in platform Secrets — never commit them.

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
