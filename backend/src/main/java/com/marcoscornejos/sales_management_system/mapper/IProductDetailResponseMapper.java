package com.marcoscornejos.sales_management_system.mapper;

import com.marcoscornejos.sales_management_system.dto.ProductDetailResponseDTO;
import com.marcoscornejos.sales_management_system.dto.EnumDTO;
import com.marcoscornejos.sales_management_system.model.Product;
import com.marcoscornejos.sales_management_system.model.ProductStatus;
import com.marcoscornejos.sales_management_system.model.UnitOfMeasure;
import org.mapstruct.Mapper;

/**
 * Mapper for converting {@link Product} entity to {@link ProductDetailResponseDTO}.
 *
 * <p>
 * Handles mapping of enums to EnumDTO with specific representations.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface IProductDetailResponseMapper {

    /**
     * Maps a {@link Product} entity to a {@link ProductDetailResponseDTO}.
     *
     * @param product the Product entity
     * @return ProductDetailResponseDTO with mapped fields
     */
    ProductDetailResponseDTO toDto(Product product);

    /**
     * Maps {@link ProductStatus} to {@link EnumDTO}.
     * Uses label for display.
     */
    default EnumDTO map(ProductStatus status) {
        if (status == null) return null;
        return new EnumDTO(
                status.name(),
                status.getDisplayName()
        );
    }

    /**
     * Maps {@link UnitOfMeasure} to {@link EnumDTO}.
     * Uses abbreviation instead of display value.
     */
    default EnumDTO map(UnitOfMeasure unit) {
        if (unit == null) return null;
        return new EnumDTO(
                unit.name(),
                unit.getDisplayName()
        );
    }
}
