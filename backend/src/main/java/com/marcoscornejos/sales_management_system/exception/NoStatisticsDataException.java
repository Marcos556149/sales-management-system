package com.marcoscornejos.sales_management_system.exception;

/**
 * Excepción lanzada cuando no existen datos estadísticos
 * para los filtros seleccionados.
 *
 * <p>
 * Esta excepción se produce cuando una consulta de estadísticas
 * o una solicitud de generación de reporte no encuentra
 * registros de ventas que coincidan con los criterios.
 * </p>
 *
 * <p>
 * Esta excepción utiliza el formato de error estandarizado:
 * </p>
 *
 * <pre>
 * {
 *   "code": "NO_STATISTICS_DATA",
 *   "message": "Mensaje legible para el usuario",
 *   "field": null
 * }
 * </pre>
 */
public class NoStatisticsDataException
        extends StatisticsException {

    private static final String CODE =
            "NO_STATISTICS_DATA";

    /**
     * Crea una excepción cuando no hay datos disponibles.
     *
     * @param message mensaje de error legible para el usuario
     */
    public NoStatisticsDataException(
            String message
    ) {
        super(CODE, message, null);
    }

    /**
     * Crea una excepción cuando no hay datos disponibles
     * con un campo relacionado opcional.
     *
     * @param message mensaje de error legible para el usuario
     * @param field nombre del campo relacionado (puede ser null)
     */
    public NoStatisticsDataException(
            String message,
            String field
    ) {
        super(CODE, message, field);
    }
}