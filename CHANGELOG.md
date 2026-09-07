# 更新日志

## [1.0.0] - 2026-09-06
- 新增「个人统计」页面：所有已登录用户均可查看本人（按模型聚合的输入/输出 Token、费用与汇总）统计，支持单日或日期范围，数据按登录 session 推导、前端不传用户名，不提供 Excel 导出；导航与路由改为按角色分流——root(100)/管理员(10) 可见全部模块，普通用户仅可见个人统计，直接访问管理页 URL 会被重定向到个人统计。
- 全站统计统一区分输入/输出 Token：hourly/rank/summary/dashboard 接口新增 promptTokens、completionTokens 字段（totalTokens 仍为二者之和）；模型消耗统计与时段统计主图改为输入Token、输出Token + 费用(美元) 的并排分组柱状图（不再把 quota 当作 Token 展示）；仪表盘汇总卡片拆分展示输入/输出 Token；Excel 导出各 rankType 统一为 输入Token | 输出Token | 总Token | 花费(美元) | 调用次数（hourly 含 小时/用户数），daily 导出改用真实 prompt+completion 合计。
- 修复浏览器无法登录：XSRF-TOKEN Cookie 的 Path 由上下文路径 `/new-api-stat-api` 改为 `/`，使位于 `/` 的前端 SPA 页面（Vite 开发服务器/反向代理部署）能通过 document.cookie 读取 token 并自动附带 X-XSRF-TOKEN 请求头，避免登录等写操作被 CSRF 拦截返回 403。
- 按 db/MySQL.sql（含同步的 db/PostgreSQL.sql）结构同步 logs、users 实体字段映射：LogEntity 补齐 request_id/upstream_request_id 并将 type 改为 Long；UserEntity 补齐社交绑定、access_token、额度/请求数、邀请返利、setting/remark、created_at/last_login_at/auth_version 等字段，统一为两种数据库都可读的通用列类型映射。
- 支持 PostgreSQL 与 MySQL 两种数据库：通过 `DB_URL` 切换，驱动/Hibernate 方言自动探测，统计 SQL 按时区与保留字按方言自适应。
- 根据目标 new-api 实例重新生成数据库结构参考脚本：新增 `db/MySQL.sql`，并同步 `db/PostgreSQL.sql`，均覆盖 `logs`、`users` 两张表。
- 首次整理为可公开发布的 `new-api-stat` 工程。
- 使用环境变量配置数据库和部署参数。
- 增加 Session 登录、角色访问控制、CSRF Token 和请求参数限制。
- 移除任意目标数据库连接测试接口。
- 统一前端查询参数并修复 Excel 导出文件扩展名。
- CI 增加 gitleaks 秘密扫描，`.gitignore` 补充密钥/证书忽略规则，贡献指南明确凭据管理要求。
