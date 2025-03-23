package com.epam.spring.error.hander;

import com.epam.spring.dto.response.ErrorResponseDTO;
import com.epam.spring.error.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void shouldHandleUnknownExceptions() {
        ResponseEntity<ErrorResponseDTO> response = exceptionHandler.handleUnknownExceptions();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertTrue(Objects.requireNonNull(response.getBody()).getDetailsList().contains("Internal error occurred, please try again later."));
    }

    @Test
    void shouldHandleResourceNotFoundException() {
        ResourceNotFoundException exception = new ResourceNotFoundException("testUser");

        ResponseEntity<ErrorResponseDTO> response = exceptionHandler.handleResourceNotFound(exception);

        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
        assertNotNull(response.getBody());
        assertTrue(Objects.requireNonNull(response.getBody()).getDetailsList().contains("Resource not found."));
    }

    @Test
    void shouldHandleIncorrectCredentialsException() {
        ResponseEntity<ErrorResponseDTO> response = exceptionHandler.handleIncorrectCredentialsException();

        assertEquals(response.getStatusCode(), HttpStatus.UNAUTHORIZED);
        assertNotNull(response.getBody());
        assertTrue(Objects.requireNonNull(response.getBody()).getDetailsList().contains("Incorrect credentials."));
    }

    @Test
    void shouldHandleUniqueConstraintException() {
        ResponseEntity<ErrorResponseDTO> response = exceptionHandler.handleUniqueConstraint();

        assertEquals(response.getStatusCode(), HttpStatus.CONFLICT);
        assertNotNull(response.getBody());
        assertTrue(Objects.requireNonNull(response.getBody()).getDetailsList().contains("This trainee already has another training scheduled at this time."));
    }
}