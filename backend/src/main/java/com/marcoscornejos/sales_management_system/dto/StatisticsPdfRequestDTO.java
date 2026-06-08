package com.marcoscornejos.sales_management_system.dto;

import com.marcoscornejos.sales_management_system.model.ProductQuantityOrderType;
import com.marcoscornejos.sales_management_system.model.ProductRankingMetric;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Solicitud utilizada para generar un reporte PDF de estadísticas de ventas.
 *
 * <p>
 * Esta solicitud contiene:
 * <ul>
 *     <li>Filtros globales de estadísticas</li>
 *     <li>Selección de secciones del reporte</li>
 *     <li>Configuración de ranking de productos</li>
 *     <li>Límites de listas detalladas de productos</li>
 * </ul>
 * </p>
 *
 * <p>
 * Los filtros proporcionados determinan el conjunto de datos utilizado para generar
 * el reporte. Todas las secciones incluidas se calculan exclusivamente a partir
 * de las ventas y productos que coinciden con estos filtros.
 * </p>
 *
 * <p>
 * Comportamiento por defecto:
 * <ul>
 *     <li>Usuario → Todos los usuarios</li>
 *     <li>Rango de fechas → Fecha actual</li>
 *     <li>Sección de información de ventas → incluida</li>
 *     <li>Sección de información de productos → incluida</li>
 *     <li>Métrica de ranking → Ingresos generados</li>
 *     <li>Orden de ranking → Mayor a menor</li>
 *     <li>Límite de productos vendidos → 20</li>
 *     <li>Límite de productos no vendidos → 20</li>
 * </ul>
 * </p>
 *
 * <p>
 * Al menos una sección del reporte debe estar seleccionada.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsPdfRequestDTO {

    /**
     * Filtro opcional por usuario.
     *
     * <p>
     * Cuando es null, las estadísticas se generan utilizando
     * las ventas de todos los usuarios.
     * </p>
     */
    private Long userId;

    /**
     * Filtro opcional de fecha de inicio.
     *
     * <p>
     * Define el inicio del rango de fechas
     * utilizado para calcular las estadísticas del reporte.
     * </p>
     */
    private LocalDate startDate;

    /**
     * Filtro opcional de fecha de fin.
     *
     * <p>
     * Define el final del rango de fechas
     * utilizado para calcular las estadísticas del reporte.
     * </p>
     */
    private LocalDate endDate;

    /**
     * Indica si la sección de información de ventas
     * debe incluirse en el reporte generado.
     *
     * <p>
     * Valor por defecto: true.
     * </p>
     */
    private Boolean includeSalesInformation;

    /**
     * Indica si la sección de información de productos
     * debe incluirse en el reporte generado.
     *
     * <p>
     * Valor por defecto: true.
     * </p>
     */
    private Boolean includeProductInformation;

    /**
     * Métrica utilizada para ordenar productos vendidos
     * en la lista de ranking de productos.
     *
     * <p>
     * Valores disponibles:
     * <ul>
     *     <li>QUANTITY_SOLD</li>
     *     <li>REVENUE_GENERATED</li>
     * </ul>
     * </p>
     *
     * <p>
     * Valor por defecto: REVENUE_GENERATED.
     * </p>
     */
    private ProductRankingMetric metric;

    /**
     * Orden aplicado a la lista de ranking de productos.
     *
     * <p>
     * Valores disponibles:
     * <ul>
     *     <li>MOST_TO_LEAST</li>
     *     <li>LEAST_TO_MOST</li>
     * </ul>
     * </p>
     *
     * <p>
     * Valor por defecto: MOST_TO_LEAST.
     * </p>
     */
    private ProductQuantityOrderType order;

    /**
     * Cantidad máxima de productos incluidos
     * en la lista de productos vendidos.
     *
     * <p>
     * Valores permitidos:
     * <ul>
     *     <li>10</li>
     *     <li>20</li>
     *     <li>50</li>
     *     <li>100</li>
     * </ul>
     * </p>
     *
     * <p>
     * Valor por defecto: 20.
     * </p>
     */
    private Integer soldProductsLimit;

    /**
     * Cantidad máxima de productos incluidos
     * en la lista de productos no vendidos.
     *
     * <p>
     * Valores permitidos:
     * <ul>
     *     <li>10</li>
     *     <li>20</li>
     *     <li>50</li>
     *     <li>100</li>
     * </ul>
     * </p>
     *
     * <p>
     * Valor por defecto: 20.
     * </p>
     */
    private Integer unsoldProductsLimit;

}
