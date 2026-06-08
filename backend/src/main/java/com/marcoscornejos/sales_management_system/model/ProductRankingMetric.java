/**
 * Enumeración que define la métrica utilizada para el ranking de productos
 * en las estadísticas de ventas.
 *
 * <p>
 * Esta enumeración determina cómo se evalúan y ordenan los productos dentro
 * de los reportes estadísticos, permitiendo clasificarlos según la cantidad
 * vendida o los ingresos generados.
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
public enum ProductRankingMetric {
    QUANTITY_SOLD("Cantidad Vendida"),
    REVENUE_GENERATED("Ingresos Generados");

    private final String displayName;

    ProductRankingMetric(String displayName) {
        this.displayName = displayName;
    }
}
