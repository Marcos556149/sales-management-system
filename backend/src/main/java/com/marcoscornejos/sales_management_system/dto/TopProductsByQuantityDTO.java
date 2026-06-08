package com.marcoscornejos.sales_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Representa un producto destacado ordenado por cantidad vendida.
 *
 * <p>
 * Este DTO se utiliza para visualizaciones de gráficos que muestran
 * los productos con mayor número de unidades vendidas dentro del
 * rango de filtros seleccionado.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TopProductsByQuantityDTO {

    /**
     * Código único del producto que lo identifica.
     */
    private String productCode;

    /**
     * Nombre del producto.
     */
    private String productName;

    /**
     * Cantidad total vendida (puede ser fraccionaria dependiendo del tipo de unidad,
     * por ejemplo: kilogramos, litros, etc.).
     */
    private BigDecimal quantitySold;
}
