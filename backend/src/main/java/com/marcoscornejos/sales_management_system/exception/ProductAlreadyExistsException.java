package com.marcoscornejos.sales_management_system.exception;

/**
 * Excepción lanzada al intentar crear un producto
 * que ya existe.
 *
 * <p>
 * Se utiliza comúnmente cuando ya existe un producto con el mismo
 * identificador único (por ejemplo, el código).
 * </p>
 *
 * <p>
 * Esta excepción utiliza el formato de error estandarizado:
 * </p>
 *
 * <pre>
 * {
 *   "code": "PRODUCT_ALREADY_EXISTS",
 *   "message": "Mensaje legible por el usuario",
 *   "field": null
 * }
 * </pre>
 */
public class ProductAlreadyExistsException extends ProductException {

    private static final String CODE = "PRODUCT_ALREADY_EXISTS";

    /**
     * Crea una excepción cuando un producto ya existe.
     *
     * @param message mensaje de error legible por el usuario
     */
    public ProductAlreadyExistsException(String message) {
        super(CODE, message, null);
    }
}
