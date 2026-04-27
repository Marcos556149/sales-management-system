package com.marcoscornejos.sales_management_system.exception;

/**
 * Exception thrown when provided sale data is invalid.
 *
 * <p>
 * This includes invalid input values, failed validations,
 * or business rule violations related to sale operations.
 * </p>
 *
 * <p>
 * This exception uses the standardized error format:
 * </p>
 *
 * <pre>
 * {
 *   "code": "INVALID_SALE_DATA",
 *   "message": "Human readable message",
 *   "field": "optionalFieldName"
 * }
 * </pre>
 */
public class InvalidSaleDataException extends SaleException {

    private static final String CODE = "INVALID_SALE_DATA";

    /**
     * Creates a validation exception without a specific field.
     *
     * @param message human-readable error message
     */
    public InvalidSaleDataException(String message) {
        super(CODE, message, null);
    }

    /**
     * Creates an exception for invalid sale data.
     *
     * @param message human-readable error message
     * @param field related field name (can be null)
     */
    public InvalidSaleDataException(String message, String field) {
        super(CODE, message, field);
    }
}