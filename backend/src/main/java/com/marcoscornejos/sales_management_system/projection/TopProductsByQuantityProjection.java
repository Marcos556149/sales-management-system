package com.marcoscornejos.sales_management_system.projection;

import java.math.BigDecimal;

/**
 * Projection interface used to retrieve top-performing products
 * ranked by quantity sold.
 *
 * <p>
 * Provides only the fields required for quantity-based
 * product ranking charts.
 * </p>
 */
public interface TopProductsByQuantityProjection {

    /**
     * Unique product code that identifies the product.
     *
     * @return product code
     */
    String getProductCode();

    /**
     * Name of the product.
     *
     * @return product name
     */
    String getProductName();

    /**
     * Total quantity sold (supports fractional values depending on unit type).
     */
    BigDecimal getQuantitySold();
}