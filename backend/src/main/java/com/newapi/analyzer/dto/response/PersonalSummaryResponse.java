package com.newapi.analyzer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonalSummaryResponse {
    private Long totalCount;
    private Long promptTokens;
    private Long completionTokens;
    private Long totalTokens;
    private Double totalCost;
}
