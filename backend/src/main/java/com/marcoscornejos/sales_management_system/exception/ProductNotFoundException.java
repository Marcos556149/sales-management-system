package com.marcoscornejos.sales_management_system.exception;

/**
 * Exception thrown when a product cannot be found.
 *
 * <p>
 * Typically used when a product lookup by ID or code
 * does not return any result.
 * </p>
 *
 * <p>
 * This exception uses the standardized error format:
 * </p>
 *
 * <pre>
 * {
 *   "code": "PRODUCT_NOT_FOUND",
 *   "message": "Human readable message",
 *   "field": null
 * }
 * </pre>
 */
public class ProductNotFoundException extends ProductException {

    private static final String CODE = "PRODUCT_NOT_FOUND";

    /**
     * Creates an exception when a product is not found.
     *
     * @param message human-readable error message
     */
    public ProductNotFoundException(String message) {
        super(CODE, message, null);
    }
}
