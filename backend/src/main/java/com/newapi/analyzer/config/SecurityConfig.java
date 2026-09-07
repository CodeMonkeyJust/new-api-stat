package com.newapi.analyzer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        CookieCsrfTokenRepository csrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        // 前端 SPA 页面位于 "/"（如 Vite 开发服务器 / 反向代理部署），而后端上下文路径是 /new-api-stat-api。
        // 若 Cookie Path 使用上下文路径，浏览器不会把 XSRF-TOKEN 暴露给 document.cookie，axios 将读不到
        // token 而无法附带 X-XSRF-TOKEN 请求头，导致登录等写操作被 CSRF 拦截返回 403。这里显式设为 "/"。
        csrfTokenRepository.setCookiePath("/");

        http
                .csrf(csrf -> csrf
                        .csrfTokenRepository(csrfTokenRepository)
                        // Cookie 仓库保存的是原始 token，前端 axios 读取 Cookie 原值作为 X-XSRF-TOKEN 请求头，
                        // 因此必须使用明文处理器；Spring Security 6 默认的 XOR 处理器会导致登录等写操作 403。
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))
                .headers(headers -> headers
                        .contentTypeOptions(contentTypeOptions -> {})
                        .frameOptions(frameOptions -> frameOptions.deny())
                        .referrerPolicy(referrerPolicy -> referrerPolicy
                                .policy(org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER)))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/auth/login", "/api/auth/logout", "/api/auth/current").permitAll()
                        .anyRequest().permitAll())
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(formLogin -> formLogin.disable())
                .logout(logout -> logout.disable())
                .sessionManagement(session -> session
                        .sessionFixation(sessionFixation -> sessionFixation.changeSessionId()));
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
