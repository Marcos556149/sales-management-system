package com.marcoscornejos.sales_management_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO utilizado para registrar un nuevo usuario en el sistema.
 *
 * <p>
 * Contiene la información necesaria para la creación de un usuario,
 * incluyendo sus credenciales de acceso iniciales.
 * </p>
 *
 * <p>
 * Este DTO es utilizado únicamente en el proceso de alta de usuarios
 * y no en operaciones de actualización.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO {

    /**
     * Nombre de usuario utilizado para el inicio de sesión.
     *
     * <p>
     * Debe ser único dentro del sistema y no puede estar vacío.
     * Su longitud máxima es de 100 caracteres.
     * </p>
     */
    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(max = 100, message = "El nombre de usuario no debe superar los 100 caracteres")
    private String userName;

    /**
     * Contraseña del usuario en texto plano.
     *
     * <p>
     * Será encriptada antes de ser persistida en la base de datos.
     * Su longitud máxima permitida es de 72 caracteres debido al límite de BCrypt.
     * </p>
     */
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(max = 72, message = "La contraseña no debe superar los 72 caracteres")
    private String userPassword;

}
