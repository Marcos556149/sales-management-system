package com.marcoscornejos.sales_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Contains aggregated statistical information about products based on sales performance
 * within a selected filter range.
 *
 * <p>
 * This DTO groups different product analytics views, including top-performing products,
 * dynamic rankings, and products with no sales activity. It is used as part of the
 * sales statistics dashboard to provide insights into product behavior.
 * </p>
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductStatisticsDTO {

    /**
     * Top 10 products by quantity sold.
     */
    private List<SoldProductDTO> topProductsByQuantity;

    /**
     * Top 10 products by revenue generated.
     */
    private List<SoldProductDTO> topProductsByRevenue;

    /**
     * Full product ranking based on selected metric and order.
     */
    private List<SoldProductDTO> productRanking;

    /**
     * Products with no sales in selected range.
     */
    private List<UnsoldProductDTO> unsoldProducts;
}
