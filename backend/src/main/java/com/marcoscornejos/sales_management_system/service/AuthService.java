package com.marcoscornejos.sales_management_system.service;

import com.marcoscornejos.sales_management_system.dto.LoginRequestDTO;
import com.marcoscornejos.sales_management_system.dto.LoginResponseDTO;
import com.marcoscornejos.sales_management_system.exception.AuthException;
import com.marcoscornejos.sales_management_system.mapper.ILoginResponseMapper;
import com.marcoscornejos.sales_management_system.model.User;
import com.marcoscornejos.sales_management_system.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

/**
 * Servicio encargado de la autenticación de usuarios.
 *
 * <p>Valida las credenciales del usuario contra el sistema de seguridad
 * de Spring Security y gestiona la creación de la sesión autenticada.</p>
 *
 * <p>Responsabilidades principales:
 * <ul>
 *     <li>Autenticar credenciales mediante {@link AuthenticationManager}</li>
 *     <li>Registrar la autenticación en el {@link SecurityContextHolder}</li>
 *     <li>Persistir la sesión HTTP del usuario autenticado</li>
 *     <li>Retornar la información del usuario autenticado en formato DTO</li>
 * </ul>
 * </p>
 */
@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService{

    private final ILoginResponseMapper iLoginResponseMapper;
    private final AuthenticationManager authenticationManager;

    /**
     * Autentica a un usuario en el sistema.
     *
     * <p>Si las credenciales son válidas, se crea una sesión autenticada
     * y se devuelve la información del usuario.</p>
     *
     * @param request datos de login (usuario y contraseña)
     * @param httpRequest solicitud HTTP utilizada para crear la sesión
     * @return información del usuario autenticado
     * @throws AuthException si las credenciales son inválidas o el usuario
     *                       no está activo
     */
    @Override
    public LoginResponseDTO login(
            LoginRequestDTO request,
            HttpServletRequest httpRequest
    ) {

        try {

            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    request.getUserName(),
                                    request.getUserPassword()
                            )
                    );

            // 🔐 1. Guardar en SecurityContext
            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);

            // 🔐 2. Guardar en sesión HTTP
            HttpSession session = httpRequest.getSession(true);
            session.setAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext()
            );

            CustomUserDetails userDetails =
                    (CustomUserDetails) authentication.getPrincipal();

            User user = userDetails.getUser();

            return iLoginResponseMapper.toDto(user);

        } catch (BadCredentialsException ex) {

            throw new AuthException(
                    "INVALID_CREDENTIALS",
                    "Usuario o contraseña incorrectos",
                    null
            );

        } catch (LockedException | DisabledException ex) {

            throw new AuthException(
                    "USER_INACTIVE",
                    "La cuenta de usuario no está activa",
                    null
            );
        }
    }
}
