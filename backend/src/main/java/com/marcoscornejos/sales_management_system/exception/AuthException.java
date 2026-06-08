package com.marcoscornejos.sales_management_system.exception;

import lombok.Getter;

/**
 * Excepción para errores relacionados con autenticación.
 *
 * <p>
 * Se utiliza cuando se violan reglas de autenticación o autorización,
 * como credenciales inválidas o intentos de acceso no autorizados.
 * </p>
 */
@Getter
public class AuthException extends RuntimeException {

    private final String code;
    private final String field;

    /**
     * Crea una nueva AuthException.
     *
     * @param code código de error legible por máquina
     * @param message mensaje de error legible para el usuario
     * @param field campo opcional relacionado con el error (puede ser null)
     */
    public AuthException(String code, String message, String field) {
        super(message);
        this.code = code;
        this.field = field;
    }
}