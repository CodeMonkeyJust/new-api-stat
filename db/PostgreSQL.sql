-- PostgreSQL reference schema
--
-- This file documents the tables/columns read by new-api-stat. It is not a
-- migration script and should not be applied blindly to an existing database.
-- Mirrors the current new-api layout (kept in sync with db/MySQL.sql, which is
-- generated from a target new-api MySQL 8 instance).
-- Documented against new-api v1.0.0-rc.32; the layout may change in other versions.
-- public.logs definition

-- Drop table

-- DROP TABLE public.logs;

CREATE TABLE public.logs (
	id bigserial NOT NULL,
	user_id int8 NULL,
	created_at int8 NULL,
	"type" int8 NULL,
	"content" text NULL,
	username text DEFAULT ''::text NULL,
	token_name text DEFAULT ''::text NULL,
	model_name text DEFAULT ''::text NULL,
	quota int8 DEFAULT 0 NULL,
	prompt_tokens int8 DEFAULT 0 NULL,
	completion_tokens int8 DEFAULT 0 NULL,
	use_time int8 DEFAULT 0 NULL,
	is_stream bool NULL,
	channel_id int8 NULL,
	channel_name text NULL,
	token_id int8 DEFAULT 0 NULL,
	"group" text NULL,
	ip text DEFAULT ''::text NULL,
	request_id varchar(64) DEFAULT ''::character varying NULL,
	upstream_request_id varchar(128) DEFAULT ''::character varying NULL,
	other text NULL,
	CONSTRAINT logs_pkey PRIMARY KEY (id)
);
CREATE INDEX idx_created_at_id ON public.logs USING btree (created_at, id);
CREATE INDEX idx_created_at_type ON public.logs USING btree (created_at, type);
CREATE INDEX idx_logs_channel_id ON public.logs USING btree (channel_id);
CREATE INDEX idx_logs_group ON public.logs USING btree ("group");
CREATE INDEX idx_logs_ip ON public.logs USING btree (ip);
CREATE INDEX idx_logs_model_name ON public.logs USING btree (model_name);
CREATE INDEX idx_logs_request_id ON public.logs USING btree (request_id);
CREATE INDEX idx_logs_token_id ON public.logs USING btree (token_id);
CREATE INDEX idx_logs_token_name ON public.logs USING btree (token_name);
CREATE INDEX idx_logs_upstream_request_id ON public.logs USING btree (upstream_request_id);
CREATE INDEX idx_logs_user_id ON public.logs USING btree (user_id);
CREATE INDEX idx_user_id_id ON public.logs USING btree (user_id, id);
CREATE INDEX idx_logs_username ON public.logs USING btree (username);
CREATE INDEX index_username_model_name ON public.logs USING btree (model_name, username);

-- public.users definition

-- Drop table

-- DROP TABLE public.users;

CREATE TABLE public.users (
	id bigserial NOT NULL,
	username text NULL,
	"password" text NOT NULL,
	display_name text NULL,
	"role" int8 DEFAULT 1 NULL,
	status int8 DEFAULT 1 NULL,
	email text NULL,
	github_id text NULL,
	discord_id text NULL,
	oidc_id text NULL,
	wechat_id text NULL,
	telegram_id text NULL,
	access_token bpchar(32) NULL,
	quota int8 DEFAULT 0 NULL,
	used_quota int8 DEFAULT 0 NULL,
	request_count int8 DEFAULT 0 NULL,
	"group" varchar(64) DEFAULT 'default'::character varying NULL,
	aff_code varchar(32) NULL,
	aff_count int8 DEFAULT 0 NULL,
	aff_quota int8 DEFAULT 0 NULL,
	aff_history int8 DEFAULT 0 NULL,
	inviter_id int8 NULL,
	deleted_at timestamptz NULL,
	linux_do_id text NULL,
	setting text NULL,
	remark varchar(255) NULL,
	stripe_customer varchar(64) NULL,
	created_at int8 NULL,
	last_login_at int8 DEFAULT 0 NULL,
	auth_version int8 NOT NULL DEFAULT 1,
	CONSTRAINT users_pkey PRIMARY KEY (id),
	CONSTRAINT users_username_key UNIQUE (username)
);
CREATE UNIQUE INDEX idx_users_access_token ON public.users USING btree (access_token);
CREATE UNIQUE INDEX idx_users_aff_code ON public.users USING btree (aff_code);
CREATE INDEX idx_users_deleted_at ON public.users USING btree (deleted_at);
CREATE INDEX idx_users_discord_id ON public.users USING btree (discord_id);
CREATE INDEX idx_users_display_name ON public.users USING btree (display_name);
CREATE INDEX idx_users_email ON public.users USING btree (email);
CREATE INDEX idx_users_git_hub_id ON public.users USING btree (github_id);
CREATE INDEX idx_users_inviter_id ON public.users USING btree (inviter_id);
CREATE INDEX idx_users_linux_do_id ON public.users USING btree (linux_do_id);
CREATE INDEX idx_users_oidc_id ON public.users USING btree (oidc_id);
CREATE INDEX idx_users_stripe_customer ON public.users USING btree (stripe_customer);
CREATE INDEX idx_users_telegram_id ON public.users USING btree (telegram_id);
CREATE INDEX idx_users_username ON public.users USING btree (username);
CREATE INDEX idx_users_we_chat_id ON public.users USING btree (wechat_id);
