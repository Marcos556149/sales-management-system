package com.marcoscornejos.sales_management_system.mapper;

import com.marcoscornejos.sales_management_system.dto.SaleDetailCreateRequestDTO;
import com.marcoscornejos.sales_management_system.model.SaleDetail;
import org.mapstruct.Mapper;

/**
 * Mapper para convertir entre {@link SaleDetailCreateRequestDTO}
 * y entidades {@link SaleDetail}.
 *
 * <p>
 * Gestiona las transformaciones necesarias para las solicitudes
 * de creación de detalles de venta y la persistencia de datos de {@link SaleDetail}.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface ISaleDetailCreateRequestMapper {

    /**
     * Convierte un {@link SaleDetailCreateRequestDTO} en una entidad {@link SaleDetail}.
     *
     * @param dto DTO de solicitud de creación de detalle de venta que contiene los datos del detalle
     * @return entidad SaleDetail con los campos correspondientes asignados
     */
    SaleDetail toSaleDetail(SaleDetailCreateRequestDTO dto);

    /**
     * Convierte una entidad {@link SaleDetail} en un {@link SaleDetailCreateRequestDTO}.
     *
     * @param saleDetail entidad SaleDetail
     * @return SaleDetailCreateRequestDTO con los campos correspondientes asignados
     */
    SaleDetailCreateRequestDTO toDto(SaleDetail saleDetail);
}