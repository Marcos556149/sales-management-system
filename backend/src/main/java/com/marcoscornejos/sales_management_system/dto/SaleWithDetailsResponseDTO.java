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
 * Objeto de Transferencia de Datos utilizado para devolver información detallada de una venta.
 *
 * <p>
 * Este DTO representa la información completa de una venta individual
 * para ser mostrada en la vista de detalle de venta.
 * </p>
 */
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class SaleWithDetailsResponseDTO {

    /**
     * Identificador único de la venta.
     */
    private Long saleId;

    /**
     * Fecha en que se realizó la venta.
     */
    private LocalDate saleDate;

    /**
     * Hora en que se realizó la venta.
     */
    private LocalTime saleTime;

    /**
     * Nombre de usuario del vendedor.
     */
    private String userName;

    /**
     * Importe total de la venta.
     */
    private BigDecimal totalAmount;

    /**
     * Lista de productos incluidos en la venta.
     */
    private List<SaleDetailResponseDTO> saleDetails;

}