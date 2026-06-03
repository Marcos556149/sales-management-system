package com.marcoscornejos.sales_management_system.mapper;

import com.marcoscornejos.sales_management_system.dto.ProductListResponseDTO;
import com.marcoscornejos.sales_management_system.dto.EnumDTO;
import com.marcoscornejos.sales_management_system.model.Product;
import com.marcoscornejos.sales_management_system.model.ProductStatus;
import com.marcoscornejos.sales_management_system.model.UnitOfMeasure;
import org.mapstruct.Mapper;

/**
 * Mapper para convertir la entidad {@link Product} en {@link ProductListResponseDTO}.
 *
 * <p>
 * Gestiona el mapeo de enumeraciones a {@link EnumDTO} utilizando
 * representaciones específicas.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface IProductListResponseMapper {

    /**
     * Mapea una entidad {@link Product} a un {@link ProductListResponseDTO}.
     *
     * @param product entidad Product
     * @return ProductListResponseDTO con los campos mapeados
     */
    ProductListResponseDTO toDto(Product product);

    /**
     * Mapea {@link ProductStatus} a {@link EnumDTO}.
     * Utiliza la etiqueta para su representación visual.
     */
    default EnumDTO map(ProductStatus status) {
        if (status == null) return null;
        return new EnumDTO(
                status.name(),
                status.getDisplayName()
        );
    }

    /**
     * Mapea {@link UnitOfMeasure} a {@link EnumDTO}.
     * Utiliza la abreviatura en lugar del nombre para mostrar.
     */
    default EnumDTO map(UnitOfMeasure unit) {
        if (unit == null) return null;
        return new EnumDTO(
                unit.name(),
                unit.getAbbreviation()
        );
    }
}
