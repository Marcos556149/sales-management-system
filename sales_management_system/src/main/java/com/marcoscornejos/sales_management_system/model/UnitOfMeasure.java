/**
 * Enumeration that defines the unit of measure for products.
 *
 * <p>
 * Used to represent how a product is quantified in inventory and sales,
 * such as by units, kilograms, or liters.
 * </p>
 *
 * <p>
 * This enum is essential for correctly handling product quantities,
 * especially when integrating with features like weighted products
 * (e.g., scales) or bulk sales.
 * </p>
 *
 * <p>
 * Each enum value includes a human-readable display name intended for UI representation.
 * </p>
 */

package com.marcoscornejos.sales_management_system.model;

import lombok.Getter;

@Getter
public enum UnitOfMeasure {
    UNITS("Units"),
    KILOGRAMS("Kilograms"),
    LITERS("Liters");

    private final String displayName;

    UnitOfMeasure(String displayName) {
        this.displayName = displayName;
    }
}
