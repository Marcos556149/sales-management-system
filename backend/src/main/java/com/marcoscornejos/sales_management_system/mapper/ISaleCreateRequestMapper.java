package com.marcoscornejos.sales_management_system.mapper;

import com.marcoscornejos.sales_management_system.dto.SaleCreateRequestDTO;
import com.marcoscornejos.sales_management_system.model.Sale;
import org.mapstruct.Mapper;

/**
 * Mapper para convertir entre {@link SaleCreateRequestDTO}
 * y entidades {@link Sale}.
 *
 * <p>
 * Gestiona las transformaciones necesarias para las solicitudes
 * de creación de ventas y la persistencia de datos de {@link Sale}.
 * </p>
 *
 * <p>
 * Los campos generados automáticamente, como el identificador,
 * la fecha, la hora, el importe total y el usuario autenticado,
 * se ignoran durante el mapeo de la solicitud porque son asignados
 * por la capa de negocio.
 * </p>
 */
@Mapper(
        componentModel = "spring",
        uses = {ISaleDetailCreateRequestMapper.class}
)
public interface ISaleCreateRequestMapper {

    /**
     * Convierte un {@link SaleCreateRequestDTO} en una entidad {@link Sale}.
     *
     * @param dto DTO de solicitud de creación de venta que contiene los detalles de la venta
     * @return entidad Sale con los campos correspondientes asignados
     */
    Sale toSale(SaleCreateRequestDTO dto);

    /**
     * Convierte una entidad {@link Sale} en un {@link SaleCreateRequestDTO}.
     *
     * @param sale entidad Sale
     * @return SaleCreateRequestDTO con los campos correspondientes asignados
     */
    SaleCreateRequestDTO toDto(Sale sale);
}