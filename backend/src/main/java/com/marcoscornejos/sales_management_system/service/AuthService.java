package com.marcoscornejos.sales_management_system.service;

import com.marcoscornejos.sales_management_system.dto.LoginRequestDTO;
import com.marcoscornejos.sales_management_system.dto.LoginResponseDTO;
import com.marcoscornejos.sales_management_system.exception.AuthException;
import com.marcoscornejos.sales_management_system.mapper.ILoginRequestMapper;
import com.marcoscornejos.sales_management_system.mapper.ILoginResponseMapper;
import com.marcoscornejos.sales_management_system.model.User;
import com.marcoscornejos.sales_management_system.model.UserStatus;
import com.marcoscornejos.sales_management_system.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service for handling authentication logic.
 *
 * <p>Validates user credentials and ensures the user account is active.</p>
 */
@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService{

    private final IUserRepository iUserRepository;
    private final ILoginRequestMapper iLoginRequestMapper;
    private final ILoginResponseMapper iLoginResponseMapper;

    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {

        // Convert DTO to User
        User loginUser = iLoginRequestMapper.toUser(request);

        // Find user
        User user = iUserRepository.findByUserName(loginUser.getUserName())
                .orElseThrow(() -> new AuthException(
                        "USER_NOT_FOUND",
                        "User not found",
                        "userName"
                ));

        // Validate password
        if (!user.getUserPassword().equals(loginUser.getUserPassword())) {
            throw new AuthException(
                    "INVALID_CREDENTIALS",
                    "Invalid username or password",
                    "userPassword"
            );
        }

        // Validate status
        if (user.getUserStatus() != UserStatus.ACTIVE) {
            throw new AuthException(
                    "USER_INACTIVE",
                    "User account is not active",
                    "userStatus"
            );
        }

        // Success
        return iLoginResponseMapper.toDto(user);
    }
}
