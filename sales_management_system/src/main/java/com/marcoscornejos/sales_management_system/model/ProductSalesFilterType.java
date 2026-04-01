/**
 * Enumeration that defines filtering criteria for products based on their sales status.
 *
 * <p>
 * Used to determine whether to retrieve only sold products, unsold products,
 * or all products regardless of their sales history.
 * </p>
 *
 * <p>
 * Each enum value includes a human-readable display name intended for UI representation.
 * </p>
 */

package com.marcoscornejos.sales_management_system.model;

import lombok.Getter;

@Getter
public enum ProductSalesFilterType {
    SOLD("Sold products"),
    UNSOLD("Unsold products"),
    ALL("All");

    private final String displayName;

    ProductSalesFilterType(String displayName) {
        this.displayName = displayName;
    }
}
