package com.marcoscornejos.sales_management_system.service;

import com.marcoscornejos.sales_management_system.dto.*;
import com.marcoscornejos.sales_management_system.model.SortDirection;

import java.time.LocalDate;

public interface ISaleService {

    PageResponseDTO<SaleListResponseDTO> getSales(Long searchSaleId, LocalDate date, SortDirection timeSort, int page,
            int size);

    SaleWithDetailsResponseDTO getSaleById(Long saleId);

    SaleFiltersResponseDTO getFilters();

    Long registerSale(SaleCreateRequestDTO request);

    String generateSaleTicket(Long saleId);
}
