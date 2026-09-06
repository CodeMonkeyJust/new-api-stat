package com.newapi.analyzer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HourlyResponse {
    private Integer hour;
    private Long quota;
    private Double cost;
    private Long count;
    private Long users;
    private Long promptTokens;
    private Long completionTokens;
}
