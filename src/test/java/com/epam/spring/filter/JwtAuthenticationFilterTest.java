package com.epam.spring.filter;

import com.epam.spring.service.auth.JwtService;
import com.epam.spring.service.auth.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private TokenService tokenService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private PrintWriter printWriter;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private final String validToken = "valid.jwt.token";
    private final String validUsername = "testuser@example.com";
    private final UserDetails userDetails = new User(validUsername, "password",
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

    @ParameterizedTest
    @ValueSource(strings = {
            "/api/v1/users/login",
            "/api/v1/trainers",
            "/api/v1/trainees",
            "/swagger-ui/index.html",
            "/api-docs/swagger-config"
    })
    void doFilterInternal_WithAllowedEndpoints_ShouldSkipAuthentication(String endpoint) throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn(endpoint);
        lenient().when(response.getWriter()).thenReturn(printWriter);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService);
        verifyNoInteractions(tokenService);
    }

    @Test
    void doFilterInternal_WithMissingAuthHeader_ShouldSendErrorResponse() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/api/v1/protected");
        when(request.getHeader(JwtAuthenticationFilter.AUTH_HEADER_NAME)).thenReturn(null);
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        when(response.getWriter()).thenReturn(printWriter);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(401);
        verify(printWriter).write("{}");
        verifyNoInteractions(filterChain);
    }

    @Test
    void doFilterInternal_WithInvalidAuthHeaderFormat_ShouldSendErrorResponse() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/api/v1/protected");
        when(request.getHeader(JwtAuthenticationFilter.AUTH_HEADER_NAME)).thenReturn("Token " + validToken);
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        when(response.getWriter()).thenReturn(printWriter);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(401);
        verify(printWriter).write("{}");
        verifyNoInteractions(filterChain);
    }

    @Test
    void doFilterInternal_WithValidTokenAndNoExistingAuth_ShouldAuthenticateUser() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/api/v1/protected");
        when(request.getHeader(JwtAuthenticationFilter.AUTH_HEADER_NAME)).thenReturn(JwtAuthenticationFilter.BEARER_PREFIX + validToken);

        when(jwtService.extractUsername(validToken)).thenReturn(validUsername);
        when(userDetailsService.loadUserByUsername(validUsername)).thenReturn(userDetails);
        when(tokenService.isTokenBlackListed(validToken)).thenReturn(false);
        when(jwtService.isTokenValid(validToken, userDetails)).thenReturn(true);

        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(null);

            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            verify(securityContext).setAuthentication(any(UsernamePasswordAuthenticationToken.class));
            verify(filterChain).doFilter(request, response);
        }
    }

    @Test
    void doFilterInternal_WithTokenInBlacklist_ShouldNotAuthenticate() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/api/v1/protected");
        when(request.getHeader(JwtAuthenticationFilter.AUTH_HEADER_NAME)).thenReturn(JwtAuthenticationFilter.BEARER_PREFIX + validToken);

        when(jwtService.extractUsername(validToken)).thenReturn(validUsername);
        when(userDetailsService.loadUserByUsername(validUsername)).thenReturn(userDetails);
        when(tokenService.isTokenBlackListed(validToken)).thenReturn(true);

        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(null);

            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            verify(securityContext, never()).setAuthentication(any(UsernamePasswordAuthenticationToken.class));
            verify(filterChain).doFilter(request, response);
        }
    }

    @Test
    void doFilterInternal_WithInvalidToken_ShouldSendErrorResponse() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/api/v1/protected");
        when(request.getHeader(JwtAuthenticationFilter.AUTH_HEADER_NAME)).thenReturn(JwtAuthenticationFilter.BEARER_PREFIX + validToken);
        when(jwtService.extractUsername(validToken)).thenThrow(new RuntimeException("Invalid token"));
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        when(response.getWriter()).thenReturn(printWriter);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(401);
        verify(printWriter).write("{}");
        verifyNoInteractions(filterChain);
    }

    @Test
    void doFilterInternal_WithExistingAuthentication_ShouldNotSetAuthentication() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/api/v1/protected");
        when(request.getHeader(JwtAuthenticationFilter.AUTH_HEADER_NAME)).thenReturn(JwtAuthenticationFilter.BEARER_PREFIX + validToken);

        when(jwtService.extractUsername(validToken)).thenReturn(validUsername);

        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(mock(UsernamePasswordAuthenticationToken.class));

            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            verify(userDetailsService, never()).loadUserByUsername(anyString());
            verify(securityContext, never()).setAuthentication(any());
            verify(filterChain).doFilter(request, response);
        }
    }
}
