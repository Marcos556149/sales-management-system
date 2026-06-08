package com.marcoscornejos.sales_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Representa un punto de datos en una serie temporal para gráficos.
 *
 * <p>
 * Este DTO se utiliza para construir visualizaciones de series temporales como
 * ingresos a lo largo del tiempo o cantidad de ventas a lo largo del tiempo.
 * El label normalmente representa una unidad de tiempo (hora, día, mes o año
 * dependiendo de la granularidad seleccionada), y el value representa la métrica
 * agregada para ese período.
 * </p>
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TimeSeriesPointDTO {

    /**
     * Etiqueta que representa la unidad de tiempo del punto de datos
     * (por ejemplo: hora, día, mes o año según la granularidad).
     */
    private String label;

    /**
     * Valor agregado para la unidad de tiempo correspondiente
     * (por ejemplo: ingresos o cantidad de ventas).
     */
    private BigDecimal value;
}
