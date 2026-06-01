package com.marcoscornejos.sales_management_system.exception;

/**
 * Exception thrown when the system fails
 * to generate the statistics PDF report.
 *
 * <p>
 * This may occur due to:
 * <ul>
 *     <li>PDF document creation errors</li>
 *     <li>Invalid PDF content generation</li>
 *     <li>Chart rendering failures</li>
 *     <li>I/O related PDF processing errors</li>
 * </ul>
 * </p>
 *
 * <p>
 * This exception uses the standardized error format:
 * </p>
 *
 * <pre>
 * {
 *   "code": "PDF_GENERATION_ERROR",
 *   "message": "Human readable message",
 *   "field": null
 * }
 * </pre>
 */
public class PdfGenerationException
        extends StatisticsException {

    private static final String CODE =
            "PDF_GENERATION_ERROR";

    /**
     * Creates a PDF generation exception.
     *
     * @param message human-readable error message
     */
    public PdfGenerationException(String message) {
        super(CODE, message, null);
    }
}
