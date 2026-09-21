package com.newapi.analyzer.service;

import com.newapi.analyzer.dto.response.TokenIpResponse;
import com.newapi.analyzer.dto.response.TokenMinuteResponse;
import com.newapi.analyzer.dto.response.TokenRiskResponse;
import com.newapi.analyzer.dto.response.TokenSecurityResponse;
import com.newapi.analyzer.dto.response.TokenSecuritySummary;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * 基于调用日志的令牌来源风险分析。
 *
 * <p>评分是排查线索，不是泄露判定：令牌在多 IP 5 分钟内并发调用、跨网段、相对上一周期出现
 * 新 IP、IP 数量异常增长或峰值请求数达到 35 次/分钟时加分。NAT、代理池、移动网络或主动共享
 * 令牌都可能产生类似信号。
 */
@Component
public class TokenRiskAnalyzer {

    private static final int MAX_IP_DETAILS = 100;
    private static final int MAX_MINUTE_DETAILS = 10;
    private static final long HIGH_RPM_THRESHOLD = 35L;
    private static final int HIGH_RPM_SCORE = 30;
    private static final double QUOTA_PER_USD = 500000.0;

    public TokenSecurityResponse analyze(List<TokenIpUsage> usages,
                                         Map<String, Set<String>> baselineIpsByToken,
                                         Map<String, Long> sharedWindowsByToken,
                                         int limit) {
        return analyze(usages, List.of(), baselineIpsByToken, sharedWindowsByToken, limit);
    }

    public TokenSecurityResponse analyze(List<TokenIpUsage> usages,
                                         List<TokenMinuteUsage> minuteUsages,
                                         Map<String, Set<String>> baselineIpsByToken,
                                         Map<String, Long> sharedWindowsByToken,
                                         int limit) {
        Map<String, TokenAccumulator> tokens = new HashMap<>();
        Map<String, List<TokenMinuteUsage>> minuteUsagesByToken = new HashMap<>();
        Set<String> allKnownIps = new HashSet<>();
        long totalLogCount = 0L;
        long emptyIpLogCount = 0L;
        long usableIpLogCount = 0L;

        for (TokenMinuteUsage usage : minuteUsages) {
            minuteUsagesByToken.computeIfAbsent(usage.tokenKey(), ignored -> new ArrayList<>()).add(usage);
        }

        for (TokenIpUsage usage : usages) {
            TokenAccumulator token = tokens.computeIfAbsent(usage.tokenKey(),
                    ignored -> new TokenAccumulator(usage.tokenKey(), usage.tokenId(), usage.tokenName(), usage.username()));
            token.add(usage);
            long usageCalls = usage.callCount() == null ? 0L : usage.callCount();
            totalLogCount += usageCalls;
            IpAddressInfo ipInfo = new IpAddressInfo(usage.ip(), usage.ipType(), usage.network(),
                    !"UNKNOWN".equals(usage.ipType()));
            if (ipInfo.known()) {
                allKnownIps.add(ipInfo.canonical());
                usableIpLogCount += usageCalls;
            } else {
                emptyIpLogCount += usageCalls;
            }
        }

        long totalCalls = 0L;
        long riskyTokens = 0L;
        long highRiskTokens = 0L;
        List<TokenRiskResponse> responses = new ArrayList<>();

        for (TokenAccumulator token : tokens.values()) {
            totalCalls += token.callCount;
            TokenRiskResponse response = assessToken(token, baselineIpsByToken, sharedWindowsByToken,
                    minuteUsagesByToken.getOrDefault(token.tokenKey, List.of()));
            responses.add(response);
            if (!"NONE".equals(response.getRiskLevel())) {
                riskyTokens++;
            }
            if ("HIGH".equals(response.getRiskLevel())) {
                highRiskTokens++;
            }
        }

        responses.sort(Comparator
                .comparingInt((TokenRiskResponse response) -> response.getRiskScore())
                .reversed()
                .thenComparing(response -> response.getCost(), Comparator.reverseOrder())
                .thenComparing(response -> response.getCallCount(), Comparator.reverseOrder()));

        List<TokenRiskResponse> limitedResponses = responses.size() > limit
                ? new ArrayList<>(responses.subList(0, limit))
                : responses;

        double ipCoveragePercent = totalLogCount == 0L
                ? 0d
                : usableIpLogCount * 100d / totalLogCount;
        TokenSecuritySummary summary = new TokenSecuritySummary(
                (long) responses.size(),
                riskyTokens,
                highRiskTokens,
                totalCalls,
                (long) allKnownIps.size(),
                totalLogCount,
                emptyIpLogCount,
                ipCoveragePercent,
                usableIpLogCount > 0L);
        return new TokenSecurityResponse(summary, limitedResponses, Instant.now().getEpochSecond());
    }

