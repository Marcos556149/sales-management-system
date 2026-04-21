package com.marcoscornejos.sales_management_system.exception;

/**
 * Exception thrown when provided product data is invalid.
 *
 * <p>
 * This includes invalid input values, failed validations,
 * or business rule violations related to product operations.
 * </p>
 */
public class InvalidProductDataException extends ProductException {

    public InvalidProductDataException(String message) {
        super(message);
    }
}
