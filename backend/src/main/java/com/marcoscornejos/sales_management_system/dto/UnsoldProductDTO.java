package com.marcoscornejos.sales_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representa un producto que no tuvo ventas dentro del rango de filtros seleccionado.
 *
 * <p>
 * Este DTO se utiliza para identificar productos con actividad de ventas nula,
 * lo que ayuda a analizar ineficiencias de inventario, productos de baja demanda
 * o posibles problemas de stock.
 * </p>
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UnsoldProductDTO {

    /**
     * Código único del producto que lo identifica.
     */
    private String productCode;

    /**
     * Nombre del producto.
     */
    private String productName;
}