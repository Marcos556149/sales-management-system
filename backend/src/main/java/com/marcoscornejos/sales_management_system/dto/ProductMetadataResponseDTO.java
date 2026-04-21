package com.marcoscornejos.sales_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * DTO containing metadata required for product-related operations.
 *
 * <p>
 * Provides dynamic values such as enums and defaults
 * used by the frontend.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductMetadataResponseDTO {

    /**
     * List of available unit of measure options.
     *
     * <p>
     * Each option is represented as an {@link EnumDTO},
     * containing the enum code and its display label.
     * </p>
     */
    private List<EnumDTO> unitOfMeasureOptions;

}