    static String tokenKey(Long tokenId, Long userId, String username, String tokenName) {
        if (tokenId != null && tokenId > 0) {
            return "id:" + tokenId;
        }
        String safeTokenName = tokenName == null ? "" : tokenName.trim();
        if (userId != null && userId > 0) {
            return "user:" + userId + ":token:" + safeTokenName;
        }
        String safeUsername = username == null ? "" : username.trim();
        return "user:" + safeUsername + ":token:" + safeTokenName;
    }

    private TokenRiskResponse assessToken(TokenAccumulator token,
                                          Map<String, Set<String>> baselineIpsByToken,
                                          Map<String, Long> sharedWindowsByToken,
                                          List<TokenMinuteUsage> minuteUsages) {
        Set<String> baselineIps = baselineIpsByToken.getOrDefault(token.tokenKey, Set.of());
        boolean hasBaseline = !baselineIps.isEmpty();
        long sharedWindows = sharedWindowsByToken.getOrDefault(token.tokenKey, 0L);

        Set<String> knownNetworks = new HashSet<>();
        int publicIpCount = 0;
        int nonPublicIpCount = 0;
        int newIpCount = 0;

        for (IpAccumulator ip : token.ips.values()) {
            if (ip.known) {
                knownNetworks.add(ip.network);
                if ("PUBLIC".equals(ip.ipType)) {
                    publicIpCount++;
                } else {
                    nonPublicIpCount++;
                }
                if (hasBaseline && !baselineIps.contains(ip.ip)) {
                    newIpCount++;
                }
            }
        }

        int distinctIpCount = (int) token.ips.values().stream().filter(ip -> ip.known).count();
        int distinctNetworkCount = knownNetworks.size();

        Map<Long, Long> requestsByMinute = new TreeMap<>();
        for (TokenMinuteUsage minuteUsage : minuteUsages) {
            if (minuteUsage.minuteStart() == null || minuteUsage.minuteStart() < 0) {
                continue;
            }
            long requestCount = minuteUsage.requestCount() == null ? 0L : minuteUsage.requestCount();
            Long existingCount = requestsByMinute.get(minuteUsage.minuteStart());
            requestsByMinute.put(minuteUsage.minuteStart(),
                    existingCount == null ? requestCount : existingCount + requestCount);
        }

        long activeMinuteCount = requestsByMinute.size();
        long minuteCallCount = 0L;
        for (Long count : requestsByMinute.values()) {
            if (count != null) {
                minuteCallCount += count;
            }
        }
        double averageRequestsPerMinute = activeMinuteCount == 0L
                ? 0d
                : minuteCallCount * 1d / activeMinuteCount;
        long peakRequestsPerMinute = 0L;
        Long peakMinute = null;
        for (Map.Entry<Long, Long> entry : requestsByMinute.entrySet()) {
            if (entry.getValue() > peakRequestsPerMinute) {
                peakRequestsPerMinute = entry.getValue();
                peakMinute = entry.getKey();
            }
        }

        List<String> reasons = new ArrayList<>();
        int score = 0;

        if (peakRequestsPerMinute >= HIGH_RPM_THRESHOLD) {
            reasons.add("HIGH_RPM");
            score += HIGH_RPM_SCORE;
        }
        if (sharedWindows > 0) {
            reasons.add("CONCURRENT_MULTI_IP");
            score += sharedWindows >= 5 ? 55 : sharedWindows >= 2 ? 45 : 35;
        }
        if (distinctNetworkCount >= 2) {
            reasons.add("MULTIPLE_NETWORKS");
            score += distinctNetworkCount >= 5 ? 30 : distinctNetworkCount >= 3 ? 20 : 10;
        }
        if (hasBaseline && newIpCount > 0) {
            reasons.add("NEW_IP_ACTIVITY");
            score += Math.min(30, 8 + newIpCount * 4);
        }
        if (distinctIpCount >= 5) {
            reasons.add("MANY_IPS");
            score += distinctIpCount >= 20 ? 25 : distinctIpCount >= 10 ? 15 : 5;
        }
        if (publicIpCount > 0 && nonPublicIpCount > 0) {
            reasons.add("MIXED_NETWORK_SCOPE");
            score += 10;
        }

        score = Math.min(100, score);
        String riskLevel = score >= 50 ? "HIGH" : score >= 25 ? "MEDIUM" : score > 0 ? "LOW" : "NONE";

        List<TokenMinuteResponse> busiestMinutes = requestsByMinute.entrySet().stream()
                .map(entry -> new TokenMinuteResponse(entry.getKey(), entry.getValue()))
                .sorted(Comparator
                        .comparingLong((TokenMinuteResponse minute) -> minute.getRequestCount())
                        .reversed()
                        .thenComparingLong(minute -> minute.getMinuteStart()))
                .limit(MAX_MINUTE_DETAILS)
                .toList();

        List<TokenIpResponse> ipResponses = token.ips.values().stream()
                .filter(ip -> ip.known)
                .sorted(Comparator.comparingLong((IpAccumulator ip) -> ip.callCount).reversed()
                        .thenComparing(ip -> ip.ip))
                .limit(MAX_IP_DETAILS)
                .map(ip -> new TokenIpResponse(
                        ip.ip,
                        ip.ipType,
                        ip.network,
                        ip.callCount,
                        ip.promptTokens + ip.completionTokens,
                        ip.quota / QUOTA_PER_USD,
                        ip.firstSeen,
                        ip.lastSeen,
                        ip.known && hasBaseline && !baselineIps.contains(ip.ip)))
                .toList();

        return new TokenRiskResponse(
                token.tokenId != null && token.tokenId > 0 ? token.tokenId : null,
                token.tokenName,
                token.username,
                token.callCount,
                token.promptTokens,
                token.completionTokens,
                token.promptTokens + token.completionTokens,
                token.quota / QUOTA_PER_USD,
                distinctIpCount,
                distinctNetworkCount,
                sharedWindows,
                newIpCount,
                peakRequestsPerMinute,
                peakMinute,
                activeMinuteCount,
                averageRequestsPerMinute,
                busiestMinutes,
                score,
                riskLevel,
                reasons,
                ipResponses,
                token.ips.size() > MAX_IP_DETAILS);
    }

