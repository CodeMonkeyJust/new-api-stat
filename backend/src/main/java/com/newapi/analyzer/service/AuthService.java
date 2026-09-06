package com.newapi.analyzer.service;

import com.newapi.analyzer.dto.request.LoginRequest;
import com.newapi.analyzer.dto.response.LoginResponse;
import com.newapi.analyzer.dto.response.UserDTO;
import com.newapi.analyzer.entity.UserEntity;
import com.newapi.analyzer.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        String username = request.getUsername().trim();
        String password = request.getPassword();

        UserEntity user = userRepository.findByUsernameOrEmail(username, username).orElse(null);
        if (user == null || user.getDeletedAt() != null || !passwordEncoder.matches(password, user.getPassword())) {
            return new LoginResponse(false, "用户名或密码错误", null);
        }
        if (user.getStatus() == null || user.getStatus() != 1) {
            return new LoginResponse(false, "用户名或密码错误", null);
        }

        HttpSession session = httpRequest.getSession(true);
        httpRequest.changeSessionId();
        session.setAttribute("id", user.getId());
        session.setAttribute("username", user.getUsername());
        session.setAttribute("role", user.getRole());
        session.setAttribute("status", user.getStatus());
        session.setAttribute("group", user.getGroup());

        return new LoginResponse(true, "登录成功", toUserDTO(user));
    }

    public void logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
    }

    public UserDTO getCurrentUser(HttpSession session) {
        if (session == null) {
            return null;
        }
        Object sessionId = session.getAttribute("id");
        if (!(sessionId instanceof Number)) {
            return null;
        }
        Long id = ((Number) sessionId).longValue();
        return userRepository.findById(id)
                .filter(user -> user.getDeletedAt() == null && user.getStatus() != null && user.getStatus() == 1)
                .map(this::toUserDTO)
                .orElse(null);
    }

    private UserDTO toUserDTO(UserEntity user) {
        return new UserDTO(
                user.getId(), user.getUsername(), user.getEmail(), user.getDisplayName(),
                user.getRole(), user.getStatus(), user.getGroup()
        );
    }
}
