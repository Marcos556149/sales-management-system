package com.marcoscornejos.sales_management_system.controller;

import com.marcoscornejos.sales_management_system.dto.LoginRequestDTO;
import com.marcoscornejos.sales_management_system.dto.LoginResponseDTO;
import com.marcoscornejos.sales_management_system.exception.AuthException;
import com.marcoscornejos.sales_management_system.service.IAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling authentication endpoints.
 *
 * <p>Delegates authentication logic to {@link IAuthService}.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService iAuthService;

    /**
     * Endpoint for user login.
     *
     * @param request the login request DTO containing username and password
     * @return {@link LoginResponseDTO} with user information if authentication is successful
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(iAuthService.login(request));
    }
}
