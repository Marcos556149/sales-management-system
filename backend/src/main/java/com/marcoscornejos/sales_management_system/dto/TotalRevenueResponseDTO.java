package com.marcoscornejos.sales_management_system.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO that represents the total revenue calculated
 * based on the selected filters.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TotalRevenueResponseDTO {

    /**
     * Sum of all sales amounts.
     */
    private BigDecimal totalRevenue;
}