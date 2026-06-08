package com.marcoscornejos.sales_management_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO utilizado para actualizar la configuración global del sistema.
 *
 * <p>
 * Contiene los valores de configuración que pueden ser modificados por el usuario.
 * Estos valores son compartidos en todo el sistema.
 * </p>
 */
@Getter
@Setter
public class SystemConfigurationRequestDTO {

    /**
     * Nombre del negocio.
     *
     * <p>
     * No puede estar vacío y no debe superar los 100 caracteres.
     * Este valor es compartido por todos los usuarios.
     * </p>
     */
    @NotBlank(message = "El nombre del negocio es obligatorio")
    @Size(max = 100, message = "El nombre del negocio no debe superar los 100 caracteres")
    private String businessName;

    /**
     * Dirección del negocio.
     *
     * <p>
     * No puede estar vacía y no debe superar los 200 caracteres.
     * Este valor es compartido por todos los usuarios.
     * </p>
     */
    @NotBlank(message = "La dirección del negocio es obligatoria")
    @Size(max = 200, message = "La dirección del negocio no debe superar los 200 caracteres")
    private String businessAddress;
}