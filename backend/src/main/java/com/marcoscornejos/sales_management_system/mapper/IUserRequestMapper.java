package com.marcoscornejos.sales_management_system.mapper;

import com.marcoscornejos.sales_management_system.dto.UserRequestDTO;
import com.marcoscornejos.sales_management_system.model.User;
import org.mapstruct.Mapper;

/**
 * Mapper encargado de convertir objetos {@link UserRequestDTO}
 * en entidades {@link User}.
 *
 * <p>Se utiliza para transformar la información recibida desde la API
 * en objetos de dominio que pueden ser procesados y persistidos por
 * la capa de negocio.</p>
 */
@Mapper(componentModel = "spring")
public interface IUserRequestMapper {

    /**
     * Convierte un DTO de creación de usuario en una entidad.
     *
     * @param dto información del usuario recibida en la solicitud
     * @return entidad {@link User} generada a partir del DTO
     */
    User toEntity(UserRequestDTO dto);

}
