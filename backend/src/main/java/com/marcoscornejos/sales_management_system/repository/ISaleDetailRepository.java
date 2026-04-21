/**
 * Repository interface for managing {@link SaleDetail} entities.
 *
 * <p>
 * Extends {@link JpaRepository}, providing standard CRUD operations
 * such as save, delete, findById, and findAll without requiring
 * explicit implementation.
 * </p>
 *
 * <p>
 * This repository handles data access for sale details, which represent
 * the individual items within a sale.
 * Custom query methods can be defined here when needed.
 * </p>
 */

package com.marcoscornejos.sales_management_system.repository;

import com.marcoscornejos.sales_management_system.model.SaleDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ISaleDetailRepository extends JpaRepository<SaleDetail, Long> {
}
