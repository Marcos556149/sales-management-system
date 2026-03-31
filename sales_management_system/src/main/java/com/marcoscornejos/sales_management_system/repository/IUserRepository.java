package com.marcoscornejos.sales_management_system.repository;

import com.marcoscornejos.sales_management_system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserRepository extends JpaRepository<User, Long> {
}
