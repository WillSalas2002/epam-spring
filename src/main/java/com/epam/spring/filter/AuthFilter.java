package com.epam.spring.filter;

import com.epam.spring.dto.response.ErrorResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Component
@AllArgsConstructor
public class AuthFilter implements Filter {

    private final ObjectMapper objectMapper;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        try {
            if (isPublicEndpoint(request)) {
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }

            if (!isAuthorized(request)) {
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid token or credentials");
                return;
            }

            filterChain.doFilter(servletRequest, servletResponse);
        } catch (Exception ex) {
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    private boolean isPublicEndpoint(HttpServletRequest request) {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        return (method.equalsIgnoreCase("POST") && (uri.equals("/api/v1/trainees") || uri.equals("/api/v1/trainers"))) ||
                (method.equalsIgnoreCase("GET") && (uri.contains("swagger-ui") || uri.contains("favicon.ico") || uri.contains("api-docs")));
    }

    private boolean isAuthorized(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Basic ")) {
            String credentials = decodeCredentials(authorization.substring("Basic ".length()));
            String[] values = credentials.split(":", 2);
            return values.length == 2 && "Admin".equals(values[0]) && "Admin".equals(values[1]);
        }
        return false;
    }

    private String decodeCredentials(String base64Credentials) {
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        return new String(credDecoded, StandardCharsets.UTF_8);
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setContentType("application/json");
        response.setStatus(status);
        objectMapper.writeValue(response.getWriter(), createErrorResponse(message));
    }

    private ErrorResponseDTO createErrorResponse(String message) {
        return new ErrorResponseDTO(
                LocalDateTime.now(),
                "Error",
                List.of(message)
        );
    }
}
