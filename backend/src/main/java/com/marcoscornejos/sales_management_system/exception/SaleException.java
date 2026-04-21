package com.marcoscornejos.sales_management_system.exception;

/**
 * Base exception for all sale-related errors.
 *
 * <p>
 * This exception represents any issue that occurs within the Sale domain.
 * Specific sale exceptions should extend this class.
 * </p>
 */
public class SaleException extends RuntimeException {

    public SaleException(String message) {
        super(message);
    }

    public SaleException(String message, Throwable cause) {
        super(message, cause);
    }
}