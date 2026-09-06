package com.newapi.analyzer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBalanceResponse {
    private Long id;
    private String username;
    private String displayName;
    private String email;
    private Long quota;
    private Long usedQuota;
    private Double remainingBalance;
    private Double spentBalance;
    private Long requestCount;
    private Integer status;
    private String group;
}
