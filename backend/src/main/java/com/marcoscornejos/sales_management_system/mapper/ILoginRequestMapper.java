package com.marcoscornejos.sales_management_system.mapper;

import com.marcoscornejos.sales_management_system.dto.LoginRequestDTO;
import com.marcoscornejos.sales_management_system.model.User;
import org.mapstruct.Mapper;

/**
 * Mapper para convertir entre {@link LoginRequestDTO} y {@link User}.
 *
 * <p>
 * Maneja las transformaciones necesarias para las solicitudes de autenticación
 * y los datos del usuario.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface ILoginRequestMapper {

    /**
     * Mapea un {@link LoginRequestDTO} a una entidad {@link User}.
     *
     * @param dto DTO de login que contiene nombre de usuario y contraseña
     * @return entidad User con nombre de usuario y contraseña asignados
     */
    User toUser(LoginRequestDTO dto);

    /**
     * Mapea una entidad {@link User} a un {@link LoginRequestDTO}.
     *
     * @param user entidad User
     * @return LoginRequestDTO con nombre de usuario y contraseña asignados
     */
    LoginRequestDTO toDto(User user);
}