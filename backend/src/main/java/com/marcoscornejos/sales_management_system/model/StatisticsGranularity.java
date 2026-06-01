/**
 * Enumeration that defines the aggregation granularity
 * used for time-based sales statistics and charts.
 *
 * <p>
 * Granularity determines how statistical data is grouped
 * when generating analytics such as revenue over time
 * and sales count over time.
 * </p>
 *
 * <p>
 * The system automatically selects the most appropriate granularity
 * based on the selected date range in order to:
 * <ul>
 *   <li>Improve chart readability</li>
 *   <li>Avoid excessive data points in large ranges</li>
 *   <li>Maintain consistent visual aggregation</li>
 * </ul>
 * </p>
 *
 * <p>
 * Granularity rules:
 * <ul>
 *   <li>{@code HOUR}: used for single-day ranges</li>
 *   <li>{@code DAY}: used for short ranges (up to 31 days)</li>
 *   <li>{@code MONTH}: used for medium ranges (up to 365 days)</li>
 *   <li>{@code YEAR}: used for long ranges (more than 365 days)</li>
 * </ul>
 * </p>
 */
package com.marcoscornejos.sales_management_system.model;

public enum StatisticsGranularity {

    /**
     * Groups statistics by hour.
     */
    HOUR,

    /**
     * Groups statistics by day.
     */
    DAY,

    /**
     * Groups statistics by month.
     */
    MONTH,

    /**
     * Groups statistics by year.
     */
    YEAR
}
