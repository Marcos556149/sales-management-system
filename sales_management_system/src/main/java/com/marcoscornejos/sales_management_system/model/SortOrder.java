/**
 * Enumeration that defines the sorting direction for ordered results.
 *
 * <p>
 * Used to specify whether data should be sorted in ascending or descending order.
 * This enum is generic and can be applied across different entities and queries
 * within the system.
 * </p>
 *
 * <p>
 * Each enum value includes a human-readable display name intended for UI representation.
 * </p>
 */

package com.marcoscornejos.sales_management_system.model;

import lombok.Getter;

@Getter
public enum SortOrder {
    ASCENDING("Ascending"),
    DESCENDING("Descending");

    private final String displayName;

    SortOrder(String displayName) {
        this.displayName = displayName;
    }
}
