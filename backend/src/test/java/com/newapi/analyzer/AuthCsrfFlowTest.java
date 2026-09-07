package com.newapi.analyzer;

import com.newapi.analyzer.config.SecurityConfig;
import com.newapi.analyzer.controller.AuthController;
import com.newapi.analyzer.repository.UserRepository;
import com.newapi.analyzer.service.AuthService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 模拟浏览器 + axios 的 CSRF 流程：
 * 1) GET /api/auth/csrf 写入 XSRF-TOKEN Cookie。Cookie Path 必须为 "/"：前端 SPA 页面位于 "/"
 *    （Vite 开发服务器 / 反向代理部署），若 Path 是上下文路径 /new-api-stat-api，浏览器不会把该
 *    Cookie 暴露给 document.cookie，axios 将读不到 token 而无法附带请求头；
 * 2) 后续写操作（登录）必须携带 X-XSRF-TOKEN 头，否则应被 Spring Security 拒绝(403)。
 * 该测试经过真实的 SecurityFilterChain，可验证 CookieCsrfTokenRepository 配置是否与
 * 前端 axios（读 Cookie 原值作为请求头）兼容。
 */
@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthCsrfFlowTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    void csrfEndpointIssuesTokenCookie() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/auth/csrf"))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("XSRF-TOKEN"))
                .andExpect(cookie().path("XSRF-TOKEN", "/"))
                .andReturn();

        String body = result.getResponse().getContentAsString();
        assertThat(body).contains("token");
        Cookie tokenCookie = result.getResponse().getCookie("XSRF-TOKEN");
        if (tokenCookie == null) {
            throw new AssertionError("XSRF-TOKEN cookie should have been issued");
        }
        assertThat(tokenCookie.getValue()).isNotBlank();
    }

    @Test
    void loginWithoutCsrfHeaderIsRejected() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content("{\"username\":\"admin\",\"password\":\"secret\"}"))
                .andExpect(status().isForbidden());

        verify(authService, never()).login(any(), any());
    }

    @Test
    void loginWithRawCookieCsrfHeaderSucceeds() throws Exception {
        MockHttpSession session = new MockHttpSession();
        MvcResult csrfResult = mockMvc.perform(get("/api/auth/csrf").session(session))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("XSRF-TOKEN"))
                .andExpect(cookie().path("XSRF-TOKEN", "/"))
                .andReturn();

        // axios 的行为：读取 Cookie 中的原始 token 值并原样放入 X-XSRF-TOKEN 请求头
        Cookie tokenCookie = csrfResult.getResponse().getCookie("XSRF-TOKEN");
        if (tokenCookie == null) {
            throw new AssertionError("XSRF-TOKEN cookie should have been issued");
        }
        String rawToken = tokenCookie.getValue();
        assertThat(rawToken).isNotBlank();

        mockMvc.perform(post("/api/auth/login")
                        .session(session)
                        .cookie(csrfResult.getResponse().getCookie("XSRF-TOKEN"))
                        .header("X-XSRF-TOKEN", rawToken)
                        .contentType("application/json")
                        .content("{\"username\":\"admin\",\"password\":\"secret\"}"))
                .andExpect(status().isOk());

        verify(authService).login(any(), any());
    }
}
