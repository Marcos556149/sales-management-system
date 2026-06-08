/**
 * Enumeración que define los criterios de ordenamiento basados en la cantidad de productos vendidos.
 *
 * <p>
 * Se utiliza para especificar cómo deben ordenarse los productos al consultar
 * estadísticas de ventas, como clasificar los productos desde los más vendidos
 * hasta los menos vendidos o viceversa.
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
public enum ProductQuantityOrderType {
    MOST_TO_LEAST("Más vendido → menos vendido"),
    LEAST_TO_MOST("Menos vendido → más vendido");

    private final String displayName;

    ProductQuantityOrderType(String displayName) {
        this.displayName = displayName;
    }
}
