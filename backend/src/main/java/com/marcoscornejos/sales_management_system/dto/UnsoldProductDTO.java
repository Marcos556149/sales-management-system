package com.marcoscornejos.sales_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a product that had no sales within the selected filter range.
 *
 * <p>
 * This DTO is used to identify products with zero sales activity,
 * helping analyze inventory inefficiencies, low-demand products,
 * or potential stock issues.
 * </p>
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UnsoldProductDTO {

    /**
     * Unique product code that identifies the product.
     */
    private String productCode;

    /**
     * Name of the product.
     */
    private String productName;
}