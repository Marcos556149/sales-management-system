package com.marcoscornejos.sales_management_system.mapper;

import com.marcoscornejos.sales_management_system.dto.ProductCreateRequestDTO;
import com.marcoscornejos.sales_management_system.model.Product;
import org.mapstruct.Mapper;

/**
 * Mapper para convertir entre entidades {@link Product} y
 * {@link ProductCreateRequestDTO}.
 *
 * <p>
 * Gestiona las transformaciones necesarias para las solicitudes de creación
 * de productos y la persistencia de los datos de {@link Product}.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface IProductCreateRequestMapper {

    /**
     * Mapea un {@link ProductCreateRequestDTO} a una entidad {@link Product}.
     *
     * @param dto DTO de solicitud de creación de producto que contiene los datos del producto
     * @return una entidad Product con los campos correspondientes asignados
     */
    Product toProduct(ProductCreateRequestDTO dto);

    /**
     * Mapea una entidad {@link Product} a un {@link ProductCreateRequestDTO}.
     *
     * @param product entidad Product
     * @return un ProductCreateRequestDTO con los campos correspondientes asignados
     */
    ProductCreateRequestDTO toDto(Product product);
}