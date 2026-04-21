package com.marcoscornejos.sales_management_system.exception;

/**
 * Exception thrown when a user cannot be found.
 *
 * <p>
 * Typically used when a user lookup by ID, username,
 * or other criteria does not return any result.
 * </p>
 */
public class UserNotFoundException extends UserException {

    public UserNotFoundException(String message) {
        super(message);
    }
}