/**
 * Repository interface for managing {@link Sale} entities.
 *
 * <p>
 * Extends {@link JpaRepository}, providing standard CRUD operations
 * such as save, delete, findById, and findAll without requiring
 * explicit implementation.
 * </p>
 *
 * <p>
 * This repository handles data access for sales.
 * Custom query methods can be defined here when needed.
 * </p>
 */

package com.marcoscornejos.sales_management_system.repository;

import com.marcoscornejos.sales_management_system.model.Sale;
import com.marcoscornejos.sales_management_system.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ISaleRepository extends JpaRepository<Sale, Long> {

    /**
     * Retrieves sales filtered by sale date,
     * applying pagination and sorting at the database level.
     *
     * <p>
     * The query filters sales by exact match on sale date.
     * Pagination and sorting are handled using {@link Pageable},
     * ensuring efficient server-side data retrieval.
     * </p>
     *
     * <p>
     * The associated user is loaded using {@link EntityGraph}
     * to avoid N+1 query problems.
     * </p>
     *
     * @param date sale date filter
     * @param pageable pagination and sorting configuration
     * @return paginated result of matching sales
     */
    @EntityGraph(attributePaths = "user")
    @Query("""
        SELECT s FROM Sale s
        WHERE s.saleDate = :date
    """)
    Page<Sale> findSales(LocalDate date, Pageable pageable);

    /**
     * Retrieves a sale with its details, products, and user.
     *
     * <p>
     * Uses JOIN FETCH to load associated entities and avoid additional queries
     * caused by lazy loading.
     * </p>
     *
     * @param idSale the unique identifier of the sale
     * @return an Optional containing the sale with its details, products, and user
     */
    @Query("""
        SELECT DISTINCT s FROM Sale s
        JOIN FETCH s.saleDetails sd
        JOIN FETCH sd.product
        JOIN FETCH s.user
        WHERE s.saleId = :idSale
    """)
    Optional<Sale> findByIdWithDetailsAndProducts(Long idSale);
}
