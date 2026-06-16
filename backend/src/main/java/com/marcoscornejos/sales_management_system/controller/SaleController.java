package com.marcoscornejos.sales_management_system.controller;

import com.marcoscornejos.sales_management_system.dto.*;
import com.marcoscornejos.sales_management_system.model.SortDirection;
import com.marcoscornejos.sales_management_system.service.ISaleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * Controlador REST responsable de gestionar las consultas relacionadas con ventas.
 *
 * <p>
 * Proporciona endpoints para obtener ventas con filtrado opcional por fecha,
 * ordenamiento por hora y soporte de paginación.
 * </p>
 */
@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SaleController {

        private final ISaleService iSaleService;

        /**
         * Obtiene una lista paginada de ventas registradas en el sistema.
         *
         * <p>
         * Cada venta incluye información general como identificador, fecha y hora
         * de la venta, nombre de usuario del vendedor e importe total.
         * </p>
         *
         * <p>
         * Soporta paginación del lado del servidor, filtrado por fecha y
         * ordenamiento cronológico.
         * </p>
         *
         * @param searchSaleId búsqueda opcional por identificador de venta
         * @param date         filtro por fecha de venta (si no se proporciona,
         *                     se utiliza la fecha actual por defecto)
         * @param timeSort     dirección de ordenamiento por hora de venta
         *                     (NEWEST_FIRST u OLDEST_FIRST, por defecto: NEWEST_FIRST)
         * @param page         número de página (por defecto: 0)
         * @param size         cantidad de ventas por página (por defecto: 50)
         * @return respuesta paginada que contiene las ventas y los metadatos de paginación
         */
        @GetMapping
        @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
        public ResponseEntity<PageResponseDTO<SaleListResponseDTO>> getSales(
                        @RequestParam(required = false) Long searchSaleId,

                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,

                        @RequestParam(defaultValue = "NEWEST_FIRST") SortDirection timeSort,

                        @RequestParam(defaultValue = "0") int page,

                        @RequestParam(defaultValue = "50") int size) {
                PageResponseDTO<SaleListResponseDTO> response = iSaleService.getSales(
                                searchSaleId,
                                date,
                                timeSort,
                                page,
                                size);

                return ResponseEntity.ok(response);
        }

        /**
         * Obtiene la información detallada de una venta específica mediante su identificador.
         *
         * <p>
         * Este endpoint permite a los clientes obtener los datos de una venta,
         * incluyendo su identificador, fecha, hora, nombre de usuario del vendedor,
         * importe total y la lista de productos vendidos con sus respectivos detalles.
         * </p>
         *
         * @param saleId identificador único de la venta
         * @return los detalles de la venta como DTO de respuesta
         */
        @GetMapping("/{saleId}")
        @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
        public ResponseEntity<SaleWithDetailsResponseDTO> getSaleById(
                        @PathVariable Long saleId) {

                SaleWithDetailsResponseDTO sale = iSaleService.getSaleById(saleId);

                return ResponseEntity.ok(sale);
        }

        /**
         * Obtiene las opciones de ordenamiento disponibles para las ventas.
         *
         * <p>
         * Este endpoint proporciona datos de configuración dinámicos para el frontend,
         * incluyendo las opciones de ordenamiento por hora de venta.
         * Evita el uso de valores codificados de forma fija en la aplicación cliente.
         * </p>
         *
         * @return ResponseEntity que contiene un SaleFiltersResponseDTO
         */
        @GetMapping("/filters")
        @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
        public ResponseEntity<SaleFiltersResponseDTO> getFilters() {

                SaleFiltersResponseDTO response = iSaleService.getFilters();

                return ResponseEntity.ok(response);
        }

        /**
         * Registra una nueva venta en el sistema.
         *
         * <p>
         * Crea una venta junto con sus detalles, asignando automáticamente la fecha,
         * hora, importe total y el usuario autenticado. La venta debe contener al menos
         * un producto y todas las reglas de negocio (stock, estado del producto y cantidad)
         * son validadas durante el procesamiento.
         * </p>
         *
         * <p>
         * Devuelve el identificador único de la venta creada, el cual puede utilizarse
         * para obtener información adicional como el ticket imprimible.
         * </p>
         *
         * @param request los datos de la venta, incluyendo productos y cantidades
         * @return una respuesta de éxito estandarizada que contiene el ID de la venta creada
         */
        @PostMapping
        @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
        public ResponseEntity<SuccessResponseDTO<Long>> registerSale(
                @RequestBody @Valid SaleCreateRequestDTO request) {

                Long saleId = iSaleService.registerSale(request);

                SuccessResponseDTO<Long> response = new SuccessResponseDTO<>(
                        "SALE_CREATED",
                        "Venta registrada correctamente",
                        saleId
                );

                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        /**
         * Genera un ticket imprimible para una venta específica.
         *
         * <p>
         * Este endpoint recupera la venta y sus detalles asociados,
         * y devuelve un ticket formateado listo para ser impreso en una impresora térmica.
         * El ticket incluye información del negocio, fecha y hora,
         * productos vendidos, cantidades, precios unitarios, subtotales e importe total.
         * </p>
         *
         * @param saleId identificador único de la venta
         * @return el ticket de venta formateado como texto plano
         */
        @GetMapping("/{saleId}/ticket")
        @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
        public ResponseEntity<String> generateSaleTicket(
                        @PathVariable Long saleId) {

                String ticket = iSaleService.generateSaleTicket(saleId);

                return ResponseEntity.ok()
                                .header("Content-Type", "text/plain; charset=UTF-8")
                                .body(ticket);
        }

}