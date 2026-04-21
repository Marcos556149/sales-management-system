package com.marcoscornejos.sales_management_system.mapper;

import com.marcoscornejos.sales_management_system.dto.SaleDetailCreateRequestDTO;
import com.marcoscornejos.sales_management_system.model.SaleDetail;
import org.mapstruct.Mapper;

/**
 * Mapper for converting between {@link SaleDetailCreateRequestDTO}
 * and {@link SaleDetail} entities.
 *
 * <p>
 * Handles transformations required for sale detail creation requests
 * and persistence of SaleDetail data.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface ISaleDetailCreateRequestMapper {

    /**
     * Maps a {@link SaleDetailCreateRequestDTO} to a {@link SaleDetail} entity.
     *
     * @param dto the sale detail creation request DTO containing detail data
     * @return a SaleDetail entity with corresponding fields set
     */
    SaleDetail toSaleDetail(SaleDetailCreateRequestDTO dto);

    /**
     * Maps a {@link SaleDetail} entity to a {@link SaleDetailCreateRequestDTO}.
     *
     * @param saleDetail the SaleDetail entity
     * @return a SaleDetailCreateRequestDTO with corresponding fields set
     */
    SaleDetailCreateRequestDTO toDto(SaleDetail saleDetail);
}