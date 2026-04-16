package com.marcoscornejos.sales_management_system.controller;

import com.marcoscornejos.sales_management_system.dto.PageResponseDTO;
import com.marcoscornejos.sales_management_system.dto.SaleFiltersResponseDTO;
import com.marcoscornejos.sales_management_system.dto.SaleListResponseDTO;
import com.marcoscornejos.sales_management_system.dto.SaleWithDetailsResponseDTO;
import com.marcoscornejos.sales_management_system.model.SortOrder;
import com.marcoscornejos.sales_management_system.service.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * REST controller responsible for handling sale queries.
 *
 * <p>
 * Provides endpoints to retrieve sales with optional filtering by date,
 * sorting by time, and pagination support.
 * </p>
 */
@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService saleService;

    /**
     * Retrieves a paginated list of sales registered in the system.
     *
     * <p>
     * Each sale includes general information such as identifier, date and time,
     * seller username, and total amount.
     * </p>
     *
     * <p>
     * Supports server-side pagination and allows sorting by sale time.
     * </p>
     *
     * @param date Sale date filter (day, month, and year)
     * @param timeSort Sorting order by sale time (ASCENDING or DESCENDING)
     * @param page Page number (default: 0)
     * @param size Number of sales per page (default: 50)
     * @return A paginated response containing sales and pagination metadata
     */
    @GetMapping
    public PageResponseDTO<SaleListResponseDTO> getSales(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,

            @RequestParam(defaultValue = "DESCENDING")
            SortOrder timeSort,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "50")
            int size
    ) {
        return saleService.getSales(date, timeSort, page, size);
    }

    /**
     * Retrieves detailed information of a specific sale by its identifier.
     *
     * <p>
     * This endpoint allows clients to fetch a single sale's data,
     * including its identifier, date, time, seller username,
     * total amount, and the list of sold products with their details.
     * </p>
     *
     * @param saleId the unique identifier of the sale
     * @return the sale details as a response DTO
     */
    @GetMapping("/{saleId}")
    public ResponseEntity<SaleWithDetailsResponseDTO> getSaleById(
            @PathVariable Long saleId
    ) {

        SaleWithDetailsResponseDTO sale = saleService.getSaleById(saleId);

        return ResponseEntity.ok(sale);
    }

    /**
     * Retrieves available sorting options for sales.
     *
     * <p>
     * This endpoint provides dynamic configuration data for the frontend,
     * including sorting options by sale time.
     * It avoids hardcoded values in the client application.
     * </p>
     *
     * @return SaleFiltersResponseDTO containing sort options
     */
    @GetMapping("/filters")
    public SaleFiltersResponseDTO getFilters() {
        return saleService.getFilters();
    }
}