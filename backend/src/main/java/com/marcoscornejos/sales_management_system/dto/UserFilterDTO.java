package com.marcoscornejos.sales_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO que representa un usuario disponible para filtros
 * en el módulo de estadísticas.
 *
 * <p>
 * Este DTO es utilizado por el frontend para mostrar usuarios seleccionables
 * en los filtros desplegables.
 * </p>
 *
 * <p>
 * El frontend utiliza:
 * <ul>
 *   <li>userId → para el filtrado en backend</li>
 *   <li>userName → para propósitos de visualización</li>
 * </ul>
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserFilterDTO {

    /**
     * Identificador único del usuario.
     *
     * <p>
     * Este valor se envía al backend al aplicar filtros de estadísticas.
     * </p>
     */
    private Long userId;

    /**
     * Nombre visible del usuario.
     *
     * <p>
     * Este valor se muestra en la interfaz del frontend.
     * </p>
     */
    private String userName;
}