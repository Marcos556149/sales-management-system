package com.marcoscornejos.sales_management_system.projection;

import java.math.BigDecimal;

/**
 * Projection used for time-series statistical aggregations.
 *
 * <p>
 * Represents a single aggregated chart point,
 * including a time label and its associated value.
 * </p>
 */
public interface TimeSeriesProjection {

    /**
     * Time label representing the aggregation period
     * (day, month, or year).
     *
     * @return chart label
     */
    String getLabel();

    /**
     * Aggregated value for the time period.
     *
     * @return aggregated metric value
     */
    BigDecimal getValue();
}