/**
 * Enumeración que define la granularidad de agrupación
 * utilizada para estadísticas de ventas y gráficos basados en el tiempo.
 *
 * <p>
 * La granularidad determina cómo se agrupan los datos estadísticos
 * al generar análisis como ingresos a lo largo del tiempo
 * y cantidad de ventas a lo largo del tiempo.
 * </p>
 *
 * <p>
 * El sistema selecciona automáticamente la granularidad más apropiada
 * según el rango de fechas seleccionado con el fin de:
 * <ul>
 *   <li>Mejorar la legibilidad de los gráficos</li>
 *   <li>Evitar una cantidad excesiva de puntos de datos en rangos amplios</li>
 *   <li>Mantener una agregación visual consistente</li>
 * </ul>
 * </p>
 *
 * <p>
 * Reglas de granularidad:
 * <ul>
 *   <li>{@code HOUR}: utilizada para rangos de un solo día</li>
 *   <li>{@code DAY}: utilizada para rangos cortos (hasta 31 días)</li>
 *   <li>{@code MONTH}: utilizada para rangos medios (hasta 365 días)</li>
 *   <li>{@code YEAR}: utilizada para rangos largos (más de 365 días)</li>
 * </ul>
 * </p>
 */
package com.marcoscornejos.sales_management_system.model;

public enum StatisticsGranularity {

    /**
     * Agrupa las estadísticas por hora.
     */
    HOUR,

    /**
     * Agrupa las estadísticas por día.
     */
    DAY,

    /**
     * Agrupa las estadísticas por mes.
     */
    MONTH,

    /**
     * Agrupa las estadísticas por año.
     */
    YEAR
}
