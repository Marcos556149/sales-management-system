package com.marcoscornejos.sales_management_system.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO utilizado para registrar un detalle de venta como parte de una nueva venta.
 *
 * <p>
 * Representa un producto incluido en la venta y la cantidad
 * solicitada por el usuario.
 * </p>
 *
 * <p>
 * El identificador del detalle de venta, el precio unitario y el subtotal
 * no se incluyen porque son resueltos automáticamente por el sistema
 * durante el registro de la venta.
 * </p>
 */
@Getter
@Setter
public class SaleDetailCreateRequestDTO {

    /**
     * Código único del producto que se asociará a la venta.
     *
     * <p>
     * No debe estar vacío y no debe superar los 100 caracteres.
     * </p>
     */
    @NotBlank(message = "El código del producto es obligatorio")
    @Size(max = 100, message = "El código del producto no debe superar los 100 caracteres")
    private String productCode;

    /**
     * Cantidad solicitada para el producto seleccionado.
     *
     * <p>
     * Debe ser mayor que 0 y respetar la restricción de base de datos
     * NUMERIC(12,2), permitiendo hasta 10 dígitos enteros y 2 decimales.
     * </p>
     *
     * <p>
     * La compatibilidad con la unidad de medida del producto
     * (por ejemplo, no permitir decimales para productos vendidos por unidades)
     * se valida en la capa de negocio.
     * </p>
     */
    @NotNull(message = "La cantidad del producto es obligatoria")
    @DecimalMin(value = "0.01", inclusive = true, message = "La cantidad del producto debe ser mayor que 0")
    @Digits(integer = 10, fraction = 2, message = "La cantidad del producto debe tener hasta 10 dígitos enteros y 2 decimales")
    private BigDecimal productQuantity;

}