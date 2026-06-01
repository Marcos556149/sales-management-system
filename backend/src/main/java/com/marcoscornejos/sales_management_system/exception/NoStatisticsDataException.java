package com.marcoscornejos.sales_management_system.exception;

/**
 * Exception thrown when no statistical data exists
 * for the selected filters.
 *
 * <p>
 * This exception is raised when a statistics query
 * or report generation request does not match any
 * sales records.
 * </p>
 *
 * <p>
 * This exception uses the standardized error format:
 * </p>
 *
 * <pre>
 * {
 *   "code": "NO_STATISTICS_DATA",
 *   "message": "Human readable message",
 *   "field": null
 * }
 * </pre>
 */
public class NoStatisticsDataException
        extends StatisticsException {

    private static final String CODE =
            "NO_STATISTICS_DATA";

    /**
     * Creates a no-data exception.
     *
     * @param message human-readable error message
     */
    public NoStatisticsDataException(
            String message
    ) {
        super(CODE, message, null);
    }

    /**
     * Creates a no-data exception with
     * an optional related field.
     *
     * @param message human-readable error message
     * @param field related field name (can be null)
     */
    public NoStatisticsDataException(
            String message,
            String field
    ) {
        super(CODE, message, field);
    }
}