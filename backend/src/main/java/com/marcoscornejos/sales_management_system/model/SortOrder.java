/**
 * Enumeración que define el criterio de ordenamiento para resultados ordenados.
 *
 * <p>
 * Se utiliza para especificar si los datos deben ordenarse de forma
 * ascendente o descendente. Esta enumeración es genérica y puede
 * aplicarse a distintas entidades y consultas dentro del sistema.
 * </p>
 *
 * <p>
 * Cada valor de la enumeración incluye un nombre descriptivo destinado
 * a su representación en la interfaz de usuario.
 * </p>
 */

package com.marcoscornejos.sales_management_system.model;

import lombok.Getter;

@Getter
public enum SortOrder {
    ASCENDING("Ascendente"),
    DESCENDING("Descendente");

    private final String displayName;

    SortOrder(String displayName) {
        this.displayName = displayName;
    }
}
