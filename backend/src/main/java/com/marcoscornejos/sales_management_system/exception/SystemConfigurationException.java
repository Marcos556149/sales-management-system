package com.marcoscornejos.sales_management_system.exception;

import lombok.Getter;

/**
 * Excepción base para todos los errores relacionados con la configuración del sistema.
 *
 * <p>
 * Proporciona una estructura estandarizada para el manejo de errores de configuración
 * en toda la aplicación.
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
public class SystemConfigurationException extends RuntimeException {

    private final String code;
    private final String field;

    /**
     * Crea una nueva SystemConfigurationException.
     *
     * @param code código de error legible por máquina
     * @param message mensaje de error legible para el usuario
     * @param field campo opcional relacionado con el error (puede ser null)
     */
    public SystemConfigurationException(String code, String message, String field) {
        super(message);
        this.code = code;
        this.field = field;
    }
}
