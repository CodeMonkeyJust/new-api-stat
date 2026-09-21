package com.newapi.analyzer.service;

import com.newapi.analyzer.dto.request.TokenSecurityRequest;
import com.newapi.analyzer.dto.response.TokenSecurityResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 读取 new-api logs 表中的 token_id/token_name/ip，生成令牌来源与泄露风险报告。
 */
@Service
public class TokenSecurityService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;
    private final EntityManager entityManager;
    private final TokenRiskAnalyzer riskAnalyzer;
    private final ZoneId appZone;

    public TokenSecurityService(EntityManager entityManager,
                                TokenRiskAnalyzer riskAnalyzer,
                                @Value("${app.time-zone:Asia/Shanghai}") String appTimeZone) {
        this.entityManager = entityManager;
        this.riskAnalyzer = riskAnalyzer;
        try {
            this.appZone = ZoneId.of(appTimeZone);
        } catch (Exception exception) {
            throw new IllegalArgumentException("APP_TIME_ZONE 无效: " + appTimeZone, exception);
        }
    }

    public TokenSecurityResponse analyze(TokenSecurityRequest request) {
        long startTime = convertToTimestamp(request.getStartDate());
        long endTime = endOfDayTimestamp(request.getEndDate());
        List<String> usernames = request.getUsernames();

        List<TokenIpUsage> usages = loadCurrentUsage(startTime, endTime, usernames);
        List<TokenMinuteUsage> minuteUsages = loadMinuteUsage(startTime, endTime, usernames);
        Map<String, Long> sharedWindows = loadSharedFiveMinuteWindows(startTime, endTime, usernames);
        Map<String, Set<String>> baselineIps = loadBaselineIps(startTime, endTime - startTime, usernames);
        int limit = request.getTopN() == null ? 100 : request.getTopN();

        return riskAnalyzer.analyze(usages, minuteUsages, baselineIps, sharedWindows, limit);
    }

    private List<TokenIpUsage> loadCurrentUsage(long startTime, long endTime, List<String> usernames) {
        String sql = "SELECT token_id, token_name, user_id, username, ip, " +
                "COUNT(*), COALESCE(SUM(prompt_tokens), 0), COALESCE(SUM(completion_tokens), 0), " +
                "COALESCE(SUM(quota), 0), MIN(created_at), MAX(created_at) " +
                "FROM logs WHERE type = 2 AND created_at >= ? AND created_at < ? " +
                usernameFilter(usernames) +
                "GROUP BY token_id, token_name, user_id, username, ip";

        Query query = bindRangeAndUsers(sql, startTime, endTime, usernames);
        List<TokenIpUsage> usages = new ArrayList<>();
        for (Object[] row : resultList(query)) {
            Long tokenId = asLong(row[0]);
            String tokenName = asString(row[1]);
            Long userId = asLong(row[2]);
            String username = asString(row[3]);
            IpAddressInfo ipInfo = IpAddressInfo.from(asString(row[4]));
            usages.add(new TokenIpUsage(
                    TokenRiskAnalyzer.tokenKey(tokenId, userId, username, tokenName),
                    tokenId,
                    tokenName,
                    userId,
                    username,
                    ipInfo.canonical(),
                    ipInfo.ipType(),
                    ipInfo.network(),
                    asLong(row[5]),
                    asLong(row[6]),
                    asLong(row[7]),
                    asLong(row[8]),
                    asLong(row[9]),
                    asLong(row[10])));
        }
        return usages;
    }

    private List<TokenMinuteUsage> loadMinuteUsage(long startTime, long endTime, List<String> usernames) {
        String sql = "SELECT token_id, token_name, user_id, username, FLOOR(created_at / 60.0), COUNT(*) " +
                "FROM logs WHERE type = 2 AND created_at >= ? AND created_at < ? " +
                usernameFilter(usernames) +
                "GROUP BY token_id, token_name, user_id, username, FLOOR(created_at / 60.0)";

        Query query = bindRangeAndUsers(sql, startTime, endTime, usernames);
        List<TokenMinuteUsage> minuteUsages = new ArrayList<>();
        for (Object[] row : resultList(query)) {
            String tokenKey = TokenRiskAnalyzer.tokenKey(asLong(row[0]), asLong(row[2]), asString(row[3]), asString(row[1]));
            long minuteStart = asLong(row[4]) * 60L;
            minuteUsages.add(new TokenMinuteUsage(tokenKey, minuteStart, asLong(row[5])));
        }
        return minuteUsages;
    }

    private Map<String, Long> loadSharedFiveMinuteWindows(long startTime, long endTime, List<String> usernames) {
        String sql = "SELECT token_id, token_name, user_id, username, " +
                "FLOOR(created_at / 300.0), " +
                "COUNT(DISTINCT CASE WHEN ip IS NULL OR TRIM(ip) = '' THEN NULL ELSE TRIM(ip) END) " +
                "FROM logs WHERE type = 2 AND created_at >= ? AND created_at < ? " +
                usernameFilter(usernames) +
                "GROUP BY token_id, token_name, user_id, username, FLOOR(created_at / 300.0) " +
                "HAVING COUNT(DISTINCT CASE WHEN ip IS NULL OR TRIM(ip) = '' THEN NULL ELSE TRIM(ip) END) >= 2";

        Query query = bindRangeAndUsers(sql, startTime, endTime, usernames);
        Map<String, Long> sharedWindows = new HashMap<>();
        for (Object[] row : resultList(query)) {
            String tokenKey = TokenRiskAnalyzer.tokenKey(asLong(row[0]), asLong(row[2]), asString(row[3]), asString(row[1]));
            Long current = sharedWindows.get(tokenKey);
            sharedWindows.put(tokenKey, current == null ? 1L : current + 1L);
        }
        return sharedWindows;
    }

    private Map<String, Set<String>> loadBaselineIps(long startTime, long durationSeconds, List<String> usernames) {
        long previousStart = startTime - durationSeconds;
        String sql = "SELECT token_id, token_name, user_id, username, ip " +
                "FROM logs WHERE type = 2 AND created_at >= ? AND created_at < ? " +
                usernameFilter(usernames) +
                "GROUP BY token_id, token_name, user_id, username, ip";

        Query query = bindRangeAndUsers(sql, previousStart, startTime, usernames);
        Map<String, Set<String>> baselineIps = new HashMap<>();
        for (Object[] row : resultList(query)) {
            String tokenKey = TokenRiskAnalyzer.tokenKey(asLong(row[0]), asLong(row[2]), asString(row[3]), asString(row[1]));
            IpAddressInfo ipInfo = IpAddressInfo.from(asString(row[4]));
            if (ipInfo.known()) {
                baselineIps.computeIfAbsent(tokenKey, ignored -> new HashSet<>()).add(ipInfo.canonical());
            }
        }
        return baselineIps;
    }

    private Query bindRangeAndUsers(String sql, long startTime, long endTime, List<String> usernames) {
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter(1, startTime);
        query.setParameter(2, endTime);
        if (usernames != null && !usernames.isEmpty()) {
            for (int i = 0; i < usernames.size(); i++) {
                query.setParameter(3 + i, usernames.get(i));
            }
        }
        return query;
    }

    private String usernameFilter(List<String> usernames) {
        if (usernames == null || usernames.isEmpty()) {
            return "";
        }
        StringBuilder filter = new StringBuilder("AND username IN (");
        for (int i = 0; i < usernames.size(); i++) {
            if (i > 0) {
                filter.append(", ");
            }
            filter.append('?');
        }
        return filter.append(") ").toString();
    }

    @SuppressWarnings("unchecked")
    private static List<Object[]> resultList(Query query) {
        return query.getResultList();
    }

    private long convertToTimestamp(String date) {
        try {
            return LocalDate.parse(date, DATE_FORMAT).atStartOfDay(appZone).toEpochSecond();
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("日期格式必须为 YYYY-MM-DD", exception);
        }
    }

    private long endOfDayTimestamp(String date) {
        try {
            return LocalDate.parse(date, DATE_FORMAT).plusDays(1).atStartOfDay(appZone).toEpochSecond();
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("日期格式必须为 YYYY-MM-DD", exception);
        }
    }

    private static String asString(Object value) {
        return value == null ? null : value.toString();
    }

    private static Long asLong(Object value) {
        return value == null ? 0L : ((Number) value).longValue();
    }
}
