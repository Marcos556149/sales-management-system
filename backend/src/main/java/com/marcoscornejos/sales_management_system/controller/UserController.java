package com.marcoscornejos.sales_management_system.controller;

import com.marcoscornejos.sales_management_system.dto.*;
import com.marcoscornejos.sales_management_system.service.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador responsable de la gestión de usuarios del sistema.
 *
 * <p>Proporciona endpoints para crear, consultar, actualizar y modificar
 * el estado de los usuarios registrados.
 *
 * <p>El acceso a todos los endpoints de este controlador está restringido
 * exclusivamente a usuarios con rol ADMIN.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final IUserService iUserService;

    /**
     * Crea un nuevo usuario en el sistema.
     *
     * @param request datos necesarios para registrar el usuario
     * @return respuesta HTTP 201 con la información del usuario creado
     */
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(
            @Valid @RequestBody UserRequestDTO request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(iUserService.createUser(request));
    }

    /**
     * Obtiene la lista de todos los usuarios registrados.
     *
     * @return listado de usuarios del sistema
     */
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(
                iUserService.getAllUsers()
        );
    }

    /**
     * Obtiene la información de un usuario a partir de su identificador.
     *
     * @param userId identificador único del usuario
     * @return información del usuario solicitado
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> getUserById(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(
                iUserService.getUserById(userId)
        );
    }

    /**
     * Actualiza los datos de un usuario existente.
     *
     * @param userId identificador del usuario a actualizar
     * @param request datos actualizados del usuario
     * @return información actualizada del usuario
     */
    @PutMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRequestDTO request
    ) {
        return ResponseEntity.ok(
                iUserService.updateUser(userId, request)
        );
    }

    /**
     * Modifica el estado de un usuario.
     *
     * <p>Permite activar, suspender u otro cambio de estado definido
     * por las reglas de negocio del sistema.
     *
     * @param userId identificador del usuario cuyo estado será modificado
     * @param request nuevo estado a asignar
     * @return información actualizada del usuario
     */
    @PatchMapping("/{userId}/status")
    public ResponseEntity<UserResponseDTO> changeUserStatus(
            @PathVariable Long userId,
            @Valid @RequestBody ChangeUserStatusRequestDTO request
    ) {
        return ResponseEntity.ok(
                iUserService.changeUserStatus(userId, request)
        );
    }

    /**
     * Recupera los metadatos necesarios para las operaciones relacionadas con usuarios.
     *
     * <p>
     * Incluye datos dinámicos utilizados por el frontend, como las opciones
     * disponibles de estado de usuario para formularios de edición.
     * </p>
     *
     * @return metadatos de usuarios (estados disponibles)
     */
    @GetMapping("/metadata")
    public ResponseEntity<UserMetadataResponseDTO> getUserMetadata() {

        UserMetadataResponseDTO response = iUserService.getUserMetadata();

        return ResponseEntity.ok(response);
    }
}
