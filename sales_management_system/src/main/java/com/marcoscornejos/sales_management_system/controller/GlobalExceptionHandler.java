package com.marcoscornejos.sales_management_system.controller;

import com.marcoscornejos.sales_management_system.exception.AuthException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for all controllers in the system.
 *
 * <p>This class intercepts exceptions thrown by controllers and services,
 * providing consistent HTTP responses to the front-end.</p>
 *
 * <p>It handles both authentication-related exceptions and
 * validation errors from request DTOs annotated with {@link jakarta.validation.Valid}.</p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles authentication exceptions thrown by AuthService.
     *
     * @param ex the {@link AuthException} thrown during authentication
     * @return ResponseEntity with HTTP 401 Unauthorized and the exception message
     */
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<Map<String, String>> handleAuthException(AuthException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    /**
     * Handles validation errors for request DTOs annotated with {@link jakarta.validation.Valid}.
     *
     * @param ex the {@link MethodArgumentNotValidException} containing validation errors
     * @return ResponseEntity with HTTP 400 Bad Request and a map of field-specific error messages
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * Handles all other uncaught exceptions.
     *
     * @param ex the unexpected {@link Exception}
     * @return ResponseEntity with HTTP 500 Internal Server Error and a generic error message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "An unexpected error occurred: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
