/**
 * Repository interface for managing {@link Product} entities.
 *
 * <p>
 * Extends {@link JpaRepository}, providing standard CRUD operations
 * such as save, delete, findById, and findAll without requiring
 * explicit implementation.
 * </p>
 *
 * <p>
 * This repository is responsible for data access related to products.
 * Custom query methods can be defined here when needed.
 * </p>
 */

package com.marcoscornejos.sales_management_system.repository;

import com.marcoscornejos.sales_management_system.model.Product;
import com.marcoscornejos.sales_management_system.model.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IProductRepository extends JpaRepository<Product, String> {
    /**
     * Retrieves products with optional search and status filtering,
     * applying pagination and sorting at the database level.
     *
     * <p>
     * Search is applied on product name or code (case-insensitive, partial match).
     * If {@code searchCodeOrName} is null, search is ignored.
     * </p>
     *
     * <p>
     * If {@code statusFilter} is ALL, no status filtering is applied.
     * </p>
     *
     * <p>
     * Pagination and sorting are handled using {@link Pageable}, ensuring
     * efficient data retrieval directly from the database (server-side pagination).
     * </p>
     *
     * @param searchCodeOrName optional search term (name or code)
     * @param statusFilter product status filter (ALL, ACTIVE, INACTIVE)
     * @param pageable pagination and sorting configuration
     * @return paginated result of matching products
     */
    @Query("""
    SELECT p FROM Product p
    WHERE
        (:searchCodeOrName IS NULL OR
        LOWER(p.productName) LIKE LOWER(CONCAT('%', CAST(:searchCodeOrName AS string), '%')) OR
        LOWER(p.productCode) LIKE LOWER(CONCAT('%', CAST(:searchCodeOrName AS string), '%')))
    AND
        (:statusFilter = 'ALL' OR p.productStatus = :statusFilter)
    """)
    Page<Product> findProducts(String searchCodeOrName, ProductStatus statusFilter, Pageable pageable);
}
