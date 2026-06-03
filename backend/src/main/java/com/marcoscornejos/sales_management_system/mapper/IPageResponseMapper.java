package com.marcoscornejos.sales_management_system.mapper;

import com.marcoscornejos.sales_management_system.dto.PageResponseDTO;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Mapper para convertir objetos Page de Spring en PageResponseDTO.
 */
@Mapper(componentModel = "spring")
public interface IPageResponseMapper {

    /**
     * Mapea contenido paginado y metadatos a un PageResponseDTO.
     *
     * @param content contenido mapeado
     * @param page página actual (base 0)
     * @param size tamaño de página
     * @param totalPages cantidad total de páginas
     * @param totalElements cantidad total de elementos
     * @param totalGlobalElements cantidad total de registros sin filtros
     * @param <T> tipo de contenido
     * @return PageResponseDTO con los datos de paginación
     */
    default <T> PageResponseDTO<T> toPageResponseDTO(
            List<T> content,
            int page,
            int size,
            int totalPages,
            long totalElements,
            Long totalGlobalElements
    ) {
        return new PageResponseDTO<>(
                content,
                page,
                size,
                totalPages,
                totalElements,
                totalGlobalElements
        );
    }
}
