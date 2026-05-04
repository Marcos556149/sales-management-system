package com.marcoscornejos.sales_management_system.exception;

import lombok.Getter;

/**
 * Base exception for all system configuration related errors.
 *
 * <p>
 * Provides a standardized structure for handling configuration errors
 * across the application.
 * </p>
 *
 * <p>
 * Each exception includes:
 * <ul>
 *   <li><b>code</b>: machine-readable error identifier</li>
 *   <li><b>message</b>: human-readable description of the error</li>
 *   <li><b>field</b>: optional field related to validation errors</li>
 * </ul>
 * </p>
 */
@Getter
public class SystemConfigurationException extends RuntimeException {

    private final String code;
    private final String field;

    /**
     * Creates a new SystemConfigurationException.
     *
     * @param code machine-readable error code
     * @param message human-readable error message
     * @param field optional field related to the error (can be null)
     */
    public SystemConfigurationException(String code, String message, String field) {
        super(message);
        this.code = code;
        this.field = field;
    }
}
