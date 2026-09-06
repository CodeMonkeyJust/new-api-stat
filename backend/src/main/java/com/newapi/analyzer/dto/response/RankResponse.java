package com.newapi.analyzer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RankResponse {
    private String name;
    private Long promptTokens;
    private Long completionTokens;
    private Long value;
    private Double cost;
    private Long count;
}
