package com.marcoscornejos.sales_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO that represents the total number of sales
 * calculated based on the selected filters.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TotalSalesResponseDTO {

    /**
     * Total number of matching sales.
     */
    private Long totalSales;
}
