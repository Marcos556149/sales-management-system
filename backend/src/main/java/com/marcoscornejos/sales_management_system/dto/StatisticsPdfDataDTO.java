package com.marcoscornejos.sales_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO que contiene toda la información necesaria
 * para generar un reporte PDF de estadísticas de ventas.
 *
 * <p>
 * Este DTO actúa como una estructura de datos intermedia
 * entre la obtención de estadísticas y la generación del PDF.
 * </p>
 *
 * <p>
 * Toda la información del reporte se encuentra pre-calculada y
 * preparada antes de la creación del documento PDF, permitiendo
 * que la capa de generación del PDF se enfoque exclusivamente
 * en el renderizado del documento.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsPdfDataDTO {

    /**
     * Título del reporte mostrado en el PDF.
     */
    private String reportTitle;

    /**
     * Fecha y hora en la que se generó el reporte.
     */
    private LocalDateTime generationDateTime;

    /**
     * Usuario seleccionado mostrado en el reporte.
     *
     * <p>
     * Valores posibles:
     * <ul>
     *     <li>"Todos los usuarios"</li>
     *     <li>Nombre de un usuario específico</li>
     * </ul>
     * </p>
     */
    private String selectedUser;

    /**
     * Fecha de inicio seleccionada para el reporte.
     */
    private LocalDate startDate;

    /**
     * Fecha de fin seleccionada para el reporte.
     */
    private LocalDate endDate;

    /*
     * Secciones incluidas
     */

    /**
     * Indica si la sección de información de ventas
     * está incluida en el reporte.
     */
    private boolean includeSalesInformation;

    /**
     * Indica si la sección de información de productos
     * está incluida en el reporte.
     */
    private boolean includeProductInformation;

    /*
     * Información de ventas
     */

    /**
     * Estadísticas de ingresos totales.
     */
    private TotalRevenueResponseDTO totalRevenue;

    /**
     * Estadísticas de ventas totales.
     */
    private TotalSalesResponseDTO totalSales;

    /**
     * Estadísticas del ticket promedio.
     */
    private AverageTicketResponseDTO averageTicket;

    /**
     * Estadísticas de la hora pico de ventas.
     */
    private PeakHoursResponseDTO peakHours;

    /**
     * Serie temporal de estadísticas incluida en
     * la sección de información de ventas del reporte.
     */
    private SalesTimeSeriesResponseDTO salesTimeSeries;

    /*
     * Información de productos
     */

    /**
     * Estadísticas de productos destacados utilizadas para generar:
     * <ul>
     *     <li>Productos con mayor cantidad vendida</li>
     *     <li>Productos con mayores ingresos generados</li>
     * </ul>
     */
    private TopProductsResponseDTO topProducts;

    /*
     * Productos vendidos
     */

    /**
     * Número total de productos vendidos que coinciden
     * con los filtros seleccionados.
     */
    private Long totalSoldProducts;

    /**
     * Número de productos vendidos incluidos
     * en el reporte.
     */
    private Integer includedSoldProducts;

    /**
     * Lista de productos vendidos incluida
     * en el reporte.
     */
    private List<SoldProductDTO> soldProducts;

    /*
     * Productos no vendidos
     */

    /**
     * Número total de productos no vendidos que coinciden
     * con los filtros seleccionados.
     */
    private Long totalUnsoldProducts;

    /**
     * Número de productos no vendidos incluidos
     * en el reporte.
     */
    private Integer includedUnsoldProducts;

    /**
     * Lista de productos no vendidos incluida
     * en el reporte.
     */
    private List<UnsoldProductDTO> unsoldProducts;

    /**
     * Métrica de ranking legible para humanos mostrada
     * en la subsección de ranking de productos vendidos.
     */
    private String soldProductsMetric;

    /**
     * Orden de ranking legible para humanos mostrado
     * en la subsección de ranking de productos vendidos.
     */
    private String soldProductsOrder;
}