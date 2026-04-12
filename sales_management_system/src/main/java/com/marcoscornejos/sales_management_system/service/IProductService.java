package com.marcoscornejos.sales_management_system.service;

import com.marcoscornejos.sales_management_system.dto.PageResponseDTO;
import com.marcoscornejos.sales_management_system.dto.ProductDetailResponseDTO;
import com.marcoscornejos.sales_management_system.dto.ProductFiltersResponseDTO;
import com.marcoscornejos.sales_management_system.dto.ProductListResponseDTO;
import com.marcoscornejos.sales_management_system.model.ProductStatus;
import com.marcoscornejos.sales_management_system.model.SortOrder;


public interface IProductService {
    PageResponseDTO<ProductListResponseDTO> getProducts(String searchCodeOrName, ProductStatus statusFilter, SortOrder nameSort, int page, int size);

    ProductFiltersResponseDTO getFilters();

    ProductDetailResponseDTO getProductByCode(String productCode);

    void deactivateProduct(String productCode);

    void activateProduct(String productCode);
}
