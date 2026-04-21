package com.marcoscornejos.sales_management_system.service;

import com.marcoscornejos.sales_management_system.dto.*;
import com.marcoscornejos.sales_management_system.model.Sale;
import com.marcoscornejos.sales_management_system.model.SortOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface ISaleService{

    PageResponseDTO<SaleListResponseDTO> getSales(LocalDate date, SortOrder timeSort, int page, int size);

    SaleWithDetailsResponseDTO getSaleById(Long saleId);

    SaleFiltersResponseDTO getFilters();

    void registerSale(SaleCreateRequestDTO request);
}
