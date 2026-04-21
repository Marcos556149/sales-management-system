package com.marcoscornejos.sales_management_system.exception;

/**
 * Base exception for all user-related errors.
 *
 * <p>
 * This exception represents any issue that occurs within the User domain.
 * Specific user exceptions should extend this class.
 * </p>
 */
public class UserException extends RuntimeException {

    public UserException(String message) {
        super(message);
    }

    public UserException(String message, Throwable cause) {
        super(message, cause);
    }
}
