package com.newapi.analyzer.service;

import com.newapi.analyzer.dto.request.QueryRequest;
import com.newapi.analyzer.dto.request.PersonalStatsRequest;
import com.newapi.analyzer.dto.response.*;
import com.newapi.analyzer.repository.LogRepository;
import com.newapi.analyzer.entity.UserEntity;
import com.newapi.analyzer.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service
public class AnalyzerService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    private final EntityManager entityManager;
    private final LogRepository logRepository;
    private final UserRepository userRepository;
    private final ZoneId appZone;
    private final DatabaseType databaseType;

    public AnalyzerService(EntityManager entityManager, LogRepository logRepository, UserRepository userRepository,
                           @Value("${app.time-zone:Asia/Shanghai}") String appTimeZone,
                           @Value("${spring.datasource.url:}") String databaseUrl) {
        this.entityManager = entityManager;
        this.logRepository = logRepository;
        this.userRepository = userRepository;
        this.databaseType = DatabaseType.fromJdbcUrl(databaseUrl);
        try {
            this.appZone = ZoneId.of(appTimeZone);
        } catch (Exception exception) {
            throw new IllegalArgumentException("APP_TIME_ZONE 无效: " + appTimeZone, exception);
        }
    }

    /**
     * 执行原生查询并返回行集合。JPA 的 getResultList() 返回原始 List，
     * 这里集中做一次受检类型转换，避免在每个调用点产生 unchecked 警告。
     */
    @SuppressWarnings("unchecked")
    private static List<Object[]> getResultList(Query query) {
        return query.getResultList();
    }

    public SummaryResponse getSummary(QueryRequest request) {
        Long startTime = convertToTimestamp(request.getStartDate());
        Long endTime = endOfDayTimestamp(request.getEndDate());

        Long totalCount = logRepository.countByTypeAndDateRange(startTime, endTime);
        Long promptTokens = logRepository.sumPromptTokensByTypeAndDateRange(startTime, endTime);
        Long completionTokens = logRepository.sumCompletionTokensByTypeAndDateRange(startTime, endTime);
        Long totalQuota = logRepository.sumQuotaByTypeAndDateRange(startTime, endTime);
        Double avgTime = logRepository.avgUseTimeByTypeAndDateRange(startTime, endTime);

        Long promptTokensValue = promptTokens != null ? promptTokens : 0L;
        Long completionTokensValue = completionTokens != null ? completionTokens : 0L;
        Double totalCost = totalQuota != null ? totalQuota / 500000.0 : 0.0;

        return new SummaryResponse(totalCount, promptTokensValue + completionTokensValue, totalCost,
                avgTime != null ? avgTime : 0.0, promptTokensValue, completionTokensValue);
    }

    public List<RankResponse> getRank(QueryRequest request) {
        Long startTime = convertToTimestamp(request.getStartDate());
        Long endTime = endOfDayTimestamp(request.getEndDate());

        String dimension = request.getDimension();
        String rankType = request.getRankType();
        Integer topN = request.getTopN();

        String groupByField;
        String nameField;
        switch (dimension) {
            case "model":
                groupByField = "model_name";
                nameField = "model_name";
                break;
            case "group":
                groupByField = databaseType.quoted("group");
                nameField = databaseType.quoted("group");
                break;
            default:
                groupByField = "username";
                nameField = "username";
        }

        String orderByField = "cost";
        if ("call_count".equals(rankType)) {
            orderByField = "call_count";
        }

        String sql = String.format(
                "SELECT %s as name, " +
                        "SUM(prompt_tokens) as prompt_tokens, " +
                        "SUM(completion_tokens) as completion_tokens, " +
                        "SUM(prompt_tokens + completion_tokens) as value, " +
                        "SUM(quota) / 500000.0 as cost, " +
                        "COUNT(*) as call_count " +
                        "FROM logs " +
                        "WHERE type = 2 AND created_at >= ? AND created_at < ? " +
                        "GROUP BY %s " +
                        "ORDER BY %s DESC " +
                        "LIMIT ?",
                nameField, groupByField, orderByField
        );

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter(1, startTime);
        query.setParameter(2, endTime);
        query.setParameter(3, topN);

        List<Object[]> results = getResultList(query);
        List<RankResponse> responses = new ArrayList<>();

        for (Object[] row : results) {
            String name = (String) row[0];
            Long promptTokens = ((Number) row[1]).longValue();
            Long completionTokens = ((Number) row[2]).longValue();
            Long value = ((Number) row[3]).longValue();
            Double cost = ((Number) row[4]).doubleValue();
            Long count = ((Number) row[5]).longValue();
            responses.add(new RankResponse(name, promptTokens, completionTokens, value, cost, count));
        }

        return responses;
    }

    public List<HourlyResponse> getHourly(QueryRequest request) {
        Long startTime = convertToTimestamp(request.getStartDate());
        Long endTime = endOfDayTimestamp(request.getEndDate());

        StringBuilder sqlBuilder = new StringBuilder("SELECT " +
                databaseType.hourExpression() + " as hour, " +
                "SUM(quota) as quota, " +
                "SUM(quota) / 500000.0 as cost, " +
                "COUNT(*) as count, " +
                "COUNT(DISTINCT user_id) as users, " +
                "SUM(prompt_tokens) as prompt_tokens, " +
                "SUM(completion_tokens) as completion_tokens " +
                "FROM logs " +
                "WHERE type = 2 AND created_at >= ? AND created_at < ? ");

        if (request.getUsernames() != null && !request.getUsernames().isEmpty()) {
            sqlBuilder.append("AND username IN (");
            for (int i = 0; i < request.getUsernames().size(); i++) {
                if (i > 0) {
                    sqlBuilder.append(", ");
                }
                sqlBuilder.append("?");
            }
            sqlBuilder.append(") ");
        } else if (request.getUsername() != null && !request.getUsername().isEmpty()) {
            sqlBuilder.append("AND username = ? ");
        }

        sqlBuilder.append("GROUP BY " + databaseType.hourExpression() + " " +
                "ORDER BY hour");

        Query query = entityManager.createNativeQuery(sqlBuilder.toString());
        query.setParameter(1, startTime);
        query.setParameter(2, endTime);

        int paramIndex = 3;
        if (request.getUsernames() != null && !request.getUsernames().isEmpty()) {
            for (String username : request.getUsernames()) {
                query.setParameter(paramIndex++, username);
            }
        } else if (request.getUsername() != null && !request.getUsername().isEmpty()) {
            query.setParameter(paramIndex, request.getUsername());
        }

        List<Object[]> results = getResultList(query);
        List<HourlyResponse> responses = new ArrayList<>();

        for (Object[] row : results) {
            Integer hour = ((Number) row[0]).intValue();
            Long quota = ((Number) row[1]).longValue();
            Double cost = ((Number) row[2]).doubleValue();
            Long count = ((Number) row[3]).longValue();
            Long users = ((Number) row[4]).longValue();
            Long promptTokens = ((Number) row[5]).longValue();
            Long completionTokens = ((Number) row[6]).longValue();
            responses.add(new HourlyResponse(hour, quota, cost, count, users, promptTokens, completionTokens));
        }

        return responses;
    }

    public List<HourlyUserResponse> getHourlyUsers(QueryRequest request, Integer hour) {
        Long startTime = convertToTimestamp(request.getStartDate());
        Long endTime = endOfDayTimestamp(request.getEndDate());

        StringBuilder sqlBuilder = new StringBuilder("SELECT " +
                "username, " +
                "SUM(prompt_tokens) as prompt_tokens, " +
                "SUM(completion_tokens) as completion_tokens, " +
                "SUM(prompt_tokens + completion_tokens) as total_tokens, " +
                "SUM(quota) / 500000.0 as cost, " +
                "COUNT(*) as call_count " +
                "FROM logs " +
                "WHERE type = 2 AND created_at >= ? AND created_at < ? " +
                "AND " + databaseType.hourExpression() + " = ? ");

        if (request.getUsernames() != null && !request.getUsernames().isEmpty()) {
            sqlBuilder.append("AND username IN (");
            for (int i = 0; i < request.getUsernames().size(); i++) {
                if (i > 0) {
                    sqlBuilder.append(", ");
                }
                sqlBuilder.append("?");
            }
            sqlBuilder.append(") ");
        } else if (request.getUsername() != null && !request.getUsername().isEmpty()) {
            sqlBuilder.append("AND username = ? ");
        }

        sqlBuilder.append("GROUP BY username " +
                "ORDER BY cost DESC");

        Query query = entityManager.createNativeQuery(sqlBuilder.toString());
        query.setParameter(1, startTime);
        query.setParameter(2, endTime);
        query.setParameter(3, hour);

        int paramIndex = 4;
        if (request.getUsernames() != null && !request.getUsernames().isEmpty()) {
            for (String username : request.getUsernames()) {
                query.setParameter(paramIndex++, username);
            }
        } else if (request.getUsername() != null && !request.getUsername().isEmpty()) {
            query.setParameter(paramIndex, request.getUsername());
        }

        List<Object[]> results = getResultList(query);
        List<HourlyUserResponse> responses = new ArrayList<>();

        for (Object[] row : results) {
            String username = (String) row[0];
            Long promptTokens = ((Number) row[1]).longValue();
            Long completionTokens = ((Number) row[2]).longValue();
            Long totalTokens = ((Number) row[3]).longValue();
            Double cost = ((Number) row[4]).doubleValue();
            Long callCount = ((Number) row[5]).longValue();
            responses.add(new HourlyUserResponse(username, promptTokens, completionTokens, totalTokens, cost, callCount));
        }

        return responses;
    }

    public List<DailyResponse> getDaily(QueryRequest request) {
        Long startTime = convertToTimestamp(request.getStartDate());
        Long endTime = endOfDayTimestamp(request.getEndDate());

        String sql = "SELECT " +
                databaseType.dateExpression() + " as date, " +
                "SUM(quota) as quota, " +
                "SUM(quota) / 500000.0 as cost, " +
                "COUNT(*) as count, " +
                "SUM(prompt_tokens) as prompt_tokens, " +
                "SUM(completion_tokens) as completion_tokens " +
                "FROM logs " +
                "WHERE type = 2 AND created_at >= ? AND created_at < ? " +
                "GROUP BY " + databaseType.dateExpression() + " " +
                "ORDER BY date";

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter(1, startTime);
        query.setParameter(2, endTime);

        List<Object[]> results = getResultList(query);
        List<DailyResponse> responses = new ArrayList<>();

        for (Object[] row : results) {
            String date = (String) row[0];
            Long quota = ((Number) row[1]).longValue();
            Double cost = ((Number) row[2]).doubleValue();
            Long count = ((Number) row[3]).longValue();
            Long promptTokens = ((Number) row[4]).longValue();
            Long completionTokens = ((Number) row[5]).longValue();
            responses.add(new DailyResponse(date, quota, cost, count, promptTokens, completionTokens));
        }

        return responses;
    }

    public List<ModelDailyResponse> getModelDaily(QueryRequest request) {
        Long startTime = convertToTimestamp(request.getStartDate());
        Long endTime = endOfDayTimestamp(request.getEndDate());

        StringBuilder sqlBuilder = new StringBuilder("SELECT " +
                databaseType.dateExpression() + " as date, " +
                "model_name as model, " +
                "SUM(prompt_tokens) as prompt_tokens, " +
                "SUM(completion_tokens) as completion_tokens, " +
                "SUM(prompt_tokens + completion_tokens) as total_tokens, " +
                "SUM(quota) / 500000.0 as cost, " +
                "COUNT(*) as call_count " +
                "FROM logs " +
                "WHERE type = 2 AND created_at >= ? AND created_at < ? ");

        if (request.getUsernames() != null && !request.getUsernames().isEmpty()) {
            sqlBuilder.append("AND username IN (");
            for (int i = 0; i < request.getUsernames().size(); i++) {
                if (i > 0) {
                    sqlBuilder.append(", ");
                }
                sqlBuilder.append("?");
            }
            sqlBuilder.append(") ");
        } else if (request.getUsername() != null && !request.getUsername().isEmpty()) {
            sqlBuilder.append("AND username = ? ");
        }

        sqlBuilder.append("GROUP BY " + databaseType.dateExpression() + ", model_name " +
                "ORDER BY date, cost DESC");

        Query query = entityManager.createNativeQuery(sqlBuilder.toString());
        query.setParameter(1, startTime);
        query.setParameter(2, endTime);

        int paramIndex = 3;
        if (request.getUsernames() != null && !request.getUsernames().isEmpty()) {
            for (String username : request.getUsernames()) {
                query.setParameter(paramIndex++, username);
            }
        } else if (request.getUsername() != null && !request.getUsername().isEmpty()) {
            query.setParameter(paramIndex, request.getUsername());
        }

        List<Object[]> results = getResultList(query);
        List<ModelDailyResponse> responses = new ArrayList<>();

        for (Object[] row : results) {
            String date = (String) row[0];
            String model = (String) row[1];
            Long promptTokens = ((Number) row[2]).longValue();
            Long completionTokens = ((Number) row[3]).longValue();
            Long totalTokens = ((Number) row[4]).longValue();
            Double cost = ((Number) row[5]).doubleValue();
            Long callCount = ((Number) row[6]).longValue();
            responses.add(new ModelDailyResponse(date, model, promptTokens, completionTokens, totalTokens, cost, callCount));
        }

        return responses;
    }

    public List<ModelUserResponse> getModelUsers(QueryRequest request, String model) {
        Long startTime = convertToTimestamp(request.getStartDate());
        Long endTime = endOfDayTimestamp(request.getEndDate());

        StringBuilder sqlBuilder = new StringBuilder("SELECT " +
                "username, " +
                "SUM(prompt_tokens) as prompt_tokens, " +
                "SUM(completion_tokens) as completion_tokens, " +
                "SUM(prompt_tokens + completion_tokens) as total_tokens, " +
                "SUM(quota) / 500000.0 as cost, " +
                "COUNT(*) as call_count " +
                "FROM logs " +
                "WHERE type = 2 AND created_at >= ? AND created_at < ? " +
                "AND model_name = ? ");

        if (request.getUsernames() != null && !request.getUsernames().isEmpty()) {
            sqlBuilder.append("AND username IN (");
            for (int i = 0; i < request.getUsernames().size(); i++) {
                if (i > 0) {
                    sqlBuilder.append(", ");
                }
                sqlBuilder.append("?");
            }
            sqlBuilder.append(") ");
        } else if (request.getUsername() != null && !request.getUsername().isEmpty()) {
            sqlBuilder.append("AND username = ? ");
        }

        sqlBuilder.append("GROUP BY username " +
                "ORDER BY cost DESC");

        Query query = entityManager.createNativeQuery(sqlBuilder.toString());
        query.setParameter(1, startTime);
        query.setParameter(2, endTime);
        query.setParameter(3, model);

        int paramIndex = 4;
        if (request.getUsernames() != null && !request.getUsernames().isEmpty()) {
            for (String username : request.getUsernames()) {
                query.setParameter(paramIndex++, username);
            }
        } else if (request.getUsername() != null && !request.getUsername().isEmpty()) {
            query.setParameter(paramIndex, request.getUsername());
        }

        List<Object[]> results = getResultList(query);
        List<ModelUserResponse> responses = new ArrayList<>();

        for (Object[] row : results) {
            String username = (String) row[0];
            Long promptTokens = ((Number) row[1]).longValue();
            Long completionTokens = ((Number) row[2]).longValue();
            Long totalTokens = ((Number) row[3]).longValue();
            Double cost = ((Number) row[4]).doubleValue();
            Long callCount = ((Number) row[5]).longValue();
            responses.add(new ModelUserResponse(username, promptTokens, completionTokens, totalTokens, cost, callCount));
        }

        return responses;
    }

    public PersonalStatsResponse getPersonalStats(PersonalStatsRequest request, Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("当前登录用户不存在"));
        String username = user.getUsername();

        Long startTime = convertToTimestamp(request.getStartDate());
        Long endTime = endOfDayTimestamp(request.getEndDate());

        String sql = "SELECT " +
                "model_name as model, " +
                "SUM(prompt_tokens) as prompt_tokens, " +
                "SUM(completion_tokens) as completion_tokens, " +
                "SUM(prompt_tokens + completion_tokens) as total_tokens, " +
                "SUM(quota) / 500000.0 as cost, " +
                "COUNT(*) as call_count " +
                "FROM logs " +
                "WHERE type = 2 AND created_at >= ? AND created_at < ? " +
                "AND username = ? " +
                "GROUP BY model_name " +
                "ORDER BY cost DESC";

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter(1, startTime);
        query.setParameter(2, endTime);
        query.setParameter(3, username);

        List<Object[]> results = getResultList(query);
        List<PersonalModelResponse> models = new ArrayList<>();
        long totalCount = 0L;
        long promptTokens = 0L;
        long completionTokens = 0L;
        double totalCost = 0.0;

        for (Object[] row : results) {
            String model = (String) row[0];
            Long modelPromptTokens = ((Number) row[1]).longValue();
            Long modelCompletionTokens = ((Number) row[2]).longValue();
            Long modelTotalTokens = ((Number) row[3]).longValue();
            Double modelCost = ((Number) row[4]).doubleValue();
            Long callCount = ((Number) row[5]).longValue();
            models.add(new PersonalModelResponse(model, modelPromptTokens, modelCompletionTokens, modelTotalTokens, modelCost, callCount));
            totalCount += callCount;
            promptTokens += modelPromptTokens;
            completionTokens += modelCompletionTokens;
            totalCost += modelCost;
        }

        PersonalSummaryResponse summary = new PersonalSummaryResponse(
                totalCount, promptTokens, completionTokens, promptTokens + completionTokens, totalCost);
        return new PersonalStatsResponse(user.getUsername(), user.getDisplayName(), summary, models);
    }

    private Long convertToTimestamp(String dateStr) {
        try {
            return LocalDate.parse(dateStr, DATE_FORMAT).atStartOfDay(appZone).toEpochSecond();
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("日期格式必须为 YYYY-MM-DD", exception);
        }
    }

    private Long endOfDayTimestamp(String dateStr) {
        try {
            return LocalDate.parse(dateStr, DATE_FORMAT).plusDays(1).atStartOfDay(appZone).toEpochSecond();
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("日期格式必须为 YYYY-MM-DD", exception);
        }
    }

    public List<UserBalanceResponse> getUserBalances() {
        String sql = "SELECT " +
                "id, " +
                "username, " +
                "display_name, " +
                "email, " +
                "quota, " +
                "used_quota, " +
                "request_count, " +
                "status, " +
                databaseType.quoted("group") + " " +
                "FROM users " +
                "WHERE deleted_at IS NULL " +
                "ORDER BY quota ASC";

        Query query = entityManager.createNativeQuery(sql);
        List<Object[]> results = getResultList(query);
        List<UserBalanceResponse> responses = new ArrayList<>();

        for (Object[] row : results) {
            Long id = ((Number) row[0]).longValue();
            String username = (String) row[1];
            String displayName = (String) row[2];
            String email = (String) row[3];
            Long quota = ((Number) row[4]).longValue();
            Long usedQuota = ((Number) row[5]).longValue();
            Long requestCount = ((Number) row[6]).longValue();
            Integer status = ((Number) row[7]).intValue();
            String group = (String) row[8];

            Double remainingBalance = quota / 500000.0;
            Double spentBalance = usedQuota / 500000.0;

            responses.add(new UserBalanceResponse(id, username, displayName, email, quota, usedQuota, remainingBalance, spentBalance, requestCount, status, group));
        }

        return responses;
    }

    public DashboardResponse getDashboard() {
        DashboardSummary total = getDashboardSummary(null, null);
        DashboardSummary today = getDashboardSummary(getTodayStart(), getTodayEnd());
        DashboardSummary yesterday = getDashboardSummary(getYesterdayStart(), getYesterdayEnd());
        List<DailyTopUser> last7DaysTopUsers = getLast7DaysTopUsers();

        return new DashboardResponse(total, today, yesterday, last7DaysTopUsers);
    }

    private DashboardSummary getDashboardSummary(Long startTime, Long endTime) {
        String sql = "SELECT " +
                "COUNT(*) as count, " +
                "SUM(prompt_tokens) as prompt_tokens, " +
                "SUM(completion_tokens) as completion_tokens, " +
                "SUM(quota) / 500000.0 as cost, " +
                "AVG(use_time) as avg_time " +
                "FROM logs " +
                "WHERE type = 2";

        if (startTime != null && endTime != null) {
            sql += " AND created_at >= ? AND created_at < ?";
        }

        Query query = entityManager.createNativeQuery(sql);

        if (startTime != null && endTime != null) {
            query.setParameter(1, startTime);
            query.setParameter(2, endTime);
        }

        Object[] result = (Object[]) query.getSingleResult();
        Long totalCount = ((Number) result[0]).longValue();
        Long promptTokens = result[1] != null ? ((Number) result[1]).longValue() : 0L;
        Long completionTokens = result[2] != null ? ((Number) result[2]).longValue() : 0L;
        Double totalCost = result[3] != null ? ((Number) result[3]).doubleValue() : 0.0;
        Double avgTime = result[4] != null ? ((Number) result[4]).doubleValue() : 0.0;

        return new DashboardSummary(totalCount, promptTokens + completionTokens, totalCost, avgTime, promptTokens, completionTokens);
    }

    private List<DailyTopUser> getLast7DaysTopUsers() {
        List<DailyTopUser> results = new ArrayList<>();
        LocalDate today = LocalDate.now(appZone);

        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            Long startTime = date.atStartOfDay(appZone).toEpochSecond();
            Long endTime = date.plusDays(1).atStartOfDay(appZone).toEpochSecond();

            String dateStr = date.toString();

            String topCostSql = "SELECT username, SUM(quota) / 500000.0 as cost " +
                    "FROM logs " +
                    "WHERE type = 2 AND created_at >= ? AND created_at < ? " +
                    "GROUP BY username " +
                    "ORDER BY cost DESC " +
                    "LIMIT 1";

            Query topCostQuery = entityManager.createNativeQuery(topCostSql);
            topCostQuery.setParameter(1, startTime);
            topCostQuery.setParameter(2, endTime);

            Object[] topCostResult = null;
            try {
                topCostResult = (Object[]) topCostQuery.getSingleResult();
            } catch (Exception e) {
            }

            String topCostUser = topCostResult != null ? (String) topCostResult[0] : "-";
            Double topCostValue = topCostResult != null ? ((Number) topCostResult[1]).doubleValue() : 0.0;

            String topPromptTokensSql = "SELECT username, SUM(prompt_tokens) as tokens " +
                    "FROM logs " +
                    "WHERE type = 2 AND created_at >= ? AND created_at < ? " +
                    "GROUP BY username " +
                    "ORDER BY tokens DESC " +
                    "LIMIT 1";

            Query topPromptTokensQuery = entityManager.createNativeQuery(topPromptTokensSql);
            topPromptTokensQuery.setParameter(1, startTime);
            topPromptTokensQuery.setParameter(2, endTime);

            Object[] topPromptTokensResult = null;
            try {
                topPromptTokensResult = (Object[]) topPromptTokensQuery.getSingleResult();
            } catch (Exception e) {
            }

            String topPromptTokensUser = topPromptTokensResult != null ? (String) topPromptTokensResult[0] : "-";
            Long topPromptTokensValue = topPromptTokensResult != null ? ((Number) topPromptTokensResult[1]).longValue() : 0L;

            String topCompletionTokensSql = "SELECT username, SUM(completion_tokens) as tokens " +
                    "FROM logs " +
                    "WHERE type = 2 AND created_at >= ? AND created_at < ? " +
                    "GROUP BY username " +
                    "ORDER BY tokens DESC " +
                    "LIMIT 1";

            Query topCompletionTokensQuery = entityManager.createNativeQuery(topCompletionTokensSql);
            topCompletionTokensQuery.setParameter(1, startTime);
            topCompletionTokensQuery.setParameter(2, endTime);

            Object[] topCompletionTokensResult = null;
            try {
                topCompletionTokensResult = (Object[]) topCompletionTokensQuery.getSingleResult();
            } catch (Exception e) {
            }

            String topCompletionTokensUser = topCompletionTokensResult != null ? (String) topCompletionTokensResult[0] : "-";
            Long topCompletionTokensValue = topCompletionTokensResult != null ? ((Number) topCompletionTokensResult[1]).longValue() : 0L;

            results.add(new DailyTopUser(dateStr, topCostUser, topCostValue, topPromptTokensUser, topPromptTokensValue, topCompletionTokensUser, topCompletionTokensValue));
        }

        return results;
    }

    private Long getTodayStart() {
        return LocalDate.now(appZone).atStartOfDay(appZone).toEpochSecond();
    }

    private Long getTodayEnd() {
        return LocalDate.now(appZone).plusDays(1).atStartOfDay(appZone).toEpochSecond();
    }

    private Long getYesterdayStart() {
        return LocalDate.now(appZone).minusDays(1).atStartOfDay(appZone).toEpochSecond();
    }

    private Long getYesterdayEnd() {
        return LocalDate.now(appZone).atStartOfDay(appZone).toEpochSecond();
    }

    public List<UserResponse> getUsers() {
        String sql = "SELECT " +
                "id, " +
                "username, " +
                "display_name " +
                "FROM users " +
                "WHERE deleted_at IS NULL " +
                "ORDER BY username";

        Query query = entityManager.createNativeQuery(sql);
        List<Object[]> results = getResultList(query);
        List<UserResponse> responses = new ArrayList<>();

        for (Object[] row : results) {
            Long id = ((Number) row[0]).longValue();
            String username = (String) row[1];
            String displayName = (String) row[2];
            responses.add(new UserResponse(id, username, displayName));
        }

        return responses;
    }

    public List<UserDailyResponse> getUserDaily(QueryRequest request) {
        Long startTime = convertToTimestamp(request.getStartDate());
        Long endTime = endOfDayTimestamp(request.getEndDate());

        StringBuilder sqlBuilder = new StringBuilder(
                "SELECT " +
                        "username, " +
                        databaseType.dayExpression() + " as date, " +
                        "SUM(prompt_tokens) as prompt_tokens, " +
                        "SUM(completion_tokens) as completion_tokens, " +
                        "SUM(prompt_tokens + completion_tokens) as total_tokens, " +
                        "SUM(quota) / 500000.0 as cost, " +
                        "COUNT(*) as call_count " +
                        "FROM logs " +
                        "WHERE type = 2 AND created_at >= ? AND created_at < ? "
        );

        List<String> usernames = request.getUsernames();
        if (usernames != null && !usernames.isEmpty()) {
            sqlBuilder.append("AND username IN (");
            for (int i = 0; i < usernames.size(); i++) {
                if (i > 0) {
                    sqlBuilder.append(", ");
                }
                sqlBuilder.append("?");
            }
            sqlBuilder.append(") ");
        }

        sqlBuilder.append("GROUP BY username, " + databaseType.dayExpression() + " ORDER BY username, date");

        Query query = entityManager.createNativeQuery(sqlBuilder.toString());
        query.setParameter(1, startTime);
        query.setParameter(2, endTime);

        if (usernames != null && !usernames.isEmpty()) {
            for (int i = 0; i < usernames.size(); i++) {
                query.setParameter(3 + i, usernames.get(i));
            }
        }

        List<Object[]> results = getResultList(query);
        List<UserDailyResponse> responses = new ArrayList<>();

        for (Object[] row : results) {
            String username = (String) row[0];
            String date = row[1].toString();
            Long promptTokens = ((Number) row[2]).longValue();
            Long completionTokens = ((Number) row[3]).longValue();
            Long totalTokens = ((Number) row[4]).longValue();
            Double cost = ((Number) row[5]).doubleValue();
            Long callCount = ((Number) row[6]).longValue();
            responses.add(new UserDailyResponse(username, date, promptTokens, completionTokens, totalTokens, cost, callCount));
        }

        return responses;
    }
}