    private static final class TokenAccumulator {
        private final String tokenKey;
        private final Long tokenId;
        private String tokenName;
        private String username;
        private final Map<String, IpAccumulator> ips = new HashMap<>();
        private long callCount;
        private long promptTokens;
        private long completionTokens;
        private long quota;

        private TokenAccumulator(String tokenKey, Long tokenId, String tokenName, String username) {
            this.tokenKey = tokenKey;
            this.tokenId = tokenId;
            this.tokenName = tokenName;
            this.username = username;
        }

        private void add(TokenIpUsage usage) {
            if ((tokenName == null || tokenName.isBlank()) && usage.tokenName() != null) {
                tokenName = usage.tokenName();
            }
            if ((username == null || username.isBlank()) && usage.username() != null) {
                username = usage.username();
            }
            callCount += usage.callCount();
            promptTokens += usage.promptTokens();
            completionTokens += usage.completionTokens();
            quota += usage.quota();
            if (!"UNKNOWN".equals(usage.ipType())) {
                ips.computeIfAbsent(usage.ip(), ignored -> new IpAccumulator(usage)).add(usage);
            }
        }
    }

    private static final class IpAccumulator {
        private final String ip;
        private final String ipType;
        private final String network;
        private final boolean known;
        private long callCount;
        private long promptTokens;
        private long completionTokens;
        private long quota;
        private long firstSeen;
        private long lastSeen;

        private IpAccumulator(TokenIpUsage usage) {
            this.ip = usage.ip();
            this.ipType = usage.ipType();
            this.network = usage.network();
            this.known = !"UNKNOWN".equals(usage.ipType());
        }

        private void add(TokenIpUsage usage) {
            callCount += usage.callCount();
            promptTokens += usage.promptTokens();
            completionTokens += usage.completionTokens();
            quota += usage.quota();
            firstSeen = firstSeen == 0L ? usage.firstSeen() : Math.min(firstSeen, usage.firstSeen());
            lastSeen = Math.max(lastSeen, usage.lastSeen());
        }
    }
}
