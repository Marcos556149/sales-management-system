package com.marcoscornejos.sales_management_system.service;

import com.marcoscornejos.sales_management_system.dto.*;
import com.marcoscornejos.sales_management_system.exception.InvalidSaleDataException;
import com.marcoscornejos.sales_management_system.exception.ProductNotFoundException;
import com.marcoscornejos.sales_management_system.exception.SaleNotFoundException;
import com.marcoscornejos.sales_management_system.exception.UserNotFoundException;
import com.marcoscornejos.sales_management_system.mapper.IPageResponseMapper;
import com.marcoscornejos.sales_management_system.mapper.ISaleCreateRequestMapper;
import com.marcoscornejos.sales_management_system.mapper.ISaleListResponseMapper;
import com.marcoscornejos.sales_management_system.mapper.ISaleWithDetailsResponseMapper;
import com.marcoscornejos.sales_management_system.model.*;
import com.marcoscornejos.sales_management_system.repository.IProductRepository;
import com.marcoscornejos.sales_management_system.repository.ISaleRepository;
import com.marcoscornejos.sales_management_system.repository.IUserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.util.function.Function;
import java.util.stream.Collectors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SaleService implements ISaleService{

    private final ISaleRepository iSaleRepository;
    private final IPageResponseMapper iPageResponseMapper;
    private final ISaleListResponseMapper iSaleListResponseMapper;
    private final ISaleWithDetailsResponseMapper iSaleWithDetailsResponseMapper;
    private final IUserRepository iUserRepository;
    private final IProductRepository iProductRepository;

    /**
     * Retrieves a paginated list of sales applying:
     * <ul>
     *     <li>Optional search by sale identifier</li>
     *     <li>Filter by sale date</li>
     *     <li>Chronological sorting by sale time</li>
     *     <li>Pagination (page number and size)</li>
     * </ul>
     *
     * <p>
     * Search by sale identifier is optional and ignored if null.
     * If no date is provided, the current date is used by default.
     * Pagination and sorting are executed at database level.
     * </p>
     *
     * @param searchSaleId Optional sale identifier
     * @param date Sale date filter (if null, current date is used)
     * @param timeSort Sorting direction (NEWEST_FIRST / OLDEST_FIRST)
     * @param page Page number (0-based)
     * @param size Number of elements per page
     * @return Paginated list of sales mapped to DTO
     */
    @Override
    public PageResponseDTO<SaleListResponseDTO> getSales(Long searchSaleId,
                                                         LocalDate date,
                                                         SortDirection timeSort,
                                                         int page,
                                                         int size) {

        // Validate pagination parameters
        if (page < 0) {
            throw new InvalidSaleDataException(
                    "Page index must not be negative",
                    "page"
            );
        }

        if (size <= 0) {
            throw new InvalidSaleDataException(
                    "Page size must be greater than zero",
                    "size"
            );
        }

        if (size > 50) {
            throw new InvalidSaleDataException(
                    "Page size must not exceed 50",
                    "size"
            );
        }

        // Validate optional search parameter
        if (searchSaleId != null && searchSaleId <= 0) {
            throw new InvalidSaleDataException(
                    "Sale ID must be greater than zero",
                    "searchSaleId"
            );
        }

        // Business rule: default date = current date
        if (date == null) {
            date = LocalDate.now();
        }

        // Build sorting configuration (by sale time)
        Sort sort = Sort.by("saleTime");

        if (timeSort == SortDirection.NEWEST_FIRST) {
            sort = sort.descending();
        } else {
            sort = sort.ascending();
        }

        // Pagination configuration (server-side)
        Pageable pageable = PageRequest.of(page, size, sort);

        // Execute query with search, filtering and pageable
        Page<Sale> salePage = iSaleRepository.findSales(
                searchSaleId,
                date,
                pageable
        );


        // Total number of sales in database without filters.
        // This value is only calculated when the filtered query returns no results,
        // allowing the frontend to distinguish between:
        //
        // 1) No sales exist in the database
        // 2) Sales exist, but none match the selected date filter
        //
        // When filtered results exist, this value remains null to avoid an
        // unnecessary extra COUNT(*) query and improve performance.
        Long totalGlobalElements = null;

        if (salePage.getTotalElements() == 0) {
            totalGlobalElements = iSaleRepository.count();
        }

        // Map entities to DTOs using MapStruct
        return iPageResponseMapper.toPageResponseDTO(
                salePage.getContent()
                        .stream()
                        .map(iSaleListResponseMapper::toDto)
                        .toList(),
                salePage.getNumber(),
                salePage.getSize(),
                salePage.getTotalPages(),
                salePage.getTotalElements(),
                totalGlobalElements
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
                .orElseThrow(() -> new SaleNotFoundException(
                        String.format("Sale with ID '%s' not found", saleId)
                ));

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

        List<EnumDTO> timeSortOptions = Arrays.stream(SortDirection.values())
                .map(sort -> new EnumDTO(
                        sort.name(),
                        sort.getDisplayName()
                ))
                .toList();

        return new SaleFiltersResponseDTO(timeSortOptions);
    }

    /**
     * Registers a new sale in the system.
     *
     * <p>
     * Validates that all requested products exist, are active, and have sufficient
     * stock before creating the sale. If the same product appears multiple times
     * in the request, quantities are consolidated into a single line item.
     * </p>
     *
     * <p>
     * Each sale is associated with the user performing the operation, and the
     * total sale amount is calculated based on product prices at the time of sale.
     * </p>
     *
     * <p>
     * The operation is executed within a transactional context to ensure atomic
     * persistence of the sale and its details.
     * </p>
     *
     * @param request the sale creation request containing products and quantities
     *
     * @throws UserNotFoundException if the user performing the sale does not exist
     * @throws ProductNotFoundException if any product does not exist
     * @throws InvalidSaleDataException if a product is inactive, has invalid quantity,
     *                                  or insufficient stock is available
     */
    @Override
    @Transactional
    public void registerSale(SaleCreateRequestDTO request) {

        // Temporary default user until Spring Security is implemented
        User user = iUserRepository.findById(1L)
                .orElseThrow(() ->
                        new UserNotFoundException("Authenticated user not found")
                );

    /*
    // Future implementation with authenticated user
    String username = SecurityContextHolder.getContext()
            .getAuthentication()
            .getName();

    User user = iUserRepository.findByUserName(username)
            .orElseThrow(() ->
                    new UserNotFoundException("Authenticated user not found")
            );
    */

        // Consolidate repeated product codes into a single line item
        Map<String, BigDecimal> groupedDetails = new LinkedHashMap<>();

        for (SaleDetailCreateRequestDTO detail : request.getSaleDetails()) {
            groupedDetails.merge(
                    detail.getProductCode(),
                    detail.getProductQuantity(),
                    BigDecimal::add
            );
        }

        // Build normalized and validated sale details list
        List<SaleDetail> saleDetails = new ArrayList<>();

        // Load all requested products in a single query
        List<String> productCodes = new ArrayList<>(groupedDetails.keySet());

        Map<String, Product> productsByCode = iProductRepository
                .findAllById(productCodes)
                .stream()
                .collect(Collectors.toMap(
                        Product::getProductCode,
                        Function.identity()
                ));

        // Validate each requested product and build normalized sale details
        for (Map.Entry<String, BigDecimal> entry : groupedDetails.entrySet()) {

            String productCode = entry.getKey();
            BigDecimal quantity = entry.getValue();

            Product product = productsByCode.get(productCode);

            if (product == null) {
                throw new ProductNotFoundException(
                        "Product with code '" + productCode + "' not found"
                );
            }

            String productLabel = product.getProductCode()
                    + " - "
                    + product.getProductName();

            // Only active products can be sold
            if (product.getProductStatus() != ProductStatus.ACTIVE) {
                throw new InvalidSaleDataException(
                        "Product '" + productLabel + "' is inactive and cannot be added to the sale",
                        "productCode"
                );
            }

            // Products sold by units do not allow decimal quantities
            if (product.getUnitOfMeasure() == UnitOfMeasure.UNITS
                    && quantity.stripTrailingZeros().scale() > 0) {

                throw new InvalidSaleDataException(
                        "Product '" + productLabel + "' only accepts whole numbers because it is sold by units",
                        "productQuantity"
                );
            }



            // Requested quantity must not exceed available stock
            if (product.getProductStock().compareTo(quantity) < 0) {
                throw new InvalidSaleDataException(
                        "Insufficient stock for product " + productLabel,
                        "productQuantity"
                );
            }







            // Create sale detail using current product price and unit of measure snapshot
            SaleDetail saleDetail = new SaleDetail();
            saleDetail.setProduct(product);
            saleDetail.setProductQuantity(quantity);
            saleDetail.setSalePrice(product.getProductPrice());
            saleDetail.setUnitOfMeasureAtSale(product.getUnitOfMeasure());

            saleDetails.add(saleDetail);
        }

        // Create sale entity and associate user
        Sale sale = new Sale();
        sale.setUser(user);

        // Synchronize bidirectional relationship between Sale and SaleDetail
        for (SaleDetail detail : saleDetails) {
            detail.setSale(sale);
            sale.getSaleDetails().add(detail);
        }

        // Persist sale and sale details (handled via CascadeType.PERSIST)
        // Stock updates and final total recalculation are managed by database triggers,
        // ensuring consistency at persistence level.
        iSaleRepository.save(sale);
    }
}
