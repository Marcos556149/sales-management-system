package com.marcoscornejos.sales_management_system.controller;

import com.marcoscornejos.sales_management_system.dto.*;
import com.marcoscornejos.sales_management_system.model.SortDirection;
import com.marcoscornejos.sales_management_system.service.ISaleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
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

    private final ISaleService iSaleService;

    /**
     * Retrieves a paginated list of sales registered in the system.
     *
     * <p>
     * Each sale includes general information such as identifier, sale date and time,
     * seller username, and total amount.
     * </p>
     *
     * <p>
     * Supports server-side pagination, filtering by date, and chronological sorting.
     * </p>
     * @param searchSaleId Optional sale identifier search
     * @param date Sale date filter (defaults to current date if not provided)
     * @param timeSort Sorting direction by sale time
     *                 (NEWEST_FIRST or OLDEST_FIRST, default: NEWEST_FIRST)
     * @param page Page number (default: 0)
     * @param size Number of sales per page (default: 50)
     * @return A paginated response containing sales and pagination metadata
     */
    @GetMapping
    public ResponseEntity<PageResponseDTO<SaleListResponseDTO>> getSales(
            @RequestParam(required = false) Long searchSaleId,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,

            @RequestParam(defaultValue = "NEWEST_FIRST")
            SortDirection timeSort,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "50")
            int size
    ) {
        PageResponseDTO<SaleListResponseDTO> response = iSaleService.getSales(
                searchSaleId,
                date,
                timeSort,
                page,
                size
        );

        return ResponseEntity.ok(response);
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

        SaleWithDetailsResponseDTO sale = iSaleService.getSaleById(saleId);

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
     * @return ResponseEntity containing SaleFiltersResponseDTO
     */
    @GetMapping("/filters")
    public ResponseEntity<SaleFiltersResponseDTO> getFilters() {

        SaleFiltersResponseDTO response = iSaleService.getFilters();

        return ResponseEntity.ok(response);
    }

    /**
     * Registers a new sale in the system.
     *
     * <p>
     * Creates a sale with its details, automatically assigning date, time,
     * total amount, and the authenticated user. The sale must contain at least
     * one product, and all business rules (stock, product status, quantity)
     * are validated during processing.
     * </p>
     *
     * @param request the sale data including products and quantities
     * @return a standardized success response confirming the sale registration
     */
    @PostMapping
    public ResponseEntity<SuccessResponseDTO<Void>> registerSale(
            @RequestBody @Valid SaleCreateRequestDTO request
    ) {

        iSaleService.registerSale(request);

        SuccessResponseDTO<Void> response = new SuccessResponseDTO<>(
                "SALE_CREATED",
                "Sale successfully registered",
                null
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}