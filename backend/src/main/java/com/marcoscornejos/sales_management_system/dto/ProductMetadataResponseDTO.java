package com.marcoscornejos.sales_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * DTO que contiene la información complementaria requerida
 * para las operaciones relacionadas con productos.
 *
 * <p>
 * Proporciona valores dinámicos, como enumeraciones y opciones
 * utilizadas por el frontend.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductMetadataResponseDTO {

    /**
     * Lista de opciones disponibles de unidades de medida.
     *
     * <p>
     * Cada opción se representa mediante un {@link EnumDTO},
     * que contiene el código del enum y su etiqueta de visualización.
     * </p>
     */
    private List<EnumDTO> unitOfMeasureOptions;

}