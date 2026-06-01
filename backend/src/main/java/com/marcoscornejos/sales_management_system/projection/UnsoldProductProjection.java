package com.marcoscornejos.sales_management_system.projection;

/**
 * Projection interface used to retrieve products that have no sales
 * within a given filter range.
 *
 * <p>
 * This projection only returns basic product identification data,
 * since no aggregated sales metrics exist for unsold products.
 * </p>
 */
public interface UnsoldProductProjection {

    /**
     * Unique product code that identifies the product.
     */
    String getProductCode();

    /**
     * Name of the product.
     */
    String getProductName();
}