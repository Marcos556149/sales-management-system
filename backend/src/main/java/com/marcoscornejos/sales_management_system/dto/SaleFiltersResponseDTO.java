package com.marcoscornejos.sales_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Response DTO that contains available sorting options
 * for the Sale module.
 *
 * <p>
 * This DTO is used to dynamically provide the frontend with
 * all valid values for sorting sales,
 * avoiding hardcoded values on the client side.
 * </p>
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class SaleFiltersResponseDTO {

    /**
     * List of available sorting options for sales.
     *
     * <p>
     * Each option is represented as an {@link EnumDTO},
     * containing the sort code and its display label.
     * </p>
     */
    private List<EnumDTO> sortOptions;

}