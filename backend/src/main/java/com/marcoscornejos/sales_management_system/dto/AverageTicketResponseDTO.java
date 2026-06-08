package com.marcoscornejos.sales_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO que representa el valor promedio del ticket
 * calculado en base a los filtros seleccionados.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AverageTicketResponseDTO {

    /**
     * Valor promedio del ticket.
     *
     * <p>
     * Se calcula como:
     * ingresos totales / cantidad total de ventas.
     * </p>
     */
    private BigDecimal averageTicket;
}
