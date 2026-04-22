package com.marcoscornejos.sales_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Response DTO that contains available filter and sorting options
 * for the Product module.
 *
 * <p>This DTO is used to dynamically provide the frontend with
 * all valid values for filtering and sorting products,
 * avoiding hardcoded values on the client side.</p>
 */

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductFiltersResponseDTO {

    /**
     * List of available product status options.
     *
     * <p>Each option is represented as an {@link EnumDTO},
     * containing the enum code and its display label.</p>
     */
    private List<EnumDTO> statusOptions;

    /**
     * List of available sorting options for products.
     *
     * <p>Each option is represented as an {@link EnumDTO},
     * containing the sort code and its display label.</p>
     */
    private List<EnumDTO> nameSortOptions;

    /**
     * List of available stock level filter options.
     * * <p>Each option is represented as an {@link EnumDTO}
     *
     * <p>Represents predefined stock filtering states
     */
    private List<EnumDTO> stockLevelOptions;

}
