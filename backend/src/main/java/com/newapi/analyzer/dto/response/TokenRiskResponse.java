package com.newapi.analyzer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenRiskResponse {
    private Long tokenId;
    private String tokenName;
    private String username;
    private Long callCount;
    private Long promptTokens;
    private Long completionTokens;
    private Long totalTokens;
    private Double cost;
    private Integer distinctIpCount;
    private Integer distinctNetworkCount;
    private Long sharedFiveMinuteWindows;
    private Integer newIpCount;
    private Long peakRequestsPerMinute;
    private Long peakMinute;
    private Long activeMinuteCount;
    private Double averageRequestsPerMinute;
    private List<TokenMinuteResponse> busiestMinutes;
    private Integer riskScore;
    private String riskLevel;
    private List<String> riskReasons;
    private List<TokenIpResponse> ips;
    private Boolean ipDetailsTruncated;
}
