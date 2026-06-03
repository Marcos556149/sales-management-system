package com.marcoscornejos.sales_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * DTO genérico para respuestas paginadas.
 *
 * <p>
 * Proporciona una estructura estable y controlada para los datos de paginación,
 * evitando exponer directamente la implementación interna de Page de Spring.
 * </p>
 *
 * @param <T> tipo de contenido en la página
 */
@Getter
@AllArgsConstructor
public class PageResponseDTO<T> {

    /**
     * Lista de elementos de la página actual.
     */
    private List<T> content;

    /**
     * Número de la página actual (basado en índice 0).
     */
    private int page;

    /**
     * Cantidad de elementos por página.
     */
    private int size;

    /**
     * Número total de páginas.
     */
    private int totalPages;

    /**
     * Cantidad total de elementos considerando todas las páginas.
     */
    private long totalElements;

    /**
     * Cantidad total de registros en la tabla sin aplicar filtros.
     */
    private Long totalGlobalElements;
}
