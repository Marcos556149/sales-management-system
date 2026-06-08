package com.marcoscornejos.sales_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO que representa la cantidad total de ventas
 * calculadas en base a los filtros seleccionados.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TotalSalesResponseDTO {

    /**
     * Cantidad total de ventas coincidentes.
     */
    private Long totalSales;
}
