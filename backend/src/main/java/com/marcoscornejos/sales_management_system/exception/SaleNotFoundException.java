package com.marcoscornejos.sales_management_system.exception;

/**
 * Exception thrown when a sale cannot be found.
 *
 * <p>
 * Typically used when a sale lookup by ID or filters
 * does not return any result.
 * </p>
 *
 * <p>
 * This exception uses the standardized error format:
 * </p>
 *
 * <pre>
 * {
 *   "code": "SALE_NOT_FOUND",
 *   "message": "Human readable message",
 *   "field": null
 * }
 * </pre>
 */
public class SaleNotFoundException extends SaleException {

    private static final String CODE = "SALE_NOT_FOUND";

    /**
     * Creates an exception when a sale is not found.
     *
     * @param message human-readable error message
     */
    public SaleNotFoundException(String message) {
        super(CODE, message, null);
    }
}