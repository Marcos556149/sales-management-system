package com.marcoscornejos.sales_management_system.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO que representa los ingresos totales calculados
 * en base a los filtros seleccionados.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TotalRevenueResponseDTO {

    /**
     * Suma total de los montos de ventas.
     */
    private BigDecimal totalRevenue;
}