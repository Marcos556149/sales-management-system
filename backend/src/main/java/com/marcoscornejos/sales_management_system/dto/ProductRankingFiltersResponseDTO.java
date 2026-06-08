package com.marcoscornejos.sales_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * DTO de respuesta que contiene las opciones de filtros disponibles
 * para las estadísticas de ranking de productos.
 *
 * <p>
 * Este DTO proporciona datos de configuración basados en enums
 * utilizados por el frontend para poblar dinámicamente
 * los selectores de filtros de ranking.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRankingFiltersResponseDTO {

    /**
     * Opciones disponibles de métricas de ranking de productos.
     *
     * <p>
     * Define cómo se ordenan los productos:
     * por cantidad vendida o por ingresos generados.
     * </p>
     */
    private List<EnumDTO> metricOptions;

    /**
     * Opciones disponibles de ordenamiento para el ranking de productos.
     *
     * <p>
     * Define si los productos se ordenan de:
     * mayor a menor, o de menor a mayor.
     * </p>
     */
    private List<EnumDTO> orderOptions;
}
