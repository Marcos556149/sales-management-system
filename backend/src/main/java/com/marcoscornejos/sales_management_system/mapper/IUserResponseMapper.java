package com.marcoscornejos.sales_management_system.mapper;

import com.marcoscornejos.sales_management_system.dto.EnumDTO;
import com.marcoscornejos.sales_management_system.dto.UserResponseDTO;
import com.marcoscornejos.sales_management_system.model.User;
import com.marcoscornejos.sales_management_system.model.UserRole;
import com.marcoscornejos.sales_management_system.model.UserStatus;
import org.mapstruct.Mapper;

/**
 * Mapper encargado de convertir la entidad {@link User}
 * en su representación de respuesta {@link UserResponseDTO}.
 *
 * <p>Este mapper se utiliza en la capa de presentación para transformar
 * entidades de dominio en objetos DTO expuestos a la API.</p>
 */
@Mapper(componentModel = "spring")
public interface IUserResponseMapper {

    /**
     * Convierte una entidad {@link User} en un DTO de respuesta.
     *
     * @param user entidad de dominio
     * @return representación DTO del usuario
     */
    UserResponseDTO toDto(User user);

    /**
     * Convierte un {@link UserRole} en un {@link EnumDTO}.
     *
     * <p>Se utiliza para estandarizar la representación de enums
     * en las respuestas de la API.</p>
     */
    default EnumDTO map(UserRole role) {
        if (role == null) return null;

        return new EnumDTO(
                role.name(),
                role.getDisplayName()
        );
    }

    /**
     * Convierte un {@link UserStatus} en un {@link EnumDTO}.
     *
     * <p>Se utiliza para estandarizar la representación de enums
     * en las respuestas de la API.</p>
     */
    default EnumDTO map(UserStatus status) {
        if (status == null) return null;

        return new EnumDTO(
                status.name(),
                status.getDisplayName()
        );
    }
}
