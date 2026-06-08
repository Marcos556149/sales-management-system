package com.marcoscornejos.sales_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO que representa estadísticas de ventas agregadas
 * basadas en los filtros seleccionados.
 *
 * <p>
 * Este objeto agrupa toda la información analítica,
 * como KPIs de ventas, rankings de productos y datos
 * de productos no vendidos.
 * </p>
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SalesStatisticsResponseDTO {

    /**
     * Información agregada de ventas (KPIs y métricas temporales).
     */
    private SalesInfoDTO salesInfo;

    /**
     * Estadísticas relacionadas con productos (vendidos y no vendidos).
     */
    private ProductStatisticsDTO productStatistics;
}
