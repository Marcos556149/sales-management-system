package com.marcoscornejos.sales_management_system.service;

import com.marcoscornejos.sales_management_system.dto.*;
import com.marcoscornejos.sales_management_system.model.ProductStatus;
import com.marcoscornejos.sales_management_system.model.SortOrder;
import com.marcoscornejos.sales_management_system.model.StockLevelFilter;


public interface IProductService {
    PageResponseDTO<ProductListResponseDTO> getProducts(String searchCodeOrName,
                                                        ProductStatus statusFilter,
                                                        StockLevelFilter stockFilter,
                                                        SortOrder nameSort,
                                                        int page,
                                                        int size);

    ProductFiltersResponseDTO getFilters();

    ProductDetailResponseDTO getProductByCode(String productCode);

    ProductDetailResponseDTO deactivateProduct(String productCode);

    ProductDetailResponseDTO activateProduct(String productCode);

    ProductDetailResponseDTO registerProduct(ProductCreateRequestDTO request);

    ProductMetadataResponseDTO getProductMetadata();

    ProductDetailResponseDTO updateProduct(String productCode, ProductUpdateRequestDTO request);
}
