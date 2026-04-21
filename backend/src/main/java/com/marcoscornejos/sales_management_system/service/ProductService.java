package com.marcoscornejos.sales_management_system.service;

import com.marcoscornejos.sales_management_system.dto.*;
import com.marcoscornejos.sales_management_system.exception.InvalidProductDataException;
import com.marcoscornejos.sales_management_system.exception.ProductAlreadyExistsException;
import com.marcoscornejos.sales_management_system.exception.ProductNotFoundException;
import com.marcoscornejos.sales_management_system.mapper.*;
import com.marcoscornejos.sales_management_system.model.Product;
import com.marcoscornejos.sales_management_system.model.ProductStatus;
import com.marcoscornejos.sales_management_system.model.SortOrder;
import com.marcoscornejos.sales_management_system.model.UnitOfMeasure;
import com.marcoscornejos.sales_management_system.repository.IProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;


/**
 * Service responsible for retrieving products with search,
 * filtering, and sorting applied at database level.
 */
@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {

    private final IProductRepository iProductRepository;
    private final IProductListResponseMapper iProductListResponseMapper;
    private final IPageResponseMapper iPageResponseMapper;
    private final IProductDetailResponseMapper iProductDetailResponseMapper;
    private final IProductCreateRequestMapper iProductCreateRequestMapper;
    private final IProductUpdateRequestMapper iProductUpdateRequestMapper;

    /**
     * Retrieves a paginated list of products applying:
     * <ul>
     * <li>Search by name or code</li>
     * <li>Filter by status (ALL = no filter)</li>
     * <li>Sorting by name</li>
     * <li>Pagination (page number and size)</li>
     * </ul>
     *
     * <p>
     * Search is optional and ignored if null or blank.
     * Pagination and sorting are executed at database level (server-side pagination).
     * </p>
     *
     * @param searchCodeOrName Optional search term
     * @param statusFilter     Product status filter
     * @param nameSort         Sorting order (ASCENDING / DESCENDING)
     * @param page             Page number (0-based)
     * @param size             Number of elements per page
     * @return Paginated list of products mapped to DTO
     */
    @Override
    public PageResponseDTO<ProductListResponseDTO> getProducts(String searchCodeOrName,
                                                               ProductStatus statusFilter,
                                                               SortOrder nameSort,
                                                               int page,
                                                               int size) {

        // Validate pagination parameters
        if (page < 0) {
            throw new InvalidProductDataException("Page index must not be negative");
        }

        if (size <= 0) {
            throw new InvalidProductDataException("Page size must be greater than zero");
        }

        if (size > 50) {
            throw new InvalidProductDataException("Page size must not exceed 50");
        }

        // Build sorting configuration (applied at database level)
        Sort sort = Sort.by("productName");

        if (nameSort == SortOrder.DESCENDING) {
            sort = sort.descending();
        } else {
            sort = sort.ascending();
        }

        // Normalize search input:
        // Convert empty or blank strings into null so the query ignores the search
        // filter
        if (searchCodeOrName != null && searchCodeOrName.trim().isEmpty()) {
            searchCodeOrName = null;
        }


        // Creates a pagination configuration including page number,
        // page size, and sorting criteria to be applied at database level.
        Pageable pageable = PageRequest.of(page, size, sort);

        // Execute query with search, filtering, and pageable
        Page<Product> productPage = iProductRepository.findProducts(
                searchCodeOrName,
                statusFilter,
                pageable
        );

        // Map entities to DTOs
        return iPageResponseMapper.toPageResponseDTO(
                productPage.getContent()
                        .stream()
                        .map(iProductListResponseMapper::toDto)
                        .toList(),
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalPages(),
                productPage.getTotalElements()
        );
    }

    /**
     * Builds and returns available filter and sorting options for products.
     *
     * <p>This method extracts values from {@link ProductStatus} and
     * {@link SortOrder} enums and converts them into a frontend-friendly
     * {@link EnumDTO} format.</p>
     *
     * <p>It ensures that the frontend always receives up-to-date options
     * without requiring code changes on the client side.</p>
     *
     * @return ProductFiltersResponseDTO containing status and sort options
     */
    @Override
    public ProductFiltersResponseDTO getFilters() {
        List<EnumDTO> statusOptions = Arrays.stream(ProductStatus.values())
                .map(status -> new EnumDTO(
                        status.name(),
                        status.getDisplayName()
                ))
                .toList();

        List<EnumDTO> sortOptions = Arrays.stream(SortOrder.values())
                .map(sort -> new EnumDTO(
                        sort.name(),
                        sort.getDisplayName()
                ))
                .toList();

        ProductFiltersResponseDTO dto = new ProductFiltersResponseDTO();
        dto.setStatusOptions(statusOptions);
        dto.setSortOptions(sortOptions);

        return dto;
    }

    /**
     * Retrieves a product by its unique code.
     *
     * <p>
     * Searches for a product in the database using the provided code.
     * Throws an exception if the product does not exist.
     * </p>
     *
     * @param productCode the unique identifier of the product
     * @return the product details as a DTO
     * @throws ProductNotFoundException if the product is not found
     */
    @Override
    public ProductDetailResponseDTO getProductByCode(String productCode) {

        Product product = iProductRepository.findById(productCode)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        return iProductDetailResponseMapper.toDto(product);
    }

    /**
     * Deactivates a product by setting its status to INACTIVE.
     *
     * <p>
     * This operation performs a logical deletion. If the product does not exist
     * or is already inactive, an exception is thrown.
     * </p>
     *
     * Executes the operation within a transactional context to ensure
     * that the product status update is applied atomically.
     *
     * @param productCode the unique identifier of the product
     * @throws ProductNotFoundException if the product does not exist
     * @throws InvalidProductDataException if the product is already inactive
     */
    @Override
    @Transactional
    public void deactivateProduct(String productCode) {

        Product product = iProductRepository.findById(productCode)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        if (product.getProductStatus() == ProductStatus.INACTIVE) {
            throw new InvalidProductDataException("Product is already inactive");
        }

        product.setProductStatus(ProductStatus.INACTIVE);

        iProductRepository.save(product);
    }

    /**
     * Activates a product by setting its status to ACTIVE.
     *
     * <p>
     * This operation restores a previously deactivated product. If the product does not exist
     * or is already active, an exception is thrown.
     * </p>
     *
     * Executes the operation within a transactional context to ensure
     * that the product status update is applied atomically.
     *
     * @param productCode the unique identifier of the product
     * @throws ProductNotFoundException if the product does not exist
     * @throws InvalidProductDataException if the product is already active
     */
    @Override
    @Transactional
    public void activateProduct(String productCode) {
        Product product = iProductRepository.findById(productCode)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        if (product.getProductStatus() == ProductStatus.ACTIVE) {
            throw new InvalidProductDataException("Product is already active");
        }

        product.setProductStatus(ProductStatus.ACTIVE);

        iProductRepository.save(product);
    }

    /**
     * Registers a new product in the system.
     *
     * <p>
     * This operation validates that the product does not already exist
     * and persists it in the database. The product status is automatically
     * set to ACTIVE.
     * </p>
     *
     * <p>
     * If a product with the same code already exists, an exception is thrown.
     * </p>
     *
     * Executes the operation within a transactional context to ensure
     * that the product creation is applied atomically.
     *
     * @param request the product creation request containing product data
     * @throws ProductAlreadyExistsException if a product with the same code already exists
     */
    @Override
    @Transactional
    public void registerProduct(ProductCreateRequestDTO request) {

        // Check if product already exists
        if (iProductRepository.existsById(request.getProductCode())) {
            throw new ProductAlreadyExistsException("Product is already registered");
        }

        // Validates that stock is integer when unit of measure is UNITS
        if (request.getUnitOfMeasure() == UnitOfMeasure.UNITS) {
            if (request.getProductStock().stripTrailingZeros().scale() > 0) {
                throw new InvalidProductDataException(
                        "Stock must be an integer value when unit of measure is Units"
                );
            }
        }

        // Map DTO to entity
        Product product = iProductCreateRequestMapper.toProduct(request);

        // Persist product
        iProductRepository.save(product);
    }

    /**
     * Retrieves metadata required for product-related operations.
     *
     * <p>
     * Includes available unit of measure options for product creation.
     * </p>
     *
     * @return product metadata
     */
    @Override
    public ProductMetadataResponseDTO getProductMetadata() {

        List<EnumDTO> unitOfMeasureOptions = Arrays.stream(UnitOfMeasure.values())
                .map(unit -> new EnumDTO(
                        unit.name(),
                        unit.getDisplayName()
                ))
                .toList();

        return new ProductMetadataResponseDTO(unitOfMeasureOptions);
    }

    /**
     * Updates an existing product in the system.
     *
     * <p>
     * This operation validates that the product exists and that the updated data
     * complies with business rules before persisting the changes.
     * </p>
     *
     * <p>
     * Validations include:
     * - Product existence
     * - Stock constraints based on unit of measure
     * </p>
     *
     * Executes the operation within a transactional context to ensure
     * that the update is applied atomically.
     *
     * @param productCode the current code of the product to update
     * @param request the product update request containing new data
     * @throws ProductNotFoundException if the product does not exist
     * @throws InvalidProductDataException if the data does not comply with business rules
     */
    @Override
    @Transactional
    public void updateProduct(String productCode, ProductUpdateRequestDTO request) {

        // 1. Check if product exists
        Product product = iProductRepository.findById(productCode)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        // 2. Validate stock based on unit of measure
        if (request.getUnitOfMeasure() == UnitOfMeasure.UNITS) {
            if (request.getProductStock().stripTrailingZeros().scale() > 0) {
                throw new InvalidProductDataException(
                        "Stock must be an integer value when unit of measure is Units"
                );
            }
        }

        // 3. Apply updates
        iProductUpdateRequestMapper.updateProductFromDto(request, product);

        // 4. Persist changes
        iProductRepository.save(product);
    }
}