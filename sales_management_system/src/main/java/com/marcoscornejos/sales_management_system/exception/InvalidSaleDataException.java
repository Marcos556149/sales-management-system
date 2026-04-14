package com.marcoscornejos.sales_management_system.exception;

/**
 * Exception thrown when provided sale data is invalid.
 *
 * <p>
 * This includes invalid input values, failed validations,
 * or business rule violations related to sale operations.
 * </p>
 */
public class InvalidSaleDataException extends SaleException {

    public InvalidSaleDataException(String message) {
        super(message);
    }
}
