package com.newapi.analyzer.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginRequest {
    @NotBlank(message = "用户名不能为空")
    @Size(max = 128, message = "用户名不能超过 128 个字符")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(max = 256, message = "密码不能超过 256 个字符")
    private String password;

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
}
