package com.marcoscornejos.sales_management_system.service;

import com.marcoscornejos.sales_management_system.dto.*;
import com.marcoscornejos.sales_management_system.model.User;
import com.marcoscornejos.sales_management_system.model.UserRole;

import java.util.List;
import java.util.Optional;

public interface IUserService {
    UserResponseDTO createUser(UserRequestDTO request);

    List<UserResponseDTO> getAllUsers();

    UserResponseDTO getUserById(Long id);

    UserResponseDTO updateUser(Long id, UpdateUserRequestDTO request);

    UserResponseDTO changeUserStatus(Long id, ChangeUserStatusRequestDTO request);


    UserMetadataResponseDTO getUserMetadata();
}
