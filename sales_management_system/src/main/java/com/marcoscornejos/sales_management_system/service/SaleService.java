package com.marcoscornejos.sales_management_system.service;

import com.marcoscornejos.sales_management_system.dto.*;
import com.marcoscornejos.sales_management_system.exception.InvalidSaleDataException;
import com.marcoscornejos.sales_management_system.exception.SaleNotFoundException;
import com.marcoscornejos.sales_management_system.mapper.IPageResponseMapper;
import com.marcoscornejos.sales_management_system.mapper.ISaleListResponseMapper;
import com.marcoscornejos.sales_management_system.mapper.ISaleWithDetailsResponseMapper;
import com.marcoscornejos.sales_management_system.model.Sale;
import com.marcoscornejos.sales_management_system.model.SortOrder;
import com.marcoscornejos.sales_management_system.repository.ISaleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SaleService implements ISaleService{

    private final ISaleRepository iSaleRepository;
    private final IPageResponseMapper iPageResponseMapper;
    private final ISaleListResponseMapper iSaleListResponseMapper;
    private final ISaleWithDetailsResponseMapper iSaleWithDetailsResponseMapper;

    /**
     * Retrieves a paginated list of sales applying:
     * <ul>
     *     <li>Filter by sale date (required business filter)</li>
     *     <li>Sorting by sale time</li>
     *     <li>Pagination (page number and size)</li>
     * </ul>
     *
     * <p>
     * The filter by date is mandatory at business level. If no date is provided,
     * the system uses the current date as default.
     * Pagination and sorting are executed at database level (server-side pagination).
     * </p>
     *
     * @param date     Sale date filter (if null, current date is used)
     * @param timeSort Sorting order by sale time (ASCENDING / DESCENDING)
     * @param page     Page number (0-based)
     * @param size     Number of elements per page
     * @return Paginated list of sales mapped to DTO
     */
    @Override
    public PageResponseDTO<SaleListResponseDTO> getSales(LocalDate date,
                                                         SortOrder timeSort,
                                                         int page,
                                                         int size) {

        // Validate pagination parameters
        if (page < 0) {
            throw new InvalidSaleDataException("Page index must not be negative");
        }

        if (size <= 0) {
            throw new InvalidSaleDataException("Page size must be greater than zero");
        }

        if (size > 50) {
            throw new InvalidSaleDataException("Page size must not exceed 50");
        }

        // Business rule: default date = current date
        if (date == null) {
            date = LocalDate.now();
        }

        // Build sorting configuration (by sale time)
        Sort sort = Sort.by("saleTime");

        if (timeSort == SortOrder.DESCENDING) {
            sort = sort.descending();
        } else {
            sort = sort.ascending();
        }

        // Pagination configuration (server-side)
        Pageable pageable = PageRequest.of(page, size, sort);

        // Execute query with filtering and pageable
        Page<Sale> salePage = iSaleRepository.findSales(
                date,
                pageable
        );

        // Map entities to DTOs using MapStruct
        return iPageResponseMapper.toPageResponseDTO(
                salePage.getContent()
                        .stream()
                        .map(iSaleListResponseMapper::toDto)
                        .toList(),
                salePage.getNumber(),
                salePage.getSize(),
                salePage.getTotalPages(),
                salePage.getTotalElements()
        );
    }

    /**
     * Retrieves a sale by its unique identifier.
     *
     * <p>
     * Searches for a sale in the database including its details and products.
     * Throws an exception if the sale does not exist.
     * </p>
     *
     * @param saleId the unique identifier of the sale
     * @return the sale details as a DTO
     * @throws SaleNotFoundException if the sale is not found
     */
    @Override
    @Transactional
    public SaleWithDetailsResponseDTO getSaleById(Long saleId) {

        Sale sale = iSaleRepository.findByIdWithDetailsAndProducts(saleId)
                .orElseThrow(() -> new SaleNotFoundException("Sale not found"));

        return iSaleWithDetailsResponseMapper.toDto(sale);
    }

    /**
     * Builds and returns available sorting options for sales.
     *
     * <p>
     * This method extracts values from {@link SortOrder} enum
     * and converts them into a frontend-friendly {@link EnumDTO} format.
     * </p>
     *
     * <p>
     * It ensures that the frontend always receives up-to-date options
     * without requiring code changes on the client side.
     * </p>
     *
     * @return SaleFiltersResponseDTO containing sort options
     */
    @Override
    public SaleFiltersResponseDTO getFilters() {

        List<EnumDTO> sortOptions = Arrays.stream(SortOrder.values())
                .map(sort -> new EnumDTO(
                        sort.name(),
                        sort.getDisplayName()
                ))
                .toList();

        SaleFiltersResponseDTO dto = new SaleFiltersResponseDTO();
        dto.setSortOptions(sortOptions);

        return dto;
    }
}
