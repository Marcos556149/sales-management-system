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
import com.marcoscornejos.sales_management_system.projection.SoldProductProjection;
import com.marcoscornejos.sales_management_system.projection.TopProductsByQuantityProjection;
import com.marcoscornejos.sales_management_system.projection.TopProductsByRevenueProjection;
import com.marcoscornejos.sales_management_system.projection.UnsoldProductProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface IProductRepository extends JpaRepository<Product, String> {
    /**
     * Retrieves products with optional search, status filtering,
     * and stock level filtering, applying pagination and sorting
     * at the database level.
     *
     * <p>
     * Search is applied on product name or code
     * (case-insensitive, partial match).
     * If {@code searchCodeOrName} is null, search is ignored.
     * </p>
     *
     * <p>
     * If {@code statusFilter} is ALL, no status filtering is applied.
     * </p>
     *
     * <p>
     * If {@code stockFilter} is ALL, no stock filtering is applied.
     * </p>
     *
     * <p>
     * Pagination and sorting are handled using {@link Pageable},
     * ensuring efficient data retrieval directly from the database
     * (server-side pagination).
     * </p>
     *
     * @param searchCodeOrName optional search term (name or code)
     * @param statusFilter product status filter (ALL, ACTIVE, INACTIVE)
     * @param stockFilter stock level filter (ALL, NORMAL, LOW, OUT_OF_STOCK)
     * @param pageable pagination and sorting configuration
     * @return paginated result of matching products
     */
    @Query("""
    SELECT p FROM Product p
    WHERE
        (
            :searchCodeOrName IS NULL OR
            LOWER(p.productName) LIKE LOWER(CONCAT('%', CAST(:searchCodeOrName AS string), '%')) OR
            LOWER(p.productCode) LIKE LOWER(CONCAT('%', CAST(:searchCodeOrName AS string), '%'))
        )
    AND
        (
            :statusFilter = 'ALL' OR
            p.productStatus = :statusFilter
        )
    AND
        (
            :stockFilter = 'ALL'
            OR (:stockFilter = 'NORMAL' AND p.productStock > p.minimumStock)
            OR (:stockFilter = 'LOW' AND p.productStock > 0 AND p.productStock <= p.minimumStock)
            OR (:stockFilter = 'OUT_OF_STOCK' AND p.productStock = 0)
        )
    """)
    Page<Product> findProducts(String searchCodeOrName,
                               ProductStatus statusFilter,
                               String stockFilter,
                               Pageable pageable);

    /**
     * Retrieves products available for sale with optional search,
     * applying pagination and sorting at the database level.
     *
     * <p>
     * Only products with ACTIVE status and stock greater than zero
     * are included.
     * </p>
     *
     * <p>
     * Search is applied on product name or code
     * (case-insensitive, partial match).
     * If {@code searchCodeOrName} is null, search is ignored.
     * </p>
     *
     * <p>
     * Pagination and sorting are handled using {@link Pageable},
     * ensuring efficient data retrieval directly from the database
     * (server-side pagination).
     * </p>
     *
     * @param searchCodeOrName optional search term (name or code)
     * @param pageable pagination and sorting configuration
     * @return paginated result of products available for sale
     */
    @Query("""
    SELECT p FROM Product p
    WHERE
        (
            :searchCodeOrName IS NULL OR
            LOWER(p.productName) LIKE LOWER(CONCAT('%', CAST(:searchCodeOrName AS string), '%')) OR
            LOWER(p.productCode) LIKE LOWER(CONCAT('%', CAST(:searchCodeOrName AS string), '%'))
        )
    AND
        p.productStatus = 'ACTIVE'
    AND
        p.productStock > 0
    """)
    Page<Product> findProductsForSale(String searchCodeOrName,
                                      Pageable pageable);

    /**
     * Counts the total number of products available for sale.
     *
     * <p>
     * Only products with ACTIVE status and stock greater than
     * the provided value are included.
     * </p>
     *
     * @param productStatus required product status
     * @param productStock minimum exclusive stock threshold
     * @return total number of matching products
     */
    Long countByProductStatusAndProductStockGreaterThan(
            ProductStatus productStatus,
            BigDecimal productStock
    );

    /**
     * Retrieves the top 10 products ranked by quantity sold
     * within the selected filters.
     *
     * <p>
     * Results are calculated using data from {@code SaleDetail},
     * aggregating product quantities across all sales
     * in the specified date range.
     * </p>
     *
     * <p>
     * The quantity represents the total aggregated amount sold
     * and may include fractional values depending on the product unit type
     * (e.g., kilograms, liters, or other measurable units).
     * </p>
     *
     * <p>
     * If {@code userId} is null, sales from all users are included.
     * </p>
     *
     * <p>
     * Results are grouped by product and ordered by total quantity sold
     * in descending order, returning only the top 10 products.
     * </p>
     *
     * @param userId optional user filter (null = all users)
     * @param startDate start date filter (inclusive)
     * @param endDate end date filter (inclusive)
     * @return top 10 products ranked by quantity sold
     */
    @Query(value = """
    SELECT
        p.product_code AS productCode,
        p.product_name AS productName,
        SUM(sd.product_quantity) AS quantitySold
    FROM core.sale_detail sd
    INNER JOIN core.sale s ON s.sale_id = sd.sale_id
    INNER JOIN core.product p ON p.product_code = sd.product_code
    WHERE
        (:userId IS NULL OR s.user_id = :userId)
    AND
        s.sale_date BETWEEN :startDate AND :endDate
    GROUP BY p.product_code, p.product_name
    ORDER BY quantitySold DESC
    LIMIT 10
    """, nativeQuery = true)
    List<TopProductsByQuantityProjection> findTopByQuantity(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    );

    /**
     * Retrieves the top 10 products ranked by revenue generated
     * within the selected filters.
     *
     * <p>
     * Revenue is calculated as the sum of:
     * {@code product_quantity * sale_price}
     * across all matching sale details within the specified date range.
     * </p>
     *
     * <p>
     * If {@code userId} is null, sales from all users are included.
     * </p>
     *
     * <p>
     * Results are grouped by product and ordered by total revenue generated
     * in descending order, returning only the top 10 products.
     * </p>
     *
     * @param userId optional user filter (null = all users)
     * @param startDate start date filter (inclusive)
     * @param endDate end date filter (inclusive)
     * @return top 10 products ranked by revenue generated
     */
    @Query(value = """
    SELECT
        p.product_code AS productCode,
        p.product_name AS productName,
        SUM(
            COALESCE(sd.product_quantity, 0)
            *
            COALESCE(sd.sale_price, 0)
        ) AS revenueGenerated
    FROM core.sale_detail sd
    INNER JOIN core.sale s ON s.sale_id = sd.sale_id
    INNER JOIN core.product p ON p.product_code = sd.product_code
    WHERE
        (:userId IS NULL OR s.user_id = :userId)
    AND
        s.sale_date BETWEEN :startDate AND :endDate
    GROUP BY p.product_code, p.product_name
    ORDER BY
        SUM(
            COALESCE(sd.product_quantity, 0)
            *
            COALESCE(sd.sale_price, 0)
        ) DESC
    LIMIT 10
    """, nativeQuery = true)
    List<TopProductsByRevenueProjection> findTopByRevenue(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    );

    /**
     * Retrieves a paginated ranking of sold products
     * ordered from highest to lowest values.
     *
     * <p>
     * Ranking can be calculated using:
     * <ul>
     *   <li>Quantity sold</li>
     *   <li>Revenue generated</li>
     * </ul>
     * </p>
     *
     * <p>
     * Pagination is executed directly at database level.
     * </p>
     *
     * @param userId optional user filter
     * @param startDate start date filter
     * @param endDate end date filter
     * @param metric ranking metric
     * @param pageable pagination configuration
     * @return paginated sold products ranking
     */
    @Query(
            value = """
    SELECT
        p.product_code AS productCode,
        p.product_name AS productName,
    
        SUM(sd.product_quantity) AS quantitySold,
    
        SUM(
            COALESCE(sd.product_quantity,0)
            *
            COALESCE(sd.sale_price,0)
        ) AS revenueGenerated
    
    FROM core.sale_detail sd
    
    INNER JOIN core.sale s
        ON s.sale_id = sd.sale_id
    
    INNER JOIN core.product p
        ON p.product_code = sd.product_code
    
    WHERE
        (:userId IS NULL OR s.user_id = :userId)
    
    AND
        s.sale_date BETWEEN :startDate AND :endDate
    
    GROUP BY
        p.product_code,
        p.product_name
    
    ORDER BY
        CASE
            WHEN :metric='QUANTITY_SOLD'
            THEN SUM(sd.product_quantity)
    
            WHEN :metric='REVENUE_GENERATED'
            THEN SUM(
                COALESCE(sd.product_quantity,0)
                *
                COALESCE(sd.sale_price,0)
            )
        END DESC,
    
        p.product_code ASC
    """,

            countQuery = """
    SELECT COUNT(*)
    FROM (
    
        SELECT
            p.product_code
    
        FROM core.sale_detail sd
    
        INNER JOIN core.sale s
            ON s.sale_id = sd.sale_id
    
        INNER JOIN core.product p
            ON p.product_code = sd.product_code
    
        WHERE
            (:userId IS NULL OR s.user_id = :userId)
    
        AND
            s.sale_date BETWEEN :startDate AND :endDate
    
        GROUP BY p.product_code
    
    ) rankingCount
    """,

            nativeQuery = true
    )
    Page<SoldProductProjection> findRankingDesc(
            Long userId,
            LocalDate startDate,
            LocalDate endDate,
            String metric,
            Pageable pageable
    );

    /**
     * Retrieves a paginated ranking of sold products
     * ordered from lowest to highest values.
     *
     * @param userId optional user filter
     * @param startDate start date filter
     * @param endDate end date filter
     * @param metric ranking metric
     * @param pageable pagination configuration
     * @return paginated sold products ranking
     */
    @Query(
            value = """
    SELECT
        p.product_code AS productCode,
        p.product_name AS productName,
    
        SUM(sd.product_quantity) AS quantitySold,
    
        SUM(
            COALESCE(sd.product_quantity,0)
            *
            COALESCE(sd.sale_price,0)
        ) AS revenueGenerated
    
    FROM core.sale_detail sd
    
    INNER JOIN core.sale s
        ON s.sale_id = sd.sale_id
    
    INNER JOIN core.product p
        ON p.product_code = sd.product_code
    
    WHERE
        (:userId IS NULL OR s.user_id = :userId)
    
    AND
        s.sale_date BETWEEN :startDate AND :endDate
    
    GROUP BY
        p.product_code,
        p.product_name
    
    ORDER BY
        CASE
            WHEN :metric='QUANTITY_SOLD'
            THEN SUM(sd.product_quantity)
    
            WHEN :metric='REVENUE_GENERATED'
            THEN SUM(
                COALESCE(sd.product_quantity,0)
                *
                COALESCE(sd.sale_price,0)
            )
        END ASC,
    
        p.product_code ASC
    """,

            countQuery = """
    SELECT COUNT(*)
    FROM (
    
        SELECT
            p.product_code
    
        FROM core.sale_detail sd
    
        INNER JOIN core.sale s
            ON s.sale_id = sd.sale_id
    
        INNER JOIN core.product p
            ON p.product_code = sd.product_code
    
        WHERE
            (:userId IS NULL OR s.user_id = :userId)
    
        AND
            s.sale_date BETWEEN :startDate AND :endDate
    
        GROUP BY p.product_code
    
    ) rankingCount
    """,

            nativeQuery = true
    )
    Page<SoldProductProjection> findRankingAsc(
            Long userId,
            LocalDate startDate,
            LocalDate endDate,
            String metric,
            Pageable pageable
    );

    /**
     * Retrieves a paginated list of products that have not
     * registered any sales within the selected filter range.
     *
     * <p>
     * A product is considered "unsold" when no matching
     * sale records exist for the selected filters.
     * </p>
     *
     * <p>
     * Supported filters:
     * <ul>
     *     <li>User filter (optional)</li>
     *     <li>Date range filter</li>
     * </ul>
     * </p>
     *
     * <p>
     * The query uses a NOT EXISTS clause to ensure
     * only products without matching sales are returned.
     * </p>
     *
     * <p>
     * Results are ordered alphabetically by product name
     * and paginated at database level.
     * </p>
     *
     * @param userId optional user filter
     * @param startDate start date filter
     * @param endDate end date filter
     * @param pageable pagination configuration
     * @return paginated list of unsold products
     */
    @Query(
            value = """
    SELECT
        p.product_code AS productCode,
        p.product_name AS productName
    
    FROM core.product p
    
    WHERE NOT EXISTS (
    
        SELECT 1
        FROM core.sale_detail sd
    
        INNER JOIN core.sale s
            ON s.sale_id = sd.sale_id
    
        WHERE
            sd.product_code = p.product_code
        AND
            (:userId IS NULL OR s.user_id = :userId)
        AND
            s.sale_date BETWEEN :startDate AND :endDate
    )
    
    ORDER BY
        p.product_name ASC,
        p.product_code ASC
    """,

            countQuery = """
    SELECT COUNT(*)
    
    FROM core.product p
    
    WHERE NOT EXISTS (
    
        SELECT 1
        FROM core.sale_detail sd
    
        INNER JOIN core.sale s
            ON s.sale_id = sd.sale_id
    
        WHERE
            sd.product_code = p.product_code
        AND
            (:userId IS NULL OR s.user_id = :userId)
        AND
            s.sale_date BETWEEN :startDate AND :endDate
    )
    """,

            nativeQuery = true
    )
    Page<UnsoldProductProjection> findUnsoldProducts(
            Long userId,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    );
}
