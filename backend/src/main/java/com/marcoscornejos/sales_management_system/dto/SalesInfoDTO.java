package com.marcoscornejos.sales_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * Contains global sales KPIs and time-based analytics for a selected filter range.
 *
 * <p>
 * This DTO represents aggregated business metrics derived from sales data,
 * including total revenue, number of sales, average ticket value,
 * peak performance hours, and time-series data used for chart visualization.
 * </p>
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SalesInfoDTO {

    /**
     * Total revenue generated from all sales within the selected filter range.
     */
    private BigDecimal totalRevenue;

    /**
     * Total number of sales transactions within the selected filter range.
     */
    private Long totalSales;

    /**
     * Average ticket value calculated as totalRevenue divided by totalSales.
     */
    private BigDecimal averageTicket;

    /**
     * Hour of the day with the highest revenue generation.
     */
    private String highestRevenueHour;

    /**
     * Hour of the day with the highest number of sales transactions.
     */
    private String highestSalesHour;

    /**
     * Time-series data representing revenue evolution over time.
     * Each point contains a time label and the corresponding revenue value.
     */
    private List<TimeSeriesPointDTO> revenueOverTime;

    /**
     * Time-series data representing number of sales over time.
     * Each point contains a time label and the corresponding sales count.
     */
    private List<TimeSeriesPointDTO> salesOverTime;
}
