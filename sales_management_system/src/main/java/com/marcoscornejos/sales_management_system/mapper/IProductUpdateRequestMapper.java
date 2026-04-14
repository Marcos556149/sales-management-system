package com.marcoscornejos.sales_management_system.mapper;

import com.marcoscornejos.sales_management_system.dto.ProductUpdateRequestDTO;
import com.marcoscornejos.sales_management_system.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

/**
 * Mapper for updating {@link Product} entities from {@link ProductUpdateRequestDTO}.
 *
 * <p>
 * Handles transformations required for updating existing product data.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface IProductUpdateRequestMapper {

    /**
     * Updates an existing {@link Product} entity using data from the given DTO.
     *
     * @param dto the product update request DTO containing updated data
     * @param product the existing Product entity to be updated
     */
    void updateProductFromDto(ProductUpdateRequestDTO dto, @MappingTarget Product product);

    /**
     * Maps a {@link Product} entity to a {@link ProductUpdateRequestDTO}.
     *
     * @param product the Product entity
     * @return a ProductUpdateRequestDTO with corresponding fields set
     */
    ProductUpdateRequestDTO toDto(Product product);
}
