package com.marcoscornejos.sales_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO that contains all information required
 * to generate a sales statistics PDF report.
 *
 * <p>
 * This DTO acts as an intermediate data structure
 * between statistics retrieval and PDF generation.
 * </p>
 *
 * <p>
 * All report information is pre-calculated and
 * prepared before the PDF document is created,
 * allowing the PDF generation layer to focus
 * exclusively on document rendering.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsPdfDataDTO {

    /**
     * Report title displayed in the PDF.
     */
    private String reportTitle;

    /**
     * Date and time when the report was generated.
     */
    private LocalDateTime generationDateTime;

    /**
     * Selected user displayed in the report.
     *
     * <p>
     * Possible values include:
     * <ul>
     *     <li>"All Users"</li>
     *     <li>A specific user name</li>
     * </ul>
     * </p>
     */
    private String selectedUser;

    /**
     * Selected report start date.
     */
    private LocalDate startDate;

    /**
     * Selected report end date.
     */
    private LocalDate endDate;

    /*
     * Included sections
     */

    /**
     * Indicates whether the Sales Information
     * section is included in the report.
     */
    private boolean includeSalesInformation;

    /**
     * Indicates whether the Product Information
     * section is included in the report.
     */
    private boolean includeProductInformation;

    /*
     * Sales Information
     */

    /**
     * Total revenue statistics.
     */
    private TotalRevenueResponseDTO totalRevenue;

    /**
     * Total sales statistics.
     */
    private TotalSalesResponseDTO totalSales;

    /**
     * Average ticket statistics.
     */
    private AverageTicketResponseDTO averageTicket;

    /**
     * Peak sales hour statistics.
     */
    private PeakHoursResponseDTO peakHours;

    /**
     * Time-series statistics included in the
     * Sales Information section of the report.
     */
    private SalesTimeSeriesResponseDTO salesTimeSeries;

    /*
     * Product Information
     */

    /**
     * Top products statistics used to generate:
     * <ul>
     *     <li>Top products by quantity sold</li>
     *     <li>Top products by revenue generated</li>
     * </ul>
     */
    private TopProductsResponseDTO topProducts;

    /*
     * Sold Products
     */

    /**
     * Total number of sold products matching
     * the selected filters.
     */
    private Long totalSoldProducts;

    /**
     * Number of sold products included
     * in the report.
     */
    private Integer includedSoldProducts;

    /**
     * Sold products ranking list included
     * in the report.
     */
    private List<SoldProductDTO> soldProducts;

    /*
     * Unsold Products
     */

    /**
     * Total number of unsold products matching
     * the selected filters.
     */
    private Long totalUnsoldProducts;

    /**
     * Number of unsold products included
     * in the report.
     */
    private Integer includedUnsoldProducts;

    /**
     * Unsold products list included
     * in the report.
     */
    private List<UnsoldProductDTO> unsoldProducts;

    /**
     * Human-readable ranking metric displayed
     * in the Sold Products Ranking subsection.
     */
    private String soldProductsMetric;

    /**
     * Human-readable ranking order displayed
     * in the Sold Products Ranking subsection.
     */
    private String soldProductsOrder;
}