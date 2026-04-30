package com.marcoscornejos.sales_management_system.mapper;

import com.marcoscornejos.sales_management_system.dto.EnumDTO;
import com.marcoscornejos.sales_management_system.dto.ProductSaleListResponseDTO;
import com.marcoscornejos.sales_management_system.model.Product;
import com.marcoscornejos.sales_management_system.model.UnitOfMeasure;
import org.mapstruct.Mapper;

/**
 * Mapper for converting {@link Product} entity to
 * {@link ProductSaleListResponseDTO}.
 *
 * <p>
 * Handles mapping of unit of measure enum to EnumDTO
 * using abbreviated representations.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface IProductSaleListResponseMapper {

    /**
     * Maps a {@link Product} entity to a
     * {@link ProductSaleListResponseDTO}.
     *
     * @param product the Product entity
     * @return ProductSaleListResponseDTO with mapped fields
     */
    ProductSaleListResponseDTO toDto(Product product);

    /**
     * Maps {@link UnitOfMeasure} to {@link EnumDTO}.
     * Uses abbreviation for display.
     */
    default EnumDTO map(UnitOfMeasure unit) {
        if (unit == null) return null;

        return new EnumDTO(
                unit.name(),
                unit.getAbbreviation()
        );
    }
}
