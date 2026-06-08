package com.marcoscornejos.sales_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * DTO que contiene estadísticas de productos con mejor rendimiento
 * para gráficos de análisis.
 *
 * <p>
 * Este DTO proporciona:
 * <ul>
 *   <li>Top 10 productos por cantidad vendida</li>
 *   <li>Top 10 productos por ingresos generados</li>
 * </ul>
 * </p>
 *
 * <p>
 * Todos los rankings se calculan utilizando únicamente las ventas
 * que coinciden con los filtros seleccionados.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TopProductsResponseDTO {

    /**
     * Top 10 productos ordenados por cantidad vendida.
     */
    private List<TopProductsByQuantityDTO> topProductsByQuantity;

    /**
     * Top 10 productos ordenados por ingresos generados.
     */
    private List<TopProductsByRevenueDTO> topProductsByRevenue;
}