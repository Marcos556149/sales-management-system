/**
 * Enumeration that defines the stock level filter options for products.
 *
 * <p>
 * Used to classify and filter products according to their current stock
 * condition in relation to the configured minimum stock level.
 * </p>
 *
 * <p>
 * This enum is especially useful in product listing and inventory views,
 * allowing users to quickly identify products with normal stock levels,
 * low stock, or no stock available.
 * </p>
 *
 * <p>
 * Each enum value includes a human-readable display name intended for UI representation.
 * </p>
 */

package com.marcoscornejos.sales_management_system.model;

import lombok.Getter;

@Getter
public enum StockLevelFilter {

    ALL("All Stock"),
    NORMAL("Normal Stock"),
    LOW("Low Stock"),
    OUT_OF_STOCK("Out of Stock");

    private final String displayName;

    StockLevelFilter(String displayName) {
        this.displayName = displayName;
    }
}