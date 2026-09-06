package com.newapi.analyzer.repository;

import com.newapi.analyzer.entity.LogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LogRepository extends JpaRepository<LogEntity, Long> {

    @Query("SELECT l FROM LogEntity l WHERE l.type = 2 AND l.createdAt >= :startTime AND l.createdAt < :endTime")
    List<LogEntity> findByTypeAndDateRange(@Param("startTime") Long startTime, @Param("endTime") Long endTime);

    @Query("SELECT l FROM LogEntity l WHERE l.type = 2 AND l.createdAt >= :startTime AND l.createdAt < :endTime ORDER BY l.quota DESC")
    List<LogEntity> findByTypeAndDateRangeOrderByQuotaDesc(@Param("startTime") Long startTime, @Param("endTime") Long endTime);

    @Query(value = "SELECT COUNT(*) FROM logs WHERE type = 2 AND created_at >= :startTime AND created_at < :endTime", nativeQuery = true)
    Long countByTypeAndDateRange(@Param("startTime") Long startTime, @Param("endTime") Long endTime);

    @Query(value = "SELECT SUM(prompt_tokens + completion_tokens) FROM logs WHERE type = 2 AND created_at >= :startTime AND created_at < :endTime", nativeQuery = true)
    Long sumTokensByTypeAndDateRange(@Param("startTime") Long startTime, @Param("endTime") Long endTime);

    @Query(value = "SELECT SUM(prompt_tokens) FROM logs WHERE type = 2 AND created_at >= :startTime AND created_at < :endTime", nativeQuery = true)
    Long sumPromptTokensByTypeAndDateRange(@Param("startTime") Long startTime, @Param("endTime") Long endTime);

    @Query(value = "SELECT SUM(completion_tokens) FROM logs WHERE type = 2 AND created_at >= :startTime AND created_at < :endTime", nativeQuery = true)
    Long sumCompletionTokensByTypeAndDateRange(@Param("startTime") Long startTime, @Param("endTime") Long endTime);

    @Query(value = "SELECT SUM(quota) FROM logs WHERE type = 2 AND created_at >= :startTime AND created_at < :endTime", nativeQuery = true)
    Long sumQuotaByTypeAndDateRange(@Param("startTime") Long startTime, @Param("endTime") Long endTime);

    @Query(value = "SELECT AVG(use_time) FROM logs WHERE type = 2 AND created_at >= :startTime AND created_at < :endTime", nativeQuery = true)
    Double avgUseTimeByTypeAndDateRange(@Param("startTime") Long startTime, @Param("endTime") Long endTime);
}
