package com.marcoscornejos.sales_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO genérico para representar valores de enumeraciones mediante un código y una etiqueta.
 */
@Getter @Setter
@AllArgsConstructor
public class EnumDTO {

    /** Código interno de la enumeración (por ejemplo, ADMIN, ACTIVE). */
    private String code;

    /** Etiqueta legible para el usuario (por ejemplo, Administrador, Activo). */
    private String label;
}