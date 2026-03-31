package com.marcoscornejos.sales_management_system.repository;

import com.marcoscornejos.sales_management_system.model.SaleDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ISaleDetailRepository extends JpaRepository<SaleDetail, Long> {
}
