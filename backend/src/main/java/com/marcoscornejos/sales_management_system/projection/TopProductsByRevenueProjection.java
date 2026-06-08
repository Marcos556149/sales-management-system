package com.marcoscornejos.sales_management_system.projection;

import java.math.BigDecimal;

/**
 * Interfaz de proyección utilizada para obtener los productos con mejor desempeño
 * según los ingresos generados.
 *
 * <p>
 * Proporciona únicamente los campos necesarios para generar gráficos
 * de ranking de productos basados en ingresos generados.
 * </p>
 */
public interface TopProductsByRevenueProjection {

    /**
     * Código único que identifica al producto.
     *
     * @return código del producto
     */
    String getProductCode();

    /**
     * Nombre del producto.
     *
     * @return nombre del producto
     */
    String getProductName();

    /**
     * Ingresos totales generados por este producto
     * dentro del rango seleccionado.
     *
     * @return ingresos generados
     */
    BigDecimal getRevenueGenerated();
}