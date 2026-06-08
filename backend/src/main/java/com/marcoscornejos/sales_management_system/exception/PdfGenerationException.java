package com.marcoscornejos.sales_management_system.exception;

/**
 * Excepción lanzada cuando el sistema falla al generar
 * el informe PDF de estadísticas.
 *
 * <p>
 * Esto puede ocurrir debido a:
 * <ul>
 *     <li>Errores en la creación del documento PDF</li>
 *     <li>Generación de contenido PDF inválido</li>
 *     <li>Fallos en el renderizado de gráficos</li>
 *     <li>Errores de entrada/salida durante el procesamiento del PDF</li>
 * </ul>
 * </p>
 *
 * <p>
 * Esta excepción utiliza el formato de error estandarizado:
 * </p>
 *
 * <pre>
 * {
 *   "code": "PDF_GENERATION_ERROR",
 *   "message": "Mensaje legible para el usuario",
 *   "field": null
 * }
 * </pre>
 */
public class PdfGenerationException
        extends StatisticsException {

    private static final String CODE =
            "PDF_GENERATION_ERROR";

    /**
     * Crea una excepción de generación de PDF.
     *
     * @param message mensaje de error legible para el usuario
     */
    public PdfGenerationException(String message) {
        super(CODE, message, null);
    }
}
