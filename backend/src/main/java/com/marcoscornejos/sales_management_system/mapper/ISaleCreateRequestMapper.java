package com.marcoscornejos.sales_management_system.mapper;

import com.marcoscornejos.sales_management_system.dto.SaleCreateRequestDTO;
import com.marcoscornejos.sales_management_system.model.Sale;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for converting between {@link SaleCreateRequestDTO}
 * and {@link Sale} entities.
 *
 * <p>
 * Handles transformations required for sale creation requests
 * and persistence of Sale data.
 * </p>
 *
 * <p>
 * Automatically generated fields such as identifier, date,
 * time, total amount, and authenticated user are ignored
 * during request mapping because they are assigned by
 * the business layer.
 * </p>
 */
@Mapper(
        componentModel = "spring",
        uses = {ISaleDetailCreateRequestMapper.class}
)
public interface ISaleCreateRequestMapper {

    /**
     * Maps a {@link SaleCreateRequestDTO} to a {@link Sale} entity.
     *
     * @param dto the sale creation request DTO containing sale details
     * @return a Sale entity with corresponding fields set
     */
    Sale toSale(SaleCreateRequestDTO dto);

    /**
     * Maps a {@link Sale} entity to a {@link SaleCreateRequestDTO}.
     *
     * @param sale the Sale entity
     * @return a SaleCreateRequestDTO with corresponding fields set
     */
    SaleCreateRequestDTO toDto(Sale sale);
}