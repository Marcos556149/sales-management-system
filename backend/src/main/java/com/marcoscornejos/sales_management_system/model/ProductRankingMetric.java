/**
 * Enumeration that defines the metric used for product ranking in sales statistics.
 *
 * <p>
 * This enum determines how products are evaluated and ordered within statistical
 * reports, allowing ranking based on either quantity sold or revenue generated.
 * </p>
 *
 * <p>
 * Each enum value includes a human-readable display name intended for UI representation.
 * </p>
 */

package com.marcoscornejos.sales_management_system.model;

import lombok.Getter;

@Getter
public enum ProductRankingMetric {
    QUANTITY_SOLD("Quantity Sold"),
    REVENUE_GENERATED("Revenue Generated");

    private final String displayName;

    ProductRankingMetric(String displayName) {
        this.displayName = displayName;
    }
}
