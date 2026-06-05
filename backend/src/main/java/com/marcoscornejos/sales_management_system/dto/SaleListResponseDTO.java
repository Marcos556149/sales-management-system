package com.marcoscornejos.sales_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO utilizado para representar datos de una venta al devolver información al cliente.
 *
 * <p>
 * Incluye información general de la venta como identificador, fecha, hora,
 * nombre de usuario del vendedor e importe total.
 * </p>
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SaleListResponseDTO {

    /**
     * Identificador único de la venta.
     */
    private Long saleId;

    /**
     * Fecha en que se registró la venta.
     */
    private LocalDate saleDate;

    /**
     * Hora en que se registró la venta.
     */
    private LocalTime saleTime;

    /**
     * Nombre de usuario del usuario que realizó la venta.
     */
    private String userName;

    /**
     * Importe total de la venta.
     */
    private BigDecimal totalAmount;
}