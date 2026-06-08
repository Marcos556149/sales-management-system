package com.marcoscornejos.sales_management_system.mapper;

import com.marcoscornejos.sales_management_system.dto.LoginResponseDTO;
import com.marcoscornejos.sales_management_system.dto.EnumDTO;
import com.marcoscornejos.sales_management_system.model.User;
import com.marcoscornejos.sales_management_system.model.UserRole;
import org.mapstruct.Mapper;

/**
 * Mapper para convertir entre {@link User} y {@link LoginResponseDTO}.
 *
 * <p>
 * Maneja las transformaciones necesarias para devolver la información
 * del usuario después del inicio de sesión.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface ILoginResponseMapper {

    /**
     * Mapea una entidad {@link User} a un {@link LoginResponseDTO}.
     *
     * @param user entidad User
     * @return LoginResponseDTO con el nombre de usuario y rol
     */
    LoginResponseDTO toDto(User user);

    /**
     * Mapea un {@link UserRole} a un {@link EnumDTO}.
     *
     * <p>
     * Convierte el enum de rol de usuario a un DTO genérico de enumeración
     * con su valor técnico y su nombre visible.
     * </p>
     *
     * @param role rol del usuario
     * @return EnumDTO con el valor y nombre del rol, o null si es null
     */
    default EnumDTO map(UserRole role) {
        if (role == null) {
            return null;
        }
        return new EnumDTO(role.name(), role.getDisplayName());
    }
}