package com.marcoscornejos.sales_management_system.exception;

/**
 * Exception thrown when the system configuration cannot be found.
 *
 * <p>
 * This should never happen in a correctly initialized system,
 * since the configuration must exist as a single global record.
 * </p>
 *
 * <pre>
 * {
 *   "code": "SYSTEM_CONFIGURATION_NOT_FOUND",
 *   "message": "Human readable message",
 *   "field": null
 * }
 * </pre>
 */
public class SystemConfigurationNotFoundException extends SystemConfigurationException {

    private static final String CODE = "SYSTEM_CONFIGURATION_NOT_FOUND";

    /**
     * Creates an exception when system configuration is not found.
     *
     * @param message human-readable error message
     */
    public SystemConfigurationNotFoundException(String message) {
        super(CODE, message, null);
    }
}
