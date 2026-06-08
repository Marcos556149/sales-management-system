package com.marcoscornejos.sales_management_system.projection;

import java.math.BigDecimal;

/**
 * Interfaz de proyección utilizada para obtener estadísticas de ventas
 * de productos directamente desde consultas a la base de datos.
 *
 * <p>
 * Proporciona datos agregados sobre el desempeño de los productos,
 * como la cantidad vendida y los ingresos generados dentro de un rango
 * de filtros determinado.
 * </p>
 */
public interface SoldProductProjection {

    /**
     * Código único que identifica al producto.
     */
    String getProductCode();

    /**
     * Nombre del producto.
     */
    String getProductName();

    /**
     * Cantidad total vendida de este producto
     * dentro del rango seleccionado.
     */
    BigDecimal getQuantitySold();

    /**
     * Ingresos totales generados por este producto
     * dentro del rango seleccionado.
     */
    BigDecimal getRevenueGenerated();
}
