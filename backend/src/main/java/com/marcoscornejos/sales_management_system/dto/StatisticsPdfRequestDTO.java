package com.marcoscornejos.sales_management_system.dto;

import com.marcoscornejos.sales_management_system.model.ProductQuantityOrderType;
import com.marcoscornejos.sales_management_system.model.ProductRankingMetric;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Request used to generate a sales statistics PDF report.
 *
 * <p>
 * This request contains:
 * <ul>
 *     <li>Global statistics filters</li>
 *     <li>Report section selection</li>
 *     <li>Product ranking configuration</li>
 *     <li>Detailed product list limits</li>
 * </ul>
 * </p>
 *
 * <p>
 * The provided filters determine the dataset used to generate
 * the report. All included sections are calculated exclusively
 * from the sales and products matching these filters.
 * </p>
 *
 * <p>
 * Default behavior:
 * <ul>
 *     <li>User → All Users</li>
 *     <li>Date range → Current date</li>
 *     <li>Sales Information section → included</li>
 *     <li>Product Information section → included</li>
 *     <li>Ranking metric → Revenue Generated</li>
 *     <li>Ranking order → Most sold → least sold</li>
 *     <li>Sold products limit → 20</li>
 *     <li>Unsold products limit → 20</li>
 * </ul>
 * </p>
 *
 * <p>
 * At least one report section must be selected.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsPdfRequestDTO {

    /**
     * Optional user filter.
     *
     * <p>
     * When null, statistics are generated using
     * sales from all users.
     * </p>
     */
    private Long userId;

    /**
     * Optional start date filter.
     *
     * <p>
     * Defines the beginning of the date range
     * used to calculate report statistics.
     * </p>
     */
    private LocalDate startDate;

    /**
     * Optional end date filter.
     *
     * <p>
     * Defines the end of the date range
     * used to calculate report statistics.
     * </p>
     */
    private LocalDate endDate;

    /**
     * Indicates whether the Sales Information section
     * must be included in the generated report.
     *
     * <p>
     * Default value: true.
     * </p>
     */
    private Boolean includeSalesInformation;

    /**
     * Indicates whether the Product Information section
     * must be included in the generated report.
     *
     * <p>
     * Default value: true.
     * </p>
     */
    private Boolean includeProductInformation;

    /**
     * Metric used to rank sold products
     * in the Product Ranking List.
     *
     * <p>
     * Available values:
     * <ul>
     *     <li>QUANTITY_SOLD</li>
     *     <li>REVENUE_GENERATED</li>
     * </ul>
     * </p>
     *
     * <p>
     * Default value: REVENUE_GENERATED.
     * </p>
     */
    private ProductRankingMetric metric;

    /**
     * Ordering applied to the Product Ranking List.
     *
     * <p>
     * Available values:
     * <ul>
     *     <li>MOST_TO_LEAST</li>
     *     <li>LEAST_TO_MOST</li>
     * </ul>
     * </p>
     *
     * <p>
     * Default value: MOST_TO_LEAST.
     * </p>
     */
    private ProductQuantityOrderType order;

    /**
     * Maximum number of products included
     * in the sold products ranking list.
     *
     * <p>
     * Allowed values:
     * <ul>
     *     <li>10</li>
     *     <li>20</li>
     *     <li>50</li>
     *     <li>100</li>
     * </ul>
     * </p>
     *
     * <p>
     * Default value: 20.
     * </p>
     */
    private Integer soldProductsLimit;

    /**
     * Maximum number of products included
     * in the unsold products list.
     *
     * <p>
     * Allowed values:
     * <ul>
     *     <li>10</li>
     *     <li>20</li>
     *     <li>50</li>
     *     <li>100</li>
     * </ul>
     * </p>
     *
     * <p>
     * Default value: 20.
     * </p>
     */
    private Integer unsoldProductsLimit;

}
