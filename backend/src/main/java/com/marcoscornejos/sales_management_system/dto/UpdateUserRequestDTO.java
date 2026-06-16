package com.marcoscornejos.sales_management_system.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO utilizado para actualizar la información de un usuario.
 *
 * <p>Todos los campos son opcionales. Solo se actualizarán aquellos
 * que sean enviados en la solicitud.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequestDTO {

    /**
     * Nuevo nombre de usuario (opcional).
     *
     * <p>
     * Si se envía, no debe superar los 100 caracteres.
     * </p>
     */
    @Size(max = 100, message = "El nombre de usuario no debe superar los 100 caracteres")
    private String userName;

    /**
     * Nueva contraseña del usuario (opcional).
     *
     * <p>
     * Si se envía, no debe superar los 72 caracteres.
     * </p>
     */
    @Size(max = 72, message = "La contraseña no debe superar los 72 caracteres")
    private String userPassword;
}
