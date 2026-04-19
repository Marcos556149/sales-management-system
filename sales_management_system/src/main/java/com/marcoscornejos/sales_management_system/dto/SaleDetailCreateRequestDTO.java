package com.marcoscornejos.sales_management_system.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO used to register a sale detail as part of a new sale.
 *
 * <p>
 * Represents a product included in the sale and the quantity
 * requested by the user.
 * </p>
 *
 * <p>
 * The sale detail identifier, unit price, and subtotal are not included
 * because they are automatically resolved by the system during sale registration.
 * </p>
 */
@Getter
@Setter
public class SaleDetailCreateRequestDTO {

    /**
     * Unique code of the product to associate with the sale.
     *
     * <p>
     * Must not be blank and must not exceed 100 characters.
     * </p>
     */
    @NotBlank(message = "Product code is required")
    @Size(max = 100, message = "Product code must not exceed 100 characters")
    private String productCode;

    /**
     * Quantity requested for the selected product.
     *
     * <p>
     * Must be greater than 0 and respect the database constraint
     * NUMERIC(12,2), allowing up to 10 integer digits and 2 decimal places.
     * </p>
     *
     * <p>
     * Compatibility with the product unit of measure
     * (for example, no decimals for UNIT products)
     * is validated in the business layer.
     * </p>
     */
    @NotNull(message = "Product quantity is required")
    @DecimalMin(value = "0.01", inclusive = true, message = "Product quantity must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Product quantity must have up to 10 digits and 2 decimals")
    private BigDecimal productQuantity;

}