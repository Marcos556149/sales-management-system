package com.marcoscornejos.sales_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * Contiene KPIs globales de ventas y analíticas basadas en tiempo
 * para un rango de filtros seleccionado.
 *
 * <p>
 * Este DTO representa métricas de negocio agregadas derivadas de los datos de ventas,
 * incluyendo ingresos totales, número de ventas, valor promedio del ticket,
 * horas de mayor rendimiento y datos en serie temporal utilizados para visualización
 * en gráficos.
 * </p>
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SalesInfoDTO {

    /**
     * Ingresos totales generados por todas las ventas dentro del rango seleccionado.
     */
    private BigDecimal totalRevenue;

    /**
     * Número total de transacciones de ventas dentro del rango seleccionado.
     */
    private Long totalSales;

    /**
     * Valor promedio del ticket calculado como totalRevenue dividido por totalSales.
     */
    private BigDecimal averageTicket;

    /**
     * Hora del día con mayor generación de ingresos.
     */
    private String highestRevenueHour;

    /**
     * Hora del día con mayor número de transacciones de ventas.
     */
    private String highestSalesHour;

    /**
     * Datos en serie temporal que representan la evolución de ingresos en el tiempo.
     * Cada punto contiene una etiqueta temporal y el valor correspondiente de ingresos.
     */
    private List<TimeSeriesPointDTO> revenueOverTime;

    /**
     * Datos en serie temporal que representan la evolución del número de ventas en el tiempo.
     * Cada punto contiene una etiqueta temporal y el conteo de ventas correspondiente.
     */
    private List<TimeSeriesPointDTO> salesOverTime;
}
