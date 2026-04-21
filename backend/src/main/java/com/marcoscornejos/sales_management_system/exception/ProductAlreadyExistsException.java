package com.marcoscornejos.sales_management_system.exception;

/**
 * Exception thrown when attempting to create a product
 * that already exists.
 *
 * <p>
 * Commonly used when a product with the same unique
 * identifier (e.g., code) is already present.
 * </p>
 */
public class ProductAlreadyExistsException extends ProductException {

    public ProductAlreadyExistsException(String message) {
        super(message);
    }
}
