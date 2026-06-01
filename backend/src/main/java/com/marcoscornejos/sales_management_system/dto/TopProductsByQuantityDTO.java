package com.marcoscornejos.sales_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Represents a top-performing product ranked by quantity sold.
 *
 * <p>
 * This DTO is used for chart visualizations that display
 * the products with the highest number of sold units
 * within the selected filter range.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TopProductsByQuantityDTO {

    /**
     * Unique product code that identifies the product.
     */
    private String productCode;

    /**
     * Name of the product.
     */
    private String productName;

    /**
     * Total quantity sold (can be fractional depending on unit type,
     * e.g., kilograms, liters, etc.).
     */
    private BigDecimal quantitySold;
}
