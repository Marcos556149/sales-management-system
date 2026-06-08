package com.marcoscornejos.sales_management_system.projection;

import java.math.BigDecimal;

/**
 * Proyección utilizada para agregaciones estadísticas basadas en series temporales.
 *
 * <p>
 * Representa un único punto agregado de un gráfico,
 * incluyendo una etiqueta temporal y su valor asociado.
 * </p>
 */
public interface TimeSeriesProjection {

    /**
     * Etiqueta temporal que representa el período de agregación
     * (hora, día, mes o año).
     *
     * @return etiqueta del gráfico
     */
    String getLabel();

    /**
     * Valor agregado correspondiente al período de tiempo.
     *
     * @return valor de la métrica agregada
     */
    BigDecimal getValue();
}