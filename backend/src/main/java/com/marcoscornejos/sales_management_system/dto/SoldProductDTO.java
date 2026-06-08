package com.marcoscornejos.sales_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Representa un producto con métricas de rendimiento de ventas asociadas.
 *
 * <p>
 * Este DTO se utiliza en el ranking de productos y análisis de productos destacados,
 * e incluye tanto información de identidad como indicadores de rendimiento,
 * como la cantidad vendida y los ingresos generados dentro del rango
 * de filtros seleccionado.
 * </p>
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SoldProductDTO {

    /**
     * Código único del producto que lo identifica.
     */
    private String productCode;

    /**
     * Nombre del producto.
     */
    private String productName;

    /**
     * Cantidad total de unidades vendidas de este producto en el rango seleccionado.
     */
    private BigDecimal quantitySold;

    /**
     * Ingresos totales generados por este producto en el rango seleccionado.
     */
    private BigDecimal revenueGenerated;
}
