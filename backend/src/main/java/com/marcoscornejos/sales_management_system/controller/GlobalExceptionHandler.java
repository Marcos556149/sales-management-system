package com.marcoscornejos.sales_management_system.controller;

import com.marcoscornejos.sales_management_system.exception.*;
import jakarta.persistence.PersistenceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para todos los controladores del sistema.
 *
 * <p>
 * Esta clase intercepta las excepciones lanzadas por los controladores y servicios,
 * proporcionando respuestas HTTP consistentes para el frontend.
 * </p>
 *
 * <p>
 * Maneja tanto excepciones relacionadas con la autenticación como
 * errores de validación provenientes de DTOs de solicitud anotados con
 * {@link jakarta.validation.Valid}.
 * </p>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja las excepciones relacionadas con autenticación.
     *
     * <p>
     * Devuelve una respuesta de error estandarizada compatible con el manejador de errores del frontend,
     * incluyendo código de error, mensaje y referencia opcional al campo afectado.
     * </p>
     *
     * @param ex excepción de autenticación
     * @return respuesta de error estandarizada con estado HTTP 401
     */
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<Map<String, Object>> handleAuthException(AuthException ex) {

        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("code", ex.getCode());
        errorBody.put("message", ex.getMessage());
        errorBody.put("field", ex.getField());

        Map<String, Object> response = new HashMap<>();
        response.put("error", errorBody);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * Maneja los errores de validación de los DTOs de solicitud anotados con
     * {@link jakarta.validation.Valid}.
     *
     * <p>
     * Esta excepción se produce cuando fallan las restricciones definidas mediante
     * Bean Validation. Para simplificar el manejo en el frontend, solo se devuelve
     * el primer error de validación detectado.
     * </p>
     *
     * <p>
     * Devuelve una respuesta de error estandarizada:
     * </p>
     *
     * <pre>
     * {
     *   "error": {
     *     "code": "VALIDATION_ERROR",
     *     "message": "El código del producto es obligatorio",
     *     "field": "productCode"
     *   }
     * }
     * </pre>
     *
     * @param ex excepción de validación
     * @return ResponseEntity con HTTP 400 Bad Request y un cuerpo de error estructurado
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {

        FieldError fieldError = ex.getBindingResult().getFieldErrors().get(0);

        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("code", "VALIDATION_ERROR");
        errorBody.put("field", fieldError.getField());
        errorBody.put("message", fieldError.getDefaultMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("error", errorBody);

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Maneja todas las excepciones inesperadas que no son gestionadas
     * explícitamente por manejadores de excepciones específicos.
     *
     * <p>
     * Este es un mecanismo de respaldo que garantiza que ninguna excepción
     * devuelva respuestas no estructuradas al cliente.
     * </p>
     *
     * <p>
     * La respuesta sigue el formato de error estandarizado:
     * </p>
     *
     * <pre>
     * {
     *   "error": {
     *     "code": "INTERNAL_SERVER_ERROR",
     *     "message": "Ocurrió un error inesperado",
     *     "field": null
     *   }
     * }
     * </pre>
     *
     * <p>
     * Los detalles completos de la excepción se registran internamente
     * con fines de depuración.
     * </p>
     *
     * @param ex excepción inesperada
     * @return respuesta 500 Internal Server Error con formato de error estandarizado
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {

        // Log full error details for debugging
        log.error("Ocurrió un error inesperado", ex);

        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("code", "INTERNAL_SERVER_ERROR");
        errorBody.put("message", "Ocurrió un error inesperado");
        errorBody.put("field", null);

        Map<String, Object> response = new HashMap<>();
        response.put("error", errorBody);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * Maneja los errores que se producen cuando un parámetro de la solicitud
     * no puede convertirse al tipo esperado.
     *
     * <p>
     * Esto suele ocurrir cuando el cliente envía un valor inválido
     * (por ejemplo, un valor incorrecto para un enum, una cadena de texto
     * en lugar de un número, etc.).
     * </p>
     *
     * <p>
     * Devuelve una respuesta de error estandarizada utilizando el formato
     * global de errores:
     * </p>
     *
     * <pre>
     * {
     *   "error": {
     *     "code": "INVALID_PARAMETER_TYPE",
     *     "message": "Valor inválido 'ACTVEE' para el parámetro 'statusFilter'",
     *     "field": "statusFilter"
     *   }
     * }
     * </pre>
     *
     * @param ex excepción lanzada cuando falla la conversión de tipos
     * @return respuesta 400 Bad Request con un formato de error estructurado
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {

        Map<String, Object> error = new HashMap<>();

        String field = ex.getName();
        Object value = ex.getValue();

        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("code", "INVALID_PARAMETER_TYPE");
        errorBody.put("field", field);
        errorBody.put("message",
                String.format("Valor inválido '%s' para el parámetro '%s'", value, field)
        );

        error.put("error", errorBody);

        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Maneja las excepciones relacionadas con productos que se producen cuando
     * fallan reglas de negocio o validaciones dentro del dominio Product.
     *
     * <p>
     * Este manejador centraliza todas las excepciones que extienden
     * {@code ProductException}, garantizando un formato de respuesta de error
     * consistente en toda la aplicación.
     * </p>
     *
     * <p>
     * La respuesta sigue la estructura estandarizada:
     * </p>
     *
     * <pre>
     * {
     *   "error": {
     *     "code": "ERROR_CODE",
     *     "message": "Mensaje legible para el usuario",
     *     "field": "Campo opcional relacionado con el error"
     *   }
     * }
     * </pre>
     *
     * <p>
     * El frontend debe utilizar:
     * <ul>
     *   <li><b>code</b>: para determinar el tipo de error y el comportamiento de la interfaz</li>
     *   <li><b>message</b>: para mostrar o registrar información legible para el usuario</li>
     *   <li><b>field</b>: para asociar errores de validación con campos específicos</li>
     * </ul>
     * </p>
     *
     * @param ex excepción relacionada con productos que contiene los detalles del error
     * @return respuesta 400 Bad Request con un cuerpo de error estandarizado
     */
    @ExceptionHandler(ProductException.class)
    public ResponseEntity<Map<String, Object>> handleProductException(ProductException ex) {

        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("code", ex.getCode());
        errorBody.put("message", ex.getMessage());
        errorBody.put("field", ex.getField());

        Map<String, Object> response = new HashMap<>();
        response.put("error", errorBody);

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Maneja las excepciones que se producen cuando el cuerpo de la solicitud
     * contiene datos inválidos o no puede ser interpretado correctamente
     * (por ejemplo, formato JSON incorrecto, valores de enum inválidos
     * o incompatibilidades de tipos en la carga útil de la solicitud).
     *
     * <p>
     * Devuelve una respuesta 400 Bad Request estandarizada con un formato
     * de error estructurado.
     * </p>
     *
     * @param ex excepción que contiene los errores de análisis o deserialización
     * @return respuesta 400 Bad Request con detalles de error estructurados
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {

        Map<String, Object> errorBody = new HashMap<>();

        errorBody.put("code", "INVALID_REQUEST_BODY");
        errorBody.put("field", null);
        errorBody.put("message", "Cuerpo de la solicitud inválido. Verifique los valores proporcionados");

        Map<String, Object> response = new HashMap<>();
        response.put("error", errorBody);

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Maneja las excepciones relacionadas con ventas que se producen cuando
     * fallan reglas de negocio o validaciones dentro del dominio de ventas.
     *
     * <p>
     * Este manejador centraliza todas las excepciones que extienden de {@code SaleException},
     * garantizando un formato de respuesta de error consistente en toda la aplicación.
     * </p>
     *
     * <p>
     * La respuesta sigue la siguiente estructura estandarizada:
     * </p>
     *
     * <pre>
     * {
     *   "error": {
     *     "code": "ERROR_CODE",
     *     "message": "Mensaje legible para el usuario",
     *     "field": "Campo opcional relacionado con el error"
     *   }
     * }
     * </pre>
     *
     * <p>
     * El frontend debe utilizar:
     * <ul>
     *   <li><b>code</b>: para determinar el tipo de error y el comportamiento de la interfaz</li>
     *   <li><b>message</b>: para mostrar o registrar información legible para el usuario</li>
     *   <li><b>field</b>: para asociar errores de validación con campos específicos</li>
     * </ul>
     * </p>
     *
     * @param ex excepción relacionada con ventas que contiene los detalles del error
     * @return una respuesta HTTP 400 (Bad Request) con un cuerpo de error estandarizado
     */
    @ExceptionHandler(SaleException.class)
    public ResponseEntity<Map<String, Object>> handleSaleException(SaleException ex) {

        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("code", ex.getCode());
        errorBody.put("message", ex.getMessage());
        errorBody.put("field", ex.getField());

        Map<String, Object> response = new HashMap<>();
        response.put("error", errorBody);

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Maneja las excepciones producidas por la ausencia de parámetros requeridos
     * en la solicitud.
     *
     * <p>
     * Se produce cuando un parámetro de consulta obligatorio no es proporcionado
     * en la solicitud. Devuelve una respuesta 400 Bad Request estandarizada
     * indicando cuál es el parámetro faltante.
     * </p>
     *
     * @param ex excepción que contiene los detalles del parámetro faltante
     * @return respuesta 400 Bad Request con detalles de error estructurados
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> handleMissingParams(
            MissingServletRequestParameterException ex
    ) {

        Map<String, Object> errorBody = new HashMap<>();

        errorBody.put("code", "MISSING_REQUEST_PARAMETER");
        errorBody.put("field", ex.getParameterName());
        errorBody.put(
                "message",
                "Falta el parámetro requerido: " + ex.getParameterName()
        );

        Map<String, Object> response = new HashMap<>();
        response.put("error", errorBody);

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Maneja las excepciones relacionadas con usuarios lanzadas cuando las reglas de negocio
     * o validaciones fallan dentro del dominio de Usuario.
     *
     * <p>
     * Este manejador centraliza todas las excepciones que extienden de {@code UserException},
     * asegurando un formato de respuesta de error consistente en toda la aplicación.
     * </p>
     *
     * <p>
     * La respuesta sigue la estructura estandarizada:
     * </p>
     *
     * <pre>
     * {
     *   "error": {
     *     "code": "ERROR_CODE",
     *     "message": "Mensaje legible para el usuario",
     *     "field": "Campo opcional relacionado con el error"
     *   }
     * }
     * </pre>
     *
     * <p>
     * El frontend debe utilizar:
     * <ul>
     *   <li><b>code</b>: para determinar el tipo de error y el comportamiento de la UI</li>
     *   <li><b>message</b>: para mostrar o registrar información legible para el usuario</li>
     *   <li><b>field</b>: para asociar errores de validación con campos específicos</li>
     * </ul>
     * </p>
     *
     * @param ex excepción relacionada con usuarios que contiene los detalles del error
     * @return respuesta 400 Bad Request con un cuerpo de error estandarizado
     */
    @ExceptionHandler(UserException.class)
    public ResponseEntity<Map<String, Object>> handleUserException(UserException ex) {

        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("code", ex.getCode());
        errorBody.put("message", ex.getMessage());
        errorBody.put("field", ex.getField());

        Map<String, Object> response = new HashMap<>();
        response.put("error", errorBody);

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Maneja las excepciones generadas por la capa de persistencia cuando se violan
     * reglas de negocio a nivel de base de datos (triggers o restricciones de PostgreSQL).
     *
     * <p>
     * Esto incluye errores lanzados mediante {@code RAISE EXCEPTION} en triggers
     * de base de datos, como validaciones de stock insuficiente o límites máximos
     * para el total de una venta.
     * </p>
     *
     * <p>
     * El manejador extrae y normaliza los mensajes de error de la base de datos,
     * eliminando información técnica agregada por PostgreSQL, Hibernate o JDBC
     * (por ejemplo, "ERROR:" o "Where:"), conservando únicamente la información
     * relevante para el negocio que será mostrada al frontend.
     * </p>
     *
     * <p>
     * Errores de negocio soportados:
     * <ul>
     *   <li>Validación de stock insuficiente (restricción a nivel de producto)</li>
     *   <li>Validación del importe máximo permitido para una venta (restricción financiera)</li>
     * </ul>
     * </p>
     *
     * <p>
     * Si el error no coincide con una regla de negocio conocida,
     * se devuelve una respuesta genérica de error de base de datos.
     * </p>
     *
     * <p>
     * Estructura final de la respuesta:
     * </p>
     *
     * <pre>
     * {
     *   "error": {
     *     "code": "ERROR_CODE",
     *     "message": "Mensaje amigable para el negocio",
     *     "field": "Campo relacionado opcional"
     *   }
     * }
     * </pre>
     *
     * @param ex excepción propagada desde la capa de persistencia
     * @return respuesta de error estandarizada que contiene código, mensaje y campo opcional
     */
    @ExceptionHandler({
            DataIntegrityViolationException.class,
            JpaSystemException.class,
            PersistenceException.class
    })
    public ResponseEntity<Map<String, Object>> handleDatabaseTriggerException(Exception ex) {

        Throwable root = org.springframework.core.NestedExceptionUtils.getMostSpecificCause(ex);

        String rawMessage = (root != null ? root.getMessage() : ex.getMessage());

        // =========================
        // LIMPIAR MENSAJE DE BASE DE DATOS
        // =========================
        String message = rawMessage;

        if (rawMessage != null) {

            // eliminar contexto de pila agregado por PostgreSQL
            if (rawMessage.contains("Where:")) {
                message = rawMessage.split("Where:")[0].trim();
            }

            // eliminar prefijos técnicos agregados por JDBC/Hibernate
            if (message.startsWith("ERROR:")) {
                message = message.substring("ERROR:".length()).trim();
            }
        }

        String code = "DATABASE_ERROR";
        String field = null;
        String userMessage = "La operación en la base de datos ha fallado";

        Map<String, Object> errorBody = new HashMap<>();

        if (message != null) {

            // =========================
            // ERROR DE STOCK
            // =========================
            if (message.contains("Stock insuficiente para el producto")) {

                code = "INSUFFICIENT_STOCK";
                field = "productQuantity";

                userMessage = message;
            }

            // =========================
            // ERROR DE IMPORTE TOTAL
            // =========================
            else if (message.contains("El importe total de la venta supera el máximo permitido")) {

                code = "SALE_TOTAL_EXCEEDED";
                field = "totalAmount";

                userMessage = message;
            }
        }

        errorBody.put("code", code);
        errorBody.put("message", userMessage);
        errorBody.put("field", field);

        Map<String, Object> response = new HashMap<>();
        response.put("error", errorBody);

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Maneja las excepciones relacionadas con la configuración del sistema lanzadas cuando
     * las reglas de negocio o validaciones fallan dentro del dominio de Configuración del Sistema.
     *
     * <p>
     * Este manejador centraliza todas las excepciones que extienden de {@code SystemConfigurationException},
     * asegurando un formato de respuesta de error consistente en toda la aplicación.
     * </p>
     *
     * <p>
     * La respuesta sigue la estructura estandarizada:
     * </p>
     *
     * <pre>
     * {
     *   "error": {
     *     "code": "ERROR_CODE",
     *     "message": "Mensaje legible para el usuario",
     *     "field": "Campo opcional relacionado con el error"
     *   }
     * }
     * </pre>
     *
     * <p>
     * El frontend debe utilizar:
     * <ul>
     *   <li><b>code</b>: para determinar el tipo de error y el comportamiento de la UI</li>
     *   <li><b>message</b>: para mostrar o registrar información legible para el usuario</li>
     *   <li><b>field</b>: para asociar errores de validación con campos específicos</li>
     * </ul>
     * </p>
     *
     * @param ex excepción relacionada con la configuración del sistema que contiene los detalles del error
     * @return respuesta 400 Bad Request con un cuerpo de error estandarizado
     */
    @ExceptionHandler(SystemConfigurationException.class)
    public ResponseEntity<Map<String, Object>> handleSystemConfigurationException(
            SystemConfigurationException ex) {

        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("code", ex.getCode());
        errorBody.put("message", ex.getMessage());
        errorBody.put("field", ex.getField());

        Map<String, Object> response = new HashMap<>();
        response.put("error", errorBody);

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Maneja las excepciones relacionadas con estadísticas lanzadas cuando
     * las reglas de negocio o validaciones fallan dentro del dominio de Estadísticas.
     *
     * <p>
     * Este manejador centraliza todas las excepciones que extienden de {@code StatisticsException},
     * asegurando un formato de respuesta de error consistente en toda la aplicación.
     * </p>
     *
     * <p>
     * La respuesta sigue la estructura estandarizada:
     * </p>
     *
     * <pre>
     * {
     *   "error": {
     *     "code": "ERROR_CODE",
     *     "message": "Mensaje legible para el usuario",
     *     "field": "Campo opcional relacionado con el error"
     *   }
     * }
     * </pre>
     *
     * <p>
     * El frontend debe utilizar:
     * <ul>
     *   <li><b>code</b>: para determinar el tipo de error y el comportamiento de la UI</li>
     *   <li><b>message</b>: para mostrar o registrar información legible para el usuario</li>
     *   <li><b>field</b>: para asociar errores de validación con campos específicos</li>
     * </ul>
     * </p>
     *
     * @param ex excepción relacionada con estadísticas que contiene los detalles del error
     * @return respuesta 400 Bad Request con un cuerpo de error estandarizado
     */
    @ExceptionHandler(StatisticsException.class)
    public ResponseEntity<Map<String, Object>> handleStatisticsException(
            StatisticsException ex
    ) {

        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("code", ex.getCode());
        errorBody.put("message", ex.getMessage());
        errorBody.put("field", ex.getField());

        Map<String, Object> response = new HashMap<>();
        response.put("error", errorBody);

        return ResponseEntity.badRequest().body(response);
    }
}
