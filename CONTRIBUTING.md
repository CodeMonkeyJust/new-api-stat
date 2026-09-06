# 贡献指南

感谢你关注 `new-api-stat`。欢迎提交 Issue、改进文档、修复问题和增加测试。

## 提交 Issue

请尽量提供：

- new-api 版本、PostgreSQL 版本、Java/Node.js 版本；
- 可复现步骤和期望/实际结果；
- 脱敏后的日志或截图；
- 不要提交密码、Token、内网地址或生产数据。

## 提交 Pull Request

1. 从 `main` 创建分支。
2. 保持前后端 API 参数和文档同步。
3. 本地执行：
   - `frontend`: `npm ci && npm run build`
   - `backend`: `mvn clean package -DskipTests`
4. 在 PR 描述中说明兼容性、数据库字段变更和安全影响。

本项目读取 new-api 数据库，涉及 SQL 的变更必须说明适用的 new-api 版本和 PostgreSQL 版本。


## 凭据与密钥管理

- 数据库口令等敏感值只能通过环境变量或部署平台 Secret 注入（如 `DB_USERNAME`、`DB_PASSWORD`），禁止写入代码、配置文件、SQL、日志或文档。
- 仓库只允许提交示例文件（如 `frontend/.env.example`）；真实 `.env*`、密钥和证书文件已被 `.gitignore` 排除，提交前请用 `git status` 确认没有未预期的配置文件进入暂存区。
- CI 包含 gitleaks 秘密扫描（`secrets` job）；本地可先运行 `git diff --cached --check` 自查。
- 如果发现敏感信息曾进入提交历史，请立即在服务端轮换该凭据，并联系维护者清理 Git 历史，不要只删除当前文件。
