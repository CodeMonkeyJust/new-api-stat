package com.newapi.analyzer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

/**
 * new-api 的 users 表实体（只读映射）。
 *
 * <p>字段与 db/MySQL.sql（以及同步的 db/PostgreSQL.sql）中 new-api v1.0.0-rc.32 的
 * users 表结构保持一致；数据库表结构由 new-api 自身维护，本应用不做任何写入/DDL，
 * 因此实体不再重复声明索引（索引以 db/MySQL.sql、db/PostgreSQL.sql 为准）。
 */
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    private String username;

    /** 登录密码（BCrypt 哈希），仅用于登录校验，永不返回给前端。 */
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "display_name")
    private String displayName;

    /** new-api 角色：1=普通用户，10=管理员，100=根用户。 */
    @Column(name = "role")
    private Long role = 1L;

    @Column(name = "status")
    private Long status = 1L;

    @Column(name = "email")
    private String email;

    @Column(name = "github_id")
    private String githubId;

    @Column(name = "discord_id")
    private String discordId;

    @Column(name = "oidc_id")
    private String oidcId;

    @Column(name = "wechat_id")
    private String wechatId;

    @Column(name = "telegram_id")
    private String telegramId;

    @Column(name = "access_token", length = 32)
    private String accessToken;

    /** 剩余额度（quota 与 used_quota 单位为 new-api 的 1/500000 美元）。 */
    @Column(name = "quota")
    private Long quota = 0L;

    @Column(name = "used_quota")
    private Long usedQuota = 0L;

    @Column(name = "request_count")
    private Long requestCount = 0L;

    @Column(name = "group", length = 64)
    private String group = "default";

    @Column(name = "aff_code", length = 32)
    private String affCode;

    @Column(name = "aff_count")
    private Long affCount = 0L;

    @Column(name = "aff_quota")
    private Long affQuota = 0L;

    @Column(name = "aff_history")
    private Long affHistory = 0L;

    @Column(name = "inviter_id")
    private Long inviterId;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    @Column(name = "linux_do_id")
    private String linuxDoId;

    @Column(name = "setting")
    private String setting;

    @Column(name = "remark", length = 255)
    private String remark;

    @Column(name = "stripe_customer", length = 64)
    private String stripeCustomer;

    /** 注册时间，Unix 秒（epoch seconds）。 */
    @Column(name = "created_at")
    private Long createdAt;

    @Column(name = "last_login_at")
    private Long lastLoginAt = 0L;

    @Column(name = "auth_version")
    private Long authVersion = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Long getRole() {
        return role;
    }

    public void setRole(Long role) {
        this.role = role;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGithubId() {
        return githubId;
    }

    public void setGithubId(String githubId) {
        this.githubId = githubId;
    }

    public String getDiscordId() {
        return discordId;
    }

    public void setDiscordId(String discordId) {
        this.discordId = discordId;
    }

    public String getOidcId() {
        return oidcId;
    }

    public void setOidcId(String oidcId) {
        this.oidcId = oidcId;
    }

    public String getWechatId() {
        return wechatId;
    }

    public void setWechatId(String wechatId) {
        this.wechatId = wechatId;
    }

    public String getTelegramId() {
        return telegramId;
    }

    public void setTelegramId(String telegramId) {
        this.telegramId = telegramId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Long getQuota() {
        return quota;
    }

    public void setQuota(Long quota) {
        this.quota = quota;
    }

    public Long getUsedQuota() {
        return usedQuota;
    }

    public void setUsedQuota(Long usedQuota) {
        this.usedQuota = usedQuota;
    }

    public Long getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(Long requestCount) {
        this.requestCount = requestCount;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getAffCode() {
        return affCode;
    }

    public void setAffCode(String affCode) {
        this.affCode = affCode;
    }

    public Long getAffCount() {
        return affCount;
    }

    public void setAffCount(Long affCount) {
        this.affCount = affCount;
    }

    public Long getAffQuota() {
        return affQuota;
    }

    public void setAffQuota(Long affQuota) {
        this.affQuota = affQuota;
    }

    public Long getAffHistory() {
        return affHistory;
    }

    public void setAffHistory(Long affHistory) {
        this.affHistory = affHistory;
    }

    public Long getInviterId() {
        return inviterId;
    }

    public void setInviterId(Long inviterId) {
        this.inviterId = inviterId;
    }

    public OffsetDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(OffsetDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getLinuxDoId() {
        return linuxDoId;
    }

    public void setLinuxDoId(String linuxDoId) {
        this.linuxDoId = linuxDoId;
    }

    public String getSetting() {
        return setting;
    }

    public void setSetting(String setting) {
        this.setting = setting;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getStripeCustomer() {
        return stripeCustomer;
    }

    public void setStripeCustomer(String stripeCustomer) {
        this.stripeCustomer = stripeCustomer;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(Long lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public Long getAuthVersion() {
        return authVersion;
    }

    public void setAuthVersion(Long authVersion) {
        this.authVersion = authVersion;
    }
}