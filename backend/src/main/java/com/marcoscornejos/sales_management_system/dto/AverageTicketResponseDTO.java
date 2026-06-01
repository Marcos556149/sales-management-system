package com.marcoscornejos.sales_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO that represents the average ticket value
 * calculated based on the selected filters.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AverageTicketResponseDTO {

    /**
     * Average ticket value.
     *
     * <p>
     * Calculated as:
     * total revenue / total number of sales.
     * </p>
     */
    private BigDecimal averageTicket;
}
