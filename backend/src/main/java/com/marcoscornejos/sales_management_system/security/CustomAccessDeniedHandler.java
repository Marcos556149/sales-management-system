package com.marcoscornejos.sales_management_system.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Manejador personalizado para accesos denegados (HTTP 403).
 *
 * <p>Se ejecuta cuando un usuario autenticado intenta acceder a un
 * recurso para el cual no posee los permisos necesarios.</p>
 *
 * <p>Retorna una respuesta JSON estandarizada con información del error
 * de autorización.</p>
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    /**
     * Maneja las excepciones de acceso denegado y construye la respuesta HTTP.
     *
     * @param request petición HTTP que generó el error
     * @param response respuesta HTTP que será enviada al cliente
     * @param ex excepción de acceso denegado
     * @throws IOException si ocurre un error al escribir la respuesta
     */
    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException ex
    ) throws IOException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write("""
        {
            "error": {
                "code": "ACCESS_DENIED",
                "message": "No tenés permisos para acceder a este recurso",
                "field": null
            }
        }
        """);
    }
}
