package com.marcoscornejos.sales_management_system.exception;

/**
 * Exception thrown when a product cannot be found.
 *
 * <p>
 * Typically used when a product lookup by ID or code
 * does not return any result.
 * </p>
 */
public class ProductNotFoundException extends ProductException {

    public ProductNotFoundException(String message) {
        super(message);
    }
}
