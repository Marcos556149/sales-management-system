package com.marcoscornejos.sales_management_system.projection;

/**
 * Interfaz de proyección utilizada para obtener productos que no registran ventas
 * dentro de un rango de filtros determinado.
 *
 * <p>
 * Esta proyección devuelve únicamente datos básicos de identificación
 * del producto, ya que los productos no vendidos no poseen métricas
 * de ventas agregadas.
 * </p>
 */
public interface UnsoldProductProjection {

    /**
     * Código único que identifica al producto.
     */
    String getProductCode();

    /**
     * Name of the product.
     */
    String getProductName();
}