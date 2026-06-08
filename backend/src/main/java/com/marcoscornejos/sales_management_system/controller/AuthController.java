package com.marcoscornejos.sales_management_system.controller;

import com.marcoscornejos.sales_management_system.dto.LoginRequestDTO;
import com.marcoscornejos.sales_management_system.dto.LoginResponseDTO;
import com.marcoscornejos.sales_management_system.service.IAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador encargado de los endpoints de autenticación.
 *
 * <p>Delega la lógica de autenticación al servicio {@link IAuthService}.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService iAuthService;

    /**
     * Endpoint para el inicio de sesión del usuario.
     *
     * @param request DTO de inicio de sesión que contiene el nombre de usuario y la contraseña
     * @return {@link LoginResponseDTO} con la información del usuario si la autenticación es exitosa
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(iAuthService.login(request));
    }
}
