package com.marcoscornejos.sales_management_system.mapper;

import com.marcoscornejos.sales_management_system.dto.SaleListResponseDTO;
import com.marcoscornejos.sales_management_system.model.Sale;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper para convertir una entidad {@link Sale}
 * en un {@link SaleListResponseDTO}.
 *
 * <p>
 * Utiliza MapStruct para mapear automáticamente los campos
 * y extraer información anidada del usuario.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface ISaleListResponseMapper {

    /**
     * Convierte una entidad {@link Sale} en un {@link SaleListResponseDTO}.
     *
     * <p>
     * Extrae el nombre de usuario desde la entidad User asociada.
     * </p>
     *
     * @param sale entidad Sale
     * @return SaleListResponseDTO mapeado
     */
    @Mapping(source = "user.userName", target = "userName")
    SaleListResponseDTO toDto(Sale sale);
}