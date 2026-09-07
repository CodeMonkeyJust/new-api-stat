# new-api-stat

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![CI](https://github.com/CodeMonkeyJust/new-api-stat/actions/workflows/ci.yml/badge.svg)](https://github.com/CodeMonkeyJust/new-api-stat/actions/workflows/ci.yml)

<p align="center">
  <a href="./README.md">简体中文</a> |
  <a href="./README.en.md">English</a> |
  <a href="./README.fr.md">Français</a> |
  <a href="./README.ru.md">Русский</a> |
  <a href="./README.ja.md">日本語</a> |
  <a href="./README.vi.md">Tiếng Việt</a> |
  <strong>繁體中文</strong>
</p>

面向 [new-api](https://github.com/Calcium-Ion/new-api) 的 Token 消耗與呼叫統計分析工具，提供儀表板、趨勢、模型、時段、使用者與餘額等檢視；後端提供 Excel 匯出 API，目前前端頁面尚未整合匯出按鈕。

> 專案處於早期階段，建議先在測試環境驗證資料庫相容性與統計口徑，再用於正式環境。

## 功能

- 使用者登入（沿用 new-api 的 `users` 資料表）
- 總覽儀表板：總量、今日、昨日及近 7 日重點使用者
- 依使用者、模型、分組統計 Token、呼叫次數與費用
- 每日趨勢、24 小時分佈、模型每日明細、使用者每日明細
- 使用者餘額查詢
- 後端提供統計結果 `.xlsx` 匯出 API（前端入口將於後續版本完善）
- 支援 PostgreSQL 與 MySQL 資料庫（透過 `DB_URL` 切換，詳見設定說明）

## 技術棧

- 後端：Java 17、Spring Boot 3、Spring Data JPA，資料庫支援 PostgreSQL 與 MySQL
- 前端：Vue 3、TypeScript、Vite、Element Plus、ECharts

## 專案結構

```text
backend/   Spring Boot API 服務
db/        資料庫結構參考 SQL
frontend/  Vue 前端
```

## 快速開始

### 1. 準備資料庫

本專案直接讀取 new-api 的 `logs` 與 `users` 資料表做唯讀統計；資料庫支援 PostgreSQL 與 MySQL，透過 `DB_URL` 切換，以下範例均以 MySQL 為例。請先確認資料庫帳號具備唯讀查詢權限，並在測試環境驗證 SQL 與欄位版本相容性。

`db/MySQL.sql` 是目標 new-api MySQL 執行個體之庫表結構參考檔案，用於說明 new-api 的資料庫結構（本專案主要讀取其中的 `logs`、`users` 兩張資料表）。目前依 new-api **v1.0.0-rc.32** 的資料表結構驗證。該檔案僅供閱讀參考，不是移轉指令稿；請勿對任何資料庫執行，也不要覆蓋既有庫表。

### 2. 啟動後端

需求：Java 17、Maven 3.9+。後端基於 Spring Boot 3.5（版本以 `backend/pom.xml` 為準）。

```bash
cd backend
mvn clean package -DskipTests
java -jar target/new-api-stat-api-1.0.0.jar
```

後端預設監聽 `http://localhost:8082`，上下文路徑為 `/new-api-stat-api`。首次啟動前必須設定 `DB_USERNAME` 與 `DB_PASSWORD`；應用程式不會建立或修改 new-api 資料表結構。Swagger UI 與 OpenAPI JSON 預設關閉，如需開發除錯請設定 `SWAGGER_ENABLED=true`。API 文件：

- Swagger UI：`http://localhost:8082/new-api-stat-api/swagger-ui.html`
- OpenAPI JSON：`http://localhost:8082/new-api-stat-api/v3/api-docs`

### 3. 啟動前端

需求：Node.js 20.19+、npm 10+。

```bash
cd frontend
npm ci
npm run dev
```

開發伺服器預設位址為 `http://localhost:3001`，頁面路徑預設為 `/new-api-stat/`（瀏覽 `http://localhost:3001` 會自動跳轉到該路徑）。開發代理預設轉送到 `http://localhost:8082`；如後端使用其他位址，請透過環境變數設定：

```powershell
$env:VITE_API_PROXY_TARGET = "http://localhost:8082"
npm run dev
```

如果修改 `SERVER_CONTEXT_PATH`，請同步調整 `VITE_API_BASE_URL`、Vite 代理路徑與反向代理規則。

正式建置：

```bash
npm run build
```

建置產物位於 `frontend/new-api-stat/`，預設頁面路徑為 `/new-api-stat/`（例如 `https://<網域>/new-api-stat/`）。請將 `/new-api-stat/` 對應到 `frontend/new-api-stat/` 進行託管，並將 `/new-api-stat-api` 反向代理到後端。如需部署到根路徑或其他子路徑，請在建置前設定 `VITE_BASE_PATH`（例如 `VITE_BASE_PATH=/` 或 `VITE_BASE_PATH=/stat/`）。

## 設定

後端設定優先使用環境變數，避免將資料庫憑證提交到 Git：

> 說明：部署時請明確設定資料庫相關環境變數；未設定 `DB_URL` 時應用程式內建回退為 PostgreSQL 本機連線。本文所有範例（含下表中 MySQL 範例值）均以 MySQL 為例。

| 環境變數 | 預設值 | 說明 |
| --- | --- | --- |
| `DB_URL` | `jdbc:mysql://db.example.com:3306/new_api?...` | JDBC 位址，PostgreSQL 與 MySQL 均可，驅動與方言依 URL 自動偵測 |
| `DB_USERNAME` | 無預設值 | 資料庫使用者名稱，必須明確設定；建議使用唯讀帳號 |
| `DB_PASSWORD` | 無預設值 | 資料庫密碼，必須明確設定 |
| `DB_CONNECTION_INIT_SQL` | `SET time_zone = '+08:00'` | 每個連線建立後執行的初始化 SQL，用於統一 `created_at`（epoch 秒）統計時區；MySQL 需使用與 `APP_TIME_ZONE` 對應的固定偏移，PostgreSQL 語法不同（`SET TIME ZONE '<APP_TIME_ZONE>'`），請依所用資料庫設定 |
| `SERVER_PORT` | `8082` | 服務連接埠 |
| `SERVER_CONTEXT_PATH` | `/new-api-stat-api` | 服務上下文路徑 |
| `APP_TIME_ZONE` | `Asia/Shanghai` | 統計日期與小時使用的時區 |
| `APP_ANALYTICS_ROLES` | `10,100` | 允許存取統計 API 的 new-api 角色值，預設為管理員/根使用者 |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:3001,http://localhost:5173` | 允許的前端來源，以逗號分隔 |
| `SESSION_COOKIE_SECURE` | `false` | HTTPS 部署時應設為 `true` |
| `SESSION_COOKIE_SAME_SITE` | `lax` | Session Cookie 的 SameSite 屬性 |
| `SWAGGER_ENABLED` | `false` | 是否啟用 Swagger/OpenAPI |

範例（以 MySQL 為例）：

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

### 憑證與金鑰管理

資料庫口令等敏感性值只透過環境變數或部署平台 Secret 注入，本倉庫中不存在真實憑證；`.env*`、金鑰與憑證檔案已被 `.gitignore` 排除，僅保留 `*.example` 範例檔案。CI 內建 gitleaks 秘密掃描，任何形式的硬編碼金鑰都會導致檢查失敗。若曾誤提交敏感性資訊，請立即輪換憑證並清理 Git 歷史。

### 角色與存取控制

統計 API、使用者餘額、使用者清單、資料匯出與資料庫狀態介面預設只允許 `APP_ANALYTICS_ROLES` 中的 new-api 角色存取，預設值為 `10,100`。不同版本的 new-api 角色值可能不同，請以目標版本原始碼/資料庫慣例為準，並透過環境變數調整。一般登入使用者無法存取這些介面。

## Docker

目前 Dockerfile 是執行時期映像檔，不含 Maven 建置流程；請先建置後端 JAR，再建置映像檔：

```bash
cd backend
mvn clean package -DskipTests
copy target\new-api-stat-api-1.0.0.jar app.jar  # Windows PowerShell 可使用 Copy-Item
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

不要把真實密碼、內網 IP 或 `app.jar` 提交到倉庫。正式環境請透過 Secret、環境變數或平台設定注入憑證。

### Docker Compose 部署

Compose 可一鍵啟動「nginx 前端 + 後端」兩個服務，並自動完成多階段映像檔建置：後端使用 `backend/Dockerfile.compose`（Maven 打包 → JRE 執行），前端使用 `frontend/Dockerfile`（Node 建置 → nginx 託管）。建置機無需安裝 JDK、Maven 或 Node。

1. 複製 `.env.example` 為 `.env`，填寫指向既有 new-api 資料庫的連線資訊（本工具以唯讀方式使用 new-api 的 `logs`/`users` 資料表，不會建立或修改資料表，建議使用唯讀帳號）：

```powershell
Copy-Item .env.example .env
```

Linux/macOS 使用：`cp .env.example .env`

2. 建置並啟動：

```bash
docker compose up -d --build
```

3. 瀏覽 `http://<主機>:8080/new-api-stat/`（對外埠號可用 `.env` 中的 `WEB_PORT` 修改）。nginx 已將 `/new-api-stat-api` 反向代理到後端容器，頁面與 API 同源，無需額外 CORS 設定；停止服務用 `docker compose down`。

- 範例以 MySQL 為例，透過 `host.docker.internal` 存取主機上的 new-api 資料庫（Linux 下 compose 已自動加入 `host-gateway` 對應）。若資料庫執行於其他 Docker 網路，請依 `compose.yaml` 末尾註解將本堆疊加入該網路，並把 `.env` 中 `DB_URL` 的主機名稱改為對應服務名稱。
- `compose.yaml` 預設依 MySQL 提供連線初始化 SQL（`SET time_zone = '+08:00'`，與預設 `APP_TIME_ZONE=Asia/Shanghai` 對應）；如需調整時區或改用 PostgreSQL，請在 `.env` 中覆寫 `DB_CONNECTION_INIT_SQL`（PostgreSQL 語法範例：`SET TIME ZONE 'Asia/Shanghai'`）。
- 前端預設 `VITE_BASE_PATH=/new-api-stat/`、`VITE_API_BASE_URL=/new-api-stat-api/api`，與 `frontend/nginx.conf` 的 location 前綴對應；如需部署到其他子路徑，請同步修改 compose 的 `VITE_BASE_PATH` 與 `frontend/nginx.conf`。
- 真實憑證只寫入 `.env`（已被 .gitignore 忽略）或部署平台的 Secret，切勿提交到倉庫。

## 安全說明

- 資料庫帳號建議使用唯讀權限。
- 預設 CORS 僅允許本機開發位址；正式環境必須設定為實際前端網域。
- 統計介面依賴 Session 登入；預設僅允許 new-api 管理員/根使用者角色存取統計、餘額、使用者清單、匯出與資料庫狀態介面。後端啟用基於 Cookie 的 CSRF Token，請使用 HTTPS，並在反向代理層限制存取來源。
- 查詢日期範圍最多 366 天、單次最多選取 100 個使用者；匯出同樣受查詢限制。
- 應用程式不會接收或測試使用者提交的任意資料庫連線位址，僅連線啟動時設定的資料來源。
- 正式環境建議關閉 Swagger，並對應用程式增加登入限流、稽核日誌與更嚴格的角色授權。
- 本專案不負責 new-api 資料庫的備份、移轉或升級；升級前請先備份並進行回歸驗證。
- 目前應用程式使用單機記憶體 Session；正式環境多執行個體部署時，需設定共享 Session（如 Spring Session + Redis）或維持單一執行個體。
- 目前前端正式建置會產生較大的 JavaScript bundle，初次 Alpha 發佈可接受，後續建議做路由懶載入。

## 授權條款

本專案採用 [MIT License](LICENSE)。

## 相關文件

以下文件目前僅提供中文版本：

- [貢獻指南](CONTRIBUTING.md)
- [更新日誌](CHANGELOG.md)
- [安全政策](SECURITY.md)
