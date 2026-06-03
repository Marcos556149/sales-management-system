package com.marcoscornejos.sales_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * DTO de respuesta que contiene las opciones disponibles de filtrado
 * y ordenamiento para el módulo de productos.
 *
 * <p>
 * Este DTO se utiliza para proporcionar dinámicamente al frontend
 * todos los valores válidos para filtrar y ordenar productos,
 * evitando valores hardcodeados en el cliente.
 * </p>
 */

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductFiltersResponseDTO {

    /**
     * Lista de opciones disponibles para el estado del producto.
     *
     * <p>
     * Cada opción se representa mediante un {@link EnumDTO},
     * que contiene el código del enum y su etiqueta de visualización.
     * </p>
     */
    private List<EnumDTO> statusOptions;

    /**
     * Lista de opciones disponibles para el ordenamiento de productos.
     *
     * <p>
     * Cada opción se representa mediante un {@link EnumDTO},
     * que contiene el código de ordenamiento y su etiqueta de visualización.
     * </p>
     */
    private List<EnumDTO> nameSortOptions;

    /**
     * Lista de opciones disponibles para filtrar productos según su nivel de stock.
     *
     * <p>
     * Cada opción se representa mediante un {@link EnumDTO},
     * que contiene el código del filtro y su etiqueta de visualización.
     * </p>
     *
     * <p>
     * Representa los estados predefinidos de filtrado de stock.
     * </p>
     */
    private List<EnumDTO> stockLevelOptions;

}
