package com.marcoscornejos.sales_management_system.service;

import com.marcoscornejos.sales_management_system.dto.*;
import com.marcoscornejos.sales_management_system.exception.InvalidProductDataException;
import com.marcoscornejos.sales_management_system.exception.ProductAlreadyExistsException;
import com.marcoscornejos.sales_management_system.exception.ProductNotFoundException;
import com.marcoscornejos.sales_management_system.mapper.*;
import com.marcoscornejos.sales_management_system.model.*;
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
     * <li>Filter by stock level (ALL = no filter)</li>
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
     * @param stockFilter      Product stock level filter
     * @param nameSort         Sorting order (ASCENDING / DESCENDING)
     * @param page             Page number (0-based)
     * @param size             Number of elements per page
     * @return Paginated list of products mapped to DTO
     */
    @Override
    public PageResponseDTO<ProductListResponseDTO> getProducts(String searchCodeOrName,
                                                               ProductStatus statusFilter,
                                                               StockLevelFilter stockFilter,
                                                               SortOrder nameSort,
                                                               int page,
                                                               int size) {

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

        if (size > 50) {
            throw new InvalidProductDataException(
                    "Page size must not exceed 50",
                    "size"
            );
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

        // Execute query with search, status filter, stock filter and pageable
        Page<Product> productPage = iProductRepository.findProducts(
                searchCodeOrName,
                statusFilter,
                stockFilter.name(),
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
     * <p>This method extracts values from {@link ProductStatus},
     * {@link SortOrder} and {@link StockLevelFilter} enums and converts them
     * into a frontend-friendly {@link EnumDTO} format.</p>
     *
     * <p>It ensures that the frontend always receives up-to-date options
     * without requiring code changes on the client side, acting as the single
     * source of truth for product filtering capabilities.</p>
     *
     * @return ProductFiltersResponseDTO containing status, sort and stock filter options
     */
    @Override
    public ProductFiltersResponseDTO getFilters() {

        List<EnumDTO> statusOptions = Arrays.stream(ProductStatus.values())
                .map(status -> new EnumDTO(
                        status.name(),
                        status.getDisplayName()
                ))
                .toList();

        List<EnumDTO> nameSortOptions = Arrays.stream(SortOrder.values())
                .map(sort -> new EnumDTO(
                        sort.name(),
                        sort.getDisplayName()
                ))
                .toList();

        List<EnumDTO> stockLevelOptions = Arrays.stream(StockLevelFilter.values())
                .map(stock -> new EnumDTO(
                        stock.name(),
                        stock.getDisplayName()
                ))
                .toList();

        return new ProductFiltersResponseDTO(
                statusOptions,
                nameSortOptions,
                stockLevelOptions
        );
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
                .orElseThrow(() -> new ProductNotFoundException(
                        String.format("Product with code '%s' not found", productCode)
                ));

        return iProductDetailResponseMapper.toDto(product);
    }

    /**
     * Deactivates a product by setting its status to INACTIVE.
     *
     * <p>
     * This operation performs a logical deletion. If the product does not exist
     * or is already inactive, a business exception is thrown.
     * </p>
     *
     * <p>
     * The operation is executed within a transactional context to ensure
     * atomicity of the status update.
     * </p>
     *
     * @param productCode the unique identifier of the product
     * @throws ProductNotFoundException if the product does not exist
     * @throws InvalidProductDataException if the product is already inactive
     */
    @Override
    @Transactional
    public void deactivateProduct(String productCode) {

        Product product = iProductRepository.findById(productCode)
                .orElseThrow(() -> new ProductNotFoundException(
                        String.format("Product with code '%s' not found", productCode)
                ));

        if (product.getProductStatus() == ProductStatus.INACTIVE) {
            throw new InvalidProductDataException(
                    String.format("Product with code '%s is already inactive", productCode)
            );
        }

        product.setProductStatus(ProductStatus.INACTIVE);

        iProductRepository.save(product);
    }

    /**
     * Activates a product by setting its status to ACTIVE.
     *
     * <p>
     * This operation restores a previously deactivated product. If the product does not exist
     * or is already active, a business exception is thrown.
     * </p>
     *
     * <p>
     * The operation is executed within a transactional context to ensure
     * atomicity of the status update.
     * </p>
     *
     * @param productCode the unique identifier of the product
     * @throws ProductNotFoundException if the product does not exist
     * @throws InvalidProductDataException if the product is already active
     */
    @Override
    @Transactional
    public void activateProduct(String productCode) {

        Product product = iProductRepository.findById(productCode)
                .orElseThrow(() -> new ProductNotFoundException(
                        String.format("Product with code '%s' not found", productCode)
                ));

        if (product.getProductStatus() == ProductStatus.ACTIVE) {
            throw new InvalidProductDataException(
                    String.format("Product with code '%s' is already active", productCode)
            );
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
     * If a product with the same code already exists or validation rules
     * are violated, a business exception is thrown.
     * </p>
     *
     * <p>
     * The operation is executed within a transactional context to ensure
     * atomicity of the product creation.
     * </p>
     *
     * @param request the product creation request containing product data
     * @throws ProductAlreadyExistsException if a product with the same code already exists
     * @throws InvalidProductDataException if business validation rules are violated
     */
    @Override
    @Transactional
    public ProductDetailResponseDTO registerProduct(ProductCreateRequestDTO request) {

        // Check if product already exists
        if (iProductRepository.existsById(request.getProductCode())) {
            throw new ProductAlreadyExistsException(
                    String.format("Product with code '%s' already exists", request.getProductCode())
            );
        }

        // Validate stock format when unit is UNITS
        if (request.getUnitOfMeasure() == UnitOfMeasure.UNITS) {
            if (request.getProductStock().stripTrailingZeros().scale() > 0) {
                throw new InvalidProductDataException(
                        "Stock must be an integer value when unit of measure is Units",
                        "productStock"
                );
            }

            if (request.getMinimumStock().stripTrailingZeros().scale() > 0) {
                throw new InvalidProductDataException(
                        "Minimum stock must be an integer value when unit of measure is Units",
                        "minimumStock"
                );
            }
        }

        // Map DTO to entity
        Product product = iProductCreateRequestMapper.toProduct(request);

        // Persist product
        iProductRepository.save(product);

        // Reload persisted entity to ensure we return the exact state stored in DB
        Product persisted = iProductRepository.findById(product.getProductCode())
                .orElseThrow();

        // Map entity to response DTO
        return iProductDetailResponseMapper.toDto(persisted);
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
     * This operation validates that the product exists and applies business rules
     * before persisting the updated data.
     * The product is updated atomically within a transactional context.
     * </p>
     *
     * @param productCode the code of the product to update
     * @param request the updated product data
     * @return the updated product details
     * @throws ProductNotFoundException if the product does not exist
     * @throws InvalidProductDataException if business rules are violated
     */
    @Override
    @Transactional
    public ProductDetailResponseDTO updateProduct(String productCode, ProductUpdateRequestDTO request) {

        // 1. Check if product exists
        Product product = iProductRepository.findById(productCode)
                .orElseThrow(() -> new ProductNotFoundException(
                        String.format("Product with code '%s' not found", productCode)
                ));

        // 2. Validate stock rules when unit is UNITS
        if (request.getUnitOfMeasure() == UnitOfMeasure.UNITS) {

            if (request.getProductStock().stripTrailingZeros().scale() > 0) {
                throw new InvalidProductDataException(
                        "Stock must be an integer value when unit of measure is Units",
                        "productStock"
                );
            }

            if (request.getMinimumStock().stripTrailingZeros().scale() > 0) {
                throw new InvalidProductDataException(
                        "Minimum stock must be an integer value when unit of measure is Units",
                        "minimumStock"
                );
            }
        }

        // 3. Apply updates
        iProductUpdateRequestMapper.updateProductFromDto(request, product);

        // 4. Persist changes
        iProductRepository.save(product);

        // 5. Reload persisted entity to ensure we return the exact state stored in DB
        Product persisted = iProductRepository.findById(product.getProductCode())
                .orElseThrow();

        // 6. Map entity to response DTO
        return iProductDetailResponseMapper.toDto(persisted);
    }
}