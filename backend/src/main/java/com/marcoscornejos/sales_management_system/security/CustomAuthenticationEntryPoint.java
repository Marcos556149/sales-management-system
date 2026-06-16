package com.marcoscornejos.sales_management_system.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Punto de entrada personalizado para solicitudes no autenticadas (HTTP 401).
 *
 * <p>Se ejecuta cuando un usuario intenta acceder a un recurso protegido
 * sin estar autenticado o sin un token válido.</p>
 *
 * <p>Retorna una respuesta JSON estandarizada con información del error
 * de autenticación.</p>
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * Maneja los errores de autenticación y genera la respuesta HTTP 401.
     *
     * @param request petición HTTP que provocó el error
     * @param response respuesta HTTP enviada al cliente
     * @param authException excepción de autenticación
     * @throws IOException si ocurre un error al escribir la respuesta
     */
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        response.getWriter().write("""
        {
            "error": {
                "code": "UNAUTHORIZED",
                "message": "No estas autenticado",
                "field": null
            }
        }
        """);
    }
}
