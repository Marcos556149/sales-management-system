package com.marcoscornejos.sales_management_system.exception;

/**
 * Exception for authentication errors.
 */
public class AuthException extends RuntimeException {
    public AuthException(String message) {
        super(message);
    }
}