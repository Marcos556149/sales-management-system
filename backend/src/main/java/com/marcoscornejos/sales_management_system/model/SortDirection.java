/**
 * Enumeración que define las opciones de dirección de ordenamiento para datos cronológicos.
 *
 * <p>
 * Se utiliza para especificar el orden en que se ordenan los registros basados en tiempo,
 * como el historial de ventas, transacciones o reportes.
 * </p>
 *
 * <p>
 * Esta enumeración es especialmente útil en operaciones de listado donde los usuarios necesitan
 * ver primero los registros más recientes o los más antiguos.
 * </p>
 *
 * <p>
 * Cada valor del enum incluye un nombre legible destinado a su representación en la interfaz de usuario.
 * </p>
 */

package com.marcoscornejos.sales_management_system.model;

import lombok.Getter;

@Getter
public enum SortDirection {

    NEWEST_FIRST("Más recientes primero"),
    OLDEST_FIRST("Más antiguos primero");

    private final String displayName;

    SortDirection(String displayName) {
        this.displayName = displayName;
    }
}
