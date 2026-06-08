package com.marcoscornejos.sales_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Contiene información estadística agregada de productos basada en el rendimiento de ventas
 * dentro de un rango de filtros seleccionado.
 *
 * <p>
 * Este DTO agrupa diferentes vistas analíticas de productos, incluyendo productos con mejor rendimiento,
 * rankings dinámicos y productos sin actividad de ventas. Se utiliza como parte del dashboard
 * de estadísticas de ventas para proporcionar información sobre el comportamiento de los productos.
 * </p>
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductStatisticsDTO {

    /**
     * Top 10 productos por cantidad vendida.
     */
    private List<SoldProductDTO> topProductsByQuantity;

    /**
     * Top 10 productos por ingresos generados.
     */
    private List<SoldProductDTO> topProductsByRevenue;

    /**
     * Ranking completo de productos basado en la métrica y orden seleccionados.
     */
    private List<SoldProductDTO> productRanking;

    /**
     * Productos sin ventas en el rango seleccionado.
     */
    private List<UnsoldProductDTO> unsoldProducts;
}
