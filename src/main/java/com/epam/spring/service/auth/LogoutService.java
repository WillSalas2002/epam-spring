package com.epam.spring.service.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final TokenService tokenService;
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTH_HEADER_NAME = "Authorization";

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String authHeader = request.getHeader(AUTH_HEADER_NAME);
        String jwt;
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            return;
        }
        jwt = authHeader.substring(BEARER_PREFIX.length());
        tokenService.addTokenToBlackList(jwt);
        SecurityContextHolder.clearContext();
    }
}
