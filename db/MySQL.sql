-- ============================================================================
-- new-api 数据库结构参考（仅供阅读说明，请勿执行！）
-- ----------------------------------------------------------------------------
-- 本文件是从目标 new-api MySQL 实例导出的建表语句，仅用于说明 new-api 的
-- 数据库结构；本项目主要读取其中的 logs、users 两张表做只读统计。
-- 它不是迁移脚本，请勿对任何数据库直接执行，也不要用来覆盖已有库表；
-- new-api 的库表结构由 new-api 自身创建和维护。
-- 当前按 new-api v1.0.0-rc.32 的表结构整理，其他版本可能有差异。
-- ============================================================================
-- new_api.abilities definition

CREATE TABLE `abilities` (
  `group` varchar(64) NOT NULL,
  `model` varchar(255) NOT NULL,
  `channel_id` bigint NOT NULL,
  `enabled` tinyint(1) DEFAULT NULL,
  `priority` bigint DEFAULT '0',
  `weight` bigint unsigned DEFAULT '0',
  `tag` varchar(191) DEFAULT NULL,
  PRIMARY KEY (`group`,`model`,`channel_id`),
  KEY `idx_abilities_channel_id` (`channel_id`),
  KEY `idx_abilities_priority` (`priority`),
  KEY `idx_abilities_weight` (`weight`),
  KEY `idx_abilities_tag` (`tag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- new_api.auth_flows definition

CREATE TABLE `auth_flows` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `token_hash` char(64) NOT NULL,
  `purpose` varchar(32) NOT NULL,
  `provider` varchar(64) DEFAULT NULL,
  `intent` varchar(16) DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  `session_id` varchar(64) DEFAULT NULL,
  `payload` text,
  `created_at` datetime(3) DEFAULT NULL,
  `expires_at` datetime(3) NOT NULL,
  `consumed_at` datetime(3) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_auth_flows_token_hash` (`token_hash`),
  KEY `idx_auth_flow_purpose_expiry` (`purpose`,`expires_at`),
  KEY `idx_auth_flows_user_id` (`user_id`),
  KEY `idx_auth_flows_session_id` (`session_id`),
  KEY `idx_auth_flows_consumed_at` (`consumed_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- new_api.authz_roles definition

CREATE TABLE `authz_roles` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `key` varchar(64) NOT NULL,
  `name` varchar(100) NOT NULL,
  `description` text,
  `built_in` tinyint(1) DEFAULT NULL,
  `enabled` tinyint(1) DEFAULT NULL,
  `sort` bigint DEFAULT NULL,
  `created_at` bigint DEFAULT NULL,
  `updated_at` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_authz_roles_key` (`key`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- new_api.casbin_rule definition

CREATE TABLE `casbin_rule` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `ptype` varchar(100) DEFAULT NULL,
  `v0` varchar(100) DEFAULT NULL,
  `v1` varchar(100) DEFAULT NULL,
  `v2` varchar(100) DEFAULT NULL,
  `v3` varchar(100) DEFAULT NULL,
  `v4` varchar(100) DEFAULT NULL,
  `v5` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_casbin_rule_unique` (`ptype`,`v0`,`v1`,`v2`,`v3`,`v4`,`v5`),
  KEY `idx_casbin_rule` (`ptype`,`v0`,`v1`,`v2`,`v3`,`v4`,`v5`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- new_api.channels definition

CREATE TABLE `channels` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `type` bigint DEFAULT '0',
  `key` longtext NOT NULL,
  `open_ai_organization` longtext,
  `test_model` longtext,
  `status` bigint DEFAULT '1',
  `name` varchar(191) DEFAULT NULL,
  `weight` bigint unsigned DEFAULT '0',
  `created_time` bigint DEFAULT NULL,
  `test_time` bigint DEFAULT NULL,
  `response_time` bigint DEFAULT NULL,
  `base_url` varchar(191) DEFAULT '',
  `other` longtext,
  `balance` double DEFAULT NULL,
  `balance_updated_time` bigint DEFAULT NULL,
  `models` longtext,
  `group` varchar(64) DEFAULT 'default',
  `used_quota` bigint DEFAULT '0',
  `model_mapping` text,
  `status_code_mapping` varchar(1024) DEFAULT '',
  `priority` bigint DEFAULT '0',
  `auto_ban` bigint DEFAULT '1',
  `other_info` longtext,
  `tag` varchar(191) DEFAULT NULL,
  `setting` text,
  `param_override` text,
  `header_override` text,
  `remark` varchar(255) DEFAULT NULL,
  `channel_info` json DEFAULT NULL,
  `settings` longtext,
  PRIMARY KEY (`id`),
  KEY `idx_channels_name` (`name`),
  KEY `idx_channels_tag` (`tag`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- new_api.checkins definition

CREATE TABLE `checkins` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `checkin_date` varchar(10) NOT NULL,
  `quota_awarded` bigint NOT NULL,
  `created_at` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_user_checkin_date` (`user_id`,`checkin_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- new_api.custom_oauth_providers definition

CREATE TABLE `custom_oauth_providers` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `slug` varchar(64) NOT NULL,
  `icon` varchar(128) DEFAULT '',
  `enabled` tinyint(1) DEFAULT '0',
  `client_id` varchar(256) DEFAULT NULL,
  `client_secret` varchar(512) DEFAULT NULL,
  `authorization_endpoint` varchar(512) DEFAULT NULL,
  `token_endpoint` varchar(512) DEFAULT NULL,
  `user_info_endpoint` varchar(512) DEFAULT NULL,
  `scopes` varchar(256) DEFAULT 'openid profile email',
  `user_id_field` varchar(128) DEFAULT 'sub',
  `username_field` varchar(128) DEFAULT 'preferred_username',
  `display_name_field` varchar(128) DEFAULT 'name',
  `email_field` varchar(128) DEFAULT 'email',
  `well_known` varchar(512) DEFAULT NULL,
  `auth_style` bigint DEFAULT '0',
  `access_policy` text,
  `access_denied_message` varchar(512) DEFAULT NULL,
  `created_at` datetime(3) DEFAULT NULL,
  `updated_at` datetime(3) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_custom_oauth_providers_slug` (`slug`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- new_api.external_identity_claims definition

CREATE TABLE `external_identity_claims` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `provider` varchar(32) NOT NULL,
  `subject` varchar(128) NOT NULL,
  `user_id` bigint NOT NULL,
  `created_at` datetime(3) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_external_identity_subject` (`provider`,`subject`),
  UNIQUE KEY `idx_external_identity_user` (`provider`,`user_id`),
  KEY `idx_external_identity_claims_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- new_api.login_encryption_keys definition

CREATE TABLE `login_encryption_keys` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `slot` varchar(32) NOT NULL,
  `private_key_pem` text NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_login_encryption_keys_slot` (`slot`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- new_api.logs definition

CREATE TABLE `logs` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL,
  `created_at` bigint DEFAULT NULL,
  `type` bigint DEFAULT NULL,
  `content` longtext,
  `username` varchar(191) DEFAULT '',
  `token_name` varchar(191) DEFAULT '',
  `model_name` varchar(191) DEFAULT '',
  `quota` bigint DEFAULT '0',
  `prompt_tokens` bigint DEFAULT '0',
  `completion_tokens` bigint DEFAULT '0',
  `use_time` bigint DEFAULT '0',
  `is_stream` tinyint(1) DEFAULT NULL,
  `channel_id` bigint DEFAULT NULL,
  `channel_name` longtext,
  `token_id` bigint DEFAULT '0',
  `group` varchar(191) DEFAULT NULL,
  `ip` varchar(191) DEFAULT '',
  `request_id` varchar(64) DEFAULT '',
  `upstream_request_id` varchar(128) DEFAULT '',
  `other` longtext,
  PRIMARY KEY (`id`),
  KEY `idx_logs_channel_id` (`channel_id`),
  KEY `idx_logs_ip` (`ip`),
  KEY `idx_logs_upstream_request_id` (`upstream_request_id`),
  KEY `idx_user_id_id` (`user_id`,`id`),
  KEY `idx_logs_user_id` (`user_id`),
  KEY `idx_created_at_type` (`created_at`,`type`),
  KEY `idx_logs_username` (`username`),
  KEY `index_username_model_name` (`model_name`,`username`),
  KEY `idx_logs_token_id` (`token_id`),
  KEY `idx_logs_group` (`group`),
  KEY `idx_logs_request_id` (`request_id`),
  KEY `idx_created_at_id` (`created_at`,`id`),
  KEY `idx_logs_token_name` (`token_name`),
  KEY `idx_logs_model_name` (`model_name`)
) ENGINE=InnoDB AUTO_INCREMENT=1359 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- new_api.midjourneys definition

CREATE TABLE `midjourneys` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` bigint DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  `action` varchar(40) DEFAULT NULL,
  `mj_id` varchar(191) DEFAULT NULL,
  `prompt` longtext,
  `prompt_en` longtext,
  `description` longtext,
  `state` longtext,
  `submit_time` bigint DEFAULT NULL,
  `start_time` bigint DEFAULT NULL,
  `finish_time` bigint DEFAULT NULL,
  `image_url` longtext,
  `video_url` longtext,
  `video_urls` longtext,
  `status` varchar(20) DEFAULT NULL,
  `progress` varchar(30) DEFAULT NULL,
  `fail_reason` longtext,
  `channel_id` bigint DEFAULT NULL,
  `quota` bigint DEFAULT NULL,
  `buttons` longtext,
  `properties` longtext,
  `token_id` bigint DEFAULT '0',
  `billing_channel_id` bigint DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_midjourneys_finish_time` (`finish_time`),
  KEY `idx_midjourneys_status` (`status`),
  KEY `idx_midjourneys_progress` (`progress`),
  KEY `idx_midjourneys_user_id` (`user_id`),
  KEY `idx_midjourneys_action` (`action`),
  KEY `idx_midjourneys_mj_id` (`mj_id`),
  KEY `idx_midjourneys_submit_time` (`submit_time`),
  KEY `idx_midjourneys_start_time` (`start_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- new_api.models definition

CREATE TABLE `models` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `model_name` varchar(128) NOT NULL,
  `description` text,
  `icon` varchar(128) DEFAULT NULL,
  `tags` varchar(255) DEFAULT NULL,
  `vendor_id` bigint DEFAULT NULL,
  `endpoints` text,
  `status` bigint DEFAULT '1',
  `sync_official` bigint DEFAULT '1',
  `created_time` bigint DEFAULT NULL,
  `updated_time` bigint DEFAULT NULL,
  `deleted_at` datetime(3) DEFAULT NULL,
  `name_rule` bigint DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_model_name_delete_at` (`model_name`,`deleted_at`),
  KEY `idx_models_vendor_id` (`vendor_id`),
  KEY `idx_models_deleted_at` (`deleted_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- new_api.`options` definition

CREATE TABLE `options` (
  `key` varchar(191) NOT NULL,
  `value` longtext,
  PRIMARY KEY (`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- new_api.passkey_credentials definition

CREATE TABLE `passkey_credentials` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `credential_id` varchar(512) NOT NULL,
  `public_key` text NOT NULL,
  `attestation_type` varchar(255) DEFAULT NULL,
  `aa_guid` varchar(512) DEFAULT NULL,
  `sign_count` int unsigned DEFAULT '0',
  `clone_warning` tinyint(1) DEFAULT NULL,
  `user_present` tinyint(1) DEFAULT NULL,
  `user_verified` tinyint(1) DEFAULT NULL,
  `backup_eligible` tinyint(1) DEFAULT NULL,
  `backup_state` tinyint(1) DEFAULT NULL,
  `transports` text,
  `attachment` varchar(32) DEFAULT NULL,
  `last_used_at` datetime(3) DEFAULT NULL,
  `created_at` datetime(3) DEFAULT NULL,
  `updated_at` datetime(3) DEFAULT NULL,
  `deleted_at` datetime(3) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_passkey_credentials_user_id` (`user_id`),
  UNIQUE KEY `idx_passkey_credentials_credential_id` (`credential_id`),
  KEY `idx_passkey_credentials_deleted_at` (`deleted_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- new_api.perf_metrics definition

CREATE TABLE `perf_metrics` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `model_name` varchar(128) DEFAULT NULL,
  `group` varchar(64) DEFAULT NULL,
  `bucket_ts` bigint DEFAULT NULL,
  `request_count` bigint DEFAULT '0',
  `success_count` bigint DEFAULT '0',
  `total_latency_ms` bigint DEFAULT '0',
  `ttft_sum_ms` bigint DEFAULT '0',
  `ttft_count` bigint DEFAULT '0',
  `output_tokens` bigint DEFAULT '0',
  `generation_ms` bigint DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_perf_model_group_bucket` (`model_name`,`group`,`bucket_ts`),
  KEY `idx_perf_bucket_ts` (`bucket_ts`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- new_api.prefill_groups definition

CREATE TABLE `prefill_groups` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `type` varchar(32) NOT NULL,
  `items` json DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `created_time` bigint DEFAULT NULL,
  `updated_time` bigint DEFAULT NULL,
  `deleted_at` datetime(3) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_prefill_name` (`name`),
  KEY `idx_prefill_groups_type` (`type`),
  KEY `idx_prefill_groups_deleted_at` (`deleted_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- new_api.quota_data definition

CREATE TABLE `quota_data` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL,
  `username` varchar(64) DEFAULT '',
  `model_name` varchar(64) DEFAULT '',
  `created_at` bigint DEFAULT NULL,
  `use_group` varchar(64) DEFAULT '',
  `token_id` bigint DEFAULT '0',
  `channel_id` bigint DEFAULT '0',
  `node_name` varchar(64) DEFAULT '',
  `token_used` bigint DEFAULT '0',
  `count` bigint DEFAULT '0',
  `quota` bigint DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_quota_data_use_group` (`use_group`),
  KEY `idx_quota_data_token_id` (`token_id`),
  KEY `idx_quota_data_channel_id` (`channel_id`),
  KEY `idx_quota_data_node_name` (`node_name`),
  KEY `idx_quota_data_user_id` (`user_id`),
  KEY `idx_qdt_model_user_name` (`model_name`,`username`),
  KEY `idx_qdt_created_at` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- new_api.redemptions definition

CREATE TABLE `redemptions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL,
  `key` char(32) DEFAULT NULL,
  `status` bigint DEFAULT '1',
  `name` varchar(191) DEFAULT NULL,
  `quota` bigint DEFAULT '100',
  `created_time` bigint DEFAULT NULL,
  `redeemed_time` bigint DEFAULT NULL,
  `used_user_id` bigint DEFAULT NULL,
  `deleted_at` datetime(3) DEFAULT NULL,
  `expired_time` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_redemptions_key` (`key`),
  KEY `idx_redemptions_name` (`name`),
  KEY `idx_redemptions_deleted_at` (`deleted_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- new_api.setups definition

CREATE TABLE `setups` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `version` varchar(50) NOT NULL,
  `initialized_at` bigint NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- new_api.subscription_orders definition

CREATE TABLE `subscription_orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL,
  `plan_id` bigint DEFAULT NULL,
  `money` double DEFAULT NULL,
  `trade_no` varchar(255) DEFAULT NULL,
  `payment_method` varchar(50) DEFAULT NULL,
  `payment_provider` varchar(50) DEFAULT '',
  `status` longtext,
  `create_time` bigint DEFAULT NULL,
  `complete_time` bigint DEFAULT NULL,
  `provider_payload` text,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uni_subscription_orders_trade_no` (`trade_no`),
  KEY `idx_subscription_orders_user_id` (`user_id`),
  KEY `idx_subscription_orders_plan_id` (`plan_id`),
  KEY `idx_subscription_orders_trade_no` (`trade_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- new_api.subscription_plans definition

CREATE TABLE `subscription_plans` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(128) NOT NULL,
  `subtitle` varchar(255) DEFAULT '',
  `price_amount` decimal(10,6) NOT NULL DEFAULT '0.000000',
  `currency` varchar(8) NOT NULL DEFAULT 'USD',
  `duration_unit` varchar(16) NOT NULL DEFAULT 'month',
  `duration_value` bigint NOT NULL DEFAULT '1',
  `custom_seconds` bigint NOT NULL DEFAULT '0',
  `enabled` tinyint(1) DEFAULT '1',
  `sort_order` bigint DEFAULT '0',
  `allow_balance_pay` tinyint(1) DEFAULT NULL,
  `allow_wallet_overflow` tinyint(1) DEFAULT NULL,
  `stripe_price_id` varchar(128) DEFAULT '',
  `creem_product_id` varchar(128) DEFAULT '',
  `waffo_pancake_product_id` varchar(128) DEFAULT '',
  `max_purchase_per_user` bigint DEFAULT '0',
  `upgrade_group` varchar(64) DEFAULT '',
  `downgrade_group` varchar(64) DEFAULT '',
  `total_amount` bigint NOT NULL DEFAULT '0',
  `quota_reset_period` varchar(16) DEFAULT 'never',
  `quota_reset_custom_seconds` bigint DEFAULT '0',
  `created_at` bigint DEFAULT NULL,
  `updated_at` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- new_api.subscription_pre_consume_records definition

CREATE TABLE `subscription_pre_consume_records` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `request_id` varchar(64) DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  `user_subscription_id` bigint DEFAULT NULL,
  `pre_consumed` bigint NOT NULL DEFAULT '0',
  `status` varchar(32) DEFAULT NULL,
  `created_at` bigint DEFAULT NULL,
  `updated_at` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_subscription_pre_consume_records_request_id` (`request_id`),
  KEY `idx_subscription_pre_consume_records_updated_at` (`updated_at`),
  KEY `idx_subscription_pre_consume_records_user_id` (`user_id`),
  KEY `idx_subscription_pre_consume_records_user_subscription_id` (`user_subscription_id`),
  KEY `idx_subscription_pre_consume_records_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- new_api.system_instances definition

CREATE TABLE `system_instances` (
  `node_name` varchar(128) NOT NULL,
  `info` text,
  `started_at` bigint DEFAULT NULL,
  `last_seen_at` bigint DEFAULT NULL,
  `created_at` bigint DEFAULT NULL,
  `updated_at` bigint DEFAULT NULL,
  PRIMARY KEY (`node_name`),
  KEY `idx_system_instances_updated_at` (`updated_at`),
  KEY `idx_system_instances_started_at` (`started_at`),
  KEY `idx_system_instances_last_seen_at` (`last_seen_at`),
  KEY `idx_system_instances_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- new_api.system_task_locks definition

CREATE TABLE `system_task_locks` (
  `type` varchar(64) NOT NULL,
  `task_id` varchar(64) DEFAULT NULL,
  `locked_by` varchar(128) DEFAULT NULL,
  `locked_until` bigint DEFAULT NULL,
  `updated_at` bigint DEFAULT NULL,
  PRIMARY KEY (`type`),
  KEY `idx_system_task_locks_task_id` (`task_id`),
  KEY `idx_system_task_locks_locked_by` (`locked_by`),
  KEY `idx_system_task_locks_locked_until` (`locked_until`),
  KEY `idx_system_task_locks_updated_at` (`updated_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- new_api.system_tasks definition

CREATE TABLE `system_tasks` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `task_id` varchar(64) DEFAULT NULL,
  `type` varchar(64) DEFAULT NULL,
  `status` varchar(32) DEFAULT NULL,
  `active_key` varchar(64) DEFAULT NULL,
  `payload` text,
  `state` text,
  `result` text,
  `error` text,
  `locked_by` varchar(128) DEFAULT NULL,
  `created_at` bigint DEFAULT NULL,
  `updated_at` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_system_tasks_task_id` (`task_id`),
  UNIQUE KEY `idx_system_tasks_active_key` (`active_key`),
  KEY `idx_system_tasks_created_at` (`created_at`),
  KEY `idx_system_tasks_updated_at` (`updated_at`),
  KEY `idx_system_tasks_type` (`type`),
  KEY `idx_system_tasks_status` (`status`),
  KEY `idx_system_tasks_locked_by` (`locked_by`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- new_api.task_plugins definition

CREATE TABLE `task_plugins` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `key` varchar(128) NOT NULL,
  `api_version` bigint NOT NULL,
  `version` varchar(64) NOT NULL,
  `source` text NOT NULL,
  `source_hash` varchar(64) NOT NULL,
  `enabled` tinyint(1) NOT NULL,
  `active` tinyint(1) NOT NULL,
  `created_at` bigint NOT NULL,
  `remark` text,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_task_plugin_key_version` (`key`,`version`),
  KEY `idx_task_plugins_active` (`active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- new_api.tasks definition

CREATE TABLE `tasks` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` bigint DEFAULT NULL,
  `updated_at` bigint DEFAULT NULL,
  `task_id` varchar(191) DEFAULT NULL,
  `platform` varchar(30) DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  `group` varchar(50) DEFAULT NULL,
  `channel_id` bigint DEFAULT NULL,
  `quota` bigint DEFAULT NULL,
  `action` varchar(40) DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `fail_reason` longtext,
  `submit_time` bigint DEFAULT NULL,
  `start_time` bigint DEFAULT NULL,
  `finish_time` bigint DEFAULT NULL,
  `progress` varchar(20) DEFAULT NULL,
  `properties` json DEFAULT NULL,
  `private_data` json DEFAULT NULL,
  `data` json DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_tasks_created_at` (`created_at`),
  KEY `idx_tasks_task_id` (`task_id`),
  KEY `idx_tasks_action` (`action`),
  KEY `idx_tasks_start_time` (`start_time`),
  KEY `idx_tasks_finish_time` (`finish_time`),
  KEY `idx_tasks_platform` (`platform`),
  KEY `idx_tasks_user_id` (`user_id`),
  KEY `idx_tasks_channel_id` (`channel_id`),
  KEY `idx_tasks_status` (`status`),
  KEY `idx_tasks_submit_time` (`submit_time`),
  KEY `idx_tasks_progress` (`progress`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- new_api.tokens definition

CREATE TABLE `tokens` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL,
  `key` varchar(128) DEFAULT NULL,
  `status` bigint DEFAULT '1',
  `name` varchar(191) DEFAULT NULL,
  `created_time` bigint DEFAULT NULL,
  `accessed_time` bigint DEFAULT NULL,
  `expired_time` bigint DEFAULT '-1',
  `remain_quota` bigint DEFAULT '0',
  `unlimited_quota` tinyint(1) DEFAULT NULL,
  `model_limits_enabled` tinyint(1) DEFAULT NULL,
  `model_limits` text,
  `allow_ips` varchar(191) DEFAULT '',
  `used_quota` bigint DEFAULT '0',
  `group` varchar(191) DEFAULT '',
  `cross_group_retry` tinyint(1) DEFAULT NULL,
  `auto_groups` text,
  `deleted_at` datetime(3) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_tokens_key` (`key`),
  KEY `idx_tokens_name` (`name`),
  KEY `idx_tokens_deleted_at` (`deleted_at`),
  KEY `idx_tokens_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- new_api.top_ups definition

CREATE TABLE `top_ups` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL,
  `amount` bigint DEFAULT NULL,
  `money` double DEFAULT NULL,
  `trade_no` varchar(255) DEFAULT NULL,
  `payment_method` varchar(50) DEFAULT NULL,
  `payment_provider` varchar(50) DEFAULT '',
  `create_time` bigint DEFAULT NULL,
  `complete_time` bigint DEFAULT NULL,
  `status` longtext,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uni_top_ups_trade_no` (`trade_no`),
  KEY `idx_top_ups_user_id` (`user_id`),
  KEY `idx_top_ups_trade_no` (`trade_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- new_api.two_fa_backup_codes definition

CREATE TABLE `two_fa_backup_codes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `code_hash` varchar(255) NOT NULL,
  `is_used` tinyint(1) DEFAULT NULL,
  `used_at` datetime(3) DEFAULT NULL,
  `created_at` datetime(3) DEFAULT NULL,
  `deleted_at` datetime(3) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_two_fa_backup_codes_user_id` (`user_id`),
  KEY `idx_two_fa_backup_codes_deleted_at` (`deleted_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- new_api.two_fas definition

CREATE TABLE `two_fas` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `secret` varchar(255) NOT NULL,
  `is_enabled` tinyint(1) DEFAULT NULL,
  `failed_attempts` bigint DEFAULT '0',
  `locked_until` datetime(3) DEFAULT NULL,
  `last_used_at` datetime(3) DEFAULT NULL,
  `created_at` datetime(3) DEFAULT NULL,
  `updated_at` datetime(3) DEFAULT NULL,
  `deleted_at` datetime(3) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uni_two_fas_user_id` (`user_id`),
  KEY `idx_two_fas_user_id` (`user_id`),
  KEY `idx_two_fas_deleted_at` (`deleted_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- new_api.user_oauth_bindings definition

CREATE TABLE `user_oauth_bindings` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `provider_id` bigint NOT NULL,
  `provider_user_id` varchar(256) NOT NULL,
  `created_at` datetime(3) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ux_user_provider` (`user_id`,`provider_id`),
  UNIQUE KEY `ux_provider_userid` (`provider_id`,`provider_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- new_api.user_sessions definition

CREATE TABLE `user_sessions` (
  `sid` varchar(64) NOT NULL,
  `user_id` bigint NOT NULL,
  `version` bigint NOT NULL DEFAULT '1',
  `user_auth_version` bigint NOT NULL,
  `status` varchar(16) NOT NULL,
  `refresh_hash` char(64) NOT NULL,
  `previous_refresh_hash` varchar(64) DEFAULT NULL,
  `previous_valid_until` bigint NOT NULL DEFAULT '0',
  `login_method` varchar(32) NOT NULL,
  `ip` varchar(64) DEFAULT NULL,
  `user_agent` text,
  `created_at` bigint DEFAULT NULL,
  `last_active_at` bigint NOT NULL,
  `expires_at` bigint NOT NULL,
  `revoked_at` bigint NOT NULL DEFAULT '0',
  `revoked_reason` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`sid`),
  KEY `idx_user_sessions_user_status_expiry` (`user_id`,`status`,`expires_at`),
  KEY `idx_user_sessions_user_created` (`user_id`,`created_at`),
  KEY `idx_user_sessions_status_revoked` (`status`,`revoked_at`),
  KEY `idx_user_sessions_expires_at` (`expires_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- new_api.user_subscriptions definition

CREATE TABLE `user_subscriptions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL,
  `plan_id` bigint DEFAULT NULL,
  `amount_total` bigint NOT NULL DEFAULT '0',
  `amount_used` bigint NOT NULL DEFAULT '0',
  `start_time` bigint DEFAULT NULL,
  `end_time` bigint DEFAULT NULL,
  `status` varchar(32) DEFAULT NULL,
  `source` varchar(32) DEFAULT 'order',
  `last_reset_time` bigint DEFAULT '0',
  `next_reset_time` bigint DEFAULT '0',
  `upgrade_group` varchar(64) DEFAULT '',
  `prev_user_group` varchar(64) DEFAULT '',
  `downgrade_group` varchar(64) DEFAULT '',
  `allow_wallet_overflow` tinyint(1) DEFAULT NULL,
  `created_at` bigint DEFAULT NULL,
  `updated_at` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_user_subscriptions_end_time` (`end_time`),
  KEY `idx_user_subscriptions_status` (`status`),
  KEY `idx_user_subscriptions_next_reset_time` (`next_reset_time`),
  KEY `idx_user_subscriptions_user_id` (`user_id`),
  KEY `idx_user_sub_active` (`user_id`,`status`,`end_time`),
  KEY `idx_user_subscriptions_plan_id` (`plan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- new_api.users definition

CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(191) DEFAULT NULL,
  `password` longtext NOT NULL,
  `display_name` varchar(191) DEFAULT NULL,
  `role` bigint DEFAULT '1',
  `status` bigint DEFAULT '1',
  `email` varchar(191) DEFAULT NULL,
  `github_id` varchar(191) DEFAULT NULL,
  `discord_id` varchar(191) DEFAULT NULL,
  `oidc_id` varchar(191) DEFAULT NULL,
  `wechat_id` varchar(191) DEFAULT NULL,
  `telegram_id` varchar(191) DEFAULT NULL,
  `access_token` char(32) DEFAULT NULL,
  `quota` bigint DEFAULT '0',
  `used_quota` bigint DEFAULT '0',
  `request_count` bigint DEFAULT '0',
  `group` varchar(64) DEFAULT 'default',
  `aff_code` varchar(32) DEFAULT NULL,
  `aff_count` bigint DEFAULT '0',
  `aff_quota` bigint DEFAULT '0',
  `aff_history` bigint DEFAULT '0',
  `inviter_id` bigint DEFAULT NULL,
  `deleted_at` datetime(3) DEFAULT NULL,
  `linux_do_id` varchar(191) DEFAULT NULL,
  `setting` text,
  `remark` varchar(255) DEFAULT NULL,
  `stripe_customer` varchar(64) DEFAULT NULL,
  `created_at` bigint DEFAULT NULL,
  `last_login_at` bigint DEFAULT '0',
  `auth_version` bigint NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_users_aff_code` (`aff_code`),
  UNIQUE KEY `idx_users_access_token` (`access_token`),
  UNIQUE KEY `uni_users_username` (`username`),
  KEY `idx_users_email` (`email`),
  KEY `idx_users_git_hub_id` (`github_id`),
  KEY `idx_users_oidc_id` (`oidc_id`),
  KEY `idx_users_we_chat_id` (`wechat_id`),
  KEY `idx_users_telegram_id` (`telegram_id`),
  KEY `idx_users_inviter_id` (`inviter_id`),
  KEY `idx_users_stripe_customer` (`stripe_customer`),
  KEY `idx_users_username` (`username`),
  KEY `idx_users_display_name` (`display_name`),
  KEY `idx_users_discord_id` (`discord_id`),
  KEY `idx_users_deleted_at` (`deleted_at`),
  KEY `idx_users_linux_do_id` (`linux_do_id`)
) ENGINE=InnoDB AUTO_INCREMENT=129 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- new_api.vendors definition

CREATE TABLE `vendors` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL,
  `description` text,
  `icon` varchar(128) DEFAULT NULL,
  `status` bigint DEFAULT '1',
  `created_time` bigint DEFAULT NULL,
  `updated_time` bigint DEFAULT NULL,
  `deleted_at` datetime(3) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_vendor_name_delete_at` (`name`,`deleted_at`),
  KEY `idx_vendors_deleted_at` (`deleted_at`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;