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
import org.springframework.http.MediaType;
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

    private static final String HTTP_POST = "POST";
    private static final String HTTP_GET = "GET";

    private static final String TRAINEES_ENDPOINT = "/api/v1/trainees";
    private static final String TRAINERS_ENDPOINT = "/api/v1/trainers";
    private static final String SWAGGER_UI = "swagger-ui";
    private static final String FAVICON = "favicon.ico";
    private static final String API_DOCS = "api-docs";

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BASIC_AUTH_PREFIX = "Basic ";
    private static final String ADMIN_USERNAME = "Admin";
    private static final String ADMIN_PASSWORD = "Admin";

    private static final String ERROR_INVALID_CREDENTIALS = "Invalid token or credentials";
    private static final String ERROR_INTERNAL_SERVER = "Internal server error";

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
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, ERROR_INVALID_CREDENTIALS);
                return;
            }

            filterChain.doFilter(servletRequest, servletResponse);
        } catch (Exception ex) {
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ERROR_INTERNAL_SERVER);
        }
    }

    private boolean isPublicEndpoint(HttpServletRequest request) {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        return (HTTP_POST.equalsIgnoreCase(method) && (TRAINEES_ENDPOINT.equals(uri) || TRAINERS_ENDPOINT.equals(uri))) ||
                (HTTP_GET.equalsIgnoreCase(method) && (uri.contains(SWAGGER_UI) || uri.contains(FAVICON) || uri.contains(API_DOCS)));
    }

    private boolean isAuthorized(HttpServletRequest request) {
        String authorization = request.getHeader(AUTHORIZATION_HEADER);
        if (authorization != null && authorization.startsWith(BASIC_AUTH_PREFIX)) {
            String credentials = decodeCredentials(authorization.substring(BASIC_AUTH_PREFIX.length()));
            String[] values = credentials.split(":", 2);
            return values.length == 2 && ADMIN_USERNAME.equals(values[0]) && ADMIN_PASSWORD.equals(values[1]);
        }
        return false;
    }

    private String decodeCredentials(String base64Credentials) {
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        return new String(credDecoded, StandardCharsets.UTF_8);
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
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
