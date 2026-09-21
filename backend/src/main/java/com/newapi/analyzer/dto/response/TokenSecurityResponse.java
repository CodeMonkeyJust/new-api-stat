package com.newapi.analyzer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenSecurityResponse {
    private TokenSecuritySummary summary;
    private List<TokenRiskResponse> tokens;
    private Long generatedAt;
}
