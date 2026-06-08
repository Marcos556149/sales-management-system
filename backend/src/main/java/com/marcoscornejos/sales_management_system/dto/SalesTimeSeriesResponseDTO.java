package com.marcoscornejos.sales_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * DTO que contiene datos estadísticos en serie temporal
 * para gráficos de análisis de ventas.
 *
 * <p>
 * Este DTO proporciona:
 * <ul>
 *   <li>Evolución de ingresos totales a lo largo del tiempo</li>
 *   <li>Evolución del número total de ventas a lo largo del tiempo</li>
 * </ul>
 * </p>
 *
 * <p>
 * La granularidad de la agregación de datos se determina automáticamente
 * en función del rango de fechas seleccionado.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SalesTimeSeriesResponseDTO {

    /**
     * Serie temporal que representa la evolución de ingresos en el tiempo.
     */
    private List<TimeSeriesPointDTO> revenueOverTime;

    /**
     * Serie temporal que representa la evolución del número de ventas en el tiempo.
     */
    private List<TimeSeriesPointDTO> salesOverTime;
}
