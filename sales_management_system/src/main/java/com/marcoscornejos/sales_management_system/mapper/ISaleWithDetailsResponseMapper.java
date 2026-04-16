package com.marcoscornejos.sales_management_system.mapper;

import com.marcoscornejos.sales_management_system.dto.EnumDTO;
import com.marcoscornejos.sales_management_system.dto.SaleWithDetailsResponseDTO;
import com.marcoscornejos.sales_management_system.dto.SaleItemDTO;
import com.marcoscornejos.sales_management_system.model.Sale;
import com.marcoscornejos.sales_management_system.model.SaleDetail;
import com.marcoscornejos.sales_management_system.model.UnitOfMeasure;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

/**
 * Mapper for converting {@link Sale} entity to {@link SaleWithDetailsResponseDTO}.
 *
 * <p>
 * Handles mapping of sale data and its associated details.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface ISaleWithDetailsResponseMapper {

    /**
     * Maps a {@link Sale} entity to a {@link SaleWithDetailsResponseDTO}.
     *
     * @param sale the Sale entity
     * @return SaleWithDetailsResponseDTO with mapped fields
     */
    @Mapping(source = "user.userName", target = "userName")
    SaleWithDetailsResponseDTO toDto(Sale sale);


    /**
     * Maps a {@link SaleDetail} entity to a SaleItemDTO.
     *
     * @param detail the SaleDetail entity
     * @return SaleItemDTO with mapped fields
     */
    @Mapping(source = "product.productCode", target = "productCode")
    @Mapping(source = "product.productName", target = "productName")
    @Mapping(source = "product.unitOfMeasure", target = "unitOfMeasure")
    @Mapping(target = "subtotal", expression = "java(calculateSubtotal(detail))")
    SaleItemDTO toItemDto(SaleDetail detail);

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

    /**
     * Calculates the subtotal for a sale detail.
     *
     * <p>
     * Multiplies the unit sale price by the quantity sold.
     * Returns null if any required value is missing.
     * </p>
     *
     * @param detail the SaleDetail entity
     * @return the calculated subtotal
     */
    default BigDecimal calculateSubtotal(SaleDetail detail) {
        if (detail == null || detail.getSalePrice() == null || detail.getProductQuantity() == null) {
            return null;
        }
        return detail.getSalePrice().multiply(detail.getProductQuantity());
    }
}