package com.newapi.analyzer.config;

import com.newapi.analyzer.entity.UserEntity;
import com.newapi.analyzer.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.io.IOException;

@Component
public class SessionAuthInterceptor implements HandlerInterceptor {

    private final UserRepository userRepository;
    private final Set<Long> analyticsRoles;

    public SessionAuthInterceptor(
            UserRepository userRepository,
            @Value("${app.analytics-roles:10,100}") String analyticsRoles) {
        this.userRepository = userRepository;
        this.analyticsRoles = Arrays.stream(analyticsRoles.split(","))
                .map(value -> value.trim())
                .filter(value -> !value.isEmpty())
                .map(Long::valueOf)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        var session = request.getSession(false);
        if (session == null || !(session.getAttribute("id") instanceof Number)) {
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "请先登录");
            return false;
        }

        Long userId = ((Number) session.getAttribute("id")).longValue();
        UserEntity user = userRepository.findById(userId)
                .filter(candidate -> candidate.getDeletedAt() == null
                        && candidate.getStatus() != null
                        && candidate.getStatus() == 1)
                .orElse(null);
        if (user == null) {
            session.invalidate();
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "登录已失效，请重新登录");
            return false;
        }

        String path = request.getRequestURI().substring(request.getContextPath().length());
        boolean isPersonalPath = path.equals("/api/analyzer/personal")
                || path.startsWith("/api/analyzer/personal/");
        if (path.startsWith("/api/")
                && !isPersonalPath
                && (user.getRole() == null || !analyticsRoles.contains(user.getRole()))) {
            writeError(response, HttpServletResponse.SC_FORBIDDEN, "仅管理员或根用户可以访问统计数据");
            return false;
        }

        return true;
    }

    private void writeError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":" + status + ",\"message\":\"" + message + "\",\"data\":null}");
    }
}
