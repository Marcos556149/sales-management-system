package com.marcoscornejos.sales_management_system.controller;

import com.marcoscornejos.sales_management_system.dto.PageResponseDTO;
import com.marcoscornejos.sales_management_system.dto.ProductDetailResponseDTO;
import com.marcoscornejos.sales_management_system.dto.ProductFiltersResponseDTO;
import com.marcoscornejos.sales_management_system.dto.ProductListResponseDTO;
import com.marcoscornejos.sales_management_system.service.ProductService;
import com.marcoscornejos.sales_management_system.model.ProductStatus;
import com.marcoscornejos.sales_management_system.model.SortOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * REST controller responsible for handling product queries.
 *
 * <p>Provides endpoints to retrieve products with optional filtering,
 * searching, and sorting capabilities.</p>
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * Retrieves a paginated list of products with optional filtering,
     * searching, and sorting capabilities.
     *
     * <p>Supports server-side pagination to efficiently handle large datasets.</p>
     *
     * @param searchCodeOrName Optional product code or name (or part of it)
     * @param statusFilter Product status filter (default: ALL)
     * @param nameSort Sorting order by name (default: ASCENDING)
     * @param page Page number (default: 0)
     * @param size Number of products per page (default: 50)
     * @return A paginated response containing products and pagination metadata
     */
    @GetMapping
    public PageResponseDTO<ProductListResponseDTO> getProducts(
            @RequestParam(required = false) String searchCodeOrName,
            @RequestParam(defaultValue = "ALL") ProductStatus statusFilter,
            @RequestParam(defaultValue = "ASCENDING") SortOrder nameSort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return productService.getProducts(searchCodeOrName, statusFilter, nameSort, page, size);
    }

    /**
     * Retrieves available filter and sorting options for products.
     *
     * <p>This endpoint provides dynamic configuration data for the frontend,
     * including product status filters and sorting options.
     * It avoids hardcoded values in the client application.</p>
     *
     * @return ProductFiltersResponseDTO containing filter and sort options
     */
    @GetMapping("/filters")
    public ProductFiltersResponseDTO getFilters() {
        return productService.getFilters();
    }

    /**
     * Retrieves detailed information of a specific product by its code.
     *
     * <p>
     * This endpoint allows clients to fetch a single product's data,
     * including its code, name, price, unit of measure, status, and stock.
     * </p>
     *
     * @param productCode the unique identifier of the product
     * @return the product details as a response DTO
     */
    @GetMapping("/{productCode}")
    public ResponseEntity<ProductDetailResponseDTO> getProductByCode(
            @PathVariable String productCode
    ) {

        ProductDetailResponseDTO product = productService.getProductByCode(productCode);

        return ResponseEntity.ok(product);
    }
}
