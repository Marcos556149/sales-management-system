/**
 * Enumeración que representa el estado de un producto.
 *
 * <p>
 * Se utiliza para indicar si un producto está activo (disponible para operaciones)
 * o inactivo (deshabilitado lógicamente). La opción ALL se utiliza
 * generalmente con fines de filtrado al consultar productos sin importar su estado.
 * </p>
 *
 * <p>
 * Esta enumeración se utiliza comúnmente junto con mecanismos de eliminación lógica
 * y operaciones de filtrado en consultas.
 * </p>
 *
 * <p>
 * Cada valor de la enumeración incluye un nombre descriptivo destinado a su
 * representación en la interfaz de usuario.
 * </p>
 */

package com.marcoscornejos.sales_management_system.model;

import lombok.Getter;

@Getter
public enum ProductStatus {
    ACTIVE("Activo"),
    INACTIVE("Inactivo"),
    ALL("Todos los estados");

    private final String displayName;

    ProductStatus(String displayName) {
        this.displayName = displayName;
    }
}
