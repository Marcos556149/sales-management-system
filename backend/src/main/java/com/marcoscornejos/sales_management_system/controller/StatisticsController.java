package com.marcoscornejos.sales_management_system.controller;

import com.marcoscornejos.sales_management_system.dto.*;
import com.marcoscornejos.sales_management_system.model.ProductQuantityOrderType;
import com.marcoscornejos.sales_management_system.model.ProductRankingMetric;
import com.marcoscornejos.sales_management_system.service.IStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * REST controller responsible for providing sales statistics and analytical data.
 *
 * <p>
 * This controller handles aggregated queries over sales and products,
 * including revenue metrics, product rankings, and time-based statistics.
 * </p>
 */
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final IStatisticsService iStatisticsService;

    /*
    @GetMapping("/sales")
    public ResponseEntity<SalesStatisticsResponseDTO> getSalesStatistics(

            @RequestParam(required = false) Long userId,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            @RequestParam(defaultValue = "REVENUE_GENERATED")
            ProductRankingMetric metric,

            @RequestParam(defaultValue = "MOST_TO_LEAST")
            ProductQuantityOrderType order
    ) {

        SalesStatisticsResponseDTO response =
                iStatisticsService.getSalesStatistics(
                        userId,
                        startDate,
                        endDate,
                        metric,
                        order
                );

        return ResponseEntity.ok(response);
    }

     */

    /**
     * Retrieves available users for statistics filtering.
     *
     * <p>
     * This endpoint provides the list of users that can be used
     * to filter sales statistics in the frontend.
     * </p>
     *
     * <p>
     * Each returned user includes:
     * <ul>
     *   <li>User identifier (used for backend filtering)</li>
     *   <li>Display name (used in frontend selectors)</li>
     * </ul>
     * </p>
     *
     * <p>
     * If no users exist in the system, an empty list is returned.
     * The frontend must interpret this as "no user-specific filtering available".
     * </p>
     *
     * <p>
     * This endpoint acts as the single source of truth for
     * user-based statistics filtering options.
     * </p>
     *
     * @return list of available users for statistics filtering
     */
    @GetMapping("/filters/users")
    public ResponseEntity<List<UserFilterDTO>> getStatisticsFilterUsers() {

        List<UserFilterDTO> response =
                iStatisticsService.getStatisticsFilterUsers();

        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves available product ranking filter options.
     *
     * <p>
     * This endpoint provides all enum-based filter options
     * required by the frontend to build the product ranking
     * filtering UI dynamically.
     * </p>
     *
     * <p>
     * The returned information includes:
     * <ul>
     *   <li>Available product ranking metrics</li>
     *   <li>Available ordering options</li>
     * </ul>
     * </p>
     *
     * <p>
     * All options are derived directly from backend enums,
     * ensuring consistency between frontend filters
     * and backend business rules.
     * </p>
     *
     * @return product ranking filter options
     */
    @GetMapping("/filters/product-ranking")
    public ResponseEntity<ProductRankingFiltersResponseDTO>
    getProductRankingFilters() {

        ProductRankingFiltersResponseDTO response =
                iStatisticsService.getProductRankingFilters();

        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves the total revenue based on selected filters.
     *
     * <p>
     * Total revenue is calculated as the sum of all sales amounts
     * that match the provided filters.
     * </p>
     *
     * <p>
     * If no filters are provided:
     * <ul>
     *   <li>User defaults to "All Users"</li>
     *   <li>Date range defaults to current date</li>
     * </ul>
     * </p>
     *
     * @param userId optional user filter (null = all users)
     * @param startDate optional start date filter
     * @param endDate optional end date filter
     * @return total revenue information
     */
    @GetMapping("/sales/total-revenue")
    public ResponseEntity<TotalRevenueResponseDTO> getTotalRevenue(

            @RequestParam(required = false) Long userId,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {

        System.out.println("userId: " + userId);
        System.out.println("startDate: " + startDate);
        System.out.println("endDate: " + endDate);

        TotalRevenueResponseDTO response =
                iStatisticsService.getTotalRevenue(
                        userId,
                        startDate,
                        endDate
                );

        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves the total number of sales based on selected filters.
     *
     * <p>
     * Total sales are calculated as the count of all sales records
     * that match the provided filters.
     * </p>
     *
     * <p>
     * If no filters are provided:
     * <ul>
     *   <li>User defaults to "All Users"</li>
     *   <li>Date range defaults to current date</li>
     * </ul>
     * </p>
     *
     * @param userId optional user filter (null = all users)
     * @param startDate optional start date filter
     * @param endDate optional end date filter
     * @return total sales information
     */
    @GetMapping("/sales/total-sales")
    public ResponseEntity<TotalSalesResponseDTO> getTotalSales(

            @RequestParam(required = false) Long userId,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {

        System.out.println("userId: " + userId);
        System.out.println("startDate: " + startDate);
        System.out.println("endDate: " + endDate);

        TotalSalesResponseDTO response =
                iStatisticsService.getTotalSales(
                        userId,
                        startDate,
                        endDate
                );

        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves the average ticket value based on selected filters.
     *
     * <p>
     * Average ticket value is calculated as:
     * total revenue divided by total number of sales.
     * </p>
     *
     * <p>
     * If no filters are provided:
     * <ul>
     *   <li>User defaults to "All Users"</li>
     *   <li>Date range defaults to current date</li>
     * </ul>
     * </p>
     *
     * @param userId optional user filter (null = all users)
     * @param startDate optional start date filter
     * @param endDate optional end date filter
     * @return average ticket information
     */
    @GetMapping("/sales/average-ticket")
    public ResponseEntity<AverageTicketResponseDTO> getAverageTicket(

            @RequestParam(required = false) Long userId,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {

        System.out.println("userId: " + userId);
        System.out.println("startDate: " + startDate);
        System.out.println("endDate: " + endDate);

        AverageTicketResponseDTO response =
                iStatisticsService.getAverageTicket(
                        userId,
                        startDate,
                        endDate
                );

        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves peak sales hours based on selected filters.
     *
     * <p>
     * This endpoint provides:
     * <ul>
     *   <li>The hour with the highest revenue generated</li>
     *   <li>The hour with the highest number of sales</li>
     * </ul>
     * </p>
     *
     * <p>
     * All values are calculated using only the sales
     * that match the provided filters.
     * </p>
     *
     * <p>
     * If no filters are provided:
     * <ul>
     *   <li>User defaults to "All Users"</li>
     *   <li>Date range defaults to current date</li>
     * </ul>
     * </p>
     *
     * <p>
     * If no matching sales exist, both values may return {@code null},
     * indicating that no peak hour data is available.
     * </p>
     *
     * @param userId optional user filter (null = all users)
     * @param startDate optional start date filter
     * @param endDate optional end date filter
     * @return peak sales hour statistics
     */
    @GetMapping("/sales/peak-hours")
    public ResponseEntity<PeakHoursResponseDTO> getPeakHours(

            @RequestParam(required = false) Long userId,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {

        PeakHoursResponseDTO response =
                iStatisticsService.getPeakHours(
                        userId,
                        startDate,
                        endDate
                );

        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves time-series sales statistics based on selected filters.
     *
     * <p>
     * This endpoint provides chart-ready statistical data including:
     * <ul>
     *   <li>Total revenue evolution over time</li>
     *   <li>Total number of sales over time</li>
     * </ul>
     * </p>
     *
     * <p>
     * All values are calculated using only the sales
     * that match the provided filters.
     * </p>
     *
     * <p>
     * Time-series aggregation granularity is automatically adjusted
     * based on the selected date range:
     * <ul>
     *   <li>HOUR → single-day ranges</li>
     *   <li>DAY → ranges up to 31 days</li>
     *   <li>MONTH → ranges up to 365 days</li>
     *   <li>YEAR → ranges greater than 365 days</li>
     * </ul>
     * </p>
     *
     * <p>
     * If no filters are provided:
     * <ul>
     *   <li>User defaults to "All Users"</li>
     *   <li>Date range defaults to current date</li>
     * </ul>
     * </p>
     *
     * <p>
     * If no matching sales exist, both series may return empty lists.
     * </p>
     *
     * @param userId optional user filter (null = all users)
     * @param startDate optional start date filter
     * @param endDate optional end date filter
     * @return time-series sales statistics
     */
    @GetMapping("/sales/time-series")
    public ResponseEntity<SalesTimeSeriesResponseDTO> getSalesTimeSeries(

            @RequestParam(required = false) Long userId,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {

        SalesTimeSeriesResponseDTO response =
                iStatisticsService.getSalesTimeSeries(
                        userId,
                        startDate,
                        endDate
                );

        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves top-performing products statistics based on selected filters.
     *
     * <p>
     * This endpoint provides:
     * <ul>
     *   <li>Top 10 products by quantity sold</li>
     *   <li>Top 10 products by revenue generated</li>
     * </ul>
     * </p>
     *
     * <p>
     * All values are calculated using only the sales
     * that match the provided filters.
     * </p>
     *
     * <p>
     * If no filters are provided:
     * <ul>
     *   <li>User defaults to "All Users"</li>
     *   <li>Date range defaults to current date</li>
     * </ul>
     * </p>
     *
     * <p>
     * If no matching sales exist, both lists may return empty lists.
     * </p>
     *
     * @param userId optional user filter (null = all users)
     * @param startDate optional start date filter
     * @param endDate optional end date filter
     * @return top-performing products statistics
     */
    @GetMapping("/products/top")
    public ResponseEntity<TopProductsResponseDTO> getTopProducts(

            @RequestParam(required = false) Long userId,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {

        TopProductsResponseDTO response =
                iStatisticsService.getTopProducts(
                        userId,
                        startDate,
                        endDate
                );

        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves a paginated ranking of sold products
     * based on selected filters.
     *
     * <p>
     * This endpoint returns products that had sales activity
     * within the selected date range, including:
     * <ul>
     *   <li>Product code</li>
     *   <li>Product name</li>
     *   <li>Total quantity sold</li>
     *   <li>Total revenue generated</li>
     * </ul>
     * </p>
     *
     * <p>
     * Results support dynamic ranking using:
     * <ul>
     *   <li>Metric:
     *      <ul>
     *          <li>Quantity Sold</li>
     *          <li>Revenue Generated</li>
     *      </ul>
     *   </li>
     *   <li>Ordering:
     *      <ul>
     *          <li>Most sold → least sold</li>
     *          <li>Least sold → most sold</li>
     *      </ul>
     *   </li>
     * </ul>
     * </p>
     *
     * <p>
     * Default behavior:
     * <ul>
     *   <li>User → All Users</li>
     *   <li>Date range → Current date</li>
     *   <li>Metric → Revenue Generated</li>
     *   <li>Order → Most sold → least sold</li>
     *   <li>Page size → 20</li>
     * </ul>
     * </p>
     *
     * <p>
     * If no matching products exist, an empty page is returned.
     * </p>
     *
     * @param userId optional user filter
     * @param startDate optional start date filter
     * @param endDate optional end date filter
     * @param metric ranking metric
     * @param order ranking order
     * @param page requested page number
     * @param size requested page size
     * @return paginated sold products ranking
     */
    @GetMapping("/products/sold")
    public ResponseEntity<PageResponseDTO<SoldProductDTO>>
    getSoldProducts(

            @RequestParam(required = false)
            Long userId,

            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate endDate,

            @RequestParam(
                    defaultValue = "REVENUE_GENERATED"
            )
            ProductRankingMetric metric,

            @RequestParam(
                    defaultValue = "MOST_TO_LEAST"
            )
            ProductQuantityOrderType order,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "20")
            int size
    ) {

        PageResponseDTO<SoldProductDTO> response =
                iStatisticsService.getSoldProductsRanking(
                        userId,
                        startDate,
                        endDate,
                        metric,
                        order,
                        page,
                        size
                );

        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves a paginated list of products with no sales
     * based on selected filters.
     *
     * <p>
     * This endpoint returns products that had zero sales activity
     * within the selected date range, including:
     * <ul>
     *     <li>Product code</li>
     *     <li>Product name</li>
     * </ul>
     * </p>
     *
     * <p>
     * Products are considered unsold only if they have no
     * matching sales records within the selected filters.
     * </p>
     *
     * <p>
     * Default behavior:
     * <ul>
     *     <li>User → All Users</li>
     *     <li>Date range → Current date</li>
     *     <li>Page size → 20</li>
     * </ul>
     * </p>
     *
     * <p>
     * If no matching products exist, an empty page is returned.
     * </p>
     *
     * @param userId optional user filter
     * @param startDate optional start date filter
     * @param endDate optional end date filter
     * @param page requested page number
     * @param size requested page size
     * @return paginated unsold products list
     */
    @GetMapping("/products/unsold")
    public ResponseEntity<PageResponseDTO<UnsoldProductDTO>>
    getUnsoldProducts(

            @RequestParam(required = false)
            Long userId,

            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate endDate,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "20")
            int size
    ) {

        PageResponseDTO<UnsoldProductDTO> response =
                iStatisticsService.getUnsoldProducts(
                        userId,
                        startDate,
                        endDate,
                        page,
                        size
                );

        return ResponseEntity.ok(response);
    }

    /**
     * Generates a downloadable PDF report containing
     * sales statistics and product performance information.
     *
     * <p>
     * This endpoint generates a PDF report using the
     * same filters and business rules defined for
     * the statistics module.
     * </p>
     *
     * <p>
     * The report may include the following sections:
     * <ul>
     *     <li>Sales Information</li>
     *     <li>Product Information</li>
     * </ul>
     * </p>
     *
     * <p>
     * Sales Information may include:
     * <ul>
     *     <li>Total revenue</li>
     *     <li>Total number of sales</li>
     *     <li>Average ticket value</li>
     *     <li>Peak revenue hour</li>
     *     <li>Peak sales hour</li>
     *     <li>Revenue over time table</li>
     *     <li>Sales over time table</li>
     * </ul>
     * </p>
     *
     * <p>
     * Product Information may include:
     * <ul>
     *     <li>Top products by quantity sold</li>
     *     <li>Top products by revenue generated</li>
     *     <li>Detailed sold products ranking list</li>
     *     <li>Detailed unsold products list</li>
     * </ul>
     * </p>
     *
     * <p>
     * The generated report always includes:
     * <ul>
     *     <li>Report title</li>
     *     <li>Report generation date and time</li>
     *     <li>Selected user</li>
     *     <li>Selected date range</li>
     * </ul>
     * </p>
     *
     * <p>
     * The user may configure:
     * <ul>
     *     <li>Included report sections</li>
     *     <li>Number of products displayed in detailed product lists</li>
     *     <li>Product ranking metric</li>
     *     <li>Product ranking order</li>
     * </ul>
     * </p>
     *
     * <p>
     * If no statistical data matches the selected filters,
     * the report is not generated.
     * </p>
     *
     * @param request PDF generation configuration
     * @return generated PDF file
     */
    @PostMapping(
            value = "/report/pdf",
            produces = MediaType.APPLICATION_PDF_VALUE
    )
    public ResponseEntity<byte[]> generateStatisticsPdf(
            @RequestBody StatisticsPdfRequestDTO request
    ) {

        byte[] pdf =
                iStatisticsService.generatePdf(request);

        String fileName =
                "sales-statistics-report-"
                        + request.getStartDate()
                        + "-to-"
                        + request.getEndDate()
                        + ".pdf";

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=" + fileName
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}