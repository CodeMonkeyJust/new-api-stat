# new-api-stat

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![CI](https://github.com/CodeMonkeyJust/new-api-stat/actions/workflows/ci.yml/badge.svg)](https://github.com/CodeMonkeyJust/new-api-stat/actions/workflows/ci.yml)

<p align="center">
  <strong>简体中文</strong> |
  <a href="./README.en.md">English</a> |
  <a href="./README.fr.md">Français</a> |
  <a href="./README.ru.md">Русский</a> |
  <a href="./README.ja.md">日本語</a> |
  <a href="./README.vi.md">Tiếng Việt</a> |
  <a href="./README.zh-TW.md">繁體中文</a>
</p>

面向 [new-api](https://github.com/Calcium-Ion/new-api) 的 Token 消耗与调用统计分析工具，提供仪表盘、趋势、模型、时段、用户和余额等视图，后端提供 Excel 导出 API；当前前端页面暂未集成导出按钮。

> 项目处于早期阶段，建议先在测试环境验证数据库兼容性和统计口径，再用于生产环境。

## 功能

- 用户登录（复用 new-api 的 `users` 表）
- 总览仪表盘：总量、今日、昨日及近 7 日重点用户
- 按用户、模型、分组统计 Token、调用次数和费用
- 每日趋势、24 小时分布、模型每日明细、用户每日明细
- 用户余额查询
- 后端提供统计结果 `.xlsx` 导出 API（前端页面入口将在后续版本完善）
- 支持 PostgreSQL 与 MySQL 数据库（通过 `DB_URL` 切换，详见配置说明）

## 技术栈

- 后端：Java 17、Spring Boot 3、Spring Data JPA，数据库支持 PostgreSQL 与 MySQL
- 前端：Vue 3、TypeScript、Vite、Element Plus、ECharts

## 项目结构

```text
backend/   Spring Boot API 服务
db/        数据库结构参考 SQL
frontend/  Vue 前端
```

## 快速开始

### 1. 准备数据库

本项目直接读取 new-api 的 `logs` 和 `users` 表做只读统计，数据库支持 PostgreSQL 与 MySQL，通过 `DB_URL` 切换，以下示例均以 MySQL 为例。请先确认数据库账号具备只读查询权限，并在测试环境验证 SQL 和字段版本兼容性。

`db/MySQL.sql` 是目标 new-api MySQL 实例的库表结构参考文件，用于说明 new-api 的数据库结构（本项目主要读取其中的 `logs`、`users` 两张表）。目前按 new-api **v1.0.0-rc.32** 的表结构验证。该文件仅供阅读参考，不是迁移脚本，请勿对任何数据库执行或覆盖已有库表。

### 2. 启动后端

要求：Java 17、Maven 3.9+。后端基于 Spring Boot 3.5（版本以 backend/pom.xml 为准）。

```bash
cd backend
mvn clean package -DskipTests
java -jar target/new-api-stat-api-1.0.0.jar
```

后端默认监听 `http://localhost:8082`，上下文路径为 `/new-api-stat-api`。首次启动前必须设置 `DB_USERNAME` 和 `DB_PASSWORD`；应用不会创建或修改 new-api 表结构。Swagger UI 和 OpenAPI JSON 默认关闭，如需开发调试请设置 `SWAGGER_ENABLED=true`。API 文档：

- Swagger UI：`http://localhost:8082/new-api-stat-api/swagger-ui.html`
- OpenAPI JSON：`http://localhost:8082/new-api-stat-api/v3/api-docs`

### 3. 启动前端

要求：Node.js 20.19+、npm 10+。

```bash
cd frontend
npm ci
npm run dev
```

开发服务器默认地址为 `http://localhost:3001`，页面路径默认为 `/new-api-stat/`（访问 `http://localhost:3001` 会自动跳转到该路径）。开发代理默认转发到 `http://localhost:8082`；如后端使用其他地址，请通过环境变量配置：

```powershell
$env:VITE_API_PROXY_TARGET = "http://localhost:8082"
npm run dev
```

如果修改 `SERVER_CONTEXT_PATH`，请同步调整 `VITE_API_BASE_URL`、Vite 代理路径和反向代理规则。

生产构建：

```bash
npm run build
```

构建产物位于 `frontend/new-api-stat/`，默认页面路径为 `/new-api-stat/`（如 `https://<域名>/new-api-stat/`）。请将 `/new-api-stat/` 映射到 `frontend/new-api-stat/` 进行托管，并将 `/new-api-stat-api` 反向代理到后端。如需部署到根路径或其他子路径，请在构建前设置 `VITE_BASE_PATH`（如 `VITE_BASE_PATH=/` 或 `VITE_BASE_PATH=/stat/`）。

## 配置

后端配置优先使用环境变量，避免将数据库凭据提交到 Git：

> 说明：部署时请显式设置数据库相关环境变量；未设置 `DB_URL` 时应用内置回退为 PostgreSQL 本地连接。本文所有示例（含下表中 MySQL 示例值）均以 MySQL 为例。

| 环境变量 | 默认值 | 说明 |
| --- | --- | --- |
| `DB_URL` | `jdbc:mysql://db.example.com:3306/new_api?...` | JDBC 地址，PostgreSQL 与 MySQL 均可，驱动与方言按 URL 自动探测 |
| `DB_USERNAME` | 无默认值 | 数据库用户名，必须显式配置，建议使用只读账号 |
| `DB_PASSWORD` | 无默认值 | 数据库密码，必须显式配置 |
| `DB_CONNECTION_INIT_SQL` | `SET time_zone = '+08:00'` | 每个连接建立后执行的初始化 SQL，用于统一 `created_at`(epoch 秒) 统计时区；MySQL 需使用与 `APP_TIME_ZONE` 对应的固定偏移，PostgreSQL 语法不同（`SET TIME ZONE '<APP_TIME_ZONE>'`），请按所用数据库设置 |
| `SERVER_PORT` | `8082` | 服务端口 |
| `SERVER_CONTEXT_PATH` | `/new-api-stat-api` | 服务上下文路径 |
| `APP_TIME_ZONE` | `Asia/Shanghai` | 统计日期和小时使用的时区 |
| `APP_ANALYTICS_ROLES` | `10,100` | 允许访问统计 API 的 new-api 角色值，默认管理员/根用户 |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:3001,http://localhost:5173` | 允许的前端来源，逗号分隔 |
| `SESSION_COOKIE_SECURE` | `false` | HTTPS 部署时应设为 `true` |
| `SESSION_COOKIE_SAME_SITE` | `lax` | Session Cookie 的 SameSite 属性 |
| `SWAGGER_ENABLED` | `false` | 是否启用 Swagger/OpenAPI |

示例（以 MySQL 为例）：

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

### 凭据与密钥管理

数据库口令等敏感值只通过环境变量或部署平台 Secret 注入，仓库中不存在真实凭据；`.env*`、密钥与证书文件已被 `.gitignore` 排除，仅保留 `*.example` 示例文件。CI 内置 gitleaks 秘密扫描，任何形式的硬编码密钥都会导致检查失败。若曾误提交敏感信息，请立即轮换凭据并清理 Git 历史。

### 角色与访问控制

统计 API、用户余额、用户列表、数据导出和数据库状态接口默认只允许 `APP_ANALYTICS_ROLES` 中的 new-api 角色访问。默认值为 `10,100`；不同版本的 new-api 角色值可能不同，请以目标版本源码/数据库约定为准，并通过环境变量调整。普通登录用户不能访问这些接口。

## Docker

当前 Dockerfile 是运行时镜像，不包含 Maven 构建过程；请先构建后端 JAR，再构建镜像：

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

不要把真实密码、内网 IP 或 `app.jar` 提交到仓库。生产环境请通过 Secret、环境变量或平台配置注入凭据。

### Docker Compose 部署

Compose 可一键启动「nginx 前端 + 后端」两个服务，并自动完成多阶段镜像构建：后端使用 `backend/Dockerfile.compose`（Maven 打包 → JRE 运行），前端使用 `frontend/Dockerfile`（Node 构建 → nginx 托管）。构建机无需安装 JDK、Maven 或 Node。

1. 复制 `.env.example` 为 `.env`，填写指向已有 new-api 数据库的连接信息（本工具只读复用 new-api 的 `logs`/`users` 表，不会建表或改表，建议使用只读账号）：

```powershell
Copy-Item .env.example .env
```

Linux/macOS 使用：`cp .env.example .env`

2. 构建并启动：

```bash
docker compose up -d --build
```

3. 访问 `http://<主机>:8080/new-api-stat/`（对外端口可用 `.env` 中的 `WEB_PORT` 修改）。nginx 已将 `/new-api-stat-api` 反向代理到后端容器，页面与 API 同源，无需额外 CORS 配置；停止服务用 `docker compose down`。

- 示例以 MySQL 为例，通过 `host.docker.internal` 访问宿主机上的 new-api 数据库（Linux 下 compose 已自动添加 `host-gateway` 映射）。若数据库运行在其它 Docker 网络，请按 `compose.yaml` 末尾注释将本栈加入该网络，并把 `.env` 中 `DB_URL` 的主机名改为对应服务名。
- `compose.yaml` 默认按 MySQL 提供连接初始化 SQL（`SET time_zone = '+08:00'`，与默认 `APP_TIME_ZONE=Asia/Shanghai` 对应）；如需调整时区或改用 PostgreSQL，请在 `.env` 中覆盖 `DB_CONNECTION_INIT_SQL`（PostgreSQL 语法示例：`SET TIME ZONE 'Asia/Shanghai'`）。
- 前端默认 `VITE_BASE_PATH=/new-api-stat/`、`VITE_API_BASE_URL=/new-api-stat-api/api`，与 `frontend/nginx.conf` 的 location 前缀对应；如需部署到其它子路径，请同步修改 compose 的 `VITE_BASE_PATH` 与 `frontend/nginx.conf`。
- 真实凭据只写入 `.env`（已被 .gitignore 忽略）或部署平台的 Secret，切勿提交到仓库。

## 安全说明

- 数据库账号建议使用只读权限。
- 默认 CORS 仅允许本地开发地址；生产环境必须设置为实际前端域名。
- 统计接口依赖 Session 登录；默认仅允许 new-api 管理员/根用户角色访问统计、余额、用户列表、导出和数据库状态接口。后端启用基于 Cookie 的 CSRF Token，请使用 HTTPS，并在反向代理层限制访问来源。
- 查询日期范围最多 366 天、一次最多选择 100 个用户；导出同样受查询限制。
- 应用不会接收或测试用户提交的任意数据库连接地址，仅连接启动时配置的数据源。
- 生产环境建议关闭 Swagger，并对应用增加登录限流、审计日志和更严格的角色授权。
- 本项目不负责 new-api 数据库的备份、迁移或升级；升级前请先备份并回归验证。
- 当前应用使用单机内存 Session，生产多实例部署需配置共享 Session（如 Spring Session + Redis）或保持单实例。
- 当前前端生产构建会生成较大的 JavaScript bundle，首次 Alpha 发布可接受，后续建议做路由懒加载。

## 许可证

本项目采用 [MIT License](LICENSE)。

## 相关文档

- [贡献指南](CONTRIBUTING.md)
- [更新日志](CHANGELOG.md)
- [安全策略](SECURITY.md)
