package com.newapi.analyzer.service;

import com.newapi.analyzer.dto.response.TokenMinuteResponse;
import com.newapi.analyzer.dto.response.TokenRiskResponse;
import com.newapi.analyzer.dto.response.TokenSecurityResponse;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class TokenRiskAnalyzerTest {

    private final TokenRiskAnalyzer analyzer = new TokenRiskAnalyzer();

    @Test
    void marksConcurrentCallsFromDifferentNetworksAsHighRisk() {
        TokenIpUsage firstIp = usage("1.1.1.1", "PUBLIC", "1.1.1.0/24", 4, 1000L);
        TokenIpUsage secondIp = usage("2.2.2.2", "PUBLIC", "2.2.2.0/24", 3, 800L);

        TokenSecurityResponse response = analyzer.analyze(
                List.of(firstIp, secondIp),
                Map.of(),
                Map.of(firstIp.tokenKey(), 2L),
                100);

        TokenRiskResponse token = response.getTokens().get(0);
        assertThat(token.getRiskLevel()).isEqualTo("HIGH");
        assertThat(token.getRiskReasons()).containsExactly("CONCURRENT_MULTI_IP", "MULTIPLE_NETWORKS");
        assertThat(token.getDistinctIpCount()).isEqualTo(2);
        assertThat(token.getSharedFiveMinuteWindows()).isEqualTo(2L);
        assertThat(response.getSummary().getHighRiskTokenCount()).isEqualTo(1L);
    }

    @Test
    void leavesStableSingleIpTokenUnscored() {
        TokenSecurityResponse response = analyzer.analyze(
                List.of(usage("10.0.0.8", "PRIVATE", "10.0.0.0/24", 20, 5000L)),
                Map.of(),
                Map.of(),
                100);

        TokenRiskResponse token = response.getTokens().get(0);
        assertThat(token.getRiskLevel()).isEqualTo("NONE");
        assertThat(token.getRiskScore()).isZero();
        assertThat(token.getRiskReasons()).isEmpty();
    }

    @Test
    void excludesMissingIpFromCountsAndReportsCoverage() {
        TokenIpUsage knownIp = usage("1.1.1.1", "PUBLIC", "1.1.1.0/24", 7, 900L);
        TokenIpUsage missingIp = usage("unknown", "UNKNOWN", "unknown", 3, 300L);

        TokenSecurityResponse response = analyzer.analyze(
                List.of(knownIp, missingIp),
                Map.of(),
                Map.of(),
                100);

        TokenRiskResponse token = response.getTokens().get(0);
        assertThat(token.getDistinctIpCount()).isEqualTo(1);
        assertThat(token.getIps()).hasSize(1);
        assertThat(token.getIps().get(0).getIp()).isEqualTo("1.1.1.1");
        assertThat(response.getSummary().getTotalLogCount()).isEqualTo(10L);
        assertThat(response.getSummary().getEmptyIpLogCount()).isEqualTo(3L);
        assertThat(response.getSummary().getIpCoveragePercent()).isCloseTo(70d, within(0.01d));
        assertThat(response.getSummary().getIpDataAvailable()).isTrue();
    }

    @Test
    void marksAnalysisUnavailableWhenAllIpDataIsMissing() {
        TokenSecurityResponse response = analyzer.analyze(
                List.of(usage("unknown", "UNKNOWN", "unknown", 5, 500L)),
                Map.of(),
                Map.of(),
                100);

        TokenRiskResponse token = response.getTokens().get(0);
        assertThat(token.getRiskLevel()).isEqualTo("NONE");
        assertThat(token.getDistinctIpCount()).isZero();
        assertThat(token.getIps()).isEmpty();
        assertThat(response.getSummary().getTotalLogCount()).isEqualTo(5L);
        assertThat(response.getSummary().getEmptyIpLogCount()).isEqualTo(5L);
        assertThat(response.getSummary().getIpCoveragePercent()).isZero();
        assertThat(response.getSummary().getIpDataAvailable()).isFalse();
    }

    @Test
    void recordsNewIpOnlyWhenPreviousPeriodHasBaselineForTheToken() {
        TokenIpUsage current = usage("8.8.8.8", "PUBLIC", "8.8.8.0/24", 2, 300L);

        TokenSecurityResponse response = analyzer.analyze(
                List.of(current),
                Map.of(current.tokenKey(), Set.of("1.1.1.1")),
                Map.of(),
                100);

        TokenRiskResponse token = response.getTokens().get(0);
        assertThat(token.getRiskLevel()).isEqualTo("LOW");
        assertThat(token.getNewIpCount()).isEqualTo(1);
        assertThat(token.getRiskReasons()).containsExactly("NEW_IP_ACTIVITY");
        assertThat(token.getIps().get(0).getNewIp()).isTrue();
    }

    @Test
    void summarizesRequestsPerMinute() {
        TokenIpUsage usage = usage("1.1.1.1", "PUBLIC", "1.1.1.0/24", 12, 1200L);
        List<TokenMinuteUsage> minuteUsages = List.of(
                new TokenMinuteUsage(usage.tokenKey(), 60L, 3L),
                new TokenMinuteUsage(usage.tokenKey(), 120L, 7L),
                new TokenMinuteUsage(usage.tokenKey(), 180L, 2L));

        TokenSecurityResponse response = analyzer.analyze(
                List.of(usage),
                minuteUsages,
                Map.of(),
                Map.of(),
                100);

        TokenRiskResponse token = response.getTokens().get(0);
        assertThat(token.getPeakRequestsPerMinute()).isEqualTo(7L);
        assertThat(token.getPeakMinute()).isEqualTo(120L);
        assertThat(token.getActiveMinuteCount()).isEqualTo(3L);
        assertThat(token.getAverageRequestsPerMinute()).isCloseTo(4d, within(0.01d));
        assertThat(token.getBusiestMinutes())
                .extracting((TokenMinuteResponse minute) -> minute.getRequestCount())
                .containsExactly(7L, 3L, 2L);
    }

    @Test
    void flagsPeakRequestRateAtConfiguredThreshold() {
        TokenIpUsage usage = usage("1.1.1.1", "PUBLIC", "1.1.1.0/24", 35, 1200L);

        TokenSecurityResponse response = analyzer.analyze(
                List.of(usage),
                List.of(new TokenMinuteUsage(usage.tokenKey(), 60L, 35L)),
                Map.of(),
                Map.of(),
                100);

        TokenRiskResponse token = response.getTokens().get(0);
        assertThat(token.getPeakRequestsPerMinute()).isEqualTo(35L);
        assertThat(token.getRiskScore()).isEqualTo(30);
        assertThat(token.getRiskLevel()).isEqualTo("MEDIUM");
        assertThat(token.getRiskReasons()).containsExactly("HIGH_RPM");
    }

    @Test
    void doesNotFlagPeakRequestRateBelowConfiguredThreshold() {
        TokenIpUsage usage = usage("1.1.1.1", "PUBLIC", "1.1.1.0/24", 34, 1200L);

        TokenSecurityResponse response = analyzer.analyze(
                List.of(usage),
                List.of(new TokenMinuteUsage(usage.tokenKey(), 60L, 34L)),
                Map.of(),
                Map.of(),
                100);

        TokenRiskResponse token = response.getTokens().get(0);
        assertThat(token.getPeakRequestsPerMinute()).isEqualTo(34L);
        assertThat(token.getRiskScore()).isZero();
        assertThat(token.getRiskLevel()).isEqualTo("NONE");
        assertThat(token.getRiskReasons()).isEmpty();
    }

    private TokenIpUsage usage(String ip, String ipType, String network, long calls, long quota) {
        return new TokenIpUsage(
                TokenRiskAnalyzer.tokenKey(7L, 2L, "alice", "prod-key"),
                7L,
                "prod-key",
                2L,
                "alice",
                ip,
                ipType,
                network,
                calls,
                400L,
                100L,
                quota,
                1_700_000_000L,
                1_700_000_300L);
    }
}
