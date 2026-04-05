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
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ISaleRepository extends JpaRepository<Sale, Long> {

    /**
     * Retrieves all sales with their associated user.
     *
     * <p>Uses a JOIN FETCH to load the user of each sale,
     * avoiding additional queries.</p>
     */
    @Query("""
            SELECT s FROM Sale s
            JOIN FETCH s.user
            """)
    List<Sale> findAllWithUser();
}
