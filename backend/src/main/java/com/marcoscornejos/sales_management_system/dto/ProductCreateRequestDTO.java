package com.marcoscornejos.sales_management_system.dto;

import com.marcoscornejos.sales_management_system.model.UnitOfMeasure;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO used to register a new product in the system.
 *
 * <p>
 * Contains all required information needed to create a product.
 * The product status is not included because it is automatically
 * set to ACTIVE by the system.
 * </p>
 */
@Getter
@Setter
public class ProductCreateRequestDTO {

    /**
     * Unique code of the product.
     *
     * <p>
     * Must not be blank and must not exceed 100 characters.
     * </p>
     */
    @NotBlank(message = "Product code is required")
    @Size(max = 100, message = "Product code must not exceed 100 characters")
    private String productCode;

    /**
     * Name of the product.
     *
     * <p>
     * Must not be blank and must not exceed 100 characters.
     * </p>
     */
    @NotBlank(message = "Product name is required")
    @Size(max = 100, message = "Product name must not exceed 100 characters")
    private String productName;

    /**
     * Unit price of the product.
     *
     * <p>
     * Must be greater than or equal to 0 and respect the database constraint
     * NUMERIC(12,2), allowing up to 10 integer digits and 2 decimal places.
     * </p>
     */
    @NotNull(message = "Product price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Product price must be greater than or equal to 0")
    @Digits(integer = 10, fraction = 2, message = "Product price must have up to 10 digits and 2 decimals")
    private BigDecimal productPrice;

    /**
     * Available stock quantity of the product.
     *
     * <p>
     * Must be greater than or equal to 0 and respect the database constraint
     * NUMERIC(12,2), allowing up to 10 integer digits and 2 decimal places.
     * </p>
     */
    @NotNull(message = "Product stock is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Product stock must be greater than or equal to 0")
    @Digits(integer = 10, fraction = 2, message = "Product stock must have up to 10 digits and 2 decimals")
    private BigDecimal productStock;

    /**
     * Unit of measure of the product (e.g., UNITS, KILOGRAMS, LITERS).
     */
    @NotNull(message = "Unit of measure is required")
    private UnitOfMeasure unitOfMeasure;

}