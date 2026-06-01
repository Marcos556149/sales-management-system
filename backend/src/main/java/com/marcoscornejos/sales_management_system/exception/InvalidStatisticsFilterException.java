package com.marcoscornejos.sales_management_system.exception;

/**
 * Exception thrown when provided statistics filters are invalid.
 *
 * <p>
 * This includes invalid date ranges, incomplete filters,
 * or failed validations related to statistics queries.
 * </p>
 *
 * <p>
 * This exception uses the standardized error format:
 * </p>
 *
 * <pre>
 * {
 *   "code": "INVALID_STATISTICS_FILTER",
 *   "message": "Human readable message",
 *   "field": "optionalFieldName"
 * }
 * </pre>
 */
public class InvalidStatisticsFilterException
        extends StatisticsException {

    private static final String CODE =
            "INVALID_STATISTICS_FILTER";

    /**
     * Creates a validation exception without a specific field.
     *
     * @param message human-readable error message
     */
    public InvalidStatisticsFilterException(String message) {
        super(CODE, message, null);
    }

    /**
     * Creates an exception for invalid statistics filters.
     *
     * @param message human-readable error message
     * @param field related field name (can be null)
     */
    public InvalidStatisticsFilterException(
            String message,
            String field
    ) {
        super(CODE, message, field);
    }
}