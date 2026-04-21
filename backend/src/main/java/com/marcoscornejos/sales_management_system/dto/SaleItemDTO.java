package com.marcoscornejos.sales_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO representing each product included in the sale.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SaleItemDTO {

    /**
     * Product code.
     */
    private String productCode;

    /**
     * Product name.
     */
    private String productName;

    /**
     * Quantity sold.
     */
    private BigDecimal productQuantity;

    /**
     * Unit of measure of the product(abbreviation).(e.g., u, kg, lt).
     */
    private EnumDTO unitOfMeasure;

    /**
     * Price of the product at the time of sale.
     */
    private BigDecimal salePrice;

    /**
     * Subtotal amount for this product.
     */
    private BigDecimal subtotal;
}
