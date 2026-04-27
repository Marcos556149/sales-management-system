package com.marcoscornejos.sales_management_system.exception;

/**
 * Exception thrown when a user cannot be found.
 *
 * <p>
 * Typically used when a user lookup by ID, username,
 * or other criteria does not return any result.
 * </p>
 *
 * <p>
 * This exception uses the standardized error format:
 * </p>
 *
 * <pre>
 * {
 *   "code": "USER_NOT_FOUND",
 *   "message": "Human readable message",
 *   "field": null
 * }
 * </pre>
 */
public class UserNotFoundException extends UserException {

    private static final String CODE = "USER_NOT_FOUND";

    /**
     * Creates an exception when a user is not found.
     *
     * @param message human-readable error message
     */
    public UserNotFoundException(String message) {
        super(CODE, message, null);
    }
}