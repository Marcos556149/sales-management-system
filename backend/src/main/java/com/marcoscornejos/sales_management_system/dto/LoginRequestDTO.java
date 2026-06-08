package com.marcoscornejos.sales_management_system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para recibir los datos de inicio de sesión desde el frontend.
 *
 * <p>Este objeto contiene el nombre de usuario y la contraseña ingresados
 * por el usuario en el formulario de login.</p>
 *
 * <p>La validación asegura que ningún campo quede vacío.</p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {

    /** Nombre de usuario ingresado por el usuario. No debe estar vacío. */
    @NotBlank(message = "El nombre de usuario es obligatorio")
    private String userName;

    /** Contraseña ingresada por el usuario. No debe estar vacía. */
    @NotBlank(message = "La contraseña es obligatoria")
    private String userPassword;
}