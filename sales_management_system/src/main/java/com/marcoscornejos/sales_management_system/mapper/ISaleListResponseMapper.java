package com.marcoscornejos.sales_management_system.mapper;

import com.marcoscornejos.sales_management_system.dto.SaleListResponseDTO;
import com.marcoscornejos.sales_management_system.model.Sale;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for converting {@link Sale} entity to {@link SaleListResponseDTO}.
 *
 * <p>
 * Uses MapStruct to automatically map fields and extract
 * nested user information.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface ISaleListResponseMapper {

    /**
     * Maps a {@link Sale} entity to a {@link SaleListResponseDTO}.
     *
     * <p>
     * Extracts userName from the nested User entity.
     * </p>
     *
     * @param sale the Sale entity
     * @return mapped SaleListResponseDTO
     */
    @Mapping(source = "user.userName", target = "userName")
    SaleListResponseDTO toDto(Sale sale);
}