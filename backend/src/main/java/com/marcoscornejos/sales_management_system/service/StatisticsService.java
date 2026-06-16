package com.marcoscornejos.sales_management_system.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.marcoscornejos.sales_management_system.dto.*;
import com.marcoscornejos.sales_management_system.exception.*;
import com.marcoscornejos.sales_management_system.mapper.IPageResponseMapper;
import com.marcoscornejos.sales_management_system.model.ProductQuantityOrderType;
import com.marcoscornejos.sales_management_system.model.ProductRankingMetric;
import com.marcoscornejos.sales_management_system.model.StatisticsGranularity;
import com.marcoscornejos.sales_management_system.model.User;
import com.marcoscornejos.sales_management_system.projection.SoldProductProjection;
import com.marcoscornejos.sales_management_system.projection.TimeSeriesProjection;
import com.marcoscornejos.sales_management_system.projection.UnsoldProductProjection;
import com.marcoscornejos.sales_management_system.repository.IProductRepository;
import com.marcoscornejos.sales_management_system.repository.ISaleRepository;
import com.marcoscornejos.sales_management_system.repository.IUserRepository;
import com.marcoscornejos.sales_management_system.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class StatisticsService implements IStatisticsService{

    private final ISaleRepository iSaleRepository;
    private final IProductRepository iProductRepository;
    private final IUserRepository iUserRepository;
    private final IPageResponseMapper iPageResponseMapper;
    private static final Set<Integer> ALLOWED_PRODUCT_LIMITS_REPORT_PDF =
            Set.of(10, 20, 50, 100);

    /**
     * Aplica el comportamiento predeterminado al rango de fechas proporcionado.
     *
     * <p>
     * Si no se proporcionan fechas, tanto la fecha de inicio como la fecha de fin
     * se establecen con la fecha actual.
     * </p>
     *
     * @param startDate fecha de inicio solicitada
     * @param endDate fecha de fin solicitada
     * @return rango de fechas normalizado
     */
    private LocalDate[] normalizeDateRange(
            LocalDate startDate,
            LocalDate endDate
    ) {

        // Aplicar valores predeterminados cuando no se proporciona un rango
        if (startDate == null && endDate == null) {
            LocalDate today = LocalDate.now();

            startDate = today;
            endDate = today;
        }

        // Validar rango incompleto
        if (startDate == null || endDate == null) {
            throw new InvalidStatisticsFilterException(
                    "Se requiere un rango de fechas válido (fecha de inicio y fecha de fin)"
            );
        }

        // Validar orden cronológico
        if (endDate.isBefore(startDate)) {
            throw new InvalidStatisticsFilterException(
                    "La fecha de fin no puede ser anterior a la fecha de inicio",
                    "endDate"
            );
        }

        return new LocalDate[]{ startDate, endDate };
    }

    /**
     * Determina la granularidad de agregación más adecuada para los gráficos
     * según el rango de fechas seleccionado.
     *
     * <p>
     * La granularidad se ajusta automáticamente para mejorar la legibilidad
     * de los gráficos y evitar una cantidad excesiva de puntos de datos en
     * rangos de fechas amplios.
     * </p>
     *
     * <p>
     * Reglas de granularidad:
     * <ul>
     *   <li>HOUR → rangos de un solo día</li>
     *   <li>DAY → rangos de hasta 31 días</li>
     *   <li>MONTH → rangos de hasta 365 días</li>
     *   <li>YEAR → rangos superiores a 365 días</li>
     * </ul>
     * </p>
     *
     * @param startDate fecha de inicio normalizada
     * @param endDate fecha de fin normalizada
     * @return granularidad estadística calculada
     */
    private StatisticsGranularity determineGranularity(
            LocalDate startDate,
            LocalDate endDate
    ) {

        long days =
                ChronoUnit.DAYS.between(startDate, endDate) + 1;

        // Un solo día → granularidad por hora
        if (days == 1) {
            return StatisticsGranularity.HOUR;
        }

        // Rangos cortos → granularidad diaria
        if (days <= 31) {
            return StatisticsGranularity.DAY;
        }

        // Rangos medios → granularidad mensual
        if (days <= 365) {
            return StatisticsGranularity.MONTH;
        }

        // Rangos largos → granularidad anual
        return StatisticsGranularity.YEAR;
    }

    /**
     * Obtiene los datos del gráfico de ingresos agregados según la
     * granularidad seleccionada.
     *
     * <p>
     * Delega la consulta al método correspondiente del repositorio según
     * la granularidad estadística calculada.
     * </p>
     *
     * <p>
     * Las consultas del repositorio devuelven proyecciones estadísticas
     * ligeras, que posteriormente son transformadas en objetos
     * {@link TimeSeriesPointDTO} utilizados por la capa de respuesta de la API.
     * </p>
     *
     * <p>
     * Cuando la granularidad es {@link StatisticsGranularity#HOUR},
     * las etiquetas de hora se transforman en rangos horarios explícitos
     * (por ejemplo, "18:00 - 18:59") para mejorar la legibilidad del gráfico
     * y representar correctamente el intervalo de tiempo agregado.
     * </p>
     *
     * @param userId identificador del usuario resuelto (null = todos los usuarios)
     * @param startDate fecha de inicio normalizada
     * @param endDate fecha de fin normalizada
     * @param granularity granularidad de agregación del gráfico
     * @return datos de la serie temporal de ingresos
     */
    private List<TimeSeriesPointDTO> getRevenueOverTime(
            Long userId,
            LocalDate startDate,
            LocalDate endDate,
            StatisticsGranularity granularity
    ) {

        List<TimeSeriesProjection> projections =
                switch (granularity) {

                    case HOUR -> iSaleRepository.getRevenuePerHour(
                            userId,
                            startDate,
                            endDate
                    );

                    case DAY -> iSaleRepository.getRevenuePerDay(
                            userId,
                            startDate,
                            endDate
                    );

                    case MONTH -> iSaleRepository.getRevenuePerMonth(
                            userId,
                            startDate,
                            endDate
                    );

                    case YEAR -> iSaleRepository.getRevenuePerYear(
                            userId,
                            startDate,
                            endDate
                    );
                };

        return projections.stream()
                .map(projection -> {

                    String label = projection.getLabel();

                    if (granularity == StatisticsGranularity.HOUR) {

                        label = formatHourRange(
                                Integer.parseInt(label)
                        );
                    }

                    return new TimeSeriesPointDTO(
                            label,
                            projection.getValue()
                    );
                })
                .toList();
    }

    /**
     * Obtiene los datos del gráfico de cantidad de ventas agregados según la
     * granularidad seleccionada.
     *
     * <p>
     * Delega la consulta al método correspondiente del repositorio según
     * la granularidad estadística calculada.
     * </p>
     *
     * <p>
     * Las consultas del repositorio devuelven proyecciones estadísticas
     * ligeras, que posteriormente son transformadas en objetos
     * {@link TimeSeriesPointDTO} utilizados por la capa de respuesta de la API.
     * </p>
     *
     * <p>
     * Cuando la granularidad es {@link StatisticsGranularity#HOUR},
     * las etiquetas de hora se transforman en rangos horarios explícitos
     * (por ejemplo, "18:00 - 18:59") para mejorar la legibilidad del gráfico
     * y representar correctamente el intervalo de tiempo agregado.
     * </p>
     *
     * @param userId identificador del usuario resuelto (null = todos los usuarios)
     * @param startDate fecha de inicio normalizada
     * @param endDate fecha de fin normalizada
     * @param granularity granularidad de agregación del gráfico
     * @return datos de la serie temporal de cantidad de ventas
     */
    private List<TimeSeriesPointDTO> getSalesOverTime(
            Long userId,
            LocalDate startDate,
            LocalDate endDate,
            StatisticsGranularity granularity
    ) {

        List<TimeSeriesProjection> projections =
                switch (granularity) {

                    case HOUR -> iSaleRepository.getSalesPerHour(
                            userId,
                            startDate,
                            endDate
                    );

                    case DAY -> iSaleRepository.getSalesPerDay(
                            userId,
                            startDate,
                            endDate
                    );

                    case MONTH -> iSaleRepository.getSalesPerMonth(
                            userId,
                            startDate,
                            endDate
                    );

                    case YEAR -> iSaleRepository.getSalesPerYear(
                            userId,
                            startDate,
                            endDate
                    );
                };

        return projections.stream()
                .map(projection -> {

                    String label = projection.getLabel();

                    if (granularity == StatisticsGranularity.HOUR) {

                        label = formatHourRange(
                                Integer.parseInt(label)
                        );
                    }

                    return new TimeSeriesPointDTO(
                            label,
                            projection.getValue()
                    );
                })
                .toList();
    }

    /**
     * Obtiene los usuarios disponibles para el filtrado de estadísticas.
     *
     * <p>
     * Los usuarios se recuperan dinámicamente desde la base de datos y se
     * transforman en DTOs ligeros destinados a los selectores de filtros
     * del frontend.
     * </p>
     *
     * <p>
     * Cada DTO contiene:
     * <ul>
     *   <li>Identificador del usuario</li>
     *   <li>Nombre de usuario</li>
     * </ul>
     * </p>
     *
     * <p>
     * Si no existen usuarios, se devuelve una lista vacía.
     * </p>
     *
     * @return lista de usuarios disponibles para el filtrado de estadísticas
     */
    @Override
    public List<UserFilterDTO> getStatisticsFilterUsers() {

        return iUserRepository.findAll()
                .stream()
                .map(user -> new UserFilterDTO(
                        user.getUserId(),
                        user.getUserName()
                ))
                .toList();
    }

    /**
     * Obtiene las opciones de filtrado disponibles para el ranking de productos.
     *
     * <p>
     * Las opciones de filtrado se generan dinámicamente a partir de las
     * enumeraciones de la aplicación para garantizar la consistencia entre
     * la lógica del backend y los selectores del frontend.
     * </p>
     *
     * <p>
     * La configuración devuelta incluye:
     * <ul>
     *   <li>Métricas del ranking de productos</li>
     *   <li>Opciones de ordenamiento del ranking de productos</li>
     * </ul>
     * </p>
     *
     * @return opciones de filtrado para el ranking de productos
     */
    @Override
    public ProductRankingFiltersResponseDTO getProductRankingFilters() {

        // Opciones de métricas
        List<EnumDTO> metricOptions =
                Arrays.stream(ProductRankingMetric.values())
                        .map(metric -> new EnumDTO(
                                metric.name(),
                                metric.getDisplayName()
                        ))
                        .toList();

        // Opciones de ordenamiento
        List<EnumDTO> orderOptions =
                Arrays.stream(ProductQuantityOrderType.values())
                        .map(order -> new EnumDTO(
                                order.name(),
                                order.getDisplayName()
                        ))
                        .toList();

        return new ProductRankingFiltersResponseDTO(
                metricOptions,
                orderOptions
        );
    }

    /**
     * Obtiene el total de ingresos según los filtros seleccionados.
     *
     * <p>
     * El total de ingresos se calcula como la suma de todos los importes
     * de venta que coinciden con los filtros proporcionados.
     * </p>
     *
     * <p>
     * Comportamiento de los filtros:
     * <ul>
     *   <li>Si userId es null, las estadísticas se calculan para todos los usuarios</li>
     *   <li>Si ambas fechas son null, se utiliza la fecha actual</li>
     *   <li>Si solo se proporciona una fecha, se lanza una excepción</li>
     * </ul>
     * </p>
     *
     * @param userId filtro opcional por usuario (null = todos los usuarios)
     * @param startDate filtro opcional de fecha de inicio
     * @param endDate filtro opcional de fecha de fin
     * @return respuesta con el total de ingresos
     */
    @Override
    public TotalRevenueResponseDTO getTotalRevenue(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    ) {

        // Normalizar rango de fechas
        LocalDate[] normalizedDates =
                normalizeDateRange(startDate, endDate);

        startDate = normalizedDates[0];
        endDate = normalizedDates[1];

        // Validar existencia del usuario
        if (userId != null && !iUserRepository.existsById(userId)) {
            throw new UserNotFoundException(
                    "El usuario seleccionado no es válido o no existe"
            );
        }

        // Calcular total de ingresos
        BigDecimal totalRevenue =
                iSaleRepository.sumRevenue(
                        userId,
                        startDate,
                        endDate
                );

        return new TotalRevenueResponseDTO(totalRevenue);
    }

    /**
     * Obtiene la cantidad total de ventas según los filtros seleccionados.
     *
     * <p>
     * La cantidad total de ventas se calcula como el conteo de todos los
     * registros de venta que coinciden con los filtros proporcionados.
     * </p>
     *
     * <p>
     * Comportamiento de los filtros:
     * <ul>
     *   <li>Si userId es null, las estadísticas se calculan para todos los usuarios</li>
     *   <li>Si ambas fechas son null, se utiliza la fecha actual</li>
     *   <li>Si solo se proporciona una fecha, se lanza una excepción</li>
     * </ul>
     * </p>
     *
     * @param userId filtro opcional por usuario (null = todos los usuarios)
     * @param startDate filtro opcional de fecha de inicio
     * @param endDate filtro opcional de fecha de fin
     * @return respuesta con la cantidad total de ventas
     */
    @Override
    public TotalSalesResponseDTO getTotalSales(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    ) {

        // Normalizar rango de fechas
        LocalDate[] normalizedDates =
                normalizeDateRange(startDate, endDate);

        startDate = normalizedDates[0];
        endDate = normalizedDates[1];

        // Validar existencia del usuario
        if (userId != null && !iUserRepository.existsById(userId)) {
            throw new UserNotFoundException(
                    "El usuario seleccionado no es válido o no existe"
            );
        }

        // Calcular cantidad total de ventas
        Long totalSales =
                iSaleRepository.countSales(
                        userId,
                        startDate,
                        endDate
                );

        return new TotalSalesResponseDTO(totalSales);
    }

    /**
     * Obtiene el valor promedio del ticket según los filtros seleccionados.
     *
     * <p>
     * El valor promedio del ticket se calcula como:
     * total de ingresos dividido por la cantidad total de ventas.
     * </p>
     *
     * <p>
     * Comportamiento de los filtros:
     * <ul>
     *   <li>Si userId es null, las estadísticas se calculan para todos los usuarios</li>
     *   <li>Si ambas fechas son null, se utiliza la fecha actual</li>
     *   <li>Si solo se proporciona una fecha, se lanza una excepción</li>
     * </ul>
     * </p>
     *
     * <p>
     * Si no existen ventas que coincidan con los filtros,
     * el valor promedio del ticket será {@link BigDecimal#ZERO}.
     * </p>
     *
     * @param userId filtro opcional por usuario (null = todos los usuarios)
     * @param startDate filtro opcional de fecha de inicio
     * @param endDate filtro opcional de fecha de fin
     * @return respuesta con el valor promedio del ticket
     */
    @Override
    public AverageTicketResponseDTO getAverageTicket(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    ) {

        // Normalizar rango de fechas
        LocalDate[] normalizedDates =
                normalizeDateRange(startDate, endDate);

        startDate = normalizedDates[0];
        endDate = normalizedDates[1];

        // Validar existencia del usuario
        if (userId != null && !iUserRepository.existsById(userId)) {
            throw new UserNotFoundException(
                    "El usuario seleccionado no es válido o no existe"
            );
        }

        // Obtener valores agregados
        BigDecimal totalRevenue =
                iSaleRepository.sumRevenue(
                        userId,
                        startDate,
                        endDate
                );

        Long totalSales =
                iSaleRepository.countSales(
                        userId,
                        startDate,
                        endDate
                );

        // Calcular valor promedio del ticket
        BigDecimal averageTicket =
                totalSales == 0
                        ? BigDecimal.ZERO
                        : totalRevenue.divide(
                        BigDecimal.valueOf(totalSales),
                        2,
                        RoundingMode.HALF_UP
                );

        return new AverageTicketResponseDTO(
                averageTicket
        );
    }

    /**
     * Obtiene las estadísticas de las horas pico de ventas según los filtros seleccionados.
     *
     * <p>
     * Este método calcula:
     * <ul>
     *   <li>La franja horaria con mayores ingresos generados</li>
     *   <li>La franja horaria con la mayor cantidad de ventas</li>
     * </ul>
     * </p>
     *
     * <p>
     * Los valores devueltos se formatean como rangos horarios
     * utilizando el siguiente patrón:
     * <pre>
     * HH:00 - HH:59
     * </pre>
     * </p>
     *
     * <p>
     * Comportamiento de los filtros:
     * <ul>
     *   <li>Si userId es null, las estadísticas se calculan para todos los usuarios</li>
     *   <li>Si ambas fechas son null, se utiliza la fecha actual</li>
     *   <li>Si solo se proporciona una fecha, se lanza una excepción</li>
     * </ul>
     * </p>
     *
     * <p>
     * Si no existen ventas que coincidan con los filtros,
     * ambos valores pueden devolver {@code null}.
     * </p>
     *
     * @param userId filtro opcional por usuario (null = todos los usuarios)
     * @param startDate filtro opcional de fecha de inicio
     * @param endDate filtro opcional de fecha de fin
     * @return estadísticas de horas pico de ventas
     */
    @Override
    public PeakHoursResponseDTO getPeakHours(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    ) {

        // Normalizar rango de fechas
        LocalDate[] normalizedDates =
                normalizeDateRange(startDate, endDate);

        startDate = normalizedDates[0];
        endDate = normalizedDates[1];

        // Validar existencia del usuario
        if (userId != null && !iUserRepository.existsById(userId)) {
            throw new UserNotFoundException(
                    "El usuario seleccionado no es válido o no existe"
            );
        }

        // Obtener horas pico sin formato
        Integer revenueHour =
                iSaleRepository.findPeakRevenueHour(
                        userId,
                        startDate,
                        endDate
                );

        Integer salesHour =
                iSaleRepository.findPeakSalesHour(
                        userId,
                        startDate,
                        endDate
                );

        // Formatear rangos horarios
        String highestRevenueHour =
                formatHourRange(revenueHour);

        String highestSalesHour =
                formatHourRange(salesHour);

        return new PeakHoursResponseDTO(
                highestRevenueHour,
                highestSalesHour
        );
    }

    /**
     * Formatea una hora en un rango horario legible para el usuario.
     *
     * <p>
     * Ejemplo:
     * <ul>
     *   <li>18 → "18:00 - 18:59"</li>
     *   <li>9 → "09:00 - 09:59"</li>
     * </ul>
     * </p>
     *
     * @param hour valor de la hora (0–23)
     * @return rango horario formateado,
     *         o null si la hora es null
     */
    private String formatHourRange(Integer hour) {

        if (hour == null) {
            return null;
        }

        return String.format(
                "%02d:00 - %02d:59",
                hour,
                hour
        );
    }

    /**
     * Obtiene las estadísticas temporales de ventas según los filtros seleccionados.
     *
     * <p>
     * Este método calcula:
     * <ul>
     *   <li>La evolución de los ingresos a lo largo del tiempo</li>
     *   <li>La evolución de la cantidad de ventas a lo largo del tiempo</li>
     * </ul>
     * </p>
     *
     * <p>
     * La granularidad de agregación de las series temporales se determina
     * automáticamente según el rango de fechas seleccionado:
     * <ul>
     *   <li>HOUR → rangos de un solo día</li>
     *   <li>DAY → rangos de hasta 31 días</li>
     *   <li>MONTH → rangos de hasta 365 días</li>
     *   <li>YEAR → rangos superiores a 365 días</li>
     * </ul>
     * </p>
     *
     * <p>
     * Comportamiento de los filtros:
     * <ul>
     *   <li>Si userId es null, las estadísticas se calculan para todos los usuarios</li>
     *   <li>Si ambas fechas son null, se utiliza la fecha actual</li>
     *   <li>Si solo se proporciona una fecha, se lanza una excepción</li>
     * </ul>
     * </p>
     *
     * <p>
     * Si no existen ventas que coincidan con los filtros,
     * ambas series pueden devolver listas vacías.
     * </p>
     *
     * @param userId filtro opcional por usuario (null = todos los usuarios)
     * @param startDate filtro opcional de fecha de inicio
     * @param endDate filtro opcional de fecha de fin
     * @return estadísticas temporales de ventas
     */
    @Override
    public SalesTimeSeriesResponseDTO getSalesTimeSeries(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    ) {

        // Normalizar rango de fechas
        LocalDate[] normalizedDates =
                normalizeDateRange(startDate, endDate);

        startDate = normalizedDates[0];
        endDate = normalizedDates[1];

        // Validar existencia del usuario
        if (userId != null && !iUserRepository.existsById(userId)) {
            throw new UserNotFoundException(
                    "El usuario seleccionado no es válido o no existe"
            );
        }

        // Determinar granularidad del gráfico
        StatisticsGranularity granularity =
                determineGranularity(startDate, endDate);

        // Obtener serie temporal de ingresos
        List<TimeSeriesPointDTO> revenueOverTime =
                getRevenueOverTime(
                        userId,
                        startDate,
                        endDate,
                        granularity
                );

        // Obtener serie temporal de cantidad de ventas
        List<TimeSeriesPointDTO> salesOverTime =
                getSalesOverTime(
                        userId,
                        startDate,
                        endDate,
                        granularity
                );

        return new SalesTimeSeriesResponseDTO(
                revenueOverTime,
                salesOverTime
        );
    }

    /**
     * Formatea una etiqueta horaria de un gráfico en un rango horario explícito.
     *
     * <p>
     * Ejemplo:
     * <ul>
     *   <li>18 → "18:00 - 18:59"</li>
     *   <li>9 → "09:00 - 09:59"</li>
     * </ul>
     * </p>
     *
     * @param label etiqueta horaria sin formato
     * @return etiqueta de rango horario formateada
     */
    private String formatHourlyChartLabel(String label) {

        if (label == null) {
            return null;
        }

        int hour = Integer.parseInt(label);

        return String.format(
                "%02d:00 - %02d:59",
                hour,
                hour
        );
    }

    /**
     * Obtiene estadísticas de los productos con mejor desempeño según los filtros seleccionados.
     *
     * <p>
     * Este método calcula:
     * <ul>
     *   <li>Los 10 productos más vendidos según la cantidad vendida</li>
     *   <li>Los 10 productos con mayores ingresos generados</li>
     * </ul>
     * </p>
     *
     * <p>
     * Comportamiento de los filtros:
     * <ul>
     *   <li>Si userId es null, las estadísticas se calculan para todos los usuarios</li>
     *   <li>Si ambas fechas son null, se utiliza la fecha actual</li>
     *   <li>Si solo se proporciona una fecha, se lanza una excepción</li>
     * </ul>
     * </p>
     *
     * <p>
     * Las consultas del repositorio devuelven proyecciones ligeras de base de datos,
     * que posteriormente son convertidas a DTOs en la capa de servicio para mantener
     * la separación entre la capa de persistencia y la capa de API.
     * </p>
     *
     * <p>
     * Si no existen ventas que coincidan con los filtros,
     * ambas listas pueden devolver listas vacías.
     * </p>
     *
     * @param userId filtro opcional por usuario (null = todos los usuarios)
     * @param startDate filtro opcional de fecha de inicio
     * @param endDate filtro opcional de fecha de fin
     * @return estadísticas de los productos con mejor desempeño
     */
    @Override
    public TopProductsResponseDTO getTopProducts(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    ) {

        // Normalizar rango de fechas
        LocalDate[] normalizedDates =
                normalizeDateRange(startDate, endDate);

        startDate = normalizedDates[0];
        endDate = normalizedDates[1];

        // Validar existencia del usuario
        if (userId != null && !iUserRepository.existsById(userId)) {
            throw new UserNotFoundException(
                    "El usuario seleccionado no es válido o no existe"
            );
        }

        // Obtener los productos más vendidos por cantidad
        List<TopProductsByQuantityDTO> topProductsByQuantity =
                iProductRepository.findTopByQuantity(
                                userId,
                                startDate,
                                endDate
                        ).stream()
                        .map(product ->
                                new TopProductsByQuantityDTO(
                                        product.getProductCode(),
                                        product.getProductName(),
                                        product.getQuantitySold()
                                )
                        )
                        .toList();

        // Obtener los productos con mayores ingresos generados
        List<TopProductsByRevenueDTO> topProductsByRevenue =
                iProductRepository.findTopByRevenue(
                                userId,
                                startDate,
                                endDate
                        ).stream()
                        .map(product ->
                                new TopProductsByRevenueDTO(
                                        product.getProductCode(),
                                        product.getProductName(),
                                        normalizeRevenue(product.getRevenueGenerated())
                                )
                        )
                        .toList();

        return new TopProductsResponseDTO(
                topProductsByQuantity,
                topProductsByRevenue
        );
    }

    /**
     * Normaliza los valores de ingresos a una escala decimal fija.
     *
     * <p>
     * Este método garantiza que todos los valores monetarios devueltos por la API
     * se formateen de manera consistente con 2 decimales, independientemente de
     * la precisión interna generada por las operaciones de agregación de la base
     * de datos (por ejemplo, SUM o multiplicaciones de campos numéricos).
     * </p>
     *
     * <p>
     * Esto es especialmente importante para:
     * <ul>
     *   <li>Cálculos de ingresos realizados mediante consultas SQL agregadas</li>
     *   <li>Garantizar una salida JSON consistente para los gráficos del frontend</li>
     *   <li>Evitar artefactos de precisión decimal (por ejemplo, 123.4500000)</li>
     * </ul>
     * </p>
     *
     * <p>
     * Estrategia de redondeo utilizada: {@link RoundingMode#HALF_UP}
     * </p>
     *
     * @param value valor de ingresos obtenido del repositorio (puede tener una escala variable)
     * @return valor normalizado con escala 2 (por ejemplo, 123.45)
     */
    private BigDecimal normalizeRevenue(BigDecimal value) {


        if (value == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        return value.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Obtiene un ranking paginado de productos vendidos
     * según los filtros y criterios de clasificación seleccionados.
     *
     * <p>
     * Soporta:
     * <ul>
     *     <li>Filtrado por usuario</li>
     *     <li>Filtrado por rango de fechas</li>
     *     <li>Clasificación por cantidad vendida o ingresos generados</li>
     *     <li>Orden ascendente o descendente</li>
     *     <li>Paginación del lado del servidor</li>
     * </ul>
     * </p>
     *
     * <p>
     * Las consultas del repositorio devuelven proyecciones ligeras
     * que posteriormente son convertidas a DTOs en la capa de servicio
     * para mantener la separación entre la capa de persistencia
     * y la capa de API.
     * </p>
     *
     * @param userId filtro opcional por usuario
     * @param startDate fecha de inicio opcional
     * @param endDate fecha de fin opcional
     * @param metric métrica de clasificación
     * @param order orden de clasificación
     * @param page número de página solicitado
     * @param size tamaño de página solicitado
     * @return ranking paginado de productos vendidos
     */
    @Override
    public PageResponseDTO<SoldProductDTO> getSoldProductsRanking(
            Long userId,
            LocalDate startDate,
            LocalDate endDate,
            ProductRankingMetric metric,
            ProductQuantityOrderType order,
            int page,
            int size
    ) {

        // Validar parámetros de paginación
        if (page < 0) {
            throw new InvalidProductDataException(
                    "El índice de página no puede ser negativo",
                    "page"
            );
        }

        if (size <= 0) {
            throw new InvalidProductDataException(
                    "El tamaño de página debe ser mayor que cero",
                    "size"
            );
        }

        if (size > 100) {
            throw new InvalidProductDataException(
                    "El tamaño de página no puede superar los 100 elementos",
                    "size"
            );
        }

        // Normalizar rango de fechas
        LocalDate[] normalizedDates =
                normalizeDateRange(startDate, endDate);

        startDate = normalizedDates[0];
        endDate = normalizedDates[1];

        // Validar existencia del usuario
        if (
                userId != null
                        &&
                        !iUserRepository.existsById(userId)
        ) {
            throw new UserNotFoundException(
                    "El usuario seleccionado no es válido o no existe"
            );
        }

        Pageable pageable =
                PageRequest.of(
                        page,
                        size
                );

        Page<SoldProductProjection> rankingPage;

        // El repositorio decide únicamente la métrica.
        // El servicio decide la dirección del ordenamiento.
        if (order == ProductQuantityOrderType.MOST_TO_LEAST) {

            rankingPage =
                    iProductRepository.findRankingDesc(
                            userId,
                            startDate,
                            endDate,
                            metric.name(),
                            pageable
                    );

        } else {

            rankingPage =
                    iProductRepository.findRankingAsc(
                            userId,
                            startDate,
                            endDate,
                            metric.name(),
                            pageable
                    );
        }

        List<SoldProductDTO> ranking =
                rankingPage.getContent()
                        .stream()
                        .map(product ->
                                new SoldProductDTO(
                                        product.getProductCode(),
                                        product.getProductName(),
                                        product.getQuantitySold(),
                                        normalizeRevenue(
                                                product.getRevenueGenerated()
                                        )
                                )
                        )
                        .toList();

        return iPageResponseMapper.toPageResponseDTO(
                ranking,
                rankingPage.getNumber(),
                rankingPage.getSize(),
                rankingPage.getTotalPages(),
                rankingPage.getTotalElements(),
                null
        );
    }

    /**
     * Obtiene una lista paginada de productos
     * sin ventas dentro de los filtros seleccionados.
     *
     * <p>
     * Soporta:
     * <ul>
     *     <li>Filtrado por usuario</li>
     *     <li>Filtrado por rango de fechas</li>
     *     <li>Paginación del lado del servidor</li>
     * </ul>
     * </p>
     *
     * <p>
     * Un producto se considera sin ventas cuando no existen ventas
     * asociadas que coincidan con el rango de filtros seleccionado.
     * </p>
     *
     * <p>
     * Las consultas del repositorio devuelven proyecciones ligeras
     * que posteriormente son convertidas a DTOs en la capa de servicio
     * para mantener la separación entre la capa de persistencia
     * y la capa de API.
     * </p>
     *
     * @param userId filtro opcional por usuario
     * @param startDate fecha de inicio opcional
     * @param endDate fecha de fin opcional
     * @param page número de página solicitado
     * @param size tamaño de página solicitado
     * @return lista paginada de productos sin ventas
     */
    @Override
    public PageResponseDTO<UnsoldProductDTO> getUnsoldProducts(
            Long userId,
            LocalDate startDate,
            LocalDate endDate,
            int page,
            int size
    ) {

        // Validar parámetros de paginación
        if (page < 0) {
            throw new InvalidProductDataException(
                    "El índice de página no puede ser negativo",
                    "page"
            );
        }

        if (size <= 0) {
            throw new InvalidProductDataException(
                    "El tamaño de página debe ser mayor que cero",
                    "size"
            );
        }

        if (size > 100) {
            throw new InvalidProductDataException(
                    "El tamaño de página no puede superar los 100 elementos",
                    "size"
            );
        }

        // Normalizar rango de fechas
        LocalDate[] normalizedDates =
                normalizeDateRange(startDate, endDate);

        startDate = normalizedDates[0];
        endDate = normalizedDates[1];

        // Validar existencia del usuario
        if (
                userId != null
                        &&
                        !iUserRepository.existsById(userId)
        ) {
            throw new UserNotFoundException(
                    "El usuario seleccionado no es válido o no existe"
            );
        }

        // Construir configuración de paginación
        Pageable pageable =
                PageRequest.of(
                        page,
                        size
                );

        // Obtener productos sin ventas
        Page<UnsoldProductProjection> unsoldProductsPage =
                iProductRepository.findUnsoldProducts(
                        userId,
                        startDate,
                        endDate,
                        pageable
                );

        // Convertir proyección → DTO
        List<UnsoldProductDTO> unsoldProducts =
                unsoldProductsPage.getContent()
                        .stream()
                        .map(product ->
                                new UnsoldProductDTO(
                                        product.getProductCode(),
                                        product.getProductName()
                                )
                        )
                        .toList();

        return iPageResponseMapper.toPageResponseDTO(
                unsoldProducts,
                unsoldProductsPage.getNumber(),
                unsoldProductsPage.getSize(),
                unsoldProductsPage.getTotalPages(),
                unsoldProductsPage.getTotalElements(),
                null
        );
    }

    /**
     * Genera un informe PDF que contiene estadísticas de ventas
     * e información de productos.
     *
     * <p>
     * Este método orquesta el proceso completo de generación del informe,
     * incluyendo la validación de la solicitud, la verificación de
     * disponibilidad de datos, la obtención de la información requerida
     * y la creación del documento PDF.
     * </p>
     *
     * <p>
     * El contenido del informe generado está determinado por los filtros
     * seleccionados y la configuración del informe proporcionada
     * en la solicitud.
     * </p>
     *
     * <p>
     * Proceso de generación:
     * <ol>
     *     <li>Validar parámetros de la solicitud y reglas de negocio</li>
     *     <li>Verificar que existan datos estadísticos para los filtros seleccionados</li>
     *     <li>Obtener toda la información requerida para el informe</li>
     *     <li>Generar el documento PDF</li>
     * </ol>
     * </p>
     *
     * <p>
     * La generación del informe se cancela si no existen datos
     * estadísticos para los criterios seleccionados.
     * </p>
     *
     * @param request configuración de generación del PDF
     * @return documento PDF generado como arreglo de bytes
     */
    @Override
    public byte[] generatePdf(
            StatisticsPdfRequestDTO request
    ) {

        /*
         * Paso 1:
         * Validar parámetros de la solicitud y reglas de negocio.
         */
        validateAndNormalizePdfRequest(request);

        /*
         * Paso 2:
         * Validar disponibilidad de datos estadísticos.
         */
        TotalSalesResponseDTO totalSales =
                validateStatisticsDataAvailability(request);

        /*
         * Paso 3:
         * Obtener todas las estadísticas requeridas para el informe.
         */
        StatisticsPdfDataDTO reportData =
                buildPdfData(
                        request,
                        totalSales
                );

        /*
         * Paso 4:
         * Generar el documento PDF.
         */
        return generatePdfDocument(reportData);
    }

    /**
     * Valida y normaliza la solicitud de generación del PDF.
     *
     * <p>
     * Este método aplica las mismas reglas de filtrado y
     * normalización de fechas utilizadas por el módulo de estadísticas
     * para garantizar la consistencia entre las estadísticas mostradas
     * y los informes generados.
     * </p>
     *
     * <p>
     * La solicitud proporcionada se actualiza con valores
     * normalizados y configuraciones predeterminadas cuando es necesario.
     * </p>
     *
     * <p>
     * Reglas de validación:
     * <ul>
     *     <li>Si se especifica un usuario, este debe existir</li>
     *     <li>Debe seleccionarse al menos una sección del informe</li>
     *     <li>Los límites de productos deben ser uno de los valores permitidos:
     *         10, 20, 50 o 100</li>
     * </ul>
     * </p>
     *
     * <p>
     * La normalización del rango de fechas se delega a la lógica
     * compartida de normalización utilizada por el módulo de estadísticas.
     * </p>
     *
     * <p>
     * Valores predeterminados:
     * <ul>
     *     <li>Sección Información de Ventas → incluida</li>
     *     <li>Sección Información de Productos → incluida</li>
     *     <li>Métrica del ranking → Ingresos generados</li>
     *     <li>Orden del ranking → Más vendidos → menos vendidos</li>
     *     <li>Límite de productos vendidos → 20</li>
     *     <li>Límite de productos sin ventas → 20</li>
     * </ul>
     * </p>
     *
     * @param request configuración de generación del PDF
     */
    private void validateAndNormalizePdfRequest(
            StatisticsPdfRequestDTO request
    ) {

        // Aplicar selección predeterminada de secciones
        if (request.getIncludeSalesInformation() == null) {
            request.setIncludeSalesInformation(true);
        }

        if (request.getIncludeProductInformation() == null) {
            request.setIncludeProductInformation(true);
        }

        // Validar secciones seleccionadas
        if (!request.getIncludeSalesInformation()
                && !request.getIncludeProductInformation()) {

            throw new InvalidStatisticsFilterException(
                    "Debe seleccionar al menos una sección"
            );
        }

        // Validar existencia del usuario
        if (request.getUserId() != null
                && !iUserRepository.existsById(request.getUserId())) {

            throw new UserNotFoundException(
                    "El usuario seleccionado no es válido o no existe"
            );
        }

        // Normalizar rango de fechas
        LocalDate[] normalizedDates =
                normalizeDateRange(
                        request.getStartDate(),
                        request.getEndDate()
                );

        // Aplicar configuración predeterminada del ranking
        if (request.getMetric() == null) {
            request.setMetric(
                    ProductRankingMetric.REVENUE_GENERATED
            );
        }

        if (request.getOrder() == null) {
            request.setOrder(
                    ProductQuantityOrderType.MOST_TO_LEAST
            );
        }

        // Aplicar límites predeterminados de productos
        if (request.getSoldProductsLimit() == null) {
            request.setSoldProductsLimit(20);
        }

        if (request.getUnsoldProductsLimit() == null) {
            request.setUnsoldProductsLimit(20);
        }

        if (!ALLOWED_PRODUCT_LIMITS_REPORT_PDF.contains(
                request.getSoldProductsLimit())) {

            throw new InvalidStatisticsFilterException(
                    "El límite de productos vendidos no es válido"
            );
        }

        if (!ALLOWED_PRODUCT_LIMITS_REPORT_PDF.contains(
                request.getUnsoldProductsLimit())) {

            throw new InvalidStatisticsFilterException(
                    "El límite de productos no vendidos no es válido"
            );
        }

        request.setStartDate(normalizedDates[0]);
        request.setEndDate(normalizedDates[1]);

    }

    /**
     * Construye el modelo de datos completo requerido para generar el reporte PDF de estadísticas.
     *
     * <p>
     * Este método prepara toda la información necesaria para la generación del reporte,
     * aplicando los filtros seleccionados y respetando la configuración indicada en la solicitud.
     * </p>
     *
     * <p>
     * Solo se incluyen en el modelo las secciones seleccionadas por el usuario.
     * Las secciones no seleccionadas se omiten completamente del objeto resultante.
     * </p>
     *
     * <p>
     * Los datos generados pueden incluir:
     * <ul>
     *     <li>Metadatos del reporte</li>
     *     <li>Información del usuario que genera el reporte</li>
     *     <li>Información del usuario seleccionado</li>
     *     <li>Rango de fechas del reporte</li>
     *     <li>Estadísticas de ventas</li>
     *     <li>Series temporales de ingresos y ventas</li>
     *     <li>Estadísticas de productos</li>
     *     <li>Listados detallados de productos vendidos y no vendidos</li>
     *     <li>Configuración del ranking de productos</li>
     * </ul>
     * </p>
     *
     * <p>
     * La información de ventas totales obtenida previamente durante la validación
     * de datos se reutiliza para evitar consultas adicionales a la base de datos.
     * </p>
     *
     * @param request solicitud normalizada para la generación del PDF
     * @param totalSales información de ventas totales previamente calculada
     * @return modelo de datos completamente preparado para la generación del PDF
     */
    private StatisticsPdfDataDTO buildPdfData(
            StatisticsPdfRequestDTO request,
            TotalSalesResponseDTO totalSales
    ) {

        StatisticsPdfDataDTO data =
                new StatisticsPdfDataDTO();

        /*
         * Metadatos del reporte
         */
        data.setReportTitle(
                "Reporte de Estadísticas de Ventas"
        );

        data.setGenerationDateTime(
                LocalDateTime.now()
        );

        Object principal = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        if (!(principal instanceof CustomUserDetails userDetails)) {
            throw new AuthException(
                    "INVALID_SESSION",
                    "Usuario no autenticado o sesión inválida",
                    null
            );
        }

        User adminUser = userDetails.getUser();

        data.setGeneratedBy(adminUser.getUserName());

        data.setSelectedUser(
                request.getUserId() == null
                        ? "Todos los usuarios"
                        : iUserRepository.findById(
                        request.getUserId()
                ).orElseThrow().getUserName()
        );

        data.setStartDate(
                request.getStartDate()
        );

        data.setEndDate(
                request.getEndDate()
        );

        data.setIncludeSalesInformation(
                request.getIncludeSalesInformation()
        );

        data.setIncludeProductInformation(
                request.getIncludeProductInformation()
        );

        /*
         * Información de Ventas
         */
        if (request.getIncludeSalesInformation()) {

            data.setTotalRevenue(
                    getTotalRevenue(
                            request.getUserId(),
                            request.getStartDate(),
                            request.getEndDate()
                    )
            );

            data.setTotalSales(totalSales);

            data.setAverageTicket(
                    getAverageTicket(
                            request.getUserId(),
                            request.getStartDate(),
                            request.getEndDate()
                    )
            );

            data.setPeakHours(
                    getPeakHours(
                            request.getUserId(),
                            request.getStartDate(),
                            request.getEndDate()
                    )
            );

            data.setSalesTimeSeries(
                    getSalesTimeSeries(
                            request.getUserId(),
                            request.getStartDate(),
                            request.getEndDate()
                    )
            );
        }

        /*
         * Información de Productos
         */
        if (request.getIncludeProductInformation()) {

            data.setTopProducts(
                    getTopProducts(
                            request.getUserId(),
                            request.getStartDate(),
                            request.getEndDate()
                    )
            );

            PageResponseDTO<SoldProductDTO> soldProductsPage =
                    getSoldProductsRanking(
                            request.getUserId(),
                            request.getStartDate(),
                            request.getEndDate(),
                            request.getMetric(),
                            request.getOrder(),
                            0,
                            request.getSoldProductsLimit()
                    );

            data.setSoldProductsMetric(
                    request.getMetric()
                            == ProductRankingMetric.QUANTITY_SOLD
                            ? "Cantidad Vendida"
                            : "Ingresos Generados"
            );

            data.setSoldProductsOrder(
                    request.getOrder()
                            == ProductQuantityOrderType.MOST_TO_LEAST
                            ? "Más vendido → menos vendido"
                            : "Menos vendido → más vendido"
            );

            data.setSoldProducts(
                    soldProductsPage.getContent()
            );

            data.setTotalSoldProducts(
                    soldProductsPage.getTotalElements()
            );

            data.setIncludedSoldProducts(
                    soldProductsPage.getContent().size()
            );

            PageResponseDTO<UnsoldProductDTO> unsoldProductsPage =
                    getUnsoldProducts(
                            request.getUserId(),
                            request.getStartDate(),
                            request.getEndDate(),
                            0,
                            request.getUnsoldProductsLimit()
                    );

            data.setUnsoldProducts(
                    unsoldProductsPage.getContent()
            );

            data.setTotalUnsoldProducts(
                    unsoldProductsPage.getTotalElements()
            );

            data.setIncludedUnsoldProducts(
                    unsoldProductsPage.getContent().size()
            );
        }

        return data;
    }

    /**
     * Valida que exista información para los filtros
     * seleccionados antes de generar el reporte.
     *
     * <p>
     * Esta validación sigue la misma regla de negocio
     * utilizada por el módulo de estadísticas.
     * </p>
     *
     * <p>
     * La cantidad total de ventas se utiliza como
     * indicador de disponibilidad de información.
     * Si no existen ventas para los criterios
     * seleccionados, el proceso de generación del
     * reporte se cancela.
     * </p>
     *
     * <p>
     * La información de ventas totales obtenida se devuelve
     * para que pueda reutilizarse durante la generación
     * del reporte, evitando una consulta adicional a la base de datos.
     * </p>
     *
     * @param request solicitud de PDF normalizada
     * @return información de ventas totales utilizada durante la generación del reporte
     */
    private TotalSalesResponseDTO validateStatisticsDataAvailability(
            StatisticsPdfRequestDTO request
    ) {

        TotalSalesResponseDTO totalSales =
                getTotalSales(
                        request.getUserId(),
                        request.getStartDate(),
                        request.getEndDate()
                );

        if (totalSales.getTotalSales() == 0) {

            throw new NoStatisticsDataException(
                    "No hay datos disponibles para los filtros seleccionados"
            );
        }

        return totalSales;
    }

    /**
     * Genera el documento PDF final utilizando los
     * datos del reporte previamente preparados.
     *
     * <p>
     * Este método orquesta el proceso completo de
     * generación del PDF, incluyendo:
     * <ul>
     *     <li>Creación del documento</li>
     *     <li>Renderizado del encabezado del reporte</li>
     *     <li>Renderizado de la información de filtros seleccionados</li>
     *     <li>Renderizado de la sección Información de Ventas</li>
     *     <li>Renderizado de la sección Información de Productos</li>
     *     <li>Finalización del documento</li>
     * </ul>
     * </p>
     *
     * <p>
     * Solo se incluyen en el PDF generado las secciones
     * seleccionadas en la configuración del reporte.
     * </p>
     *
     * <p>
     * El documento se genera completamente en memoria
     * y se devuelve como un arreglo de bytes apto para
     * ser descargado por el cliente.
     * </p>
     *
     * @param data datos del reporte completamente preparados
     * @return documento PDF generado como arreglo de bytes
     */
    private byte[] generatePdfDocument(
            StatisticsPdfDataDTO data
    ) {

        try (
                ByteArrayOutputStream outputStream =
                        new ByteArrayOutputStream()
        ) {

            /*
             * Paso 1:
             * Crear documento PDF.
             */
            Document document =
                    createDocument(outputStream);

            /*
             * Paso 2:
             * Agregar encabezado del reporte.
             */
            addReportHeader(document, data);

            /*
             * Paso 3:
             * Agregar información de filtros seleccionados.
             */
            addFiltersInformation(document, data);

            /*
             * Paso 4:
             * Agregar sección Información de Ventas.
             */
            if (data.isIncludeSalesInformation()) {
                addSalesInformationSection(document, data);
            }

            /*
             * Paso 5:
             * Agregar sección Información de Productos.
             */
            if (data.isIncludeProductInformation()) {
                addProductInformationSection(document, data);
            }

            /*
             * Paso 6:
             * Finalizar documento.
             */
            document.close();

            return outputStream.toByteArray();

        } catch (Exception e) {

            throw new PdfGenerationException(
                    "Ocurrió un error al generar el reporte PDF"
            );
        }
    }

    /**
     * Crea e inicializa el documento PDF.
     *
     * <p>
     * Este método configura:
     * <ul>
     *     <li>El tamaño de página del documento</li>
     *     <li>Los márgenes del documento</li>
     *     <li>La asociación con el escritor PDF</li>
     * </ul>
     * </p>
     *
     * <p>
     * El documento se abre automáticamente antes
     * de ser retornado.
     * </p>
     *
     * @param outputStream flujo de salida destino para el PDF
     * @return documento PDF inicializado y abierto
     * @throws DocumentException si no se puede crear el documento PDF
     */
    private Document createDocument(
            ByteArrayOutputStream outputStream
    ) throws DocumentException {

        Document document =
                new Document(PageSize.A4, 36, 36, 54, 36);

        PdfWriter.getInstance(document, outputStream);

        document.open();

        return document;
    }

    /**
     * Agrega la sección de encabezado del reporte al documento PDF.
     *
     * <p>
     * El encabezado contiene los metadatos principales que se muestran
     * siempre en el documento generado, independientemente de las secciones seleccionadas.
     * </p>
     *
     * <p>
     * Esta sección se renderiza al inicio del PDF y establece la identidad
     * del reporte antes de mostrar cualquier información detallada o filtros aplicados.
     * </p>
     *
     * <p>
     * Incluye la siguiente información:
     * <ul>
     *     <li>Título del reporte</li>
     *     <li>Usuario que generó el reporte</li>
     *     <li>Fecha y hora de generación</li>
     * </ul>
     * </p>
     *
     * @param document documento PDF de destino
     * @param data datos previamente preparados del reporte
     * @throws DocumentException si ocurre un error al escribir en el documento PDF
     */
    private void addReportHeader(
            Document document,
            StatisticsPdfDataDTO data
    ) throws DocumentException {

        Font titleFont =
                FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        20
                );

        Font metadataFont =
                FontFactory.getFont(
                        FontFactory.HELVETICA,
                        11
                );

        Paragraph title =
                new Paragraph(
                        data.getReportTitle(),
                        titleFont
                );

        title.setAlignment(Element.ALIGN_CENTER);

        document.add(title);

        document.add(Chunk.NEWLINE);

        // 🆕 USUARIO QUE GENERA EL REPORTE
        Paragraph generatedBy =
                new Paragraph(
                        "Generado por: " + data.getGeneratedBy(),
                        metadataFont
                );

        generatedBy.setAlignment(Element.ALIGN_RIGHT);

        document.add(generatedBy);

        // espacio
        document.add(Chunk.NEWLINE);

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(
                        "dd/MM/yyyy HH:mm:ss"
                );

        String formattedDate =
                data.getGenerationDateTime()
                        .format(formatter);

        Paragraph generationDate =
                new Paragraph(
                        "Generado el: " + formattedDate,
                        metadataFont
                );

        generationDate.setAlignment(Element.ALIGN_RIGHT);

        document.add(generationDate);

        document.add(Chunk.NEWLINE);
    }

    /**
     * Agrega la sección de filtros seleccionados al documento PDF.
     *
     * <p>
     * Esta sección muestra los criterios de filtrado
     * utilizados para generar el reporte:
     * <ul>
     *     <li>Usuario seleccionado</li>
     *     <li>Rango de fechas seleccionado</li>
     * </ul>
     * </p>
     *
     * <p>
     * La información mostrada refleja exactamente
     * los filtros aplicados al momento de generar el reporte,
     * permitiendo contextualizar correctamente las estadísticas
     * contenidas en el documento.
     * </p>
     *
     * @param document documento PDF de destino
     * @param data datos preparados del reporte
     * @throws DocumentException si no es posible agregar el contenido
     */
    private void addFiltersInformation(
            Document document,
            StatisticsPdfDataDTO data
    ) throws DocumentException {

        Font sectionTitleFont =
                FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        14
                );

        Font contentFont =
                FontFactory.getFont(
                        FontFactory.HELVETICA,
                        11
                );

        Paragraph sectionTitle =
                new Paragraph(
                        "Filtros Seleccionados",
                        sectionTitleFont
                );

        document.add(sectionTitle);

        document.add(Chunk.NEWLINE);

        Paragraph selectedUser =
                new Paragraph(
                        "Usuario: "
                                + data.getSelectedUser(),
                        contentFont
                );

        document.add(selectedUser);

        DateTimeFormatter dateFormatter =
                DateTimeFormatter.ofPattern("dd/MM/yyyy");

        Paragraph selectedDates =
                new Paragraph(
                        "Rango de Fechas: "
                                + data.getStartDate().format(dateFormatter)
                                + " a "
                                + data.getEndDate().format(dateFormatter),
                        contentFont
                );

        document.add(selectedDates);

        document.add(Chunk.NEWLINE);
    }

    /**
     * Agrega la sección completa de Información de Ventas
     * al documento PDF.
     *
     * <p>
     * Esta sección incluye:
     * <ul>
     *     <li>Estadísticas resumidas de ventas</li>
     *     <li>Información sobre horarios pico de ventas</li>
     *     <li>Tabla de ingresos a lo largo del tiempo</li>
     *     <li>Tabla de ventas a lo largo del tiempo</li>
     * </ul>
     * </p>
     *
     * <p>
     * Toda la información mostrada se calcula utilizando
     * los filtros seleccionados al momento de generar el reporte.
     * </p>
     *
     * @param document documento PDF de destino
     * @param data datos preparados del reporte
     * @throws DocumentException si no es posible agregar contenido al PDF
     */
    private void addSalesInformationSection(
            Document document,
            StatisticsPdfDataDTO data
    ) throws DocumentException, IOException {

        /*
         * Paso 1:
         * Agregar el título de la sección.
         */
        addSectionTitle(
                document,
                "Información de Ventas"
        );

        /*
         * Paso 2:
         * Agregar estadísticas resumidas de ventas.
         */
        addSalesSummary(
                document,
                data
        );

        /*
         * Paso 3:
         * Agregar información sobre horarios pico de ventas.
         */
        addPeakHoursInformation(
                document,
                data
        );

        /*
         * Paso 4:
         * Agregar la tabla de ingresos a lo largo del tiempo.
         */
        addRevenueOverTimeTable(
                document,
                data.getSalesTimeSeries()
        );

        /*
         * Paso 5:
         * Agregar la tabla de ventas a lo largo del tiempo.
         */
        addSalesOverTimeTable(
                document,
                data.getSalesTimeSeries()
        );
    }

    /**
     * Agrega un título de sección al documento PDF.
     *
     * <p>
     * Este método auxiliar se utiliza para separar visualmente
     * las secciones principales del reporte.
     * </p>
     *
     * @param document documento PDF de destino
     * @param title título de la sección
     * @throws DocumentException si el contenido no puede agregarse
     */
    private void addSectionTitle(
            Document document,
            String title
    ) throws DocumentException {

        Font sectionFont =
                FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        16
                );

        Paragraph paragraph =
                new Paragraph(
                        title,
                        sectionFont
                );

        paragraph.setSpacingBefore(15f);
        paragraph.setSpacingAfter(10f);

        document.add(paragraph);
    }

    /**
     * Agrega la subsección de resumen de ventas
     * al documento PDF.
     *
     * <p>
     * Esta subsección muestra los principales indicadores
     * de ventas calculados para los filtros seleccionados:
     * <ul>
     *     <li>Ingresos totales</li>
     *     <li>Cantidad total de ventas</li>
     *     <li>Ticket promedio</li>
     * </ul>
     * </p>
     *
     * @param document documento PDF de destino
     * @param data datos preparados para el reporte
     * @throws DocumentException si el contenido no puede agregarse
     */
    private void addSalesSummary(
            Document document,
            StatisticsPdfDataDTO data
    ) throws DocumentException {

        Font contentFont =
                FontFactory.getFont(
                        FontFactory.HELVETICA,
                        11
                );

        Paragraph revenue =
                new Paragraph(
                        "Ingresos Totales: $"
                                + data.getTotalRevenue()
                                .getTotalRevenue(),
                        contentFont
                );

        Paragraph totalSales =
                new Paragraph(
                        "Cantidad Total de Ventas: "
                                + data.getTotalSales()
                                .getTotalSales(),
                        contentFont
                );

        Paragraph averageTicket =
                new Paragraph(
                        "Ticket Promedio: $"
                                + data.getAverageTicket()
                                .getAverageTicket(),
                        contentFont
                );

        revenue.setSpacingAfter(5f);
        totalSales.setSpacingAfter(5f);
        averageTicket.setSpacingAfter(10f);

        document.add(revenue);
        document.add(totalSales);
        document.add(averageTicket);
    }

    /**
     * Agrega la subsección de horas pico de ventas
     * al documento PDF.
     *
     * <p>
     * Esta subsección muestra:
     * <ul>
     *     <li>La hora con mayores ingresos</li>
     *     <li>La hora con la mayor cantidad de ventas</li>
     * </ul>
     * </p>
     *
     * <p>
     * Ambos valores se calculan utilizando los filtros
     * seleccionados al momento de generar el reporte.
     * </p>
     *
     * @param document documento PDF de destino
     * @param data datos preparados para el reporte
     * @throws DocumentException si el contenido no puede agregarse
     */
    private void addPeakHoursInformation(
            Document document,
            StatisticsPdfDataDTO data
    ) throws DocumentException {

        Font contentFont =
                FontFactory.getFont(
                        FontFactory.HELVETICA,
                        11
                );

        Paragraph highestRevenueHour =
                new Paragraph(
                        "Hora con Mayores Ingresos: "
                                + data.getPeakHours()
                                .getHighestRevenueHour(),
                        contentFont
                );

        Paragraph highestSalesHour =
                new Paragraph(
                        "Hora con Más Ventas: "
                                + data.getPeakHours()
                                .getHighestSalesHour(),
                        contentFont
                );

        highestRevenueHour.setSpacingAfter(5f);
        highestSalesHour.setSpacingAfter(10f);

        document.add(highestRevenueHour);
        document.add(highestSalesHour);
    }

    /**
     * Agrega la tabla de ingresos a lo largo del tiempo
     * al documento PDF.
     *
     * <p>
     * Esta subsección muestra estadísticas de ingresos
     * agrupadas por período de tiempo.
     * </p>
     *
     * <p>
     * La granularidad del período depende del rango de fechas
     * seleccionado y puede representar:
     * <ul>
     *     <li>Horas</li>
     *     <li>Días</li>
     *     <li>Meses</li>
     *     <li>Años</li>
     * </ul>
     * </p>
     *
     * <p>
     * Cada fila representa un período de tiempo y los
     * ingresos generados correspondientes.
     * </p>
     *
     * @param document documento PDF de destino
     * @param salesTimeSeries datos de series temporales de ventas
     * @throws DocumentException si el contenido no puede agregarse
     */
    private void addRevenueOverTimeTable(
            Document document,
            SalesTimeSeriesResponseDTO salesTimeSeries
    ) throws DocumentException {

        Paragraph title =
                new Paragraph(
                        "Ingresos a lo Largo del Tiempo",
                        FontFactory.getFont(
                                FontFactory.HELVETICA_BOLD,
                                13
                        )
                );

        title.setSpacingBefore(10f);
        title.setSpacingAfter(10f);

        document.add(title);

        PdfPTable table =
                new PdfPTable(2);

        table.setWidthPercentage(100);

        table.addCell("Período");
        table.addCell("Ingresos Generados");

        for (TimeSeriesPointDTO point
                : salesTimeSeries.getRevenueOverTime()) {

            table.addCell(point.getLabel());

            table.addCell(
                    "$" + point.getValue()
            );
        }

        document.add(table);

        document.add(Chunk.NEWLINE);
    }

    /**
     * Agrega la tabla de ventas a lo largo del tiempo
     * al documento PDF.
     *
     * <p>
     * Esta subsección muestra estadísticas de ventas
     * agrupadas por período de tiempo.
     * </p>
     *
     * <p>
     * La granularidad del período depende del rango de fechas
     * seleccionado y puede representar:
     * <ul>
     *     <li>Horas</li>
     *     <li>Días</li>
     *     <li>Meses</li>
     *     <li>Años</li>
     * </ul>
     * </p>
     *
     * <p>
     * Cada fila representa un período de tiempo y su
     * correspondiente cantidad de ventas.
     * </p>
     *
     * @param document documento PDF de destino
     * @param salesTimeSeries datos de series temporales de ventas
     * @throws DocumentException si el contenido no puede agregarse
     */
    private void addSalesOverTimeTable(
            Document document,
            SalesTimeSeriesResponseDTO salesTimeSeries
    ) throws DocumentException {

        Paragraph title =
                new Paragraph(
                        "Ventas a lo Largo del Tiempo",
                        FontFactory.getFont(
                                FontFactory.HELVETICA_BOLD,
                                13
                        )
                );

        title.setSpacingBefore(10f);
        title.setSpacingAfter(10f);

        document.add(title);

        PdfPTable table =
                new PdfPTable(2);

        table.setWidthPercentage(100);

        table.addCell("Período");
        table.addCell("Cantidad de Ventas");

        for (TimeSeriesPointDTO point
                : salesTimeSeries.getSalesOverTime()) {

            table.addCell(
                    point.getLabel()
            );

            table.addCell(
                    String.valueOf(
                            point.getValue()
                    )
            );
        }

        document.add(table);

        document.add(Chunk.NEWLINE);
    }

    /**
     * Agrega la sección completa de información de productos
     * al documento PDF.
     *
     * <p>
     * Esta sección incluye:
     * <ul>
     *     <li>Productos más vendidos por cantidad</li>
     *     <li>Productos con mayores ingresos generados</li>
     *     <li>Ranking de productos vendidos</li>
     *     <li>Lista de productos sin ventas</li>
     * </ul>
     * </p>
     *
     * <p>
     * Todas las estadísticas e información de productos
     * se calculan utilizando los filtros seleccionados
     * al momento de generar el reporte.
     * </p>
     *
     * @param document documento PDF de destino
     * @param data datos preparados para el reporte
     * @throws DocumentException si el contenido PDF no puede agregarse
     */
    private void addProductInformationSection(
            Document document,
            StatisticsPdfDataDTO data
    ) throws DocumentException {

        /*
         * Paso 1:
         * Agregar título de la sección.
         */
        addSectionTitle(
                document,
                "Información de Productos"
        );

        /*
         * Paso 2:
         * Agregar resumen de productos destacados.
         */
        addTopProductsSummary(
                document,
                data
        );

        /*
         * Paso 3:
         * Agregar ranking de productos vendidos.
         */
        addSoldProductsRanking(
                document,
                data
        );

        /*
         * Paso 4:
         * Agregar lista de productos sin ventas.
         */
        addUnsoldProductsList(
                document,
                data
        );
    }

    /**
     * Agrega la subsección de resumen de productos destacados
     * al documento PDF.
     *
     * <p>
     * Esta subsección contiene:
     * <ul>
     *     <li>Tabla de productos más vendidos por cantidad</li>
     *     <li>Tabla de productos con mayores ingresos generados</li>
     * </ul>
     * </p>
     *
     * <p>
     * Estos rankings brindan una visión rápida de los
     * productos con mejor desempeño según los filtros seleccionados.
     * </p>
     *
     * @param document documento PDF de destino
     * @param data datos preparados para el reporte
     * @throws DocumentException si el contenido no puede agregarse
     */
    private void addTopProductsSummary(
            Document document,
            StatisticsPdfDataDTO data
    ) throws DocumentException {

        addTopProductsByQuantityTable(
                document,
                data.getTopProducts()
        );

        addTopProductsByRevenueTable(
                document,
                data.getTopProducts()
        );
    }

    /**
     * Agrega la tabla de productos más vendidos por cantidad
     * al documento PDF.
     *
     * <p>
     * Esta tabla muestra los productos con mayor cantidad vendida
     * según los filtros seleccionados.
     * </p>
     *
     * <p>
     * Cada fila incluye:
     * <ul>
     *     <li>Código del producto</li>
     *     <li>Nombre del producto</li>
     *     <li>Cantidad vendida</li>
     * </ul>
     * </p>
     *
     * @param document documento PDF de destino
     * @param topProducts estadísticas de productos destacados
     * @throws DocumentException si el contenido no puede agregarse
     */
    private void addTopProductsByQuantityTable(
            Document document,
            TopProductsResponseDTO topProducts
    ) throws DocumentException {

        Paragraph title =
                new Paragraph(
                        "Productos Más Vendidos por Cantidad (hasta 10 productos)",
                        FontFactory.getFont(
                                FontFactory.HELVETICA_BOLD,
                                13
                        )
                );

        title.setSpacingBefore(10f);
        title.setSpacingAfter(10f);

        document.add(title);

        PdfPTable table =
                new PdfPTable(3);

        table.setWidthPercentage(100);

        table.addCell("Código");
        table.addCell("Nombre");
        table.addCell("Cantidad Vendida");

        for (TopProductsByQuantityDTO product
                : topProducts.getTopProductsByQuantity()) {

            table.addCell(product.getProductCode());
            table.addCell(product.getProductName());
            table.addCell(
                    String.valueOf(
                            product.getQuantitySold()
                    )
            );
        }

        document.add(table);

        document.add(Chunk.NEWLINE);
    }

    /**
     * Agrega la tabla de productos más vendidos por ingresos
     * generados al documento PDF.
     *
     * <p>
     * Cada fila incluye:
     * <ul>
     *     <li>Código del producto</li>
     *     <li>Nombre del producto</li>
     *     <li>Ingresos generados</li>
     * </ul>
     * </p>
     *
     * @param document documento PDF de destino
     * @param topProducts estadísticas de productos destacados
     * @throws DocumentException si el contenido no puede agregarse
     */
    private void addTopProductsByRevenueTable(
            Document document,
            TopProductsResponseDTO topProducts
    ) throws DocumentException {

        Paragraph title =
                new Paragraph(
                        "Productos con Mayores Ingresos (hasta 10 productos)",
                        FontFactory.getFont(
                                FontFactory.HELVETICA_BOLD,
                                13
                        )
                );

        title.setSpacingBefore(10f);
        title.setSpacingAfter(10f);

        document.add(title);

        PdfPTable table =
                new PdfPTable(3);

        table.setWidthPercentage(100);

        table.addCell("Código");
        table.addCell("Nombre");
        table.addCell("Ingresos Generados");

        for (TopProductsByRevenueDTO product
                : topProducts.getTopProductsByRevenue()) {

            table.addCell(product.getProductCode());
            table.addCell(product.getProductName());
            table.addCell(
                    "$" + product.getRevenueGenerated()
            );
        }

        document.add(table);

        document.add(Chunk.NEWLINE);
    }

    /**
     * Agrega el ranking de productos vendidos al documento PDF.
     *
     * <p>
     * Esta subsección incluye:
     * <ul>
     *     <li>Métrica de ranking seleccionada</li>
     *     <li>Orden del ranking seleccionado</li>
     *     <li>Total de productos vendidos coincidentes</li>
     *     <li>Cantidad de productos incluidos</li>
     *     <li>Tabla detallada de productos vendidos</li>
     * </ul>
     * </p>
     *
     * <p>
     * La configuración del ranking mostrada en el reporte
     * refleja la métrica y el orden seleccionados al momento
     * de generar el reporte.
     * </p>
     *
     * @param document documento PDF de destino
     * @param data datos preparados para el reporte
     * @throws DocumentException si el contenido no puede agregarse
     */
    private void addSoldProductsRanking(
            Document document,
            StatisticsPdfDataDTO data
    ) throws DocumentException {

        Paragraph title =
                new Paragraph(
                        "Ranking de Productos Vendidos",
                        FontFactory.getFont(
                                FontFactory.HELVETICA_BOLD,
                                13
                        )
                );

        title.setSpacingBefore(10f);
        title.setSpacingAfter(10f);

        document.add(title);

        Paragraph metadata =
                new Paragraph(
                        "Productos incluidos: "
                                + data.getIncludedSoldProducts()
                                + " / "
                                + data.getTotalSoldProducts()
                );

        metadata.setSpacingAfter(10f);

        document.add(metadata);

        Paragraph rankingConfiguration =
                new Paragraph(
                        "Métrica: "
                                + data.getSoldProductsMetric()
                                + " | Orden: "
                                + data.getSoldProductsOrder()
                );

        rankingConfiguration.setSpacingAfter(10f);

        document.add(rankingConfiguration);

        PdfPTable table =
                new PdfPTable(4);

        table.setWidthPercentage(100);

        table.addCell("Código");
        table.addCell("Nombre");
        table.addCell("Cantidad Vendida");
        table.addCell("Ingresos Generados");

        for (SoldProductDTO product
                : data.getSoldProducts()) {

            table.addCell(product.getProductCode());
            table.addCell(product.getProductName());
            table.addCell(
                    String.valueOf(
                            product.getQuantitySold()
                    )
            );
            table.addCell(
                    "$" + product.getRevenueGenerated()
            );
        }

        document.add(table);

        document.add(Chunk.NEWLINE);
    }

    /**
     * Agrega la lista de productos sin ventas
     * al documento PDF.
     *
     * <p>
     * Esta subsección incluye:
     * <ul>
     *     <li>Total de productos sin ventas coincidentes</li>
     *     <li>Cantidad de productos incluidos</li>
     *     <li>Tabla detallada de productos sin ventas</li>
     * </ul>
     * </p>
     *
     * <p>
     * Si no existen productos sin ventas que coincidan con los filtros seleccionados,
     * se muestra el mensaje:
     * "No hay productos sin ventas para los filtros seleccionados"
     * en lugar de la tabla.
     * </p>
     *
     * @param document documento PDF de destino
     * @param data datos preparados para el reporte
     * @throws DocumentException si el contenido no puede agregarse
     */
    private void addUnsoldProductsList(
            Document document,
            StatisticsPdfDataDTO data
    ) throws DocumentException {

        Paragraph title =
                new Paragraph(
                        "Productos no vendidos",
                        FontFactory.getFont(
                                FontFactory.HELVETICA_BOLD,
                                13
                        )
                );

        title.setSpacingBefore(10f);
        title.setSpacingAfter(10f);

        document.add(title);

        if (data.getUnsoldProducts().isEmpty()) {

            Paragraph emptyMessage =
                    new Paragraph(
                            "No hay productos no vendidos para los filtros seleccionados"
                    );

            emptyMessage.setSpacingAfter(10f);

            document.add(emptyMessage);

            return;
        }

        Paragraph metadata =
                new Paragraph(
                        "Productos incluidos: "
                                + data.getIncludedUnsoldProducts()
                                + " / "
                                + data.getTotalUnsoldProducts()
                );

        metadata.setSpacingAfter(10f);

        document.add(metadata);

        PdfPTable table =
                new PdfPTable(2);

        table.setWidthPercentage(100);

        table.addCell("Código");
        table.addCell("Nombre");

        for (UnsoldProductDTO product
                : data.getUnsoldProducts()) {

            table.addCell(product.getProductCode());
            table.addCell(product.getProductName());
        }

        document.add(table);

        document.add(Chunk.NEWLINE);
    }
}
