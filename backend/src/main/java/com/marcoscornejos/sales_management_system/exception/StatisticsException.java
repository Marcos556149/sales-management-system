package com.marcoscornejos.sales_management_system.exception;

import lombok.Getter;

/**
 * Excepción base para todos los errores de negocio y validación
 * relacionados con estadísticas.
 *
 * <p>
 * Esta excepción define una estructura estándar utilizada en todo el dominio de estadísticas,
 * permitiendo un manejo consistente de errores en el GlobalExceptionHandler.
 * </p>
 *
 * <p>
 * Cada excepción incluye:
 * <ul>
 *   <li><b>code</b>: identificador de error legible por máquina</li>
 *   <li><b>message</b>: descripción legible del error para el usuario</li>
 *   <li><b>field</b>: campo opcional relacionado con errores de validación</li>
 * </ul>
 * </p>
 */
@Getter
public class StatisticsException extends RuntimeException {

    private final String code;
    private final String field;

    /**
     * Crea una nueva StatisticsException.
     *
     * @param code código de error legible por máquina
     * @param message mensaje de error legible para el usuario
     * @param field campo opcional relacionado con el error (puede ser null)
     */
    public StatisticsException(String code, String message, String field) {
        super(message);
        this.code = code;
        this.field = field;
    }
}