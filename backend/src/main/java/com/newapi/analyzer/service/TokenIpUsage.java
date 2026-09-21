package com.newapi.analyzer.service;

/**
 * logs 表按“令牌 + IP”聚合后的只读数据。
 */
record TokenIpUsage(
        String tokenKey,
        Long tokenId,
        String tokenName,
        Long userId,
        String username,
        String ip,
        String ipType,
        String network,
        Long callCount,
        Long promptTokens,
        Long completionTokens,
        Long quota,
        Long firstSeen,
        Long lastSeen
) {
}
