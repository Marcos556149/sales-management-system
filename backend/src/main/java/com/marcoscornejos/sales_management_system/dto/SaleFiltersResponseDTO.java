package com.marcoscornejos.sales_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * DTO de respuesta que contiene las opciones de ordenamiento disponibles
 * para el módulo de ventas.
 *
 * <p>
 * Este DTO se utiliza para proporcionar dinámicamente al frontend
 * todos los valores válidos para ordenar ventas,
 * evitando valores hardcodeados en el cliente.
 * </p>
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class SaleFiltersResponseDTO {

    /**
     * Lista de opciones disponibles para ordenar ventas por hora de venta.
     *
     * <p>
     * Cada opción se representa mediante un {@link EnumDTO},
     * que contiene el código de dirección de ordenamiento y su etiqueta de visualización.
     * Estas opciones son utilizadas por el frontend para ordenar ventas
     * sin valores hardcodeados.
     * </p>
     */
    private List<EnumDTO> timeSortOptions;

}