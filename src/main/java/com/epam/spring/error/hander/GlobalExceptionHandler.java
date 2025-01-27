package com.epam.spring.error.hander;

import com.epam.spring.dto.response.ErrorResponseDTO;
import com.epam.spring.error.exception.IncorrectCredentialsException;
import com.epam.spring.error.exception.UniqueConstraintException;
import com.epam.spring.error.exception.UserNotFoundException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
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

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final static String MESSAGE = "User not found";

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleUnknownExceptions(Exception ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                List.of("Unknown error occurred, try again later.")
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
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

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleResourceNotFound() {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.toString(),
                List.of(MESSAGE)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IncorrectCredentialsException.class)
    public ResponseEntity<ErrorResponseDTO> handleUnknownExceptions(IncorrectCredentialsException ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.toString(),
                List.of(ex.getMessage())
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UniqueConstraintException.class)
    public ResponseEntity<ErrorResponseDTO> handleUniqueConstraint(UniqueConstraintException ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.toString(),
                List.of(ex.getMessage())
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }
}
