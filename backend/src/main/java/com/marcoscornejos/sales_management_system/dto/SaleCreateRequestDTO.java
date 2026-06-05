package com.marcoscornejos.sales_management_system.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * DTO utilizado para registrar una nueva venta en el sistema.
 *
 * <p>
 * Contiene la lista de productos que serán incluidos en la venta,
 * junto con las cantidades solicitadas de cada uno.
 * </p>
 *
 * <p>
 * Los datos generales de la venta, como el identificador, la fecha,
 * la hora, el importe total y el usuario autenticado, son asignados
 * automáticamente por el sistema.
 * </p>
 */
@Getter
@Setter
public class SaleCreateRequestDTO {

    /**
     * Productos asociados a la venta.
     *
     * <p>
     * Una venta debe contener al menos un detalle de producto.
     * Cada detalle es validado individualmente.
     * </p>
     */
    @NotEmpty(message = "La venta debe contener al menos un producto")
    @Valid
    private List<SaleDetailCreateRequestDTO> saleDetails;

}
