package com.marcoscornejos.sales_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * DTO that contains top-performing product statistics
 * for analytics charts.
 *
 * <p>
 * This DTO provides:
 * <ul>
 *   <li>Top 10 products by quantity sold</li>
 *   <li>Top 10 products by revenue generated</li>
 * </ul>
 * </p>
 *
 * <p>
 * All rankings are calculated using only the sales
 * that match the selected filters.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TopProductsResponseDTO {

    /**
     * Top 10 products ranked by quantity sold.
     */
    private List<TopProductsByQuantityDTO> topProductsByQuantity;

    /**
     * Top 10 products ranked by revenue generated.
     */
    private List<TopProductsByRevenueDTO> topProductsByRevenue;
}