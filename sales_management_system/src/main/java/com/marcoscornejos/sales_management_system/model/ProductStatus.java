/**
 * Enumeration that represents the status of a product.
 *
 * <p>
 * Used to indicate whether a product is active (available for operations)
 * or inactive (logically disabled). The ALL option is typically used for
 * filtering purposes when retrieving products regardless of their status.
 * </p>
 *
 * <p>
 * This enum is commonly used in conjunction with logical deletion mechanisms
 * and filtering operations in queries.
 * </p>
 *
 * <p>
 * Each enum value includes a human-readable display name intended for UI representation.
 * </p>
 */

package com.marcoscornejos.sales_management_system.model;

import lombok.Getter;

@Getter
public enum ProductStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive"),
    ALL("All");

    private final String displayName;

    ProductStatus(String displayName) {
        this.displayName = displayName;
    }
}
