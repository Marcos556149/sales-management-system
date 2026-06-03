package com.marcoscornejos.sales_management_system.dto;

import com.marcoscornejos.sales_management_system.model.UnitOfMeasure;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO utilizado para registrar un nuevo producto en el sistema.
 *
 * <p>
 * Contiene toda la información necesaria para crear un producto.
 * El estado del producto no se incluye porque el sistema lo establece
 * automáticamente como ACTIVE.
 * </p>
 */
@Getter
@Setter
public class ProductCreateRequestDTO {

    /**
     * Código único del producto.
     *
     * <p>
     * No debe estar vacío ni superar los 100 caracteres.
     * </p>
     */
    @NotBlank(message = "El código del producto es obligatorio")
    @Size(max = 100, message = "El código del producto no debe superar los 100 caracteres")
    private String productCode;

    /**
     * Nombre del producto.
     *
     * <p>
     * No debe estar vacío ni superar los 100 caracteres.
     * </p>
     */
    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 100, message = "El nombre del producto no debe superar los 100 caracteres")
    private String productName;

    /**
     * Precio unitario del producto.
     *
     * <p>
     * Debe ser mayor o igual a 0 y respetar la restricción de base de datos
     * NUMERIC(12,2), permitiendo hasta 10 dígitos enteros y 2 decimales.
     * </p>
     */
    @NotNull(message = "El precio del producto es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El precio del producto debe ser mayor o igual a 0")
    @Digits(integer = 10, fraction = 2, message = "El precio del producto debe tener hasta 10 dígitos y 2 decimales")
    private BigDecimal productPrice;

    /**
     * Cantidad de stock disponible del producto.
     *
     * <p>
     * Debe ser mayor o igual a 0 y respetar la restricción de base de datos
     * NUMERIC(12,2), permitiendo hasta 10 dígitos enteros y 2 decimales.
     * </p>
     */
    @NotNull(message = "El stock del producto es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El stock del producto debe ser mayor o igual a 0")
    @Digits(integer = 10, fraction = 2, message = "El stock del producto debe tener hasta 10 dígitos y 2 decimales")
    private BigDecimal productStock;

    /**
     * Unidad de medida del producto (por ejemplo, UNITS, KILOGRAMS, LITERS).
     */
    @NotNull(message = "La unidad de medida es obligatoria")
    private UnitOfMeasure unitOfMeasure;

    /**
     * Stock mínimo configurado para el producto.
     */
    @NotNull(message = "El stock mínimo es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El stock mínimo debe ser mayor o igual a 0")
    @Digits(integer = 10, fraction = 2, message = "El stock mínimo debe tener hasta 10 dígitos y 2 decimales")
    private BigDecimal minimumStock;

}