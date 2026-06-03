package com.marcoscornejos.sales_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO de transferencia de datos utilizado para devolver información detallada de un producto.
 *
 * <p>
 * Este DTO representa los datos completos de un único producto
 * para ser mostrados en la vista de detalle del producto.
 * </p>
 */
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetailResponseDTO {

    /**
     * Código único que identifica al producto.
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
     * Unidad de medida del producto (por ejemplo, UNITS, KILOGRAMS, LITERS).
     */
    private EnumDTO unitOfMeasure;

    /**
     * Estado actual del producto (por ejemplo, ACTIVE, INACTIVE).
     */
    private EnumDTO productStatus;

    /**
     * Cantidad de stock disponible.
     */
    private BigDecimal productStock;

    /**
     * Stock mínimo configurado para el producto.
     */
    private BigDecimal minimumStock;


}
