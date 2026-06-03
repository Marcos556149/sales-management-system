package com.marcoscornejos.sales_management_system.mapper;

import com.marcoscornejos.sales_management_system.dto.ProductUpdateRequestDTO;
import com.marcoscornejos.sales_management_system.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

/**
 * Mapper para actualizar entidades {@link Product}
 * a partir de {@link ProductUpdateRequestDTO}.
 *
 * <p>
 * Gestiona las transformaciones necesarias para actualizar
 * datos de productos existentes.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface IProductUpdateRequestMapper {

    /**
     * Actualiza una entidad {@link Product} existente utilizando
     * los datos del DTO proporcionado.
     *
     * @param dto DTO de solicitud de actualización del producto que contiene los datos actualizados
     * @param product entidad Product existente que será actualizada
     */
    void updateProductFromDto(ProductUpdateRequestDTO dto, @MappingTarget Product product);

    /**
     * Mapea una entidad {@link Product} a un {@link ProductUpdateRequestDTO}.
     *
     * @param product entidad Product
     * @return un ProductUpdateRequestDTO con los campos correspondientes asignados
     */
    ProductUpdateRequestDTO toDto(Product product);
}
