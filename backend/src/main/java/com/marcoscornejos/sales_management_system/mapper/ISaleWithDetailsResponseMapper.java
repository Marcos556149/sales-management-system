package com.marcoscornejos.sales_management_system.mapper;

import com.marcoscornejos.sales_management_system.dto.EnumDTO;
import com.marcoscornejos.sales_management_system.dto.SaleWithDetailsResponseDTO;
import com.marcoscornejos.sales_management_system.dto.SaleDetailResponseDTO;
import com.marcoscornejos.sales_management_system.model.Sale;
import com.marcoscornejos.sales_management_system.model.SaleDetail;
import com.marcoscornejos.sales_management_system.model.UnitOfMeasure;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Mapper para convertir una entidad {@link Sale}
 * en un {@link SaleWithDetailsResponseDTO}.
 *
 * <p>
 * Gestiona el mapeo de los datos de la venta y sus detalles asociados.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface ISaleWithDetailsResponseMapper {

    /**
     * Convierte una entidad {@link Sale} en un {@link SaleWithDetailsResponseDTO}.
     *
     * @param sale entidad Sale
     * @return SaleWithDetailsResponseDTO con los campos mapeados
     */
    @Mapping(source = "user.userName", target = "userName")
    SaleWithDetailsResponseDTO toDto(Sale sale);


    /**
     * Convierte una entidad {@link SaleDetail} en un {@link SaleDetailResponseDTO}.
     *
     * @param detail entidad SaleDetail
     * @return SaleDetailResponseDTO con los campos mapeados
     */
    @Mapping(source = "product.productCode", target = "productCode")
    @Mapping(target = "subtotal", expression = "java(calculateSubtotal(detail))")
    SaleDetailResponseDTO toSaleDetailResponseDTO(SaleDetail detail);

    /**
     * Convierte un {@link UnitOfMeasure} en un {@link EnumDTO}.
     *
     * <p>
     * Utiliza la abreviatura de la unidad de medida para su visualización.
     * </p>
     */
    default EnumDTO map(UnitOfMeasure unit) {
        if (unit == null) return null;
        return new EnumDTO(
                unit.name(),
                unit.getAbbreviation()
        );
    }

    /**
     * Calcula el subtotal de un detalle de venta.
     *
     * <p>
     * Multiplica el precio de venta unitario por la cantidad vendida.
     * Devuelve null si falta alguno de los valores requeridos.
     * </p>
     *
     * @param detail entidad SaleDetail
     * @return subtotal calculado
     */
    default BigDecimal calculateSubtotal(SaleDetail detail) {
        if (detail == null || detail.getSalePrice() == null || detail.getProductQuantity() == null) {
            return null;
        }
        return detail.getSalePrice()
                .multiply(detail.getProductQuantity())
                .setScale(2, RoundingMode.HALF_UP);
    }
}