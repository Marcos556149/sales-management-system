/**
 * Enumeration that defines the ordering criteria based on product quantity sold.
 *
 * <p>
 * Used to specify how products should be sorted when retrieving sales statistics,
 * such as ranking products from most sold to least sold or vice versa.
 * </p>
 *
 * <p>
 * Each enum value includes a human-readable display name intended for UI representation.
 * </p>
 */

package com.marcoscornejos.sales_management_system.model;

import lombok.Getter;

@Getter
public enum ProductQuantityOrderType {
    MOST_TO_LEAST("Most sold → least sold"),
    LEAST_TO_MOST("Least sold → most sold");

    private final String displayName;

    ProductQuantityOrderType(String displayName) {
        this.displayName = displayName;
    }
}
