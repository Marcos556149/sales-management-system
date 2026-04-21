/**
 * Enumeration that defines the sorting direction options for chronological data.
 *
 * <p>
 * Used to specify the order in which time-based records are sorted,
 * such as sales history, transactions, or reports.
 * </p>
 *
 * <p>
 * This enum is especially useful in listing operations where users need
 * to view the newest records first or the oldest records first.
 * </p>
 *
 * <p>
 * Each enum value includes a human-readable display name intended for UI representation.
 * </p>
 */

package com.marcoscornejos.sales_management_system.model;

import lombok.Getter;

@Getter
public enum SortDirection {

    NEWEST_FIRST("Most Recent First"),
    OLDEST_FIRST("Oldest First");

    private final String displayName;

    SortDirection(String displayName) {
        this.displayName = displayName;
    }
}
