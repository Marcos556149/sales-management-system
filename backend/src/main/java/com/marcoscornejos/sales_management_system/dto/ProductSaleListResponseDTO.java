package com.marcoscornejos.sales_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO used to represent products available for sale.
 *
 * <p>
 * Includes the essential product data required by the sale registration
 * interface, excluding administrative fields such as product status.
 * </p>
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductSaleListResponseDTO {

    /**
     * Unique product code that identifies the product.
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
     * Available stock quantity of the product.
     */
    private BigDecimal productStock;

    /**
     * Minimum stock threshold configured for the product.
     */
    private BigDecimal minimumStock;

    /**
     * Unit of measure associated with the stock (e.g., kg, u).
     */
    private EnumDTO unitOfMeasure;
}
