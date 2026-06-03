/**
 * Enumeración que define las opciones de filtrado por nivel de stock de los productos.
 *
 * <p>
 * Se utiliza para clasificar y filtrar productos según su condición
 * actual de stock en relación con el nivel mínimo de stock configurado.
 * </p>
 *
 * <p>
 * Esta enumeración resulta especialmente útil en las vistas de productos
 * e inventario, permitiendo a los usuarios identificar rápidamente
 * productos con stock normal, bajo stock o sin stock disponible.
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
public enum StockLevelFilter {

    ALL("Todos los niveles de stock"),
    NORMAL("Stock normal"),
    LOW("Bajo stock"),
    OUT_OF_STOCK("Sin stock");

    private final String displayName;

    StockLevelFilter(String displayName) {
        this.displayName = displayName;
    }
}