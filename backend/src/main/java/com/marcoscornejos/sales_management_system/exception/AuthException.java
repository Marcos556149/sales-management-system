package com.marcoscornejos.sales_management_system.exception;

import lombok.Getter;

/**
 * Exception for authentication-related errors.
 *
 * <p>
 * Used when authentication or authorization rules are violated,
 * such as invalid credentials or unauthorized access attempts.
 * </p>
 */
@Getter
public class AuthException extends RuntimeException {

    private final String code;
    private final String field;

    /**
     * Creates a new AuthException.
     *
     * @param code machine-readable error code
     * @param message human-readable error message
     * @param field optional field related to the error (can be null)
     */
    public AuthException(String code, String message, String field) {
        super(message);
        this.code = code;
        this.field = field;
    }
}