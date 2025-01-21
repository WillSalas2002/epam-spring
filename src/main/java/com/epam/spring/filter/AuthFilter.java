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
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        try {
            if (httpRequest.getMethod().equalsIgnoreCase("POST")
                    && (httpRequest.getRequestURI().equals("/api/v1/trainees") || httpRequest.getRequestURI().equals("/api/v1/trainers"))) {
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }

            String username = servletRequest.getParameter("username");
            String password = servletRequest.getParameter("password");

            if (username == null || password == null) {
                throw new ServletException("Missing username or password in the request");
            }

            traineeService.authenticate(username, password);
        } catch (UserNotFoundException ex) {
            httpResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
            objectMapper.writeValue(httpResponse.getWriter(), createErrorResponse(ex.getMessage()));
        } catch (Exception ex) {
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(httpResponse.getWriter(), createErrorResponse(ex.getMessage()));
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    private ErrorResponseDTO createErrorResponse(String message) {
        return new ErrorResponseDTO(
                LocalDateTime.now(),
                "Not Found",
                List.of(message)
        );
    }
}
