package com.marcoscornejos.sales_management_system.controller;

import com.marcoscornejos.sales_management_system.exception.AuthException;
import com.marcoscornejos.sales_management_system.exception.ProductException;
import com.marcoscornejos.sales_management_system.exception.SaleException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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
@Slf4j
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
     * Handles all unexpected exceptions.
     *
     * <p>Returns a generic error message to the client while logging
     * the full exception details internally.</p>
     *
     * @param ex the unexpected {@link Exception}
     * @return ResponseEntity with HTTP 500 Internal Server Error and a sanitized message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {

        // Log full error details for debugging
        log.error("Unexpected error occurred", ex);

        // Return sanitized response
        Map<String, String> error = new HashMap<>();
        error.put("message", "An internal server error occurred");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * Handles errors when a request parameter cannot be converted
     * to the expected type.
     *
     * <p>This typically happens when the client sends an invalid value
     * (e.g., a wrong enum value, a string instead of a number, etc.).</p>
     *
     * <p>Returns a simple error message indicating the incorrect value
     * provided by the client.</p>
     *
     * @param ex the exception thrown when type conversion fails
     * @return a 400 Bad Request response with the invalid value message
     */

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {

        Map<String, String> error = new HashMap<>();

        String message = String.format(
                "Incorrect value: %s",
                ex.getValue()
        );

        error.put(ex.getName(), message);

        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Handles product-related exceptions thrown when business rules
     * or validations fail within the Product domain.
     *
     * <p>
     * Returns a 400 Bad Request response with a descriptive error message.
     * </p>
     *
     * @param ex the exception containing error details
     * @return a 400 Bad Request response with the error message
     */
    @ExceptionHandler(ProductException.class)
    public ResponseEntity<Map<String, String>> handleProductException(ProductException ex) {

        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());

        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Handles exceptions thrown when the request body contains invalid or
     * unreadable data (e.g., incorrect data types or invalid enum values).
     *
     * <p>
     * Returns a 400 Bad Request response with a generic error message.
     * </p>
     *
     * @param ex the exception containing error details
     * @return a 400 Bad Request response with the error message
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {

        Map<String, String> error = new HashMap<>();
        error.put("error", "Invalid request body. Please check the provided values");

        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Handles sale-related exceptions thrown when business rules
     * or validations fail within the Sale domain.
     *
     * <p>
     * Returns a 400 Bad Request response with a descriptive error message.
     * </p>
     *
     * @param ex the exception containing error details
     * @return a 400 Bad Request response with the error message
     */
    @ExceptionHandler(SaleException.class)
    public ResponseEntity<Map<String, String>> handleSaleException(SaleException ex) {

        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());

        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Handles missing request parameter exceptions.
     *
     * <p>
     * Triggered when a required query parameter is not provided in the request.
     * Returns a 400 Bad Request response indicating which parameter is missing.
     * </p>
     *
     * @param ex the exception containing details about the missing parameter
     * @return a 400 Bad Request response with an error message
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, String>> handleMissingParams(MissingServletRequestParameterException ex) {

        Map<String, String> error = new HashMap<>();
        error.put("error", "Missing required parameter: " + ex.getParameterName());

        return ResponseEntity.badRequest().body(error);
    }
}
