package com.marcoscornejos.sales_management_system.mapper;

import com.marcoscornejos.sales_management_system.dto.LoginResponseDTO;
import com.marcoscornejos.sales_management_system.dto.EnumDTO;
import com.marcoscornejos.sales_management_system.model.User;
import com.marcoscornejos.sales_management_system.model.UserRole;
import org.mapstruct.Mapper;

/**
 * Mapper for converting between {@link User} and {@link LoginResponseDTO}.
 *
 * <p>Handles transformations needed to return user info after login.</p>
 */
@Mapper(componentModel = "spring")
public interface ILoginResponseMapper {

    /**
     * Maps a {@link User} entity to a {@link LoginResponseDTO}.
     *
     * @param user the User entity
     * @return a LoginResponseDTO containing userName, userRole
     */
    LoginResponseDTO toDto(User user);

    /**
     * Maps {@link UserRole} to {@link EnumDTO}.
     */
    default EnumDTO map(UserRole role) {
        if (role == null) {
            return null;
        }
        return new EnumDTO(role.name(), role.getDisplayName());
    }
}