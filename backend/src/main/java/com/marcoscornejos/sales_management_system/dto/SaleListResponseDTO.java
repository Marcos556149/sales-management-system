package com.marcoscornejos.sales_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO used to represent sale data when returning information to the client.
 *
 * <p>
 * It includes general sale information such as identifier, date, time,
 * seller username, and total amount.
 * </p>
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SaleListResponseDTO {

    /**
     * Unique identifier of the sale.
     */
    private Long saleId;

    /**
     * Date when the sale was registered.
     */
    private LocalDate saleDate;

    /**
     * Time when the sale was registered.
     */
    private LocalTime saleTime;

    /**
     * Username of the user who performed the sale.
     */
    private String userName;

    /**
     * Total amount of the sale.
     */
    private BigDecimal totalAmount;
}