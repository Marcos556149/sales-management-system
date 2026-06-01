package com.marcoscornejos.sales_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Represents a single data point in a time-based chart series.
 *
 * <p>
 * This DTO is used to build time-series visualizations such as revenue over time
 * and number of sales over time. The label typically represents a time unit
 * (hour, day, month, or year depending on the selected granularity),
 * and the value represents the aggregated metric for that period.
 * </p>
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TimeSeriesPointDTO {

    /**
     * Label representing the time unit of the data point
     * (e.g., hour, day, month, or year depending on granularity).
     */
    private String label;

    /**
     * Aggregated value for the given time unit (e.g., revenue or number of sales).
     */
    private BigDecimal value;
}
