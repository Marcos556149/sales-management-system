package com.marcoscornejos.sales_management_system.service;

import com.marcoscornejos.sales_management_system.dto.*;
import com.marcoscornejos.sales_management_system.model.ProductQuantityOrderType;
import com.marcoscornejos.sales_management_system.model.ProductRankingMetric;

import java.time.LocalDate;
import java.util.List;

public interface IStatisticsService {

    List<UserFilterDTO> getStatisticsFilterUsers();

    ProductRankingFiltersResponseDTO getProductRankingFilters();

    TotalRevenueResponseDTO getTotalRevenue(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    );

    TotalSalesResponseDTO getTotalSales(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    );

    AverageTicketResponseDTO getAverageTicket(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    );

    PeakHoursResponseDTO getPeakHours(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    );

    SalesTimeSeriesResponseDTO getSalesTimeSeries(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    );

    TopProductsResponseDTO getTopProducts(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    );

    PageResponseDTO<SoldProductDTO> getSoldProductsRanking(
            Long userId,
            LocalDate startDate,
            LocalDate endDate,
            ProductRankingMetric metric,
            ProductQuantityOrderType order,
            int page,
            int size
    );

    PageResponseDTO<UnsoldProductDTO> getUnsoldProducts(
            Long userId,
            LocalDate startDate,
            LocalDate endDate,
            int page,
            int size
    );

    byte[] generatePdf(StatisticsPdfRequestDTO request);
}
