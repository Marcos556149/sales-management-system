package com.marcoscornejos.sales_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO genérico de respuesta para operaciones exitosas.
 *
 * <p>
 * Se utiliza para estandarizar todas las respuestas exitosas de la API,
 * proporcionando una estructura consistente para su consumo desde el frontend.
 * </p>
 *
 * @param <T> tipo de dato devuelto en la respuesta
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SuccessResponseDTO<T> {

    /**
     * Código de éxito legible por máquina utilizado por el frontend
     * para identificar el tipo de operación ejecutada.
     */
    private String code;

    /**
     * Mensaje legible para el usuario que describe el resultado de la operación.
     */
    private String message;

    /**
     * Datos opcionales devueltos por la operación.
     */
    private T data;
}