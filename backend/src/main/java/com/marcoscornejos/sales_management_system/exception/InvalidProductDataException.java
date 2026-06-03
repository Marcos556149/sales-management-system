package com.marcoscornejos.sales_management_system.exception;

/**
 * Excepción lanzada cuando los datos de entrada de un producto son inválidos
 * o incumplen las reglas de validación de negocio.
 *
 * <p>
 * Ejemplos de casos:
 * <ul>
 *   <li>Parámetros de paginación inválidos</li>
 *   <li>Precios o stock negativos</li>
 *   <li>Valores de campos inválidos</li>
 * </ul>
 * </p>
 */
public class InvalidProductDataException extends ProductException {

    private static final String CODE = "INVALID_PRODUCT_DATA";

    /**
     * Crea una excepción de validación para un campo de producto.
     *
     * @param message mensaje de error legible por el usuario
     * @param field campo que provocó el error de validación
     */
    public InvalidProductDataException(String message, String field) {
        super(CODE, message, field);
    }

    /**
     * Crea una excepción de validación sin asociarla a un campo específico.
     *
     * @param message mensaje de error legible por el usuario
     */
    public InvalidProductDataException(String message) {
        super(CODE, message, null);
    }
}