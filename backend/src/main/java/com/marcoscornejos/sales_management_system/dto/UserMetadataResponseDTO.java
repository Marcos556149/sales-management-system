package com.marcoscornejos.sales_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * DTO que contiene la información complementaria requerida
 * para las operaciones relacionadas con usuarios.
 *
 * <p>
 * Proporciona valores dinámicos utilizados por el frontend,
 * como los estados disponibles de un usuario.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserMetadataResponseDTO {

    /**
     * Lista de estados posibles de un usuario.
     *
     * <p>
     * Cada opción se representa mediante un {@link EnumDTO},
     * que contiene el código del enum y su etiqueta de visualización.
     * </p>
     */
    private List<EnumDTO> userStatusOptions;
}