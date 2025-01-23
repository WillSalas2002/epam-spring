package com.epam.spring.filter;

import com.epam.spring.dto.response.ErrorResponseDTO;
import com.epam.spring.error.exception.UserNotFoundException;
import com.epam.spring.service.TraineeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Component
@AllArgsConstructor
public class AuthFilter implements Filter {

    private final TraineeService traineeService;
    private final ObjectMapper objectMapper;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        try {
            String method = request.getMethod();
            String requestURI = request.getRequestURI();
            if (method.equalsIgnoreCase("POST") && (requestURI.equals("/api/v1/trainees")|| requestURI.equals("/api/v1/trainers"))
                    || method.equalsIgnoreCase("GET") && (requestURI.contains("swagger-ui") || requestURI.contains("favicon.ico") || requestURI.contains("api-docs"))) {
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }

            String username = servletRequest.getParameter("username");
            String password = servletRequest.getParameter("password");

            if (username == null || password == null) {
                throw new ServletException("Missing username or password in the request");
            }
            traineeService.authenticate(username, password);

            filterChain.doFilter(servletRequest, servletResponse);
        } catch (UserNotFoundException ex) {
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            objectMapper.writeValue(response.getWriter(), createErrorResponse(ex.getMessage()));
        } catch (Exception ex) {
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(response.getWriter(), createErrorResponse(ex.getMessage()));
        }
    }

    private ErrorResponseDTO createErrorResponse(String message) {
        return new ErrorResponseDTO(
                LocalDateTime.now(),
                "Not Found",
                List.of(message)
        );
    }
}
