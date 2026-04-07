package com.marcoscornejos.sales_management_system.mapper;

import com.marcoscornejos.sales_management_system.dto.LoginRequestDTO;
import com.marcoscornejos.sales_management_system.model.User;
import org.mapstruct.Mapper;

/**
 * Mapper for converting between {@link LoginRequestDTO} and {@link User} entities.
 *
 * <p>Handles transformations needed for authentication requests and User data.</p>
 */
@Mapper(componentModel = "spring")
public interface LoginRequestMapper {

    /**
     * Maps a {@link LoginRequestDTO} to a {@link User} entity.
     *
     * @param dto the login request DTO containing userName and userPassword
     * @return a User entity with userName and userPassword set
     */
    User toUser(LoginRequestDTO dto);

    /**
     * Maps a {@link User} entity to a {@link LoginRequestDTO}.
     *
     * @param user the User entity
     * @return a LoginRequestDTO with userName and userPassword set
     */
    LoginRequestDTO toDto(User user);
}