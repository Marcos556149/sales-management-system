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
    private final ISaleCreateRequestMapper iSaleCreateRequestMapper;

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

    /**
     * Registers a new sale in the system.
     *
     * <p>
     * This operation validates that all requested products exist, are active,
     * and have sufficient stock available. If the same product is repeated
     * multiple times in the request, quantities are consolidated into a single
     * sale detail before persistence.
     * </p>
     *
     * <p>
     * The sale is associated with the user who performs the operation.
     * Sale date, sale time, and total amount are automatically initialized
     * by the entity configuration. Stock updates and total recalculation are
     * managed by database triggers.
     * </p>
     *
     * <p>
     * Executes the operation within a transactional context to ensure that
     * the sale and all its details are stored atomically.
     * </p>
     *
     * @param request the sale creation request containing products and quantities
     * @throws UserNotFoundException if the assigned user does not exist
     * @throws ProductNotFoundException if any product does not exist
     * @throws InvalidSaleDataException if any product is inactive or stock is insufficient
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

        // Consolidate repeated products by summing quantities
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

        for (Map.Entry<String, BigDecimal> entry : groupedDetails.entrySet()) {

            String productCode = entry.getKey();
            BigDecimal quantity = entry.getValue();

            Product product = iProductRepository.findById(productCode)
                    .orElseThrow(() ->
                            new ProductNotFoundException(
                                    "Product not found: " + productCode
                            )
                    );

            if (product.getProductStatus() != ProductStatus.ACTIVE) {
                throw new InvalidSaleDataException(
                        "Product is inactive: " +
                                product.getProductCode() +
                                " - " +
                                product.getProductName()
                );
            }

            if (product.getUnitOfMeasure() == UnitOfMeasure.UNITS) {
                if (quantity.stripTrailingZeros().scale() > 0) {
                    throw new InvalidSaleDataException(
                            "The product " +
                                    product.getProductName() +
                                    " only accepts whole numbers because it is sold by unit."
                    );
                }
            }

            if (product.getProductStock().compareTo(quantity) < 0) {
                throw new InvalidSaleDataException(
                        "Insufficient stock for product: " +
                                product.getProductCode() +
                                " - " +
                                product.getProductName()
                );
            }

            // Create sale detail with validated data and resolved product price
            // This ensures sale_price is never null (database NOT NULL constraint)
            SaleDetail detail = new SaleDetail();
            detail.setProduct(product);
            detail.setProductQuantity(quantity);
            detail.setSalePrice(product.getProductPrice());

            saleDetails.add(detail);
        }

        // Create sale entity and associate user
        Sale sale = new Sale();
        sale.setUser(user);
        sale.setSaleDetails(new ArrayList<>());

        // Synchronize bidirectional relationship between Sale and SaleDetail
        for (SaleDetail detail : saleDetails) {
            detail.setSale(sale);
            sale.getSaleDetails().add(detail);
        }

        // Persist sale and sale details (handled via CascadeType.PERSIST)
        // Stock updates and total recalculation are managed by database triggers
        iSaleRepository.save(sale);
    }
}
