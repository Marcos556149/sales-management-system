package com.marcoscornejos.sales_management_system.controller;

import com.marcoscornejos.sales_management_system.exception.AuthException;
import com.marcoscornejos.sales_management_system.exception.ProductException;
import com.marcoscornejos.sales_management_system.exception.SaleException;
import com.marcoscornejos.sales_management_system.exception.UserException;
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
     * <p>
     * This exception is triggered when Bean Validation constraints fail.
     * Only the first validation error is returned to simplify frontend handling.
     * </p>
     *
     * <p>Returns a standardized error response:</p>
     *
     * <pre>
     * {
     *   "error": {
     *     "code": "VALIDATION_ERROR",
     *     "message": "Product code is required",
     *     "field": "productCode"
     *   }
     * }
     * </pre>
     *
     * @param ex the validation exception
     * @return ResponseEntity with HTTP 400 Bad Request and structured error body
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {

        FieldError fieldError = ex.getBindingResult().getFieldErrors().get(0);

        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("code", "VALIDATION_ERROR");
        errorBody.put("field", fieldError.getField());
        errorBody.put("message", fieldError.getDefaultMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("error", errorBody);

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handles all unexpected exceptions that are not explicitly managed
     * by specific exception handlers.
     *
     * <p>
     * This is a fallback mechanism to ensure that no exception leaks
     * unstructured responses to the client.
     * </p>
     *
     * <p>
     * The response follows the standardized error format:
     * </p>
     *
     * <pre>
     * {
     *   "error": {
     *     "code": "INTERNAL_SERVER_ERROR",
     *     "message": "An unexpected error occurred",
     *     "field": null
     *   }
     * }
     * </pre>
     *
     * <p>
     * Full exception details are logged internally for debugging purposes.
     * </p>
     *
     * @param ex the unexpected exception
     * @return a 500 Internal Server Error response with standardized error format
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {

        // Log full error details for debugging
        log.error("Unexpected error occurred", ex);

        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("code", "INTERNAL_SERVER_ERROR");
        errorBody.put("message", "An unexpected error occurred");
        errorBody.put("field", null);

        Map<String, Object> response = new HashMap<>();
        response.put("error", errorBody);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * Handles errors when a request parameter cannot be converted
     * to the expected type.
     *
     * <p>This typically happens when the client sends an invalid value
     * (e.g., a wrong enum value, a string instead of a number, etc.).</p>
     *
     * <p>Returns a standardized error response using the global error format:</p>
     *
     * <pre>
     * {
     *   "error": {
     *     "code": "INVALID_PARAMETER_TYPE",
     *     "message": "Invalid value 'ACTVEE' for parameter 'statusFilter'",
     *     "field": "statusFilter"
     *   }
     * }
     * </pre>
     *
     * @param ex the exception thrown when type conversion fails
     * @return a 400 Bad Request response with structured error format
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {

        Map<String, Object> error = new HashMap<>();

        String field = ex.getName();
        Object value = ex.getValue();

        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("code", "INVALID_PARAMETER_TYPE");
        errorBody.put("field", field);
        errorBody.put("message",
                String.format("Invalid value '%s' for parameter '%s'", value, field)
        );

        error.put("error", errorBody);

        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Handles product-related exceptions thrown when business rules
     * or validations fail within the Product domain.
     *
     * <p>
     * This handler centralizes all exceptions that extend {@code ProductException},
     * ensuring a consistent error response format across the application.
     * </p>
     *
     * <p>
     * The response follows the standardized structure:
     * </p>
     *
     * <pre>
     * {
     *   "error": {
     *     "code": "ERROR_CODE",
     *     "message": "Human readable message",
     *     "field": "Optional field related to the error"
     *   }
     * }
     * </pre>
     *
     * <p>
     * The frontend should use:
     * <ul>
     *   <li><b>code</b>: to determine error type and UI behavior</li>
     *   <li><b>message</b>: to display or log human-readable information</li>
     *   <li><b>field</b>: to associate validation errors with specific inputs</li>
     * </ul>
     * </p>
     *
     * @param ex the product-related exception containing error details
     * @return a 400 Bad Request response with a standardized error body
     */
    @ExceptionHandler(ProductException.class)
    public ResponseEntity<Map<String, Object>> handleProductException(ProductException ex) {

        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("code", ex.getCode());
        errorBody.put("message", ex.getMessage());
        errorBody.put("field", ex.getField());

        Map<String, Object> response = new HashMap<>();
        response.put("error", errorBody);

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handles exceptions thrown when the request body contains invalid or
     * unreadable data (e.g., incorrect JSON format, invalid enum values,
     * or type mismatches in request payload).
     *
     * <p>
     * Returns a standardized 400 Bad Request response with structured error format.
     * </p>
     *
     * @param ex the exception containing parsing or deserialization errors
     * @return a 400 Bad Request response with structured error details
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {

        Map<String, Object> errorBody = new HashMap<>();

        errorBody.put("code", "INVALID_REQUEST_BODY");
        errorBody.put("field", null);
        errorBody.put("message", "Invalid request body. Please check the provided values");

        Map<String, Object> response = new HashMap<>();
        response.put("error", errorBody);

        return ResponseEntity.badRequest().body(response);
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

    /**
     * Handles user-related exceptions thrown when business rules
     * or validations fail within the User domain.
     *
     * <p>
     * Returns a 400 Bad Request response with a descriptive error message.
     * </p>
     *
     * @param ex the exception containing error details
     * @return a 400 Bad Request response with the error message
     */
    @ExceptionHandler(UserException.class)
    public ResponseEntity<Map<String, String>> handleUserException(UserException ex) {

        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());

        return ResponseEntity.badRequest().body(error);
    }
}
