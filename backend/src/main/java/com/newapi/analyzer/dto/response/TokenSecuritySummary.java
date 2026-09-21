package com.newapi.analyzer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenSecuritySummary {
    private Long analyzedTokenCount;
    private Long riskyTokenCount;
    private Long highRiskTokenCount;
    private Long callCount;
    private Long distinctIpCount;
    private Long totalLogCount;
    private Long emptyIpLogCount;
    private Double ipCoveragePercent;
    private Boolean ipDataAvailable;
}
