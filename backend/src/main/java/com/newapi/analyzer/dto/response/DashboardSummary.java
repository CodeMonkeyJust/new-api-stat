package com.newapi.analyzer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummary {
    private Long totalCount;
    private Long totalTokens;
    private Double totalCost;
    private Double avgTime;
    private Long promptTokens;
    private Long completionTokens;
}
