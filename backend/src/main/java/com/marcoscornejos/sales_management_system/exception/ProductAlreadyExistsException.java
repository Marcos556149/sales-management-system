package com.marcoscornejos.sales_management_system.exception;

/**
 * Exception thrown when attempting to create a product
 * that already exists.
 *
 * <p>
 * Commonly used when a product with the same unique
 * identifier (e.g., code) is already present.
 * </p>
 *
 * <p>
 * This exception follows the standardized error format:
 * </p>
 *
 * <pre>
 * {
 *   "code": "PRODUCT_ALREADY_EXISTS",
 *   "message": "Human readable message",
 *   "field": null
 * }
 * </pre>
 */
public class ProductAlreadyExistsException extends ProductException {

    private static final String CODE = "PRODUCT_ALREADY_EXISTS";

    /**
     * Creates an exception when a product already exists.
     *
     * @param message human-readable error message
     */
    public ProductAlreadyExistsException(String message) {
        super(CODE, message, null);
    }
}
