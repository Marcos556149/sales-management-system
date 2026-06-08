package com.marcoscornejos.sales_management_system.exception;

/**
 * Excepción lanzada cuando no se puede encontrar un usuario.
 *
 * <p>
 * Se utiliza normalmente cuando una búsqueda de usuario por ID, nombre de usuario
 * u otro criterio no devuelve ningún resultado.
 * </p>
 *
 * <p>
 * Esta excepción utiliza el formato de error estandarizado:
 * </p>
 *
 * <pre>
 * {
 *   "code": "USER_NOT_FOUND",
 *   "message": "Mensaje legible para el usuario",
 *   "field": null
 * }
 * </pre>
 */
public class UserNotFoundException extends UserException {

    private static final String CODE = "USER_NOT_FOUND";

    /**
     * Crea una excepción cuando no se encuentra un usuario.
     *
     * @param message mensaje de error legible para el usuario
     */
    public UserNotFoundException(String message) {
        super(CODE, message, null);
    }
}