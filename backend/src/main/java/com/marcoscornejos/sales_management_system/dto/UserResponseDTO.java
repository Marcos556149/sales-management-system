package com.marcoscornejos.sales_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO utilizado para representar la información de un usuario
 * enviada en las respuestas de la API.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {

    /**
     * Identificador único del usuario.
     */
    private Long userId;

    /**
     * Nombre de usuario.
     */
    private String userName;

    /**
     * Rol asignado al usuario.
     */
    private EnumDTO userRole;

    /**
     * Estado actual del usuario.
     */
    private EnumDTO userStatus;

}
