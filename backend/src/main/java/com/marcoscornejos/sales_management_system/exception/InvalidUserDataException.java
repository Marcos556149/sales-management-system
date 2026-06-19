package com.marcoscornejos.sales_management_system.exception;

/**
 * Excepción lanzada cuando los datos de entrada de un usuario son inválidos
 * o incumplen las reglas de negocio del sistema.
 *
 * <p>
 * Ejemplos de casos:
 * <ul>
 *   <li>Intentar cambiar el estado a un valor no permitido</li>
 *   <li>Operaciones no válidas según el rol del usuario</li>
 *   <li>Intentar modificar un usuario ya en el estado solicitado</li>
 * </ul>
 * </p>
 *
 * <p>
 * Esta excepción utiliza el código de error:
 * <b>INVALID_USER_DATA</b>
 * </p>
 */
public class InvalidUserDataException extends UserException {

    private static final String CODE = "INVALID_USER_DATA";

    /**
     * Crea una excepción de validación para un campo de usuario.
     *
     * @param message mensaje de error legible por el usuario
     * @param field campo que provocó el error de validación
     */
    public InvalidUserDataException(String message, String field) {
        super(CODE, message, field);
    }

    /**
     * Crea una excepción de validación sin asociarla a un campo específico.
     *
     * @param message mensaje de error legible por el usuario
     */
    public InvalidUserDataException(String message) {
        super(CODE, message, null);
    }
}