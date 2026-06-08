package com.marcoscornejos.sales_management_system.exception;

/**
 * Excepción lanzada cuando los filtros de estadísticas proporcionados son inválidos.
 *
 * <p>
 * Esto incluye rangos de fechas inválidos, filtros incompletos
 * o errores de validación relacionados con consultas de estadísticas.
 * </p>
 *
 * <p>
 * Esta excepción utiliza el formato de error estandarizado:
 * </p>
 *
 * <pre>
 * {
 *   "code": "INVALID_STATISTICS_FILTER",
 *   "message": "Mensaje legible para el usuario",
 *   "field": "nombreDelCampoOpcional"
 * }
 * </pre>
 */
public class InvalidStatisticsFilterException
        extends StatisticsException {

    private static final String CODE =
            "INVALID_STATISTICS_FILTER";

    /**
     * Crea una excepción de validación sin un campo específico.
     *
     * @param message mensaje de error legible para el usuario
     */
    public InvalidStatisticsFilterException(String message) {
        super(CODE, message, null);
    }

    /**
     * Crea una excepción para filtros de estadísticas inválidos.
     *
     * @param message mensaje de error legible para el usuario
     * @param field nombre del campo relacionado (puede ser null)
     */
    public InvalidStatisticsFilterException(
            String message,
            String field
    ) {
        super(CODE, message, field);
    }
}