package com.marcoscornejos.sales_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Data Transfer Object for returning detailed sale information.
 *
 * <p>
 * This DTO represents the complete data of a single sale
 * to be displayed in the sale detail view.
 * </p>
 */
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class SaleWithDetailsResponseDTO {

    /**
     * Unique identifier of the sale.
     */
    private Long saleId;

    /**
     * Date when the sale was made.
     */
    private LocalDate saleDate;

    /**
     * Time when the sale was made.
     */
    private LocalTime saleTime;

    /**
     * Username of the seller.
     */
    private String userName;

    /**
     * Total amount of the sale.
     */
    private BigDecimal totalAmount;

    /**
     * List of products included in the sale.
     */
    private List<SaleItemDTO> saleDetails;

}