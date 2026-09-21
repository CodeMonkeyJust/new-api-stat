package com.newapi.analyzer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenIpResponse {
    private String ip;
    private String ipType;
    private String network;
    private Long callCount;
    private Long totalTokens;
    private Double cost;
    private Long firstSeen;
    private Long lastSeen;
    private Boolean newIp;
}
