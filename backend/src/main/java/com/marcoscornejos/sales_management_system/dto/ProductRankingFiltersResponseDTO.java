package com.marcoscornejos.sales_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Response DTO that contains available filter options
 * for product ranking statistics.
 *
 * <p>
 * This DTO provides enum-based configuration data
 * used by the frontend to dynamically populate
 * ranking filter selectors.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRankingFiltersResponseDTO {

    /**
     * Available product ranking metrics.
     *
     * <p>
     * Defines how products are ranked:
     * by quantity sold or by revenue generated.
     * </p>
     */
    private List<EnumDTO> metricOptions;

    /**
     * Available ordering options for product ranking.
     *
     * <p>
     * Defines whether products are sorted from:
     * most to least sold, or least to most sold.
     * </p>
     */
    private List<EnumDTO> orderOptions;
}
