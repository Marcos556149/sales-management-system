package com.marcoscornejos.sales_management_system.exception;

/**
 * Excepción lanzada cuando los datos de una venta son inválidos.
 *
 * <p>
 * Esto incluye valores de entrada inválidos, validaciones fallidas
 * o violaciones de reglas de negocio relacionadas con operaciones de venta.
 * </p>
 *
 * <p>
 * Esta excepción utiliza el formato estandarizado de errores:
 * </p>
 *
 * <pre>
 * {
 *   "code": "INVALID_SALE_DATA",
 *   "message": "Mensaje legible para el usuario",
 *   "field": "nombreCampoOpcional"
 * }
 * </pre>
 */
public class InvalidSaleDataException extends SaleException {

    private static final String CODE = "INVALID_SALE_DATA";

    /**
     * Crea una excepción de validación sin un campo específico asociado.
     *
     * @param message mensaje de error legible para el usuario
     */
    public InvalidSaleDataException(String message) {
        super(CODE, message, null);
    }

    /**
     * Crea una excepción para datos de venta inválidos.
     *
     * @param message mensaje de error legible para el usuario
     * @param field nombre del campo relacionado (puede ser null)
     */
    public InvalidSaleDataException(String message, String field) {
        super(CODE, message, field);
    }
}