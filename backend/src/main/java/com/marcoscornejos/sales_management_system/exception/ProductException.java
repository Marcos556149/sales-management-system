package com.marcoscornejos.sales_management_system.exception;

/**
 * Base exception for all product-related errors.
 *
 * <p>
 * This exception represents any issue that occurs within the Product domain.
 * Specific product exceptions should extend this class.
 * </p>
 */
public class ProductException extends RuntimeException {

    public ProductException(String message) {
        super(message);
    }

    public ProductException(String message, Throwable cause) {
        super(message, cause);
    }
}
