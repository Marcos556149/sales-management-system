package com.marcoscornejos.sales_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO used to represent product data when returning information to the client.
 *
 *
 * <p>It includes product identification, name, pricing, status, and stock information.</p>
 */

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductListResponseDTO {

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
     * Current status of the product (e.g., ACTIVE, INACTIVE).
     */
    private EnumDTO productStatus;

    /**
     * Available stock quantity of the product.
     */
    private BigDecimal productStock;

    /**
     * Unit of measure associated with the stock (e.g., kg, u).
     */
    private EnumDTO unitOfMeasure;
}
