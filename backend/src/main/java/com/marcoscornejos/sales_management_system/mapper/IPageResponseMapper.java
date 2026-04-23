package com.marcoscornejos.sales_management_system.mapper;

import com.marcoscornejos.sales_management_system.dto.PageResponseDTO;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Mapper for converting Spring Page objects into PageResponseDTO.
 */
@Mapper(componentModel = "spring")
public interface IPageResponseMapper {

    /**
     * Maps paginated content and metadata into a PageResponseDTO.
     *
     * @param content       mapped content list
     * @param page          current page (0-based)
     * @param size          page size
     * @param totalPages    total number of pages
     * @param totalElements total number of elements
     * @param totalGlobalElements total number of records without filters
     * @param <T>           type of content
     * @return PageResponseDTO with pagination data
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
