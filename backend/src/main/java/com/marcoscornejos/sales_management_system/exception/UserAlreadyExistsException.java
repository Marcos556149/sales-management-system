package com.marcoscornejos.sales_management_system.exception;

/**
 * Excepción lanzada cuando se intenta utilizar un nombre de usuario
 * que ya se encuentra registrado en el sistema.
 *
 * <p>Se utiliza cuando una operación requiere que el nombre de usuario
 * sea único y ya existe otro usuario registrado con el mismo valor.</p>
 *
 * <p>Esta excepción utiliza el siguiente código de error:</p>
 *
 * <pre>
 * {
 *   "code": "USER_ALREADY_EXISTS",
 *   "message": "Mensaje legible por el usuario",
 *   "field": null
 * }
 * </pre>
 */
public class UserAlreadyExistsException extends UserException {

    private static final String CODE = "USER_ALREADY_EXISTS";

    /**
     * Crea una excepción indicando que el nombre de usuario
     * ya se encuentra registrado.
     *
     * @param message mensaje descriptivo del error
     */
    public UserAlreadyExistsException(String message) {
        super(CODE, message, null);
    }
}