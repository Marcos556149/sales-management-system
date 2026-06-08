package com.marcoscornejos.sales_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO que representa estadísticas de horas pico de ventas
 * calculadas en base a los filtros seleccionados.
 *
 * <p>
 * Este DTO contiene:
 * <ul>
 *   <li>La hora con mayor generación de ingresos</li>
 *   <li>La hora con mayor número de ventas</li>
 * </ul>
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PeakHoursResponseDTO {

    /**
     * Hora con mayor generación de ingresos.
     *
     * <p>
     * Representa el intervalo horario que acumuló
     * el mayor ingreso total dentro de los filtros seleccionados.
     * </p>
     *
     * <p>
     * Puede ser {@code null} si no existen ventas que coincidan.
     * </p>
     */
    private String highestRevenueHour;

    /**
     * Hora con mayor número de ventas.
     *
     * <p>
     * Representa el intervalo horario con la mayor
     * cantidad de ventas dentro de los filtros seleccionados.
     * </p>
     *
     * <p>
     * Puede ser {@code null} si no existen ventas que coincidan.
     * </p>
     */
    private String highestSalesHour;
}
