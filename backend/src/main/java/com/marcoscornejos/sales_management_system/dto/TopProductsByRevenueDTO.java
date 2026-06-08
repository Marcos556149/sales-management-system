package com.marcoscornejos.sales_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Representa un producto destacado ordenado por ingresos generados.
 *
 * <p>
 * Este DTO se utiliza para visualizaciones de gráficos que muestran
 * los productos que generaron mayores ingresos dentro del rango
 * de filtros seleccionado.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TopProductsByRevenueDTO {

    /**
     * Código único del producto que lo identifica.
     */
    private String productCode;

    /**
     * Nombre del producto.
     */
    private String productName;

    /**
     * Ingresos totales generados por este producto
     * dentro del rango seleccionado.
     */
    private BigDecimal revenueGenerated;
}