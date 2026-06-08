package com.marcoscornejos.sales_management_system.projection;

import java.math.BigDecimal;

/**
 * Interfaz de proyección utilizada para obtener los productos con mejor desempeño
 * según la cantidad vendida.
 *
 * <p>
 * Proporciona únicamente los campos necesarios para generar gráficos
 * de ranking de productos basados en cantidad vendida.
 * </p>
 */
public interface TopProductsByQuantityProjection {

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
     * Cantidad total vendida (admite valores fraccionarios según
     * la unidad de medida del producto).
     */
    BigDecimal getQuantitySold();
}