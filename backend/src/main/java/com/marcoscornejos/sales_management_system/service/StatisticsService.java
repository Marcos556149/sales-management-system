package com.marcoscornejos.sales_management_system.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.marcoscornejos.sales_management_system.dto.*;
import com.marcoscornejos.sales_management_system.exception.*;
import com.marcoscornejos.sales_management_system.mapper.IPageResponseMapper;
import com.marcoscornejos.sales_management_system.model.ProductQuantityOrderType;
import com.marcoscornejos.sales_management_system.model.ProductRankingMetric;
import com.marcoscornejos.sales_management_system.model.StatisticsGranularity;
import com.marcoscornejos.sales_management_system.projection.SoldProductProjection;
import com.marcoscornejos.sales_management_system.projection.TimeSeriesProjection;
import com.marcoscornejos.sales_management_system.projection.UnsoldProductProjection;
import com.marcoscornejos.sales_management_system.repository.IProductRepository;
import com.marcoscornejos.sales_management_system.repository.ISaleRepository;
import com.marcoscornejos.sales_management_system.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.Hour;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class StatisticsService implements IStatisticsService{

    private final ISaleRepository iSaleRepository;
    private final IProductRepository iProductRepository;
    private final IUserRepository iUserRepository;
    private final IPageResponseMapper iPageResponseMapper;
    private static final Set<Integer> ALLOWED_PRODUCT_LIMITS_REPORT_PDF =
            Set.of(10, 20, 50, 100);


    /*
    @Override
    public SalesStatisticsResponseDTO getSalesStatistics(
            Long userId,
            LocalDate startDate,
            LocalDate endDate,
            ProductRankingMetric metric,
            ProductQuantityOrderType order
    ) {

        // Normalize filters
        LocalDate[] normalizedDates =
                normalizeDateRange(startDate, endDate);

        startDate = normalizedDates[0];
        endDate = normalizedDates[1];

        if (userId != null && !iUserRepository.existsById(userId)) {
            throw new UserNotFoundException(
                    "The selected user is not valid or does not exist"
            );
        }

        // Build sales information
        SalesInfoDTO salesInfo =
                buildSalesInfo(userId, startDate, endDate);

        // Build product statistics
        ProductStatisticsDTO productStatistics =
                buildProductStatistics(
                        userId,
                        startDate,
                        endDate,
                        metric,
                        order
                );

        // Final response
        return new SalesStatisticsResponseDTO(
                salesInfo,
                productStatistics
        );
    }

     */

    /**
     * Applies default behavior to the provided date range.
     *
     * <p>
     * If no dates are provided, both start and end date default to the current date.
     * </p>
     *
     * @param startDate requested start date
     * @param endDate requested end date
     * @return normalized date range
     */
    private LocalDate[] normalizeDateRange(
            LocalDate startDate,
            LocalDate endDate
    ) {

        // Apply defaults when no range is provided
        if (startDate == null && endDate == null) {
            LocalDate today = LocalDate.now();

            startDate = today;
            endDate = today;
        }

        // Validate incomplete range
        if (startDate == null || endDate == null) {
            throw new InvalidStatisticsFilterException(
                    "A valid date range is required (start and end date)"
            );
        }

        // Validate chronological order
        if (endDate.isBefore(startDate)) {
            throw new InvalidStatisticsFilterException(
                    "End date cannot be earlier than start date",
                    "endDate"
            );
        }

        return new LocalDate[]{ startDate, endDate };
    }



    /*

    private SalesInfoDTO buildSalesInfo(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    ) {

        BigDecimal totalRevenue = iSaleRepository.sumRevenue(
                userId,
                startDate,
                endDate
        );

        Long totalSales = iSaleRepository.countSales(
                userId,
                startDate,
                endDate
        );

        BigDecimal averageTicket =
                totalSales == 0
                        ? BigDecimal.ZERO
                        : totalRevenue.divide(
                        BigDecimal.valueOf(totalSales),
                        2,
                        RoundingMode.HALF_UP
                );

        String highestRevenueHour =
                iSaleRepository.findPeakRevenueHour(
                        userId,
                        startDate,
                        endDate
                );

        String highestSalesHour =
                iSaleRepository.findPeakSalesHour(
                        userId,
                        startDate,
                        endDate
                );

        StatisticsGranularity granularity =
                determineGranularity(startDate, endDate);

        List<TimeSeriesPointDTO> revenueOverTime =
                getRevenueOverTime(
                        userId,
                        startDate,
                        endDate,
                        granularity
                );

        List<TimeSeriesPointDTO> salesOverTime =
                getSalesOverTime(
                        userId,
                        startDate,
                        endDate,
                        granularity
                );

        return new SalesInfoDTO(
                totalRevenue,
                totalSales,
                averageTicket,
                highestRevenueHour,
                highestSalesHour,
                revenueOverTime,
                salesOverTime
        );
    }

     */

    /**
     * Determines the appropriate chart aggregation granularity
     * based on the selected date range.
     *
     * <p>
     * Granularity is automatically adjusted to improve chart readability
     * and avoid excessive data points for large date ranges.
     * </p>
     *
     * <p>
     * Granularity rules:
     * <ul>
     *   <li>HOUR → single-day ranges</li>
     *   <li>DAY → ranges up to 31 days</li>
     *   <li>MONTH → ranges up to 365 days</li>
     *   <li>YEAR → ranges greater than 365 days</li>
     * </ul>
     * </p>
     *
     * @param startDate normalized start date
     * @param endDate normalized end date
     * @return calculated statistics granularity
     */
    private StatisticsGranularity determineGranularity(
            LocalDate startDate,
            LocalDate endDate
    ) {

        long days =
                ChronoUnit.DAYS.between(startDate, endDate) + 1;

        // Single day → hourly granularity
        if (days == 1) {
            return StatisticsGranularity.HOUR;
        }

        // Short ranges → daily granularity
        if (days <= 31) {
            return StatisticsGranularity.DAY;
        }

        // Medium ranges → monthly granularity
        if (days <= 365) {
            return StatisticsGranularity.MONTH;
        }

        // Long ranges → yearly granularity
        return StatisticsGranularity.YEAR;
    }

    /**
     * Retrieves revenue chart data aggregated according to the selected granularity.
     *
     * <p>
     * Delegates the query to the appropriate repository method depending on the
     * calculated statistics granularity.
     * </p>
     *
     * <p>
     * Repository queries return lightweight statistical projections,
     * which are then mapped into {@link TimeSeriesPointDTO} objects
     * used by the API response layer.
     * </p>
     *
     * <p>
     * When the granularity is {@link StatisticsGranularity#HOUR},
     * raw hour labels are transformed into explicit hour ranges
     * (e.g. "18:00 - 18:59") to improve chart readability
     * and accurately represent the aggregated time bucket.
     * </p>
     *
     * @param userId resolved user identifier (null = all users)
     * @param startDate normalized start date
     * @param endDate normalized end date
     * @param granularity chart aggregation granularity
     * @return revenue time series data
     */
    private List<TimeSeriesPointDTO> getRevenueOverTime(
            Long userId,
            LocalDate startDate,
            LocalDate endDate,
            StatisticsGranularity granularity
    ) {

        List<TimeSeriesProjection> projections =
                switch (granularity) {

                    case HOUR -> iSaleRepository.getRevenuePerHour(
                            userId,
                            startDate,
                            endDate
                    );

                    case DAY -> iSaleRepository.getRevenuePerDay(
                            userId,
                            startDate,
                            endDate
                    );

                    case MONTH -> iSaleRepository.getRevenuePerMonth(
                            userId,
                            startDate,
                            endDate
                    );

                    case YEAR -> iSaleRepository.getRevenuePerYear(
                            userId,
                            startDate,
                            endDate
                    );
                };

        return projections.stream()
                .map(projection -> {

                    String label = projection.getLabel();

                    if (granularity == StatisticsGranularity.HOUR) {

                        label = formatHourRange(
                                Integer.parseInt(label)
                        );
                    }

                    return new TimeSeriesPointDTO(
                            label,
                            projection.getValue()
                    );
                })
                .toList();
    }

    /**
     * Retrieves sales count chart data aggregated according to the selected granularity.
     *
     * <p>
     * Delegates the query to the appropriate repository method depending on the
     * calculated statistics granularity.
     * </p>
     *
     * <p>
     * Repository queries return lightweight statistical projections,
     * which are then mapped into {@link TimeSeriesPointDTO} objects
     * used by the API response layer.
     * </p>
     *
     * <p>
     * When the granularity is {@link StatisticsGranularity#HOUR},
     * raw hour labels are transformed into explicit hour ranges
     * (e.g. "18:00 - 18:59") to improve chart readability
     * and accurately represent the aggregated time bucket.
     * </p>
     *
     * @param userId resolved user identifier (null = all users)
     * @param startDate normalized start date
     * @param endDate normalized end date
     * @param granularity chart aggregation granularity
     * @return sales count time series data
     */
    private List<TimeSeriesPointDTO> getSalesOverTime(
            Long userId,
            LocalDate startDate,
            LocalDate endDate,
            StatisticsGranularity granularity
    ) {

        List<TimeSeriesProjection> projections =
                switch (granularity) {

                    case HOUR -> iSaleRepository.getSalesPerHour(
                            userId,
                            startDate,
                            endDate
                    );

                    case DAY -> iSaleRepository.getSalesPerDay(
                            userId,
                            startDate,
                            endDate
                    );

                    case MONTH -> iSaleRepository.getSalesPerMonth(
                            userId,
                            startDate,
                            endDate
                    );

                    case YEAR -> iSaleRepository.getSalesPerYear(
                            userId,
                            startDate,
                            endDate
                    );
                };

        return projections.stream()
                .map(projection -> {

                    String label = projection.getLabel();

                    if (granularity == StatisticsGranularity.HOUR) {

                        label = formatHourRange(
                                Integer.parseInt(label)
                        );
                    }

                    return new TimeSeriesPointDTO(
                            label,
                            projection.getValue()
                    );
                })
                .toList();
    }

    /*
    private ProductStatisticsDTO buildProductStatistics(
            Long userId,
            LocalDate startDate,
            LocalDate endDate,
            ProductRankingMetric metric,
            ProductQuantityOrderType order
    ) {

        List<SoldProductDTO> topByQuantity =
                iProductRepository.findTopByQuantity(
                                userId,
                                startDate,
                                endDate
                        ).stream()
                        .map(p -> new SoldProductDTO(
                                p.getProductCode(),
                                p.getProductName(),
                                p.getQuantitySold(),
                                p.getRevenueGenerated()
                        ))
                        .toList();

        List<SoldProductDTO> topByRevenue =
                iProductRepository.findTopByRevenue(
                                userId,
                                startDate,
                                endDate
                        ).stream()
                        .map(p -> new SoldProductDTO(
                                p.getProductCode(),
                                p.getProductName(),
                                p.getQuantitySold(),
                                p.getRevenueGenerated()
                        ))
                        .toList();

        List<SoldProductDTO> ranking =
                iProductRepository.findRanking(
                                userId,
                                startDate,
                                endDate,
                                metric.name()
                        ).stream()
                        .map(p -> new SoldProductDTO(
                                p.getProductCode(),
                                p.getProductName(),
                                p.getQuantitySold(),
                                p.getRevenueGenerated()
                        ))
                        .collect(Collectors.toCollection(ArrayList::new));

        if (order == ProductQuantityOrderType.LEAST_TO_MOST) {
            Collections.reverse(ranking);
        }

        List<UnsoldProductDTO> unsold =
                iProductRepository.findUnsoldProducts(
                                userId,
                                startDate,
                                endDate
                        ).stream()
                        .map(p -> new UnsoldProductDTO(
                                p.getProductCode(),
                                p.getProductName()
                        ))
                        .toList();

        return new ProductStatisticsDTO(
                topByQuantity,
                topByRevenue,
                ranking,
                unsold
        );
    }

     */

    /**
     * Retrieves available users for statistics filtering.
     *
     * <p>
     * Users are retrieved dynamically from the database and mapped
     * into lightweight DTOs intended for frontend filter selectors.
     * </p>
     *
     * <p>
     * Each DTO contains:
     * <ul>
     *   <li>User identifier</li>
     *   <li>User display name</li>
     * </ul>
     * </p>
     *
     * <p>
     * If no users exist, an empty list is returned.
     * </p>
     *
     * @return list of available users for statistics filtering
     */
    @Override
    public List<UserFilterDTO> getStatisticsFilterUsers() {

        return iUserRepository.findAll()
                .stream()
                .map(user -> new UserFilterDTO(
                        user.getUserId(),
                        user.getUserName()
                ))
                .toList();
    }

    /**
     * Retrieves available product ranking filter options.
     *
     * <p>
     * Filter options are generated dynamically from
     * application enums to ensure consistency between
     * backend logic and frontend selectors.
     * </p>
     *
     * <p>
     * The returned configuration includes:
     * <ul>
     *   <li>Product ranking metrics</li>
     *   <li>Product ranking ordering options</li>
     * </ul>
     * </p>
     *
     * @return product ranking filter options
     */
    @Override
    public ProductRankingFiltersResponseDTO getProductRankingFilters() {

        // Metric options
        List<EnumDTO> metricOptions =
                Arrays.stream(ProductRankingMetric.values())
                        .map(metric -> new EnumDTO(
                                metric.name(),
                                metric.getDisplayName()
                        ))
                        .toList();

        // Order options
        List<EnumDTO> orderOptions =
                Arrays.stream(ProductQuantityOrderType.values())
                        .map(order -> new EnumDTO(
                                order.name(),
                                order.getDisplayName()
                        ))
                        .toList();

        return new ProductRankingFiltersResponseDTO(
                metricOptions,
                orderOptions
        );
    }

    /**
     * Retrieves the total revenue based on the selected filters.
     *
     * <p>
     * Total revenue is calculated as the sum of all sales amounts
     * that match the provided filters.
     * </p>
     *
     * <p>
     * Filter behavior:
     * <ul>
     *   <li>If userId is null, statistics are calculated for all users</li>
     *   <li>If both dates are null, current date is used</li>
     *   <li>If only one date is provided, an exception is thrown</li>
     * </ul>
     * </p>
     *
     * @param userId optional user filter (null = all users)
     * @param startDate optional start date filter
     * @param endDate optional end date filter
     * @return total revenue response
     */
    @Override
    public TotalRevenueResponseDTO getTotalRevenue(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    ) {

        // Normalize date range
        LocalDate[] normalizedDates =
                normalizeDateRange(startDate, endDate);

        startDate = normalizedDates[0];
        endDate = normalizedDates[1];

        // Validate user existence
        if (userId != null && !iUserRepository.existsById(userId)) {
            throw new UserNotFoundException(
                    "The selected user is not valid or does not exist"
            );
        }

        // Calculate total revenue
        BigDecimal totalRevenue =
                iSaleRepository.sumRevenue(
                        userId,
                        startDate,
                        endDate
                );

        return new TotalRevenueResponseDTO(totalRevenue);
    }

    /**
     * Retrieves the total number of sales based on the selected filters.
     *
     * <p>
     * Total sales are calculated as the count of all sales records
     * that match the provided filters.
     * </p>
     *
     * <p>
     * Filter behavior:
     * <ul>
     *   <li>If userId is null, statistics are calculated for all users</li>
     *   <li>If both dates are null, current date is used</li>
     *   <li>If only one date is provided, an exception is thrown</li>
     * </ul>
     * </p>
     *
     * @param userId optional user filter (null = all users)
     * @param startDate optional start date filter
     * @param endDate optional end date filter
     * @return total sales response
     */
    @Override
    public TotalSalesResponseDTO getTotalSales(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    ) {

        // Normalize date range
        LocalDate[] normalizedDates =
                normalizeDateRange(startDate, endDate);

        startDate = normalizedDates[0];
        endDate = normalizedDates[1];

        // Validate user existence
        if (userId != null && !iUserRepository.existsById(userId)) {
            throw new UserNotFoundException(
                    "The selected user is not valid or does not exist"
            );
        }

        // Calculate total sales
        Long totalSales =
                iSaleRepository.countSales(
                        userId,
                        startDate,
                        endDate
                );

        return new TotalSalesResponseDTO(totalSales);
    }

    /**
     * Retrieves the average ticket value based on the selected filters.
     *
     * <p>
     * Average ticket value is calculated as:
     * total revenue divided by total number of sales.
     * </p>
     *
     * <p>
     * Filter behavior:
     * <ul>
     *   <li>If userId is null, statistics are calculated for all users</li>
     *   <li>If both dates are null, current date is used</li>
     *   <li>If only one date is provided, an exception is thrown</li>
     * </ul>
     * </p>
     *
     * <p>
     * If no matching sales exist, the average ticket value
     * defaults to {@link BigDecimal#ZERO}.
     * </p>
     *
     * @param userId optional user filter (null = all users)
     * @param startDate optional start date filter
     * @param endDate optional end date filter
     * @return average ticket response
     */
    @Override
    public AverageTicketResponseDTO getAverageTicket(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    ) {

        // Normalize date range
        LocalDate[] normalizedDates =
                normalizeDateRange(startDate, endDate);

        startDate = normalizedDates[0];
        endDate = normalizedDates[1];

        // Validate user existence
        if (userId != null && !iUserRepository.existsById(userId)) {
            throw new UserNotFoundException(
                    "The selected user is not valid or does not exist"
            );
        }

        // Retrieve aggregated values
        BigDecimal totalRevenue =
                iSaleRepository.sumRevenue(
                        userId,
                        startDate,
                        endDate
                );

        Long totalSales =
                iSaleRepository.countSales(
                        userId,
                        startDate,
                        endDate
                );

        // Calculate average ticket
        BigDecimal averageTicket =
                totalSales == 0
                        ? BigDecimal.ZERO
                        : totalRevenue.divide(
                        BigDecimal.valueOf(totalSales),
                        2,
                        RoundingMode.HALF_UP
                );

        return new AverageTicketResponseDTO(
                averageTicket
        );
    }

    /**
     * Retrieves peak sales hour statistics based on the selected filters.
     *
     * <p>
     * This method calculates:
     * <ul>
     *   <li>The time range with the highest generated revenue</li>
     *   <li>The time range with the highest number of sales</li>
     * </ul>
     * </p>
     *
     * <p>
     * Returned values are formatted as hourly ranges
     * using the following pattern:
     * <pre>
     * HH:00 - HH:59
     * </pre>
     * </p>
     *
     * <p>
     * Filter behavior:
     * <ul>
     *   <li>If userId is null, statistics are calculated for all users</li>
     *   <li>If both dates are null, current date is used</li>
     *   <li>If only one date is provided, an exception is thrown</li>
     * </ul>
     * </p>
     *
     * <p>
     * If no matching sales exist, both values may return {@code null}.
     * </p>
     *
     * @param userId optional user filter (null = all users)
     * @param startDate optional start date filter
     * @param endDate optional end date filter
     * @return peak sales hour statistics
     */
    @Override
    public PeakHoursResponseDTO getPeakHours(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    ) {

        // Normalize date range
        LocalDate[] normalizedDates =
                normalizeDateRange(startDate, endDate);

        startDate = normalizedDates[0];
        endDate = normalizedDates[1];

        // Validate user existence
        if (userId != null && !iUserRepository.existsById(userId)) {
            throw new UserNotFoundException(
                    "The selected user is not valid or does not exist"
            );
        }

        // Retrieve raw peak hours
        Integer revenueHour =
                iSaleRepository.findPeakRevenueHour(
                        userId,
                        startDate,
                        endDate
                );

        Integer salesHour =
                iSaleRepository.findPeakSalesHour(
                        userId,
                        startDate,
                        endDate
                );

        // Format hour ranges
        String highestRevenueHour =
                formatHourRange(revenueHour);

        String highestSalesHour =
                formatHourRange(salesHour);

        return new PeakHoursResponseDTO(
                highestRevenueHour,
                highestSalesHour
        );
    }

    /**
     * Formats an hour value into a human-readable hour range.
     *
     * <p>
     * Example:
     * <ul>
     *   <li>18 → "18:00 - 18:59"</li>
     *   <li>9 → "09:00 - 09:59"</li>
     * </ul>
     * </p>
     *
     * @param hour hour value (0–23)
     * @return formatted hour range,
     *         or null if hour is null
     */
    private String formatHourRange(Integer hour) {

        if (hour == null) {
            return null;
        }

        return String.format(
                "%02d:00 - %02d:59",
                hour,
                hour
        );
    }

    /**
     * Retrieves time-series sales statistics based on the selected filters.
     *
     * <p>
     * This method calculates:
     * <ul>
     *   <li>Total revenue evolution over time</li>
     *   <li>Total number of sales over time</li>
     * </ul>
     * </p>
     *
     * <p>
     * Time-series aggregation granularity is automatically determined
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
     * Filter behavior:
     * <ul>
     *   <li>If userId is null, statistics are calculated for all users</li>
     *   <li>If both dates are null, current date is used</li>
     *   <li>If only one date is provided, an exception is thrown</li>
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
    @Override
    public SalesTimeSeriesResponseDTO getSalesTimeSeries(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    ) {

        // Normalize date range
        LocalDate[] normalizedDates =
                normalizeDateRange(startDate, endDate);

        startDate = normalizedDates[0];
        endDate = normalizedDates[1];

        // Validate user existence
        if (userId != null && !iUserRepository.existsById(userId)) {
            throw new UserNotFoundException(
                    "The selected user is not valid or does not exist"
            );
        }

        // Determine chart granularity
        StatisticsGranularity granularity =
                determineGranularity(startDate, endDate);

        // Retrieve revenue time-series
        List<TimeSeriesPointDTO> revenueOverTime =
                getRevenueOverTime(
                        userId,
                        startDate,
                        endDate,
                        granularity
                );

        // Retrieve sales count time-series
        List<TimeSeriesPointDTO> salesOverTime =
                getSalesOverTime(
                        userId,
                        startDate,
                        endDate,
                        granularity
                );

        return new SalesTimeSeriesResponseDTO(
                revenueOverTime,
                salesOverTime
        );
    }

    /**
     * Formats an hourly chart label into an explicit hour range.
     *
     * <p>
     * Example:
     * <ul>
     *   <li>18 -> "18:00 - 18:59"</li>
     *   <li>9 -> "09:00 - 09:59"</li>
     * </ul>
     * </p>
     *
     * @param label raw hour label
     * @return formatted hour range label
     */
    private String formatHourlyChartLabel(String label) {

        if (label == null) {
            return null;
        }

        int hour = Integer.parseInt(label);

        return String.format(
                "%02d:00 - %02d:59",
                hour,
                hour
        );
    }

    /**
     * Retrieves top-performing product statistics based on the selected filters.
     *
     * <p>
     * This method calculates:
     * <ul>
     *   <li>Top 10 products by quantity sold</li>
     *   <li>Top 10 products by revenue generated</li>
     * </ul>
     * </p>
     *
     * <p>
     * Filter behavior:
     * <ul>
     *   <li>If userId is null, statistics are calculated for all users</li>
     *   <li>If both dates are null, current date is used</li>
     *   <li>If only one date is provided, an exception is thrown</li>
     * </ul>
     * </p>
     *
     * <p>
     * Repository queries return lightweight database projections,
     * which are then mapped into DTOs at service level to ensure
     * separation between persistence and API layers.
     * </p>
     *
     * <p>
     * If no matching sales exist, both lists may return empty lists.
     * </p>
     *
     * @param userId optional user filter (null = all users)
     * @param startDate optional start date filter
     * @param endDate optional end date filter
     * @return top-performing product statistics
     */
    @Override
    public TopProductsResponseDTO getTopProducts(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    ) {

        // Normalize date range
        LocalDate[] normalizedDates =
                normalizeDateRange(startDate, endDate);

        startDate = normalizedDates[0];
        endDate = normalizedDates[1];

        // Validate user existence
        if (userId != null && !iUserRepository.existsById(userId)) {
            throw new UserNotFoundException(
                    "The selected user is not valid or does not exist"
            );
        }

        // Retrieve top products by quantity sold
        List<TopProductsByQuantityDTO> topProductsByQuantity =
                iProductRepository.findTopByQuantity(
                                userId,
                                startDate,
                                endDate
                        ).stream()
                        .map(product ->
                                new TopProductsByQuantityDTO(
                                        product.getProductCode(),
                                        product.getProductName(),
                                        product.getQuantitySold()
                                )
                        )
                        .toList();

        // Retrieve top products by revenue generated
        List<TopProductsByRevenueDTO> topProductsByRevenue =
                iProductRepository.findTopByRevenue(
                                userId,
                                startDate,
                                endDate
                        ).stream()
                        .map(product ->
                                new TopProductsByRevenueDTO(
                                        product.getProductCode(),
                                        product.getProductName(),
                                        normalizeRevenue(product.getRevenueGenerated())
                                )
                        )
                        .toList();

        return new TopProductsResponseDTO(
                topProductsByQuantity,
                topProductsByRevenue
        );
    }

    /**
     * Normalizes revenue values to a fixed decimal scale.
     *
     * <p>
     * This method ensures that all monetary values returned by the API
     * are consistently formatted with 2 decimal places, regardless of
     * the internal precision produced by database aggregation operations
     * (e.g., SUM, multiplication of numeric fields).
     * </p>
     *
     * <p>
     * This is particularly important for:
     * <ul>
     *   <li>Revenue calculations using aggregated SQL queries</li>
     *   <li>Ensuring consistent JSON output for frontend charts</li>
     *   <li>Avoiding floating precision artifacts (e.g., 123.4500000)</li>
     * </ul>
     * </p>
     *
     * <p>
     * Rounding strategy used: {@link RoundingMode#HALF_UP}
     * </p>
     *
     * @param value raw revenue value from repository (may have variable scale)
     * @return normalized value with scale 2 (e.g., 123.45)
     */
    private BigDecimal normalizeRevenue(BigDecimal value) {

        if (value == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        return value.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Retrieves a paginated ranking of sold products
     * based on selected filters and ranking criteria.
     *
     * <p>
     * Supports:
     * <ul>
     *     <li>User filtering</li>
     *     <li>Date range filtering</li>
     *     <li>Ranking by quantity sold or revenue generated</li>
     *     <li>Ascending or descending ordering</li>
     *     <li>Server-side pagination</li>
     * </ul>
     * </p>
     *
     * <p>
     * Repository queries return lightweight projections
     * which are mapped into DTOs at service level
     * to preserve separation between persistence
     * and API layers.
     * </p>
     *
     * @param userId optional user filter
     * @param startDate optional start date
     * @param endDate optional end date
     * @param metric ranking metric
     * @param order ranking order
     * @param page requested page number
     * @param size requested page size
     * @return paginated sold products ranking
     */
    @Override
    public PageResponseDTO<SoldProductDTO> getSoldProductsRanking(
            Long userId,
            LocalDate startDate,
            LocalDate endDate,
            ProductRankingMetric metric,
            ProductQuantityOrderType order,
            int page,
            int size
    ) {

        // Validate pagination parameters
        if (page < 0) {
            throw new InvalidProductDataException(
                    "Page index must not be negative",
                    "page"
            );
        }

        if (size <= 0) {
            throw new InvalidProductDataException(
                    "Page size must be greater than zero",
                    "size"
            );
        }

        if (size > 100) {
            throw new InvalidProductDataException(
                    "Page size must not exceed 100",
                    "size"
            );
        }

        // Normalize date range
        LocalDate[] normalizedDates =
                normalizeDateRange(startDate, endDate);

        startDate = normalizedDates[0];
        endDate = normalizedDates[1];

        // Validate user existence
        if (
                userId != null
                        &&
                        !iUserRepository.existsById(userId)
        ) {
            throw new UserNotFoundException(
                    "The selected user is not valid or does not exist"
            );
        }

        Pageable pageable =
                PageRequest.of(
                        page,
                        size
                );

        Page<SoldProductProjection> rankingPage;

        // Repository decides only metric.
        // Service decides ordering direction.
        if (order == ProductQuantityOrderType.MOST_TO_LEAST) {

            rankingPage =
                    iProductRepository.findRankingDesc(
                            userId,
                            startDate,
                            endDate,
                            metric.name(),
                            pageable
                    );

        } else {

            rankingPage =
                    iProductRepository.findRankingAsc(
                            userId,
                            startDate,
                            endDate,
                            metric.name(),
                            pageable
                    );
        }

        List<SoldProductDTO> ranking =
                rankingPage.getContent()
                        .stream()
                        .map(product ->
                                new SoldProductDTO(
                                        product.getProductCode(),
                                        product.getProductName(),
                                        product.getQuantitySold(),
                                        normalizeRevenue(
                                                product.getRevenueGenerated()
                                        )
                                )
                        )
                        .toList();

        return iPageResponseMapper.toPageResponseDTO(
                ranking,
                rankingPage.getNumber(),
                rankingPage.getSize(),
                rankingPage.getTotalPages(),
                rankingPage.getTotalElements(),
                null
        );
    }

    /**
     * Retrieves a paginated list of products
     * with no sales activity within the selected filters.
     *
     * <p>
     * Supports:
     * <ul>
     *     <li>User filtering</li>
     *     <li>Date range filtering</li>
     *     <li>Server-side pagination</li>
     * </ul>
     * </p>
     *
     * <p>
     * A product is considered unsold when no matching sales
     * exist within the selected filter range.
     * </p>
     *
     * <p>
     * Repository queries return lightweight projections
     * which are mapped into DTOs at service level
     * to preserve separation between persistence
     * and API layers.
     * </p>
     *
     * @param userId optional user filter
     * @param startDate optional start date
     * @param endDate optional end date
     * @param page requested page number
     * @param size requested page size
     * @return paginated unsold products list
     */
    @Override
    public PageResponseDTO<UnsoldProductDTO> getUnsoldProducts(
            Long userId,
            LocalDate startDate,
            LocalDate endDate,
            int page,
            int size
    ) {

        // Validate pagination parameters
        if (page < 0) {
            throw new InvalidProductDataException(
                    "Page index must not be negative",
                    "page"
            );
        }

        if (size <= 0) {
            throw new InvalidProductDataException(
                    "Page size must be greater than zero",
                    "size"
            );
        }

        if (size > 100) {
            throw new InvalidProductDataException(
                    "Page size must not exceed 100",
                    "size"
            );
        }

        // Normalize date range
        LocalDate[] normalizedDates =
                normalizeDateRange(startDate, endDate);

        startDate = normalizedDates[0];
        endDate = normalizedDates[1];

        // Validate user existence
        if (
                userId != null
                        &&
                        !iUserRepository.existsById(userId)
        ) {
            throw new UserNotFoundException(
                    "The selected user is not valid or does not exist"
            );
        }

        // Build pagination configuration
        Pageable pageable =
                PageRequest.of(
                        page,
                        size
                );

        // Retrieve unsold products
        Page<UnsoldProductProjection> unsoldProductsPage =
                iProductRepository.findUnsoldProducts(
                        userId,
                        startDate,
                        endDate,
                        pageable
                );

        // Map projection → DTO
        List<UnsoldProductDTO> unsoldProducts =
                unsoldProductsPage.getContent()
                        .stream()
                        .map(product ->
                                new UnsoldProductDTO(
                                        product.getProductCode(),
                                        product.getProductName()
                                )
                        )
                        .toList();

        return iPageResponseMapper.toPageResponseDTO(
                unsoldProducts,
                unsoldProductsPage.getNumber(),
                unsoldProductsPage.getSize(),
                unsoldProductsPage.getTotalPages(),
                unsoldProductsPage.getTotalElements(),
                null
        );
    }

    /**
     * Generates a PDF report containing sales statistics
     * and product information.
     *
     * <p>
     * This method orchestrates the complete report generation
     * process, including request validation, data availability
     * verification, report data retrieval, and PDF document creation.
     * </p>
     *
     * <p>
     * The generated report content is determined by the
     * selected filters and report configuration provided
     * in the request.
     * </p>
     *
     * <p>
     * Generation process:
     * <ol>
     *     <li>Validate request parameters and business rules</li>
     *     <li>Verify that statistical data exists for the selected filters</li>
     *     <li>Retrieve all data required by the report</li>
     *     <li>Generate the PDF document</li>
     * </ol>
     * </p>
     *
     * <p>
     * Report generation is aborted if no statistical data
     * is available for the selected criteria.
     * </p>
     *
     * @param request PDF generation configuration
     * @return generated PDF document as a byte array
     */
    @Override
    public byte[] generatePdf(
            StatisticsPdfRequestDTO request
    ) {

        /*
         * Step 1:
         * Validate request parameters and business rules.
         */
        validateAndNormalizePdfRequest(request);

        /*
         * Step 2:
         * Validate statistics data availability.
         */
        TotalSalesResponseDTO totalSales =
                validateStatisticsDataAvailability(request);

        /*
         * Step 3:
         * Retrieve all statistics required by the report.
         */
        StatisticsPdfDataDTO reportData =
                buildPdfData(
                        request,
                        totalSales
                );

        /*
         * Step 4:
         * Generate the PDF document.
         */
        return generatePdfDocument(reportData);
    }

    /**
     * Validates and normalizes the PDF generation request.
     *
     * <p>
     * This method applies the same filtering and date
     * normalization rules used by the statistics module
     * to ensure consistency between displayed statistics
     * and generated reports.
     * </p>
     *
     * <p>
     * The provided request is updated with normalized
     * values and default configuration when required.
     * </p>
     *
     * <p>
     * Validation rules:
     * <ul>
     *     <li>If a user is specified, the user must exist</li>
     *     <li>At least one report section must be selected</li>
     *     <li>Product list limits must be one of the allowed values:
     *         10, 20, 50, or 100</li>
     * </ul>
     * </p>
     *
     * <p>
     * Date normalization is delegated to the shared
     * statistics date-range normalization logic.
     * </p>
     *
     * <p>
     * Default values:
     * <ul>
     *     <li>Sales Information section → included</li>
     *     <li>Product Information section → included</li>
     *     <li>Ranking metric → Revenue Generated</li>
     *     <li>Ranking order → Most sold → least sold</li>
     *     <li>Sold products limit → 20</li>
     *     <li>Unsold products limit → 20</li>
     * </ul>
     * </p>
     *
     * @param request PDF generation configuration
     */
    private void validateAndNormalizePdfRequest(
            StatisticsPdfRequestDTO request
    ) {

        // Apply default section selection
        if (request.getIncludeSalesInformation() == null) {
            request.setIncludeSalesInformation(true);
        }

        if (request.getIncludeProductInformation() == null) {
            request.setIncludeProductInformation(true);
        }

        // Validate selected sections
        if (!request.getIncludeSalesInformation()
                && !request.getIncludeProductInformation()) {

            throw new InvalidStatisticsFilterException(
                    "At least one report section must be selected"
            );
        }

        // Validate user existence
        if (request.getUserId() != null
                && !iUserRepository.existsById(request.getUserId())) {

            throw new UserNotFoundException(
                    "The selected user is not valid or does not exist"
            );
        }

        // Normalize date range
        LocalDate[] normalizedDates =
                normalizeDateRange(
                        request.getStartDate(),
                        request.getEndDate()
                );

        // Apply default ranking configuration
        if (request.getMetric() == null) {
            request.setMetric(
                    ProductRankingMetric.REVENUE_GENERATED
            );
        }

        if (request.getOrder() == null) {
            request.setOrder(
                    ProductQuantityOrderType.MOST_TO_LEAST
            );
        }

        // Apply default product limits
        if (request.getSoldProductsLimit() == null) {
            request.setSoldProductsLimit(20);
        }

        if (request.getUnsoldProductsLimit() == null) {
            request.setUnsoldProductsLimit(20);
        }

        if (!ALLOWED_PRODUCT_LIMITS_REPORT_PDF.contains(
                request.getSoldProductsLimit())) {

            throw new InvalidStatisticsFilterException(
                    "Invalid sold products limit"
            );
        }

        if (!ALLOWED_PRODUCT_LIMITS_REPORT_PDF.contains(
                request.getUnsoldProductsLimit())) {

            throw new InvalidStatisticsFilterException(
                    "Invalid unsold products limit"
            );
        }

        request.setStartDate(normalizedDates[0]);
        request.setEndDate(normalizedDates[1]);

    }

    /**
     * Builds the complete data model required
     * to generate the statistics PDF report.
     *
     * <p>
     * This method retrieves and prepares all report data
     * according to the selected filters and report
     * configuration.
     * </p>
     *
     * <p>
     * Only the sections selected by the user are loaded.
     * Unselected sections are omitted from the resulting
     * report data object.
     * </p>
     *
     * <p>
     * The generated data may include:
     * <ul>
     *     <li>Report metadata</li>
     *     <li>Selected filter information</li>
     *     <li>Sales statistics</li>
     *     <li>Revenue and sales over time data</li>
     *     <li>Product statistics</li>
     *     <li>Detailed product lists</li>
     *     <li>Product ranking configuration labels</li>
     * </ul>
     * </p>
     *
     * <p>
     * The total sales information obtained during
     * data availability validation is reused to avoid
     * an additional database query.
     * </p>
     *
     * @param request normalized PDF request
     * @param totalSales previously validated total sales information
     * @return prepared report data used by the PDF generator
     */
    private StatisticsPdfDataDTO buildPdfData(
            StatisticsPdfRequestDTO request,
            TotalSalesResponseDTO totalSales
    ) {

        StatisticsPdfDataDTO data =
                new StatisticsPdfDataDTO();

        /*
         * Report metadata
         */
        data.setReportTitle(
                "Sales Statistics Report"
        );

        data.setGenerationDateTime(
                LocalDateTime.now()
        );

        data.setSelectedUser(
                request.getUserId() == null
                        ? "All Users"
                        : iUserRepository.findById(
                        request.getUserId()
                ).orElseThrow().getUserName()
        );

        data.setStartDate(
                request.getStartDate()
        );

        data.setEndDate(
                request.getEndDate()
        );

        data.setIncludeSalesInformation(
                request.getIncludeSalesInformation()
        );

        data.setIncludeProductInformation(
                request.getIncludeProductInformation()
        );

        /*
         * Sales Information
         */
        if (request.getIncludeSalesInformation()) {

            data.setTotalRevenue(
                    getTotalRevenue(
                            request.getUserId(),
                            request.getStartDate(),
                            request.getEndDate()
                    )
            );

            data.setTotalSales(totalSales);

            data.setAverageTicket(
                    getAverageTicket(
                            request.getUserId(),
                            request.getStartDate(),
                            request.getEndDate()
                    )
            );

            data.setPeakHours(
                    getPeakHours(
                            request.getUserId(),
                            request.getStartDate(),
                            request.getEndDate()
                    )
            );

            data.setSalesTimeSeries(
                    getSalesTimeSeries(
                            request.getUserId(),
                            request.getStartDate(),
                            request.getEndDate()
                    )
            );
        }

        /*
         * Product Information
         */
        if (request.getIncludeProductInformation()) {

            data.setTopProducts(
                    getTopProducts(
                            request.getUserId(),
                            request.getStartDate(),
                            request.getEndDate()
                    )
            );

            PageResponseDTO<SoldProductDTO> soldProductsPage =
                    getSoldProductsRanking(
                            request.getUserId(),
                            request.getStartDate(),
                            request.getEndDate(),
                            request.getMetric(),
                            request.getOrder(),
                            0,
                            request.getSoldProductsLimit()
                    );

            data.setSoldProductsMetric(
                    request.getMetric()
                            == ProductRankingMetric.QUANTITY_SOLD
                            ? "Quantity Sold"
                            : "Revenue Generated"
            );

            data.setSoldProductsOrder(
                    request.getOrder()
                            == ProductQuantityOrderType.MOST_TO_LEAST
                            ? "Most sold → least sold"
                            : "Least sold → most sold"
            );

            data.setSoldProducts(
                    soldProductsPage.getContent()
            );

            data.setTotalSoldProducts(
                    soldProductsPage.getTotalElements()
            );

            data.setIncludedSoldProducts(
                    soldProductsPage.getContent().size()
            );

            PageResponseDTO<UnsoldProductDTO> unsoldProductsPage =
                    getUnsoldProducts(
                            request.getUserId(),
                            request.getStartDate(),
                            request.getEndDate(),
                            0,
                            request.getUnsoldProductsLimit()
                    );

            data.setUnsoldProducts(
                    unsoldProductsPage.getContent()
            );

            data.setTotalUnsoldProducts(
                    unsoldProductsPage.getTotalElements()
            );

            data.setIncludedUnsoldProducts(
                    unsoldProductsPage.getContent().size()
            );
        }

        return data;
    }

    /**
     * Validates that data exists for the selected
     * filters before generating the report.
     *
     * <p>
     * This validation follows the same business rule
     * used by the statistics module.
     * </p>
     *
     * <p>
     * The total number of sales is used as the
     * availability indicator. If no sales exist
     * for the selected criteria, the report
     * generation process is aborted.
     * </p>
     *
     * <p>
     * The retrieved total sales information is returned
     * so it can be reused during report generation,
     * avoiding an additional database query.
     * </p>
     *
     * @param request normalized PDF request
     * @return total sales information used during report generation
     */
    private TotalSalesResponseDTO validateStatisticsDataAvailability(
            StatisticsPdfRequestDTO request
    ) {

        TotalSalesResponseDTO totalSales =
                getTotalSales(
                        request.getUserId(),
                        request.getStartDate(),
                        request.getEndDate()
                );

        if (totalSales.getTotalSales() == 0) {

            throw new NoStatisticsDataException(
                    "No data available for the selected criteria"
            );
        }

        return totalSales;
    }

    /**
     * Generates the final PDF document using the
     * previously prepared report data.
     *
     * <p>
     * This method orchestrates the complete PDF
     * rendering process, including:
     * <ul>
     *     <li>Document creation</li>
     *     <li>Report header rendering</li>
     *     <li>Selected filters rendering</li>
     *     <li>Sales Information section rendering</li>
     *     <li>Product Information section rendering</li>
     *     <li>Document finalization</li>
     * </ul>
     * </p>
     *
     * <p>
     * Only the sections selected in the report
     * configuration are included in the generated PDF.
     * </p>
     *
     * <p>
     * The document is generated entirely in memory
     * and returned as a byte array suitable for
     * download by the client.
     * </p>
     *
     * @param data fully prepared report data
     * @return generated PDF document as byte array
     */
    private byte[] generatePdfDocument(
            StatisticsPdfDataDTO data
    ) {

        try (
                ByteArrayOutputStream outputStream =
                        new ByteArrayOutputStream()
        ) {

            /*
             * Step 1:
             * Create PDF document.
             */
            Document document =
                    createDocument(outputStream);

            /*
             * Step 2:
             * Add report header.
             */
            addReportHeader(document, data);

            /*
             * Step 3:
             * Add selected filters information.
             */
            addFiltersInformation(document, data);

            /*
             * Step 4:
             * Add Sales Information section.
             */
            if (data.isIncludeSalesInformation()) {
                addSalesInformationSection(document, data);
            }

            /*
             * Step 5:
             * Add Product Information section.
             */
            if (data.isIncludeProductInformation()) {
                addProductInformationSection(document, data);
            }

            /*
             * Step 6:
             * Finalize document.
             */
            document.close();

            return outputStream.toByteArray();

        } catch (Exception e) {

            throw new PdfGenerationException(
                    "An error occurred while generating the PDF report"
            );
        }
    }

    /**
     * Creates and initializes the PDF document.
     *
     * <p>
     * This method configures:
     * <ul>
     *     <li>Document page size</li>
     *     <li>Document margins</li>
     *     <li>PDF writer binding</li>
     * </ul>
     * </p>
     *
     * <p>
     * The document is automatically opened before
     * being returned.
     * </p>
     *
     * @param outputStream target PDF output stream
     * @return initialized and opened PDF document
     * @throws DocumentException if the PDF document cannot be created
     */
    private Document createDocument(
            ByteArrayOutputStream outputStream
    ) throws DocumentException {

        Document document =
                new Document(PageSize.A4, 36, 36, 54, 36);

        PdfWriter.getInstance(document, outputStream);

        document.open();

        return document;
    }

    /**
     * Adds the report header section to the PDF document.
     *
     * <p>
     * The header contains the report metadata that is
     * always included in the generated document:
     * <ul>
     *     <li>Report title</li>
     *     <li>Report generation date and time</li>
     * </ul>
     * </p>
     *
     * <p>
     * This section is rendered at the beginning
     * of the report before any filter information
     * or statistics sections.
     * </p>
     *
     * @param document target PDF document
     * @param data prepared report data
     * @throws DocumentException if the content cannot be added
     */
    private void addReportHeader(
            Document document,
            StatisticsPdfDataDTO data
    ) throws DocumentException {

        Font titleFont =
                FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        20
                );

        Font metadataFont =
                FontFactory.getFont(
                        FontFactory.HELVETICA,
                        11
                );

        Paragraph title =
                new Paragraph(
                        data.getReportTitle(),
                        titleFont
                );

        title.setAlignment(Element.ALIGN_CENTER);

        document.add(title);

        document.add(Chunk.NEWLINE);

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(
                        "dd/MM/yyyy HH:mm:ss"
                );

        String formattedDate =
                data.getGenerationDateTime()
                        .format(formatter);

        Paragraph generationDate =
                new Paragraph(
                        "Generated on: " + formattedDate,
                        metadataFont
                );

        generationDate.setAlignment(Element.ALIGN_RIGHT);

        document.add(generationDate);

        document.add(Chunk.NEWLINE);
    }

    /**
     * Adds the selected filters section to the PDF document.
     *
     * <p>
     * This section displays the filtering criteria
     * used to generate the report:
     * <ul>
     *     <li>Selected user</li>
     *     <li>Selected date range</li>
     * </ul>
     * </p>
     *
     * <p>
     * The displayed information reflects exactly
     * the filters applied when the report was generated,
     * allowing the statistics contained in the document
     * to be properly contextualized.
     * </p>
     *
     * @param document target PDF document
     * @param data prepared report data
     * @throws DocumentException if the content cannot be added
     */
    private void addFiltersInformation(
            Document document,
            StatisticsPdfDataDTO data
    ) throws DocumentException {

        Font sectionTitleFont =
                FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        14
                );

        Font contentFont =
                FontFactory.getFont(
                        FontFactory.HELVETICA,
                        11
                );

        Paragraph sectionTitle =
                new Paragraph(
                        "Selected Filters",
                        sectionTitleFont
                );

        document.add(sectionTitle);

        document.add(Chunk.NEWLINE);

        Paragraph selectedUser =
                new Paragraph(
                        "User: "
                                + data.getSelectedUser(),
                        contentFont
                );

        document.add(selectedUser);

        Paragraph selectedDates =
                new Paragraph(
                        "Date Range: "
                                + data.getStartDate()
                                + " to "
                                + data.getEndDate(),
                        contentFont
                );

        document.add(selectedDates);

        document.add(Chunk.NEWLINE);
    }

    /**
     * Adds the complete Sales Information section
     * to the PDF document.
     *
     * <p>
     * This section includes:
     * <ul>
     *     <li>Sales summary statistics</li>
     *     <li>Peak sales hours information</li>
     *     <li>Revenue over time table</li>
     *     <li>Sales over time table</li>
     * </ul>
     * </p>
     *
     * <p>
     * All displayed information is calculated using
     * the filters selected when the report was generated.
     * </p>
     *
     * @param document target PDF document
     * @param data prepared report data
     * @throws DocumentException if PDF content cannot be added
     */
    private void addSalesInformationSection(
            Document document,
            StatisticsPdfDataDTO data
    ) throws DocumentException, IOException {

        /*
         * Step 1:
         * Add section title.
         */
        addSectionTitle(
                document,
                "Sales Information"
        );

        /*
         * Step 2:
         * Add sales summary statistics.
         */
        addSalesSummary(
                document,
                data
        );

        /*
         * Step 3:
         * Add peak sales hours information.
         */
        addPeakHoursInformation(
                document,
                data
        );

        /*
         * Step 4:
         * Add revenue over time statistics table.
         */
        addRevenueOverTimeTable(
                document,
                data.getSalesTimeSeries()
        );

        /*
         * Step 5:
         * Add sales over time statistics table.
         */
        addSalesOverTimeTable(
                document,
                data.getSalesTimeSeries()
        );
    }

    /**
     * Adds a section title to the PDF document.
     *
     * <p>
     * This helper method is used to visually separate
     * the major sections of the report.
     * </p>
     *
     * @param document target PDF document
     * @param title section title
     * @throws DocumentException if the content cannot be added
     */
    private void addSectionTitle(
            Document document,
            String title
    ) throws DocumentException {

        Font sectionFont =
                FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        16
                );

        Paragraph paragraph =
                new Paragraph(
                        title,
                        sectionFont
                );

        paragraph.setSpacingBefore(15f);
        paragraph.setSpacingAfter(10f);

        document.add(paragraph);
    }

    /**
     * Adds the sales summary subsection
     * to the PDF document.
     *
     * <p>
     * This subsection displays the main sales KPIs
     * calculated for the selected filters:
     * <ul>
     *     <li>Total revenue</li>
     *     <li>Total number of sales</li>
     *     <li>Average ticket value</li>
     * </ul>
     * </p>
     *
     * @param document target PDF document
     * @param data prepared report data
     * @throws DocumentException if the content cannot be added
     */
    private void addSalesSummary(
            Document document,
            StatisticsPdfDataDTO data
    ) throws DocumentException {

        Font contentFont =
                FontFactory.getFont(
                        FontFactory.HELVETICA,
                        11
                );

        Paragraph revenue =
                new Paragraph(
                        "Total Revenue: $"
                                + data.getTotalRevenue()
                                .getTotalRevenue(),
                        contentFont
                );

        Paragraph totalSales =
                new Paragraph(
                        "Total Sales: "
                                + data.getTotalSales()
                                .getTotalSales(),
                        contentFont
                );

        Paragraph averageTicket =
                new Paragraph(
                        "Average Ticket: $"
                                + data.getAverageTicket()
                                .getAverageTicket(),
                        contentFont
                );

        revenue.setSpacingAfter(5f);
        totalSales.setSpacingAfter(5f);
        averageTicket.setSpacingAfter(10f);

        document.add(revenue);
        document.add(totalSales);
        document.add(averageTicket);
    }

    /**
     * Adds the peak sales hours subsection
     * to the PDF document.
     *
     * <p>
     * This subsection displays:
     * <ul>
     *     <li>Hour with the highest revenue</li>
     *     <li>Hour with the highest number of sales</li>
     * </ul>
     * </p>
     *
     * <p>
     * Both values are calculated using the filters
     * selected when the report was generated.
     * </p>
     *
     * @param document target PDF document
     * @param data prepared report data
     * @throws DocumentException if the content cannot be added
     */
    private void addPeakHoursInformation(
            Document document,
            StatisticsPdfDataDTO data
    ) throws DocumentException {

        Font contentFont =
                FontFactory.getFont(
                        FontFactory.HELVETICA,
                        11
                );

        Paragraph highestRevenueHour =
                new Paragraph(
                        "Highest Revenue Hour: "
                                + data.getPeakHours()
                                .getHighestRevenueHour(),
                        contentFont
                );

        Paragraph highestSalesHour =
                new Paragraph(
                        "Highest Sales Hour: "
                                + data.getPeakHours()
                                .getHighestSalesHour(),
                        contentFont
                );

        highestRevenueHour.setSpacingAfter(5f);
        highestSalesHour.setSpacingAfter(10f);

        document.add(highestRevenueHour);
        document.add(highestSalesHour);
    }

    /**
     * Adds the revenue over time table
     * to the PDF document.
     *
     * <p>
     * This subsection displays revenue statistics
     * aggregated by time period.
     * </p>
     *
     * <p>
     * The time period granularity depends on the
     * selected date range and may represent:
     * <ul>
     *     <li>Hours</li>
     *     <li>Days</li>
     *     <li>Months</li>
     *     <li>Years</li>
     * </ul>
     * </p>
     *
     * <p>
     * Each row represents a time period and its
     * corresponding revenue generated.
     * </p>
     *
     * @param document target PDF document
     * @param salesTimeSeries sales time-series data
     * @throws DocumentException if the content cannot be added
     */
    private void addRevenueOverTimeTable(
            Document document,
            SalesTimeSeriesResponseDTO salesTimeSeries
    ) throws DocumentException {

        Paragraph title =
                new Paragraph(
                        "Revenue Over Time",
                        FontFactory.getFont(
                                FontFactory.HELVETICA_BOLD,
                                13
                        )
                );

        title.setSpacingBefore(10f);
        title.setSpacingAfter(10f);

        document.add(title);

        PdfPTable table =
                new PdfPTable(2);

        table.setWidthPercentage(100);

        table.addCell("Time Period");
        table.addCell("Revenue Generated");

        for (TimeSeriesPointDTO point
                : salesTimeSeries.getRevenueOverTime()) {

            table.addCell(point.getLabel());

            table.addCell(
                    "$" + point.getValue()
            );
        }

        document.add(table);

        document.add(Chunk.NEWLINE);
    }

    /**
     * Adds the sales over time table
     * to the PDF document.
     *
     * <p>
     * This subsection displays sales statistics
     * aggregated by time period.
     * </p>
     *
     * <p>
     * The time period granularity depends on the
     * selected date range and may represent:
     * <ul>
     *     <li>Hours</li>
     *     <li>Days</li>
     *     <li>Months</li>
     *     <li>Years</li>
     * </ul>
     * </p>
     *
     * <p>
     * Each row represents a time period and its
     * corresponding number of sales.
     * </p>
     *
     * @param document target PDF document
     * @param salesTimeSeries sales time-series data
     * @throws DocumentException if the content cannot be added
     */
    private void addSalesOverTimeTable(
            Document document,
            SalesTimeSeriesResponseDTO salesTimeSeries
    ) throws DocumentException {

        Paragraph title =
                new Paragraph(
                        "Sales Over Time",
                        FontFactory.getFont(
                                FontFactory.HELVETICA_BOLD,
                                13
                        )
                );

        title.setSpacingBefore(10f);
        title.setSpacingAfter(10f);

        document.add(title);

        PdfPTable table =
                new PdfPTable(2);

        table.setWidthPercentage(100);

        table.addCell("Time Period");
        table.addCell("Number of Sales");

        for (TimeSeriesPointDTO point
                : salesTimeSeries.getSalesOverTime()) {

            table.addCell(
                    point.getLabel()
            );

            table.addCell(
                    String.valueOf(
                            point.getValue()
                    )
            );
        }

        document.add(table);

        document.add(Chunk.NEWLINE);
    }

    /**
     * Adds the complete Product Information section
     * to the PDF document.
     *
     * <p>
     * This section includes:
     * <ul>
     *     <li>Top products by quantity sold</li>
     *     <li>Top products by revenue generated</li>
     *     <li>Sold products ranking list</li>
     *     <li>Unsold products list</li>
     * </ul>
     * </p>
     *
     * <p>
     * All statistics and product information are
     * calculated using the filters selected when
     * the report was generated.
     * </p>
     *
     * @param document target PDF document
     * @param data prepared report data
     * @throws DocumentException if PDF content cannot be added
     */
    private void addProductInformationSection(
            Document document,
            StatisticsPdfDataDTO data
    ) throws DocumentException {

        /*
         * Step 1:
         * Add section title.
         */
        addSectionTitle(
                document,
                "Product Information"
        );

        /*
         * Step 2:
         * Add top products summary.
         */
        addTopProductsSummary(
                document,
                data
        );

        /*
         * Step 3:
         * Add sold products ranking list.
         */
        addSoldProductsRanking(
                document,
                data
        );

        /*
         * Step 4:
         * Add unsold products list.
         */
        addUnsoldProductsList(
                document,
                data
        );
    }

    /**
     * Adds the top products summary subsection
     * to the PDF document.
     *
     * <p>
     * This subsection contains:
     * <ul>
     *     <li>Top products by quantity sold table</li>
     *     <li>Top products by revenue generated table</li>
     * </ul>
     * </p>
     *
     * <p>
     * These rankings provide a quick overview of the
     * best-performing products for the selected filters.
     * </p>
     *
     * @param document target PDF document
     * @param data prepared report data
     * @throws DocumentException if the content cannot be added
     */
    private void addTopProductsSummary(
            Document document,
            StatisticsPdfDataDTO data
    ) throws DocumentException {

        addTopProductsByQuantityTable(
                document,
                data.getTopProducts()
        );

        addTopProductsByRevenueTable(
                document,
                data.getTopProducts()
        );
    }

    /**
     * Adds the top products by quantity sold table
     * to the PDF document.
     *
     * <p>
     * This table displays the products with the
     * highest quantity sold for the selected filters.
     * </p>
     *
     * <p>
     * Each row includes:
     * <ul>
     *     <li>Product code</li>
     *     <li>Product name</li>
     *     <li>Quantity sold</li>
     * </ul>
     * </p>
     *
     * @param document target PDF document
     * @param topProducts top products statistics
     * @throws DocumentException if the content cannot be added
     */
    private void addTopProductsByQuantityTable(
            Document document,
            TopProductsResponseDTO topProducts
    ) throws DocumentException {

        Paragraph title =
                new Paragraph(
                        "Top Products by Quantity Sold",
                        FontFactory.getFont(
                                FontFactory.HELVETICA_BOLD,
                                13
                        )
                );

        title.setSpacingBefore(10f);
        title.setSpacingAfter(10f);

        document.add(title);

        PdfPTable table =
                new PdfPTable(3);

        table.setWidthPercentage(100);

        table.addCell("Code");
        table.addCell("Product");
        table.addCell("Quantity Sold");

        for (TopProductsByQuantityDTO product
                : topProducts.getTopProductsByQuantity()) {

            table.addCell(product.getProductCode());
            table.addCell(product.getProductName());
            table.addCell(
                    String.valueOf(
                            product.getQuantitySold()
                    )
            );
        }

        document.add(table);

        document.add(Chunk.NEWLINE);
    }

    /**
     * Adds the top products by revenue generated table
     * to the PDF document.
     *
     * <p>
     * Each row includes:
     * <ul>
     *     <li>Product code</li>
     *     <li>Product name</li>
     *     <li>Revenue generated</li>
     * </ul>
     * </p>
     *
     * @param document target PDF document
     * @param topProducts top products statistics
     * @throws DocumentException if content cannot be added
     */
    private void addTopProductsByRevenueTable(
            Document document,
            TopProductsResponseDTO topProducts
    ) throws DocumentException {

        Paragraph title =
                new Paragraph(
                        "Top Products by Revenue Generated",
                        FontFactory.getFont(
                                FontFactory.HELVETICA_BOLD,
                                13
                        )
                );

        title.setSpacingBefore(10f);
        title.setSpacingAfter(10f);

        document.add(title);

        PdfPTable table =
                new PdfPTable(3);

        table.setWidthPercentage(100);

        table.addCell("Code");
        table.addCell("Product");
        table.addCell("Revenue Generated");

        for (TopProductsByRevenueDTO product
                : topProducts.getTopProductsByRevenue()) {

            table.addCell(product.getProductCode());
            table.addCell(product.getProductName());
            table.addCell(
                    "$" + product.getRevenueGenerated()
            );
        }

        document.add(table);

        document.add(Chunk.NEWLINE);
    }

    /**
     * Adds the sold products ranking list
     * to the PDF document.
     *
     * <p>
     * This subsection includes:
     * <ul>
     *     <li>Selected ranking metric</li>
     *     <li>Selected ranking order</li>
     *     <li>Total matching sold products</li>
     *     <li>Number of included products</li>
     *     <li>Detailed sold products table</li>
     * </ul>
     * </p>
     *
     * <p>
     * The ranking configuration displayed in the report
     * reflects the metric and ordering selected when the
     * report was generated.
     * </p>
     *
     * @param document target PDF document
     * @param data prepared report data
     * @throws DocumentException if content cannot be added
     */
    private void addSoldProductsRanking(
            Document document,
            StatisticsPdfDataDTO data
    ) throws DocumentException {

        Paragraph title =
                new Paragraph(
                        "Sold Products Ranking",
                        FontFactory.getFont(
                                FontFactory.HELVETICA_BOLD,
                                13
                        )
                );

        title.setSpacingBefore(10f);
        title.setSpacingAfter(10f);

        document.add(title);

        Paragraph metadata =
                new Paragraph(
                        "Included Products: "
                                + data.getIncludedSoldProducts()
                                + " / "
                                + data.getTotalSoldProducts()
                );

        metadata.setSpacingAfter(10f);

        document.add(metadata);

        Paragraph rankingConfiguration =
                new Paragraph(
                        "Metric: "
                                + data.getSoldProductsMetric()
                                + " | Order: "
                                + data.getSoldProductsOrder()
                );

        rankingConfiguration.setSpacingAfter(10f);

        document.add(rankingConfiguration);

        PdfPTable table =
                new PdfPTable(4);

        table.setWidthPercentage(100);

        table.addCell("Code");
        table.addCell("Product");
        table.addCell("Quantity Sold");
        table.addCell("Revenue Generated");

        for (SoldProductDTO product
                : data.getSoldProducts()) {

            table.addCell(product.getProductCode());
            table.addCell(product.getProductName());
            table.addCell(
                    String.valueOf(
                            product.getQuantitySold()
                    )
            );
            table.addCell(
                    "$" + product.getRevenueGenerated()
            );
        }

        document.add(table);

        document.add(Chunk.NEWLINE);
    }

    /**
     * Adds the unsold products list
     * to the PDF document.
     *
     * <p>
     * This subsection includes:
     * <ul>
     *     <li>Total matching unsold products</li>
     *     <li>Number of included products</li>
     *     <li>Detailed unsold products table</li>
     * </ul>
     * </p>
     *
     * <p>
     * If no unsold products match the selected filters,
     * the message
     * "No unsold products for the selected filters"
     * is displayed instead of the table.
     * </p>
     *
     * @param document target PDF document
     * @param data prepared report data
     * @throws DocumentException if content cannot be added
     */
    private void addUnsoldProductsList(
            Document document,
            StatisticsPdfDataDTO data
    ) throws DocumentException {

        Paragraph title =
                new Paragraph(
                        "Unsold Products",
                        FontFactory.getFont(
                                FontFactory.HELVETICA_BOLD,
                                13
                        )
                );

        title.setSpacingBefore(10f);
        title.setSpacingAfter(10f);

        document.add(title);

        if (data.getUnsoldProducts().isEmpty()) {

            Paragraph emptyMessage =
                    new Paragraph(
                            "No unsold products for the selected filters"
                    );

            emptyMessage.setSpacingAfter(10f);

            document.add(emptyMessage);

            return;
        }

        Paragraph metadata =
                new Paragraph(
                        "Included Products: "
                                + data.getIncludedUnsoldProducts()
                                + " / "
                                + data.getTotalUnsoldProducts()
                );

        metadata.setSpacingAfter(10f);

        document.add(metadata);

        PdfPTable table =
                new PdfPTable(2);

        table.setWidthPercentage(100);

        table.addCell("Code");
        table.addCell("Product");

        for (UnsoldProductDTO product
                : data.getUnsoldProducts()) {

            table.addCell(product.getProductCode());
            table.addCell(product.getProductName());
        }

        document.add(table);

        document.add(Chunk.NEWLINE);
    }
}
