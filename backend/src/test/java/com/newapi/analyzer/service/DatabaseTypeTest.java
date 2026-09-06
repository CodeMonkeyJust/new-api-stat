package com.newapi.analyzer.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DatabaseTypeTest {

    @Test
    void infersTypeFromJdbcUrl() {
        assertThat(DatabaseType.fromJdbcUrl("jdbc:mysql://db.example.com:3306/new_api"))
                .isEqualTo(DatabaseType.MYSQL);
        assertThat(DatabaseType.fromJdbcUrl("JDBC:MYSQL://localhost:3306/new_api"))
                .isEqualTo(DatabaseType.MYSQL);
        assertThat(DatabaseType.fromJdbcUrl("jdbc:postgresql://localhost:5432/new-api"))
                .isEqualTo(DatabaseType.POSTGRESQL);
    }

    @Test
    void unknownOrMissingUrlFallsBackToPostgresql() {
        assertThat(DatabaseType.fromJdbcUrl(null)).isEqualTo(DatabaseType.POSTGRESQL);
        assertThat(DatabaseType.fromJdbcUrl("")).isEqualTo(DatabaseType.POSTGRESQL);
        assertThat(DatabaseType.fromJdbcUrl("jdbc:oracle:thin:@localhost:1521:xe"))
                .isEqualTo(DatabaseType.POSTGRESQL);
    }

    @Test
    void dateExpressionUsesDialectSpecificConversion() {
        assertThat(DatabaseType.POSTGRESQL.dateExpression())
                .isEqualTo("to_char(to_timestamp(created_at), 'YYYY-MM-DD')");
        assertThat(DatabaseType.MYSQL.dateExpression())
                .isEqualTo("DATE_FORMAT(FROM_UNIXTIME(created_at), '%Y-%m-%d')");
    }

    @Test
    void hourExpressionUsesDialectSpecificConversion() {
        assertThat(DatabaseType.POSTGRESQL.hourExpression())
                .isEqualTo("EXTRACT(HOUR FROM to_timestamp(created_at))");
        assertThat(DatabaseType.MYSQL.hourExpression())
                .isEqualTo("HOUR(FROM_UNIXTIME(created_at))");
    }

    @Test
    void dayExpressionUsesDialectSpecificConversion() {
        assertThat(DatabaseType.POSTGRESQL.dayExpression())
                .isEqualTo("DATE(to_timestamp(created_at))");
        assertThat(DatabaseType.MYSQL.dayExpression())
                .isEqualTo("DATE(FROM_UNIXTIME(created_at))");
    }

    @Test
    void quotesReservedColumnPerDialect() {
        assertThat(DatabaseType.POSTGRESQL.quoted("group")).isEqualTo("\"group\"");
        assertThat(DatabaseType.MYSQL.quoted("group")).isEqualTo("`group`");
    }
}