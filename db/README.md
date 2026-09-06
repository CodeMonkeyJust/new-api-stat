# new-api-stat

## 1. Description

`new-api-stat` supports both PostgreSQL and MySQL backends (selected via `DB_URL`). It only reads the `logs` and `users` tables and is strictly read-only with respect to the new-api database. This directory contains the table-structure reference SQL for both supported databases:

- `MySQL.sql` — generated from a target new-api MySQL 8 instance (`new_api`), covering the `logs` and `users` tables as created by new-api.
- `PostgreSQL.sql` — mirrors the same `logs`/`users` layout in PostgreSQL syntax.

The reference layout is documented against **new-api v1.0.0-rc.32** and may change in other releases.

## 2. Notes

- The scripts are reference schemas documenting the columns and indexes the application depends on. They are not migration scripts and must not be applied blindly to an existing new-api database; new-api manages its own schema.
- The layout may vary between new-api releases. Review the version running on the target instance before use.
- Prefer granting the application a read-only database role instead of using an administrator account.
