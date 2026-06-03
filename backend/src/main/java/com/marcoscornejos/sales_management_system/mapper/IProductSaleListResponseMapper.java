package com.marcoscornejos.sales_management_system.mapper;

import com.marcoscornejos.sales_management_system.dto.EnumDTO;
import com.marcoscornejos.sales_management_system.dto.ProductSaleListResponseDTO;
import com.marcoscornejos.sales_management_system.model.Product;
import com.marcoscornejos.sales_management_system.model.UnitOfMeasure;
import org.mapstruct.Mapper;

/**
 * Mapper para convertir la entidad {@link Product}
 * en {@link ProductSaleListResponseDTO}.
 *
 * <p>
 * Gestiona el mapeo de la enumeración de unidad de medida a {@link EnumDTO}
 * utilizando representaciones abreviadas.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface IProductSaleListResponseMapper {

    /**
     * Mapea una entidad {@link Product} a un
     * {@link ProductSaleListResponseDTO}.
     *
     * @param product entidad Product
     * @return ProductSaleListResponseDTO con los campos mapeados
     */
    ProductSaleListResponseDTO toDto(Product product);

    /**
     * Mapea {@link UnitOfMeasure} a {@link EnumDTO}.
     * Utiliza la abreviatura para su representación visual.
     */
    default EnumDTO map(UnitOfMeasure unit) {
        if (unit == null) return null;

        return new EnumDTO(
                unit.name(),
                unit.getAbbreviation()
        );
    }
}
