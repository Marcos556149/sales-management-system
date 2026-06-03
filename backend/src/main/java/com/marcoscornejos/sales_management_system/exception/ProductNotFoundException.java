package com.marcoscornejos.sales_management_system.exception;

/**
 * Excepción lanzada cuando no se puede encontrar un producto.
 *
 * <p>
 * Se utiliza normalmente cuando una búsqueda de producto por código
 * no devuelve ningún resultado.
 * </p>
 *
 * <p>
 * Esta excepción utiliza el formato de error estandarizado:
 * </p>
 *
 * <pre>
 * {
 *   "code": "PRODUCT_NOT_FOUND",
 *   "message": "Mensaje legible por el usuario",
 *   "field": null
 * }
 * </pre>
 */
public class ProductNotFoundException extends ProductException {

    private static final String CODE = "PRODUCT_NOT_FOUND";

    /**
     * Crea una excepción cuando no se encuentra un producto.
     *
     * @param message mensaje de error legible por el usuario
     */
    public ProductNotFoundException(String message) {
        super(CODE, message, null);
    }
}
