package com.marcoscornejos.sales_management_system.mapper;

import com.marcoscornejos.sales_management_system.dto.ProductCreateRequestDTO;
import com.marcoscornejos.sales_management_system.model.Product;
import org.mapstruct.Mapper;

/**
 * Mapper for converting between {@link ProductCreateRequestDTO} and {@link Product} entities.
 *
 * <p>
 * Handles transformations required for product creation requests and
 * persistence of Product data.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface IProductCreateRequestMapper {

    /**
     * Maps a {@link ProductCreateRequestDTO} to a {@link Product} entity.
     *
     * @param dto the product creation request DTO containing product data
     * @return a Product entity with corresponding fields set
     */
    Product toProduct(ProductCreateRequestDTO dto);

    /**
     * Maps a {@link Product} entity to a {@link ProductCreateRequestDTO}.
     *
     * @param product the Product entity
     * @return a ProductCreateRequestDTO with corresponding fields set
     */
    ProductCreateRequestDTO toDto(Product product);
}