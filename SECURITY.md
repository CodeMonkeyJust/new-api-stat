# 安全策略

new-api-stat 是面向 [new-api](https://github.com/Calcium-Ion/new-api) 的 Token 消耗与调用统计分析工具，仅读取 new-api 的 `logs` / `users` 表，不接收用户提交的任意数据库连接。

## 报告漏洞

请勿在公开 Issue 或 PR 中提交敏感信息（数据库口令、Token、内网地址、生产数据等）。请通过 GitHub 的「私有漏洞报告（Private vulnerability reporting）」功能提交，或直接联系仓库维护者。

报告中请尽量提供：

- 受影响的版本与目标 new-api 版本；
- 复现步骤与期望/实际结果；
- 脱敏后的日志或最小复现示例。

## 安全约定

- 数据库口令等敏感值只能通过环境变量或部署平台 Secret 注入，禁止写入代码、配置、SQL、日志或文档。
- 仓库不包含真实凭据；若发现敏感信息曾被误提交，请立即在服务端轮换相关凭据并清理 Git 历史。
