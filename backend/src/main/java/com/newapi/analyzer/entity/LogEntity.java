package com.newapi.analyzer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * new-api 的 logs 表实体（只读映射）。
 *
 * <p>字段与 db/MySQL.sql 中 new-api v1.0.0-rc.32 的
 * logs 表结构保持一致；数据库表结构由 new-api 自身维护，本应用不做任何写入/DDL。
 */
@Data
@Entity
@Table(name = "logs")
public class LogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    /** 调用发生时间，Unix 秒（epoch seconds），与统计 SQL 的时区换算口径一致。 */
    @Column(name = "created_at")
    private Long createdAt;

    /** 日志类型，2 表示一次实际调用（消费日志）。 */
    @Column(name = "type")
    private Long type;

    @Column(name = "content")
    private String content;

    @Column(name = "username")
    private String username;

    @Column(name = "token_name")
    private String tokenName;

    @Column(name = "model_name")
    private String modelName;

    @Column(name = "quota")
    private Long quota;

    @Column(name = "prompt_tokens")
    private Long promptTokens;

    @Column(name = "completion_tokens")
    private Long completionTokens;

    @Column(name = "use_time")
    private Long useTime;

    @Column(name = "is_stream")
    private Boolean isStream;

    @Column(name = "channel_id")
    private Long channelId;

    @Column(name = "channel_name")
    private String channelName;

    @Column(name = "token_id")
    private Long tokenId;

    @Column(name = "group")
    private String group;

    @Column(name = "ip")
    private String ip;

    @Column(name = "request_id", length = 64)
    private String requestId;

    @Column(name = "upstream_request_id", length = 128)
    private String upstreamRequestId;

    @Column(name = "other")
    private String other;
}