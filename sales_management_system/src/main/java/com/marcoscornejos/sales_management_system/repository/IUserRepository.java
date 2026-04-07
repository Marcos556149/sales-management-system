/**
 * Repository interface for managing {@link User} entities.
 *
 * <p>
 * Extends {@link JpaRepository}, providing standard CRUD operations
 * such as save, delete, findById, and findAll without requiring
 * explicit implementation.
 * </p>
 *
 * <p>
 * This repository handles data access for users.
 * Custom query methods can be defined here when needed.
 * </p>
 */

package com.marcoscornejos.sales_management_system.repository;

import com.marcoscornejos.sales_management_system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by username.
     *
     * @param userName the username to search for
     * @return an Optional containing the User if found, or empty otherwise
     */
    Optional<User> findByUserName(String userName);
}
