package com.marcoscornejos.sales_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * DTO that contains time-series statistical data
 * for sales analytics charts.
 *
 * <p>
 * This DTO provides:
 * <ul>
 *   <li>Total revenue evolution over time</li>
 *   <li>Total number of sales over time</li>
 * </ul>
 * </p>
 *
 * <p>
 * Data aggregation granularity is automatically determined
 * based on the selected date range.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SalesTimeSeriesResponseDTO {

    /**
     * Time-series data representing revenue evolution over time.
     */
    private List<TimeSeriesPointDTO> revenueOverTime;

    /**
     * Time-series data representing number of sales over time.
     */
    private List<TimeSeriesPointDTO> salesOverTime;
}
