package com.newapi.analyzer.service;

/**
 * logs 表按“令牌 + 分钟”聚合后的只读数据。
 */
record TokenMinuteUsage(
        String tokenKey,
        Long minuteStart,
        Long requestCount
) {
}
