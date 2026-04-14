package com.marcoscornejos.sales_management_system.exception;

/**
 * Exception thrown when a sale cannot be found.
 *
 * <p>
 * Typically used when a sale lookup by ID or filters
 * does not return any result.
 * </p>
 */
public class SaleNotFoundException extends SaleException {

    public SaleNotFoundException(String message) {
        super(message);
    }
}