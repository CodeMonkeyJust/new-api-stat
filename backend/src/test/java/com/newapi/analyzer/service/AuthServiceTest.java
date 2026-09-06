package com.newapi.analyzer.service;

import com.newapi.analyzer.dto.request.LoginRequest;
import com.newapi.analyzer.dto.response.LoginResponse;
import com.newapi.analyzer.dto.response.UserDTO;
import com.newapi.analyzer.entity.UserEntity;
import com.newapi.analyzer.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthServiceTest {

    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = new BCryptPasswordEncoder();
        authService = new AuthService(userRepository, passwordEncoder);
    }

    private UserEntity activeUser(Long id, String username, String email) {
        UserEntity user = new UserEntity();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("secret123"));
        user.setDisplayName("管理员");
        user.setRole(100L);
        user.setStatus(1L);
        user.setGroup("default");
        return user;
    }

    private LoginRequest loginRequest(String username, String password) {
        LoginRequest request = new LoginRequest();
        request.setUsername(username);
        request.setPassword(password);
        return request;
    }

    @Test
    void loginByUsernameSucceedsAndPopulatesSession() {
        UserEntity user = activeUser(1L, "admin", "admin@example.com");
        when(userRepository.findByUsernameOrEmail("admin", "admin")).thenReturn(Optional.of(user));

        MockHttpServletRequest httpRequest = new MockHttpServletRequest();
        LoginResponse response = authService.login(loginRequest("admin", "secret123"), httpRequest);

        assertThat(response.isSuccess()).isTrue();
        MockHttpSession session = (MockHttpSession) httpRequest.getSession(false);
        if (session == null) {
            throw new AssertionError("login should have created a session");
        }
        assertThat(session.getAttribute("id")).isEqualTo(1L);
        assertThat(session.getAttribute("username")).isEqualTo("admin");
        assertThat(session.getAttribute("role")).isEqualTo(100L);
        UserDTO dto = response.getUser();
        assertThat(dto).isNotNull();
        assertThat(dto.getUsername()).isEqualTo("admin");
        assertThat(dto.getRole()).isEqualTo(100L);
    }

    @Test
    void loginByEmailSucceeds() {
        UserEntity user = activeUser(2L, "alice", "alice@example.com");
        when(userRepository.findByUsernameOrEmail("alice@example.com", "alice@example.com"))
                .thenReturn(Optional.of(user));

        MockHttpServletRequest httpRequest = new MockHttpServletRequest();
        LoginResponse response = authService.login(loginRequest("alice@example.com", "secret123"), httpRequest);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getUser().getUsername()).isEqualTo("alice");
    }

    @Test
    void loginWithWrongPasswordFails() {
        UserEntity user = activeUser(1L, "admin", "admin@example.com");
        when(userRepository.findByUsernameOrEmail("admin", "admin")).thenReturn(Optional.of(user));

        MockHttpServletRequest httpRequest = new MockHttpServletRequest();
        LoginResponse response = authService.login(loginRequest("admin", "wrong-password"), httpRequest);

        assertThat(response.isSuccess()).isFalse();
        assertThat(httpRequest.getSession(false)).isNull();
    }

    @Test
    void loginWithUnknownUserFails() {
        when(userRepository.findByUsernameOrEmail("ghost", "ghost")).thenReturn(Optional.empty());

        MockHttpServletRequest httpRequest = new MockHttpServletRequest();
        LoginResponse response = authService.login(loginRequest("ghost", "secret123"), httpRequest);

        assertThat(response.isSuccess()).isFalse();
    }

    @Test
    void loginWithDeletedUserFails() {
        UserEntity user = activeUser(1L, "admin", "admin@example.com");
        user.setDeletedAt(java.time.OffsetDateTime.now());
        when(userRepository.findByUsernameOrEmail("admin", "admin")).thenReturn(Optional.of(user));

        MockHttpServletRequest httpRequest = new MockHttpServletRequest();
        LoginResponse response = authService.login(loginRequest("admin", "secret123"), httpRequest);

        assertThat(response.isSuccess()).isFalse();
    }

    @Test
    void loginWithDisabledUserFails() {
        UserEntity user = activeUser(1L, "admin", "admin@example.com");
        user.setStatus(0L);
        when(userRepository.findByUsernameOrEmail("admin", "admin")).thenReturn(Optional.of(user));

        MockHttpServletRequest httpRequest = new MockHttpServletRequest();
        LoginResponse response = authService.login(loginRequest("admin", "secret123"), httpRequest);

        assertThat(response.isSuccess()).isFalse();
    }

    @Test
    void getCurrentUserReturnsDtoForActiveUser() {
        UserEntity user = activeUser(1L, "admin", "admin@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("id", 1L);

        UserDTO dto = authService.getCurrentUser(session);

        assertThat(dto).isNotNull();
        assertThat(dto.getUsername()).isEqualTo("admin");
    }

    @Test
    void getCurrentUserReturnsNullForMissingSession() {
        assertThat(authService.getCurrentUser(null)).isNull();
    }

    @Test
    void getCurrentUserReturnsNullWhenSessionHasNoId() {
        MockHttpSession session = new MockHttpSession();
        assertThat(authService.getCurrentUser(session)).isNull();
    }
}
