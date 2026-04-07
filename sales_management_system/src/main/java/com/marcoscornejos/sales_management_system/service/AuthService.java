package com.marcoscornejos.sales_management_system.service;

import com.marcoscornejos.sales_management_system.dto.LoginRequestDTO;
import com.marcoscornejos.sales_management_system.dto.LoginResponseDTO;
import com.marcoscornejos.sales_management_system.exception.AuthException;
import com.marcoscornejos.sales_management_system.mapper.ILoginRequestMapper;
import com.marcoscornejos.sales_management_system.mapper.ILoginResponseMapper;
import com.marcoscornejos.sales_management_system.model.User;
import com.marcoscornejos.sales_management_system.model.UserStatus;
import com.marcoscornejos.sales_management_system.repository.IUserRepository;
import org.springframework.stereotype.Service;

/**
 * Service for handling authentication logic.
 *
 * <p>Validates user credentials and ensures the user account is active.</p>
 */
@Service
public class AuthService {

    private final IUserRepository iUserRepository;
    private final ILoginRequestMapper iLoginRequestMapper;
    private final ILoginResponseMapper iLoginResponseMapper;

    public AuthService(IUserRepository iUserRepository, ILoginRequestMapper iLoginRequestMapper, ILoginResponseMapper iLoginResponseMapper) {
        this.iUserRepository = iUserRepository;
        this.iLoginRequestMapper = iLoginRequestMapper;
        this.iLoginResponseMapper = iLoginResponseMapper;
    }

    /**
     * Validates login credentials for a user.
     *
     * <p>If credentials are valid and the user status is ACTIVE, returns
     * a {@link LoginResponseDTO} containing user information for the front-end.</p>
     *
     * @param request the login request containing username and password
     * @return LoginResponseDTO with userName, userRole, and language
     * @throws IllegalArgumentException if credentials are invalid or user is not active
     */
    public LoginResponseDTO login(LoginRequestDTO request) {
        // Convert DTO to User
        User loginUser = iLoginRequestMapper.toUser(request);

        // Look up user by username, throw exception if not found
        User user = iUserRepository.findByUserName(loginUser.getUserName())
                .orElseThrow(() -> new AuthException("Invalid credentials"));

        // Check password (plaintext for now)
        if (!user.getUserPassword().equals(loginUser.getUserPassword())) {
            throw new AuthException("Invalid credentials");
        }

        // Check user status
        if (user.getUserStatus() != UserStatus.ACTIVE) {
            throw new AuthException("User account is not active");
        }

        // Return LoginResponseDTO for the front-end
        return iLoginResponseMapper.toDto(user);

    }
}
