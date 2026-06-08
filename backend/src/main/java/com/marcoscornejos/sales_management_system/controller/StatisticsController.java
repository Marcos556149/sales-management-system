package com.marcoscornejos.sales_management_system.controller;

import com.marcoscornejos.sales_management_system.dto.*;
import com.marcoscornejos.sales_management_system.model.ProductQuantityOrderType;
import com.marcoscornejos.sales_management_system.model.ProductRankingMetric;
import com.marcoscornejos.sales_management_system.service.IStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controlador REST responsable de proporcionar estadísticas de ventas y datos analíticos.
 *
 * <p>
 * Este controlador gestiona consultas agregadas sobre ventas y productos,
 * incluyendo métricas de ingresos, rankings de productos y estadísticas basadas en el tiempo.
 * </p>
 */
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final IStatisticsService iStatisticsService;
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd-MM-yyyy");

    /**
     * Obtiene los usuarios disponibles para filtrar estadísticas.
     *
     * <p>
     * Este endpoint proporciona la lista de usuarios que pueden utilizarse
     * para filtrar las estadísticas de ventas en el frontend.
     * </p>
     *
     * <p>
     * Cada usuario devuelto incluye:
     * <ul>
     *   <li>Identificador del usuario (usado para filtrado en backend)</li>
     *   <li>Nombre visible (usado en selectores del frontend)</li>
     * </ul>
     * </p>
     *
     * <p>
     * Si no existen usuarios en el sistema, se devuelve una lista vacía.
     * El frontend debe interpretar esto como "no hay filtros por usuario disponibles".
     * </p>
     *
     * <p>
     * Este endpoint actúa como la única fuente de verdad para
     * las opciones de filtrado de estadísticas por usuario.
     * </p>
     *
     * @return lista de usuarios disponibles para filtrar estadísticas
     */
    @GetMapping("/filters/users")
    public ResponseEntity<List<UserFilterDTO>> getStatisticsFilterUsers() {

        List<UserFilterDTO> response =
                iStatisticsService.getStatisticsFilterUsers();

        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene las opciones disponibles para el filtrado del ranking de productos.
     *
     * <p>
     * Este endpoint proporciona todas las opciones basadas en enums
     * que el frontend necesita para construir dinámicamente la interfaz
     * de filtrado del ranking de productos.
     * </p>
     *
     * <p>
     * La información devuelta incluye:
     * <ul>
     *   <li>Métricas disponibles para el ranking de productos</li>
     *   <li>Opciones de ordenamiento disponibles</li>
     * </ul>
     * </p>
     *
     * <p>
     * Todas las opciones provienen directamente de enums del backend,
     * asegurando consistencia entre los filtros del frontend
     * y las reglas de negocio del backend.
     * </p>
     *
     * @return opciones de filtrado para el ranking de productos
     */
    @GetMapping("/filters/product-ranking")
    public ResponseEntity<ProductRankingFiltersResponseDTO>
    getProductRankingFilters() {

        ProductRankingFiltersResponseDTO response =
                iStatisticsService.getProductRankingFilters();

        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene los ingresos totales basados en los filtros seleccionados.
     *
     * <p>
     * Los ingresos totales se calculan como la suma de todos los montos de ventas
     * que coinciden con los filtros proporcionados.
     * </p>
     *
     * <p>
     * Si no se proporcionan filtros:
     * <ul>
     *   <li>El usuario por defecto es "Todos los usuarios"</li>
     *   <li>El rango de fechas por defecto es la fecha actual</li>
     * </ul>
     * </p>
     *
     * @param userId filtro opcional por usuario (null = todos los usuarios)
     * @param startDate filtro opcional de fecha inicial
     * @param endDate filtro opcional de fecha final
     * @return información de ingresos totales
     */
    @GetMapping("/sales/total-revenue")
    public ResponseEntity<TotalRevenueResponseDTO> getTotalRevenue(

            @RequestParam(required = false) Long userId,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {

        TotalRevenueResponseDTO response =
                iStatisticsService.getTotalRevenue(
                        userId,
                        startDate,
                        endDate
                );

        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene el número total de ventas basado en los filtros seleccionados.
     *
     * <p>
     * El total de ventas se calcula como el conteo de todos los registros de ventas
     * que coinciden con los filtros proporcionados.
     * </p>
     *
     * <p>
     * Si no se proporcionan filtros:
     * <ul>
     *   <li>El usuario por defecto es "Todos los usuarios"</li>
     *   <li>El rango de fechas por defecto es la fecha actual</li>
     * </ul>
     * </p>
     *
     * @param userId filtro opcional por usuario (null = todos los usuarios)
     * @param startDate filtro opcional de fecha inicial
     * @param endDate filtro opcional de fecha final
     * @return información del total de ventas
     */
    @GetMapping("/sales/total-sales")
    public ResponseEntity<TotalSalesResponseDTO> getTotalSales(

            @RequestParam(required = false) Long userId,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {


        TotalSalesResponseDTO response =
                iStatisticsService.getTotalSales(
                        userId,
                        startDate,
                        endDate
                );

        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene el valor promedio del ticket basado en los filtros seleccionados.
     *
     * <p>
     * El valor promedio del ticket se calcula como:
     * ingresos totales divididos por el número total de ventas.
     * </p>
     *
     * <p>
     * Si no se proporcionan filtros:
     * <ul>
     *   <li>El usuario por defecto es "Todos los usuarios"</li>
     *   <li>El rango de fechas por defecto es la fecha actual</li>
     * </ul>
     * </p>
     *
     * @param userId filtro opcional por usuario (null = todos los usuarios)
     * @param startDate filtro opcional de fecha inicial
     * @param endDate filtro opcional de fecha final
     * @return información del ticket promedio
     */
    @GetMapping("/sales/average-ticket")
    public ResponseEntity<AverageTicketResponseDTO> getAverageTicket(

            @RequestParam(required = false) Long userId,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {


        AverageTicketResponseDTO response =
                iStatisticsService.getAverageTicket(
                        userId,
                        startDate,
                        endDate
                );

        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene las horas pico de ventas basadas en los filtros seleccionados.
     *
     * <p>
     * Este endpoint proporciona:
     * <ul>
     *   <li>La hora con mayor generación de ingresos</li>
     *   <li>La hora con mayor número de ventas</li>
     * </ul>
     * </p>
     *
     * <p>
     * Todos los valores se calculan únicamente con las ventas
     * que coinciden con los filtros proporcionados.
     * </p>
     *
     * <p>
     * Si no se proporcionan filtros:
     * <ul>
     *   <li>El usuario por defecto es "Todos los usuarios"</li>
     *   <li>El rango de fechas por defecto es la fecha actual</li>
     * </ul>
     * </p>
     *
     * <p>
     * Si no existen ventas que coincidan, ambos valores pueden retornar {@code null},
     * indicando que no hay datos de horas pico disponibles.
     * </p>
     *
     * @param userId filtro opcional por usuario (null = todos los usuarios)
     * @param startDate filtro opcional de fecha inicial
     * @param endDate filtro opcional de fecha final
     * @return estadísticas de horas pico de ventas
     */
    @GetMapping("/sales/peak-hours")
    public ResponseEntity<PeakHoursResponseDTO> getPeakHours(

            @RequestParam(required = false) Long userId,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {

        PeakHoursResponseDTO response =
                iStatisticsService.getPeakHours(
                        userId,
                        startDate,
                        endDate
                );

        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene estadísticas de ventas en serie temporal basadas en los filtros seleccionados.
     *
     * <p>
     * Este endpoint proporciona datos estadísticos listos para gráficos, incluyendo:
     * <ul>
     *   <li>Evolución de ingresos totales en el tiempo</li>
     *   <li>Evolución del número total de ventas en el tiempo</li>
     * </ul>
     * </p>
     *
     * <p>
     * Todos los valores se calculan únicamente con las ventas
     * que coinciden con los filtros proporcionados.
     * </p>
     *
     * <p>
     * La granularidad de la agregación temporal se ajusta automáticamente
     * según el rango de fechas seleccionado:
     * <ul>
     *   <li>HORA → rangos de un solo día</li>
     *   <li>DÍA → rangos de hasta 31 días</li>
     *   <li>MES → rangos de hasta 365 días</li>
     *   <li>AÑO → rangos mayores a 365 días</li>
     * </ul>
     * </p>
     *
     * <p>
     * Si no se proporcionan filtros:
     * <ul>
     *   <li>El usuario por defecto es "Todos los usuarios"</li>
     *   <li>El rango de fechas por defecto es la fecha actual</li>
     * </ul>
     * </p>
     *
     * <p>
     * Si no existen ventas que coincidan, ambas series pueden devolver listas vacías.
     * </p>
     *
     * @param userId filtro opcional por usuario (null = todos los usuarios)
     * @param startDate filtro opcional de fecha inicial
     * @param endDate filtro opcional de fecha final
     * @return estadísticas de ventas en serie temporal
     */
    @GetMapping("/sales/time-series")
    public ResponseEntity<SalesTimeSeriesResponseDTO> getSalesTimeSeries(

            @RequestParam(required = false) Long userId,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {

        SalesTimeSeriesResponseDTO response =
                iStatisticsService.getSalesTimeSeries(
                        userId,
                        startDate,
                        endDate
                );

        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene estadísticas de productos con mejor rendimiento basadas en los filtros seleccionados.
     *
     * <p>
     * Este endpoint proporciona:
     * <ul>
     *   <li>Top 10 productos por cantidad vendida</li>
     *   <li>Top 10 productos por ingresos generados</li>
     * </ul>
     * </p>
     *
     * <p>
     * Todos los valores se calculan únicamente con las ventas
     * que coinciden con los filtros proporcionados.
     * </p>
     *
     * <p>
     * Si no se proporcionan filtros:
     * <ul>
     *   <li>El usuario por defecto es "Todos los usuarios"</li>
     *   <li>El rango de fechas por defecto es la fecha actual</li>
     * </ul>
     * </p>
     *
     * <p>
     * Si no existen ventas que coincidan, ambas listas pueden devolverse vacías.
     * </p>
     *
     * @param userId filtro opcional por usuario (null = todos los usuarios)
     * @param startDate filtro opcional de fecha inicial
     * @param endDate filtro opcional de fecha final
     * @return estadísticas de productos con mejor rendimiento
     */
    @GetMapping("/products/top")
    public ResponseEntity<TopProductsResponseDTO> getTopProducts(

            @RequestParam(required = false) Long userId,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {

        TopProductsResponseDTO response =
                iStatisticsService.getTopProducts(
                        userId,
                        startDate,
                        endDate
                );

        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene un ranking paginado de productos vendidos
     * basado en los filtros seleccionados.
     *
     * <p>
     * Este endpoint devuelve los productos que tuvieron actividad de ventas
     * dentro del rango de fechas seleccionado, incluyendo:
     * <ul>
     *   <li>Código del producto</li>
     *   <li>Nombre del producto</li>
     *   <li>Cantidad total vendida</li>
     *   <li>Ingresos totales generados</li>
     * </ul>
     * </p>
     *
     * <p>
     * Los resultados permiten ranking dinámico utilizando:
     * <ul>
     *   <li>Métrica:
     *      <ul>
     *          <li>Cantidad vendida</li>
     *          <li>Ingresos generados</li>
     *      </ul>
     *   </li>
     *   <li>Orden:
     *      <ul>
     *          <li>Más vendidos → menos vendidos</li>
     *          <li>Menos vendidos → más vendidos</li>
     *      </ul>
     *   </li>
     * </ul>
     * </p>
     *
     * <p>
     * Comportamiento por defecto:
     * <ul>
     *   <li>Usuario → Todos los usuarios</li>
     *   <li>Rango de fechas → Fecha actual</li>
     *   <li>Métrica → Ingresos generados</li>
     *   <li>Orden → Más vendidos → menos vendidos</li>
     *   <li>Tamaño de página → 20</li>
     * </ul>
     * </p>
     *
     * <p>
     * Si no existen productos que coincidan, se devuelve una página vacía.
     * </p>
     *
     * @param userId filtro opcional por usuario
     * @param startDate filtro opcional de fecha inicial
     * @param endDate filtro opcional de fecha final
     * @param metric métrica de ranking
     * @param order orden del ranking
     * @param page número de página solicitado
     * @param size tamaño de página solicitado
     * @return ranking paginado de productos vendidos
     */
    @GetMapping("/products/sold")
    public ResponseEntity<PageResponseDTO<SoldProductDTO>>
    getSoldProducts(

            @RequestParam(required = false)
            Long userId,

            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate endDate,

            @RequestParam(
                    defaultValue = "REVENUE_GENERATED"
            )
            ProductRankingMetric metric,

            @RequestParam(
                    defaultValue = "MOST_TO_LEAST"
            )
            ProductQuantityOrderType order,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "20")
            int size
    ) {

        PageResponseDTO<SoldProductDTO> response =
                iStatisticsService.getSoldProductsRanking(
                        userId,
                        startDate,
                        endDate,
                        metric,
                        order,
                        page,
                        size
                );

        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene una lista paginada de productos sin ventas
     * basada en los filtros seleccionados.
     *
     * <p>
     * Este endpoint devuelve los productos que no tuvieron actividad de ventas
     * dentro del rango de fechas seleccionado, incluyendo:
     * <ul>
     *     <li>Código del producto</li>
     *     <li>Nombre del producto</li>
     * </ul>
     * </p>
     *
     * <p>
     * Los productos se consideran no vendidos únicamente si no tienen
     * registros de ventas que coincidan con los filtros seleccionados.
     * </p>
     *
     * <p>
     * Comportamiento por defecto:
     * <ul>
     *     <li>Usuario → Todos los usuarios</li>
     *     <li>Rango de fechas → Fecha actual</li>
     *     <li>Tamaño de página → 20</li>
     * </ul>
     * </p>
     *
     * <p>
     * Si no existen productos que coincidan, se devuelve una página vacía.
     * </p>
     *
     * @param userId filtro opcional por usuario
     * @param startDate filtro opcional de fecha inicial
     * @param endDate filtro opcional de fecha final
     * @param page número de página solicitado
     * @param size tamaño de página solicitado
     * @return lista paginada de productos sin ventas
     */
    @GetMapping("/products/unsold")
    public ResponseEntity<PageResponseDTO<UnsoldProductDTO>>
    getUnsoldProducts(

            @RequestParam(required = false)
            Long userId,

            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate endDate,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "20")
            int size
    ) {

        PageResponseDTO<UnsoldProductDTO> response =
                iStatisticsService.getUnsoldProducts(
                        userId,
                        startDate,
                        endDate,
                        page,
                        size
                );

        return ResponseEntity.ok(response);
    }

    /**
     * Genera un reporte PDF descargable que contiene
     * estadísticas de ventas e información de rendimiento de productos.
     *
     * <p>
     * Este endpoint genera un reporte PDF utilizando los mismos filtros
     * y reglas de negocio definidas en el módulo de estadísticas.
     * </p>
     *
     * <p>
     * El reporte puede incluir las siguientes secciones:
     * <ul>
     *     <li>Información de ventas</li>
     *     <li>Información de productos</li>
     * </ul>
     * </p>
     *
     * <p>
     * La información de ventas puede incluir:
     * <ul>
     *     <li>Ingresos totales</li>
     *     <li>Número total de ventas</li>
     *     <li>Valor promedio del ticket</li>
     *     <li>Hora pico de ingresos</li>
     *     <li>Hora pico de ventas</li>
     *     <li>Tabla de ingresos en el tiempo</li>
     *     <li>Tabla de ventas en el tiempo</li>
     * </ul>
     * </p>
     *
     * <p>
     * La información de productos puede incluir:
     * <ul>
     *     <li>Top productos por cantidad vendida</li>
     *     <li>Top productos por ingresos generados</li>
     *     <li>Listado detallado de productos vendidos</li>
     *     <li>Listado detallado de productos no vendidos</li>
     * </ul>
     * </p>
     *
     * <p>
     * El reporte generado siempre incluye:
     * <ul>
     *     <li>Título del reporte</li>
     *     <li>Fecha y hora de generación</li>
     *     <li>Usuario seleccionado</li>
     *     <li>Rango de fechas seleccionado</li>
     * </ul>
     * </p>
     *
     * <p>
     * El usuario puede configurar:
     * <ul>
     *     <li>Secciones incluidas en el reporte</li>
     *     <li>Cantidad de productos mostrados en listados detallados</li>
     *     <li>Métrica del ranking de productos</li>
     *     <li>Orden del ranking de productos</li>
     * </ul>
     * </p>
     *
     * <p>
     * Si no existen datos estadísticos que coincidan con los filtros seleccionados,
     * el reporte no se genera.
     * </p>
     *
     * @param request configuración para la generación del PDF
     * @return archivo PDF generado
     */
    @PostMapping(
            value = "/report/pdf",
            produces = MediaType.APPLICATION_PDF_VALUE
    )
    public ResponseEntity<byte[]> generateStatisticsPdf(
            @RequestBody StatisticsPdfRequestDTO request
    ) {

        byte[] pdf =
                iStatisticsService.generatePdf(request);

        String fileName =
                "reporte-estadisticas-ventas-"
                        + request.getStartDate().format(DATE_FORMATTER)
                        + "-a-"
                        + request.getEndDate().format(DATE_FORMATTER)
                        + ".pdf";

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=" + fileName
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}