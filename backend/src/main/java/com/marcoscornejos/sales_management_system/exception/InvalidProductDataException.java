package com.marcoscornejos.sales_management_system.exception;

/**
 * Exception thrown when product input data is invalid or violates
 * business validation rules.
 *
 * <p>
 * Example cases:
 * <ul>
 *   <li>Invalid pagination parameters</li>
 *   <li>Negative prices or stock</li>
 *   <li>Invalid field values</li>
 * </ul>
 * </p>
 */
public class InvalidProductDataException extends ProductException {

    private static final String CODE = "INVALID_PRODUCT_DATA";

    /**
     * Creates a validation exception for a product field.
     *
     * @param message human-readable error message
     * @param field field that caused the validation error
     */
    public InvalidProductDataException(String message, String field) {
        super(CODE, message, field);
    }

    /**
     * Creates a validation exception without a specific field.
     *
     * @param message human-readable error message
     */
    public InvalidProductDataException(String message) {
        super(CODE, message, null);
    }
}