package com.marcoscornejos.sales_management_system.dto;

import com.marcoscornejos.sales_management_system.model.UnitOfMeasure;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO used to update an existing product in the system.
 *
 * <p>
 * Contains all editable product fields. The product status and code
 * is not included because it cannot be modified in this process.
 * </p>
 */
@Getter
@Setter
public class ProductUpdateRequestDTO {

    /**
     * Updated name of the product.
     *
     * <p>
     * Must not be blank and must not exceed 100 characters.
     * </p>
     */
    @NotBlank(message = "Product name is required")
    @Size(max = 100, message = "Product name must not exceed 100 characters")
    private String productName;

    /**
     * Updated unit price of the product.
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
     * Updated available stock quantity.
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
     * Updated unit of measure of the product.
     *
     * <p>
     * Must not be null and must be a valid enum value.
     * </p>
     */
    @NotNull(message = "Unit of measure is required")
    private UnitOfMeasure unitOfMeasure;

}
