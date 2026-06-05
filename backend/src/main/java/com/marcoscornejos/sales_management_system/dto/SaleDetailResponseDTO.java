package com.marcoscornejos.sales_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO que representa cada producto incluido en una venta.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SaleDetailResponseDTO {

    /**
     * Código del producto.
     */
    private String productCode;

    /**
     * Nombre del producto al momento de la venta (snapshot histórico).
     */
    private String productNameAtSale;

    /**
     * Cantidad vendida.
     */
    private BigDecimal productQuantity;

    /**
     * Unidad de medida del producto al momento de la venta.
     */
    private EnumDTO unitOfMeasureAtSale;

    /**
     * Precio del producto al momento de la venta.
     */
    private BigDecimal salePrice;

    /**
     * Importe subtotal de este producto.
     */
    private BigDecimal subtotal;
}
