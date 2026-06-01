package com.marcoscornejos.sales_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO that represents aggregated sales statistics based on selected filters.
 *
 * <p>
 * This object groups all analytical information such as sales KPIs,
 * product rankings, and unsold product data.
 * </p>
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SalesStatisticsResponseDTO {

    /**
     * Aggregated sales information (KPIs and time-based metrics).
     */
    private SalesInfoDTO salesInfo;

    /**
     * Product-related statistics (sold and unsold products).
     */
    private ProductStatisticsDTO productStatistics;
}
