package com.newapi.analyzer.service;

import java.util.Locale;

/**
 * 后端支持的数据库类型，以及原生 SQL 中少量方言相关片段的生成。
 *
 * <p>统计 SQL 里的 created_at 是 Unix 秒（epoch seconds），PostgreSQL 用
 * {@code to_timestamp(...)}、MySQL 用 {@code FROM_UNIXTIME(...)} 转换成本地时间；
 * 分组列 group 是保留字，也需要按方言加引号。其余 SQL 均可在两种数据库间直接复用。
 */
enum DatabaseType {

    POSTGRESQL,
    MYSQL;

    /**
     * 根据 JDBC URL 推断数据库类型。无法识别时按 PostgreSQL 处理，
     * 与 application.yml 中 DB_URL 的默认值保持一致。
     */
    static DatabaseType fromJdbcUrl(String jdbcUrl) {
        String url = jdbcUrl == null ? "" : jdbcUrl.toLowerCase(Locale.ROOT);
        if (url.startsWith("jdbc:mysql:") || url.startsWith("jdbc:mariadb:")) {
            return MYSQL;
        }
        return POSTGRESQL;
    }

    /** created_at(epoch 秒) -> 本地日期字符串 'YYYY-MM-DD'，用于按天分组/展示。 */
    String dateExpression() {
        return this == MYSQL
                ? "DATE_FORMAT(FROM_UNIXTIME(created_at), '%Y-%m-%d')"
                : "to_char(to_timestamp(created_at), 'YYYY-MM-DD')";
    }

    /** created_at(epoch 秒) -> 本地小时(0-23)，用于按小时分组/过滤。 */
    String hourExpression() {
        return this == MYSQL
                ? "HOUR(FROM_UNIXTIME(created_at))"
                : "EXTRACT(HOUR FROM to_timestamp(created_at))";
    }

    /** created_at(epoch 秒) -> DATE 类型（用于按天分组的日期列）。 */
    String dayExpression() {
        return this == MYSQL
                ? "DATE(FROM_UNIXTIME(created_at))"
                : "DATE(to_timestamp(created_at))";
    }

    /** 对保留字列名（如 group）按数据库方言加引号。 */
    String quoted(String column) {
        return this == MYSQL ? "`" + column + "`" : "\"" + column + "\"";
    }
}