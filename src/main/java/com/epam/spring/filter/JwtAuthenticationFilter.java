package com.epam.spring.filter;

import com.epam.spring.dto.response.ErrorResponseDTO;
import com.epam.spring.service.auth.JwtService;
import com.epam.spring.service.auth.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String BEARER_PREFIX = "Bearer ";
    public static final String AUTH_HEADER_NAME = "Authorization";
    public static final String MESSAGE_INVALID_TOKEN = "Invalid token";
    public static final String MESSAGE_TOKEN_NOT_PROVIDED = "JWT token not provided.";

    private final JwtService jwtService;
    private final TokenService tokenService;
    private final ObjectMapper objectMapper;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader(AUTH_HEADER_NAME);
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            sendErrorResponse(response, MESSAGE_TOKEN_NOT_PROVIDED);
            return;
        }
        String token = authHeader.substring(BEARER_PREFIX.length());
        try {
            String username = jwtService.extractUsername(token);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                boolean isTokenBlackListed = tokenService.isTokenBlackListed(token);
                if (!isTokenBlackListed && jwtService.isTokenValid(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            chain.doFilter(request, response);
        } catch (RuntimeException e) {
            sendErrorResponse(response, MESSAGE_INVALID_TOKEN);
        }
    }

    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        ErrorResponseDTO errorResponse = buildErrorResponse(message);
        String jsonResponse = objectMapper.writeValueAsString(errorResponse);

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(jsonResponse);
    }

    private static ErrorResponseDTO buildErrorResponse(String message) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO();
        errorResponse.setTitle(HttpStatus.UNAUTHORIZED.toString());
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setDetailsList(List.of(message));
        return errorResponse;
    }
}