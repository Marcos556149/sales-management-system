package com.marcoscornejos.sales_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Data Transfer Object for returning detailed product information.
 *
 * <p>
 * This DTO represents the complete data of a single product
 * to be displayed in the product detail view.
 * </p>
 */
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetailResponseDTO {

    /**
     * Unique code that identifies the product.
     */
    private String productCode;

    /**
     * Name of the product.
     */
    private String productName;

    /**
     * Price of the product.
     */
    private BigDecimal productPrice;

    /**
     * Unit of measure of the product (e.g., UNITS, KILOGRAMS, LITERS).
     */
    private EnumDTO unitOfMeasure;

    /**
     * Current status of the product (e.g., ACTIVE, INACTIVE).
     */
    private EnumDTO productStatus;

    /**
     * Available stock quantity.
     */
    private BigDecimal productStock;



}
