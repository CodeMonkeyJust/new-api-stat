package com.newapi.analyzer.config;

import com.newapi.analyzer.entity.UserEntity;
import com.newapi.analyzer.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.lang.NonNull;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SessionAuthInterceptorTest {

    private UserRepository userRepository;
    private SessionAuthInterceptor interceptor;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        interceptor = new SessionAuthInterceptor(userRepository, "10,100");
    }

    @NonNull
    private MockHttpServletRequest requestWithApiPath() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContextPath("/new-api-stat-api");
        request.setRequestURI("/new-api-stat-api/api/analyzer/summary");
        return request;
    }

    @NonNull
    private MockHttpServletRequest requestWithPersonalPath() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContextPath("/new-api-stat-api");
        request.setRequestURI("/new-api-stat-api/api/analyzer/personal/stats");
        return request;
    }

    private MockHttpSession sessionWithUserId(Long userId) {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("id", userId);
        return session;
    }

    private UserEntity activeUser(Long id, Long role) {
        UserEntity user = new UserEntity();
        user.setId(id);
        user.setUsername("admin");
        user.setRole(role);
        user.setStatus(1L);
        return user;
    }

    @Test
    void rejectsRequestWithoutSession() throws Exception {
        MockHttpServletRequest request = requestWithApiPath();
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean allowed = interceptor.preHandle(request, response, new Object());

        assertThat(allowed).isFalse();
        assertThat(response.getStatus()).isEqualTo(401);
    }

    @Test
    void rejectsSessionWithoutUserId() throws Exception {
        MockHttpServletRequest request = requestWithApiPath();
        request.setSession(new MockHttpSession());
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean allowed = interceptor.preHandle(request, response, new Object());

        assertThat(allowed).isFalse();
        assertThat(response.getStatus()).isEqualTo(401);
    }

    @Test
    void rejectsUnknownUserAndInvalidatesSession() throws Exception {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        MockHttpSession session = sessionWithUserId(99L);
        MockHttpServletRequest request = requestWithApiPath();
        request.setSession(session);
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean allowed = interceptor.preHandle(request, response, new Object());

        assertThat(allowed).isFalse();
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(session.isInvalid()).isTrue();
    }

    @Test
    void rejectsDeletedUser() throws Exception {
        UserEntity user = activeUser(1L, 100L);
        user.setDeletedAt(java.time.OffsetDateTime.now());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        MockHttpServletRequest request = requestWithApiPath();
        request.setSession(sessionWithUserId(1L));
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean allowed = interceptor.preHandle(request, response, new Object());

        assertThat(allowed).isFalse();
        assertThat(response.getStatus()).isEqualTo(401);
    }

    @Test
    void rejectsDisabledUser() throws Exception {
        UserEntity user = activeUser(1L, 100L);
        user.setStatus(0L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        MockHttpServletRequest request = requestWithApiPath();
        request.setSession(sessionWithUserId(1L));
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean allowed = interceptor.preHandle(request, response, new Object());

        assertThat(allowed).isFalse();
        assertThat(response.getStatus()).isEqualTo(401);
    }

    @Test
    void rejectsRoleWithoutAnalyticsPermission() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser(1L, 1L)));

        MockHttpServletRequest request = requestWithApiPath();
        request.setSession(sessionWithUserId(1L));
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean allowed = interceptor.preHandle(request, response, new Object());

        assertThat(allowed).isFalse();
        assertThat(response.getStatus()).isEqualTo(403);
    }

    @Test
    void allowsRoleWithAnalyticsPermission() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser(1L, 100L)));

        MockHttpServletRequest request = requestWithApiPath();
        request.setSession(sessionWithUserId(1L));
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean allowed = interceptor.preHandle(request, response, new Object());

        assertThat(allowed).isTrue();
        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    void allowsOrdinaryRoleOnPersonalPath() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser(1L, 1L)));

        MockHttpServletRequest request = requestWithPersonalPath();
        request.setSession(sessionWithUserId(1L));
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean allowed = interceptor.preHandle(request, response, new Object());

        assertThat(allowed).isTrue();
        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    void allowsPrivilegedRoleOnPersonalPath() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser(1L, 100L)));

        MockHttpServletRequest request = requestWithPersonalPath();
        request.setSession(sessionWithUserId(1L));
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean allowed = interceptor.preHandle(request, response, new Object());

        assertThat(allowed).isTrue();
        assertThat(response.getStatus()).isEqualTo(200);
    }

}
