package com.epam.spring.service.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LogoutServiceTest {

    @Mock
    private TokenService tokenService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private LogoutService logoutService;

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTH_HEADER_NAME = "Authorization";

    @Test
    void logout_WithValidToken_ShouldAddToBlacklistAndClearContext() {
        String token = "valid.jwt.token";
        String authHeader = BEARER_PREFIX + token;
        when(request.getHeader(AUTH_HEADER_NAME)).thenReturn(authHeader);

        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class)) {
            logoutService.logout(request, response, authentication);

            verify(tokenService).addTokenToBlackList(token);
            securityContextHolder.verify(SecurityContextHolder::clearContext);
        }
    }

    @Test
    void logout_WithNullAuthHeader_ShouldDoNothing() {
        when(request.getHeader(AUTH_HEADER_NAME)).thenReturn(null);

        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class)) {
            logoutService.logout(request, response, authentication);

            verify(tokenService, never()).addTokenToBlackList(anyString());
            securityContextHolder.verify(SecurityContextHolder::clearContext, never());
        }
    }

    @Test
    void logout_WithInvalidAuthHeaderFormat_ShouldDoNothing() {
        String invalidAuthHeader = "Token valid.jwt.token";
        when(request.getHeader(AUTH_HEADER_NAME)).thenReturn(invalidAuthHeader);

        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class)) {
            logoutService.logout(request, response, authentication);

            verify(tokenService, never()).addTokenToBlackList(anyString());
            securityContextHolder.verify(SecurityContextHolder::clearContext, never());
        }
    }

    @Test
    void logout_WithEmptyBearerToken_ShouldAddEmptyStringToBlacklist() {
        String emptyToken = "";
        String authHeader = BEARER_PREFIX + emptyToken;
        when(request.getHeader(AUTH_HEADER_NAME)).thenReturn(authHeader);

        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class)) {
            logoutService.logout(request, response, authentication);

            verify(tokenService).addTokenToBlackList(emptyToken);
            securityContextHolder.verify(SecurityContextHolder::clearContext);
        }
    }
}
