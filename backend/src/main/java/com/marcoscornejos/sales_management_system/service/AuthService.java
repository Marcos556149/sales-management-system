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
 * Servicio encargado de gestionar la lógica de autenticación.
 *
 * <p>
 * Valida las credenciales del usuario y verifica que la cuenta
 * se encuentre en estado activo.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService{

    private final IUserRepository iUserRepository;
    private final ILoginRequestMapper iLoginRequestMapper;
    private final ILoginResponseMapper iLoginResponseMapper;

    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {

        // Convertir DTO a Usuario
        User loginUser = iLoginRequestMapper.toUser(request);

        // Buscar usuario
        User user = iUserRepository.findByUserName(loginUser.getUserName())
                .orElseThrow(() -> new AuthException(
                        "USER_NOT_FOUND",
                        "Usuario no encontrado",
                        "userName"
                ));

        // Validar contraseña
        if (!user.getUserPassword().equals(loginUser.getUserPassword())) {
            throw new AuthException(
                    "INVALID_CREDENTIALS",
                    "Usuario o contraseña incorrectos",
                    "userPassword"
            );
        }

        // Validar estado
        if (user.getUserStatus() != UserStatus.ACTIVE) {
            throw new AuthException(
                    "USER_INACTIVE",
                    "La cuenta de usuario no está activa",
                    "userStatus"
            );
        }

        // Autenticación exitosa
        return iLoginResponseMapper.toDto(user);
    }
}
