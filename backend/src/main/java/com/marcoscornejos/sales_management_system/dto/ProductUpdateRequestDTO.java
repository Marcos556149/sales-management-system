package com.marcoscornejos.sales_management_system.dto;

import com.marcoscornejos.sales_management_system.model.UnitOfMeasure;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO utilizado para actualizar un producto existente en el sistema.
 *
 * <p>
 * Contiene todos los campos editables del producto.
 * El código y el estado del producto no pueden modificarse.
 * </p>
 */
@Getter
@Setter
public class ProductUpdateRequestDTO {

    /**
     * Nombre actualizado del producto.
     *
     * <p>
     * No debe estar vacío y no puede superar los 100 caracteres.
     * </p>
     */
    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 100, message = "El nombre del producto no puede superar los 100 caracteres")
    private String productName;

    /**
     * Precio unitario actualizado del producto.
     *
     * <p>
     * Debe ser mayor o igual a 0 y respetar la restricción de base de datos
     * NUMERIC(12,2), permitiendo hasta 10 dígitos enteros y 2 decimales.
     * </p>
     */
    @NotNull(message = "El precio del producto es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El precio del producto debe ser mayor o igual a 0")
    @Digits(integer = 10, fraction = 2, message = "El precio del producto debe tener hasta 10 dígitos enteros y 2 decimales")
    private BigDecimal productPrice;

    /**
     * Cantidad de stock disponible actualizada.
     *
     * <p>
     * Debe ser mayor o igual a 0 y respetar la restricción de base de datos
     * NUMERIC(12,2), permitiendo hasta 10 dígitos enteros y 2 decimales.
     * </p>
     */
    @NotNull(message = "El stock del producto es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El stock del producto debe ser mayor o igual a 0")
    @Digits(integer = 10, fraction = 2, message = "El stock del producto debe tener hasta 10 dígitos enteros y 2 decimales")
    private BigDecimal productStock;

    /**
     * Unidad de medida actualizada del producto.
     *
     * <p>
     * No debe ser nula y debe corresponder a un valor válido del enum.
     * </p>
     */
    @NotNull(message = "La unidad de medida es obligatoria")
    private UnitOfMeasure unitOfMeasure;

    /**
     * Nivel mínimo de stock actualizado.
     */
    @NotNull(message = "El stock mínimo es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El stock mínimo debe ser mayor o igual a 0")
    @Digits(integer = 10, fraction = 2, message = "El stock mínimo debe tener hasta 10 dígitos enteros y 2 decimales")
    private BigDecimal minimumStock;

}
