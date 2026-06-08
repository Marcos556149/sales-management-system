package com.marcoscornejos.sales_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO utilizado para representar productos disponibles para la venta.
 *
 * <p>
 * Incluye los datos esenciales del producto requeridos por la interfaz
 * de registro de ventas, excluyendo campos administrativos como el estado
 * del producto.
 * </p>
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductSaleListResponseDTO {

    /**
     * Código único del producto que lo identifica.
     */
    private String productCode;

    /**
     * Nombre del producto.
     */
    private String productName;

    /**
     * Precio del producto.
     */
    private BigDecimal productPrice;

    /**
     * Cantidad de stock disponible del producto.
     */
    private BigDecimal productStock;

    /**
     * Umbral mínimo de stock configurado para el producto.
     */
    private BigDecimal minimumStock;

    /**
     * Unidad de medida asociada al stock (por ejemplo: kg, u).
     */
    private EnumDTO unitOfMeasure;
}
