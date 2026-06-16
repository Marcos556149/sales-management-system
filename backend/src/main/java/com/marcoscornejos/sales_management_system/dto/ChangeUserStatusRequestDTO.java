package com.marcoscornejos.sales_management_system.dto;

import com.marcoscornejos.sales_management_system.model.UserStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO utilizado para solicitar el cambio de estado de un usuario.
 *
 * <p>Contiene el nuevo estado que será asignado al usuario
 * seleccionado.
 */
@Getter
@Setter
public class ChangeUserStatusRequestDTO {

    /**
     * Nuevo estado del usuario.
     */
    @NotNull(message = "El estado del usuario es obligatorio")
    private UserStatus userStatus;
}
