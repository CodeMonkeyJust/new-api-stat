package com.newapi.analyzer.controller;

import com.newapi.analyzer.dto.request.LoginRequest;
import com.newapi.analyzer.dto.response.LoginResponse;
import com.newapi.analyzer.dto.response.UserDTO;
import com.newapi.analyzer.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "认证接口", description = "用户登录认证相关接口")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/csrf")
    @Operation(summary = "获取 CSRF Token", description = "为前端登录和其他写操作获取 CSRF Token")
    public CsrfToken csrf(CsrfToken token, HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-store");
        return token;
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "通过用户名或邮箱登录系统")
    public LoginResponse login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        return authService.login(request, httpRequest);
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "退出登录并清除 session")
    public void logout(HttpSession session) {
        authService.logout(session);
    }

    @GetMapping("/current")
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的信息")
    public UserDTO getCurrentUser(HttpSession session) {
        return authService.getCurrentUser(session);
    }
}
