package com.marcoscornejos.sales_management_system.exception;

/**
 * Excepción lanzada cuando no se puede encontrar una venta.
 *
 * <p>
 * Generalmente se utiliza cuando una búsqueda de venta por identificador
 * o filtros no devuelve ningún resultado.
 * </p>
 *
 * <p>
 * Esta excepción utiliza el formato estandarizado de errores:
 * </p>
 *
 * <pre>
 * {
 *   "code": "SALE_NOT_FOUND",
 *   "message": "Mensaje legible para el usuario",
 *   "field": null
 * }
 * </pre>
 */
public class SaleNotFoundException extends SaleException {

    private static final String CODE = "SALE_NOT_FOUND";

    /**
     * Crea una excepción cuando una venta no es encontrada.
     *
     * @param message mensaje de error legible para el usuario
     */
    public SaleNotFoundException(String message) {
        super(CODE, message, null);
    }
}