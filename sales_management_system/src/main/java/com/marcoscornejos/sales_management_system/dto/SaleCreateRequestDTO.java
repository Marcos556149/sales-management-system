package com.marcoscornejos.sales_management_system.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * DTO used to register a new sale in the system.
 *
 * <p>
 * Contains the list of products to be included in the sale,
 * along with their requested quantities.
 * </p>
 *
 * <p>
 * General sale data such as identifier, date, time, total amount,
 * and authenticated user are automatically assigned by the system.
 * </p>
 */
@Getter
@Setter
public class SaleCreateRequestDTO {

    /**
     * Products associated with the sale.
     *
     * <p>
     * A sale must contain at least one product detail.
     * Each detail is individually validated.
     * </p>
     */
    @NotNull(message = "Sale details are required")
    @NotEmpty(message = "Sale must contain at least one product")
    @Valid
    private List<SaleDetailCreateRequestDTO> saleDetails;

}
