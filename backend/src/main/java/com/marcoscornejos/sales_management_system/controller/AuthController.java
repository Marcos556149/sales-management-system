package com.marcoscornejos.sales_management_system.controller;

import com.marcoscornejos.sales_management_system.dto.LoginRequestDTO;
import com.marcoscornejos.sales_management_system.dto.LoginResponseDTO;
import com.marcoscornejos.sales_management_system.service.IAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador responsable de gestionar los procesos de autenticación.
 *
 * <p>Expone los endpoints relacionados con el inicio de sesión y delega
 * la lógica de negocio al servicio {@link IAuthService}.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService iAuthService;

    /**
     * Autentica a un usuario utilizando sus credenciales.
     *
     * <p>Valida los datos recibidos y delega el proceso de autenticación
     * al servicio correspondiente. Si las credenciales son válidas,
     * retorna la información necesaria para la sesión autenticada.
     *
     * @param request solicitud de inicio de sesión que contiene el nombre
     *                de usuario y la contraseña
     * @param httpRequest petición HTTP utilizada para obtener información
     *                    contextual de la solicitud
     * @return respuesta HTTP con los datos del usuario autenticado
     *         encapsulados en un {@link LoginResponseDTO}
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO request,
            HttpServletRequest httpRequest
    ) {
        return ResponseEntity.ok(
                iAuthService.login(request, httpRequest)
        );
    }

    /**
     * Finaliza la sesión activa del usuario autenticado.
     *
     * <p>
     * Este endpoint invalida completamente la sesión HTTP actual,
     * eliminando el contexto de seguridad asociado al usuario.
     * </p>
     *
     * <p>
     * El proceso de cierre de sesión incluye:
     * <ul>
     *     <li>Invalidación de la sesión HTTP</li>
     *     <li>Limpieza del SecurityContext de Spring Security</li>
     *     <li>Eliminación de la autenticación activa</li>
     * </ul>
     * </p>
     *
     * <p>
     * Una vez ejecutado, el usuario queda completamente desautenticado
     * y deberá iniciar sesión nuevamente para acceder al sistema.
     * </p>
     *
     * @param request petición HTTP actual utilizada para invalidar la sesión
     * @param response respuesta HTTP utilizada para limpiar cookies de sesión
     * @return respuesta HTTP 200 OK sin contenido si el logout fue exitoso
     */
    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }

        return ResponseEntity.ok().build();
    }
}
