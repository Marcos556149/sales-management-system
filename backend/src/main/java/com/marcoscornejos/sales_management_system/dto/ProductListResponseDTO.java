package com.marcoscornejos.sales_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO utilizado para representar información de productos
 * al devolver datos al cliente.
 *
 * <p>
 * Incluye la identificación del producto, nombre, precio,
 * estado y datos relacionados con el stock.
 * </p>
 */

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductListResponseDTO {

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
     * Estado actual del producto (por ejemplo, ACTIVE o INACTIVE).
     */
    private EnumDTO productStatus;

    /**
     * Cantidad de stock disponible del producto.
     */
    private BigDecimal productStock;

    /**
     * Nivel mínimo de stock configurado para el producto.
     */
    private BigDecimal minimumStock;

    /**
     * Unidad de medida asociada al stock (por ejemplo, kg o u).
     */
    private EnumDTO unitOfMeasure;
}
