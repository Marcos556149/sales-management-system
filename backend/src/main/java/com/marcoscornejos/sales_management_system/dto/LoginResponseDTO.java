package com.marcoscornejos.sales_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO que representa la información devuelta al cliente
 * después de un inicio de sesión exitoso.
 *
 * <p>Contiene detalles visibles para el usuario, como el nombre de usuario,
 * el rol asignado y el idioma de interfaz preferido.</p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {

    /** Nombre de usuario del usuario autenticado. */
    private String userName;

    /** Rol asignado al usuario (código y etiqueta). */
    private EnumDTO userRole;

}