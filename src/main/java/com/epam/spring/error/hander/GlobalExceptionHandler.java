package com.epam.spring.error.hander;

import com.epam.spring.dto.response.ErrorResponseDTO;
import com.epam.spring.error.exception.IncorrectCredentialsException;
import com.epam.spring.error.exception.LoginAttemptException;
import com.epam.spring.error.exception.ResourceNotFoundException;
import com.epam.spring.util.TransactionContext;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final static String MESSAGE_RESOURCE_NOT_FOUND = "Resource not found.";
    private final static String MESSAGE_INTERNAL_SERVER_ERROR = "Internal error occurred, please try again later.";
    private final static String MESSAGE_INCORRECT_CREDENTIALS = "Incorrect credentials.";
    private final static String MESSAGE_UNIQUE_CONSTRAINT = "This trainee already has another training scheduled at this time.";
    private final static String MESSAGE_TOO_MANY_UNSUCCESSFUL_ATTEMPTS = "User is blocked due to too many failed attempts. Try again later";
    private final static String MESSAGE_TOKEN_EXPIRED = "JWT token has expired. Please log in again.";

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleUnknownExceptions() {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                List.of(MESSAGE_INTERNAL_SERVER_ERROR)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                               @NonNull HttpHeaders headers,
                                                               @NonNull HttpStatusCode status,
                                                               @NonNull WebRequest request) {
        BindingResult bindingResult = ex.getBindingResult();

        List<String> errors = bindingResult.getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                LocalDateTime.now(),
                status.toString(),
                errors
        );
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleResourceNotFound(ResourceNotFoundException ex) {
        log.info("Transaction ID: {}, User with username: {} not found",
                TransactionContext.getTransactionId(), ex.getUsername());
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.toString(),
                List.of(MESSAGE_RESOURCE_NOT_FOUND)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IncorrectCredentialsException.class)
    public ResponseEntity<ErrorResponseDTO> handleIncorrectCredentialsException() {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.toString(),
                List.of(MESSAGE_INCORRECT_CREDENTIALS)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleUniqueConstraint() {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.toString(),
                List.of(MESSAGE_UNIQUE_CONSTRAINT)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(LoginAttemptException.class)
    public ResponseEntity<ErrorResponseDTO> handleLoginAttempt() {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                LocalDateTime.now(),
                HttpStatus.TOO_MANY_REQUESTS.toString(),
                List.of(MESSAGE_TOO_MANY_UNSUCCESSFUL_ATTEMPTS)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponseDTO> handleTokenExpired() {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.toString(),
                List.of(MESSAGE_TOKEN_EXPIRED)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }
}
