package com.marcoscornejos.sales_management_system.exception;

/**
 * Excepción lanzada cuando no se puede encontrar la configuración del sistema.
 *
 * <p>
 * Esto no debería ocurrir en un sistema correctamente inicializado,
 * ya que la configuración debe existir como un único registro global.
 * </p>
 *
 * <pre>
 * {
 *   "code": "SYSTEM_CONFIGURATION_NOT_FOUND",
 *   "message": "Mensaje legible para el usuario",
 *   "field": null
 * }
 * </pre>
 */
public class SystemConfigurationNotFoundException extends SystemConfigurationException {

    private static final String CODE = "SYSTEM_CONFIGURATION_NOT_FOUND";

    /**
     * Crea una excepción cuando no se encuentra la configuración del sistema.
     *
     * @param message mensaje de error legible para el usuario
     */
    public SystemConfigurationNotFoundException(String message) {
        super(CODE, message, null);
    }
}
