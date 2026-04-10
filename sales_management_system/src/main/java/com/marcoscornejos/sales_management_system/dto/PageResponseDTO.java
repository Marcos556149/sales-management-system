package com.marcoscornejos.sales_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * Generic DTO for paginated responses.
 *
 * <p>
 * Provides a stable and controlled structure for pagination data,
 * avoiding direct exposure of Spring's internal Page implementation.
 * </p>
 *
 * @param <T> Type of content inside the page
 */
@Getter
@AllArgsConstructor
public class PageResponseDTO<T> {

    /**
     * List of elements in the current page.
     */
    private List<T> content;

    /**
     * Current page number (0-based).
     */
    private int page;

    /**
     * Number of elements per page.
     */
    private int size;

    /**
     * Total number of pages.
     */
    private int totalPages;

    /**
     * Total number of elements across all pages.
     */
    private long totalElements;
}
