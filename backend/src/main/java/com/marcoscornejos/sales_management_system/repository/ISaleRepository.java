/**
 * Interfaz de repositorio para la gestión de entidades {@link Sale}.
 *
 * <p>
 * Extiende {@link JpaRepository}, proporcionando operaciones CRUD estándar
 * como save, delete, findById y findAll sin requerir una implementación explícita.
 * </p>
 *
 * <p>
 * Este repositorio gestiona el acceso a los datos de las ventas.
 * Aquí pueden definirse métodos de consulta personalizados cuando sea necesario.
 * </p>
 */

package com.marcoscornejos.sales_management_system.repository;

import com.marcoscornejos.sales_management_system.model.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.marcoscornejos.sales_management_system.projection.TimeSeriesProjection;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ISaleRepository extends JpaRepository<Sale, Long> {

    /**
     * Recupera ventas con búsqueda opcional por identificador de venta,
     * filtradas por fecha de venta, aplicando paginación y ordenamiento
     * a nivel de base de datos.
     *
     * <p>
     * La búsqueda se realiza mediante coincidencia exacta del identificador de venta.
     * Si {@code searchSaleId} es null, la búsqueda se ignora.
     * </p>
     *
     * <p>
     * Las ventas siempre son filtradas mediante coincidencia exacta de la fecha de venta.
     * </p>
     *
     * <p>
     * La paginación y el ordenamiento se gestionan mediante {@link Pageable},
     * garantizando una recuperación eficiente de datos del lado del servidor.
     * </p>
     *
     * <p>
     * El usuario asociado se carga utilizando {@link EntityGraph}
     * para evitar problemas de consultas N+1.
     * </p>
     *
     * @param searchSaleId identificador de venta opcional
     * @param date filtro por fecha de venta
     * @param pageable configuración de paginación y ordenamiento
     * @return resultado paginado de las ventas coincidentes
     */
    @EntityGraph(attributePaths = "user")
    @Query("""
    SELECT s FROM Sale s
    WHERE
        (
            :searchSaleId IS NULL OR
            s.saleId = :searchSaleId
        )
    AND
        s.saleDate = :date
    """)
    Page<Sale> findSales(Long searchSaleId,
                         LocalDate date,
                         Pageable pageable);

    /**
     * Recupera una venta junto con sus detalles, productos y usuario.
     *
     * <p>
     * Utiliza JOIN FETCH para cargar las entidades asociadas y evitar consultas
     * adicionales provocadas por la carga diferida (lazy loading).
     * </p>
     *
     * @param saleId identificador único de la venta
     * @return un Optional que contiene la venta junto con sus detalles, productos y usuario
     */
    @Query("""
        SELECT DISTINCT s FROM Sale s
        JOIN FETCH s.saleDetails sd
        JOIN FETCH sd.product
        JOIN FETCH s.user
        WHERE s.saleId = :saleId
    """)
    Optional<Sale> findByIdWithDetailsAndProducts(Long saleId);


    /**
     * Recupera una venta junto con sus detalles para la generación del comprobante.
     *
     * <p>
     * Carga la venta y sus detalles de venta asociados en una única consulta.
     * Las entidades Product y User no se recuperan porque el comprobante utiliza
     * los datos históricos almacenados en SaleDetail.
     * </p>
     *
     * @param saleId identificador único de la venta
     * @return un Optional que contiene la venta junto con sus detalles
     */
    @Query("""
    SELECT DISTINCT s FROM Sale s
    JOIN FETCH s.saleDetails sd
    WHERE s.saleId = :saleId
    """)
    Optional<Sale> findByIdWithDetails(Long saleId);

    /**
     * Calcula el importe total generado por las ventas
     * dentro de los filtros proporcionados.
     *
     * <p>
     * El importe se calcula como la suma de los importes totales de todas las ventas.
     * </p>
     *
     * <p>
     * Si {@code userId} es null, se incluyen las ventas de todos los usuarios.
     * </p>
     *
     * <p>
     * Si no existen ventas coincidentes, devuelve {@link BigDecimal#ZERO}.
     * </p>
     *
     * @param userId filtro opcional por usuario (null = todos los usuarios)
     * @param startDate fecha de inicio del filtro
     * @param endDate fecha de fin del filtro
     * @return importe total de las ventas coincidentes
     */
    @Query("""
    SELECT COALESCE(SUM(s.totalAmount), 0)
    FROM Sale s
    WHERE
        (
            :userId IS NULL OR
            s.user.userId = :userId
        )
    AND
        s.saleDate BETWEEN :startDate AND :endDate
    """)
    BigDecimal sumRevenue(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    );

    /**
     * Cuenta la cantidad de ventas que coinciden con los filtros proporcionados.
     *
     * <p>
     * Si {@code userId} es null, se incluyen las ventas de todos los usuarios.
     * </p>
     *
     * <p>
     * El resultado representa la cantidad total de registros de venta
     * dentro del rango de fechas seleccionado.
     * </p>
     *
     * @param userId filtro opcional por usuario (null = todos los usuarios)
     * @param startDate fecha de inicio del filtro
     * @param endDate fecha de fin del filtro
     * @return cantidad total de ventas coincidentes
     */
    @Query("""
    SELECT COUNT(s.saleId)
    FROM Sale s
    WHERE (:userId IS NULL OR s.user.userId = :userId)
    AND s.saleDate BETWEEN :startDate AND :endDate
    """)
    Long countSales(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    );

    /**
     * Recupera la hora del día (0–23) con el mayor importe generado
     * dentro de los filtros proporcionados.
     *
     * <p>
     * Las ventas se agrupan por hora y se ordenan
     * según el importe total generado.
     * </p>
     *
     * @param userId filtro opcional por usuario (null = todos los usuarios)
     * @param startDate fecha de inicio del filtro
     * @param endDate fecha de fin del filtro
     * @return hora (0–23) con el mayor importe generado,
     *         o null si no existen ventas coincidentes
     */
    @Query(value = """
    SELECT
        EXTRACT(HOUR FROM s.sale_time) AS hour
    FROM core.sale s
    WHERE
        (
            :userId IS NULL
            OR s.user_id = :userId
        )
    AND
        s.sale_date BETWEEN :startDate AND :endDate
    GROUP BY EXTRACT(HOUR FROM s.sale_time)
    ORDER BY SUM(s.total_amount) DESC
    LIMIT 1
    """, nativeQuery = true)
    Integer findPeakRevenueHour(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    );

    /**
     * Recupera la hora del día (0–23) con la mayor cantidad de ventas
     * dentro de los filtros proporcionados.
     *
     * <p>
     * Las ventas se agrupan por hora y se ordenan
     * según la cantidad total de registros de venta.
     * </p>
     *
     * @param userId filtro opcional por usuario (null = todos los usuarios)
     * @param startDate fecha de inicio del filtro
     * @param endDate fecha de fin del filtro
     * @return hora (0–23) con la mayor cantidad de ventas,
     *         o null si no existen ventas coincidentes
     */
    @Query(value = """
    SELECT
        EXTRACT(HOUR FROM s.sale_time) AS hour
    FROM core.sale s
    WHERE
        (
            :userId IS NULL
            OR s.user_id = :userId
        )
    AND
        s.sale_date BETWEEN :startDate AND :endDate
    GROUP BY EXTRACT(HOUR FROM s.sale_time)
    ORDER BY COUNT(s.sale_id) DESC
    LIMIT 1
    """, nativeQuery = true)
    Integer findPeakSalesHour(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    );

    /**
     * Recupera el importe generado agrupado por hora para la visualización de gráficos.
     *
     * <p>
     * El importe se calcula como la suma de los importes totales de las ventas
     * agrupadas por hora de venta.
     * </p>
     *
     * <p>
     * Esta agregación se utiliza para rangos de fechas de un solo día,
     * proporcionando una visualización más detallada en los gráficos.
     * </p>
     *
     * <p>
     * Si {@code userId} es null, se incluyen las ventas de todos los usuarios.
     * </p>
     *
     * <p>
     * Los resultados se ordenan cronológicamente por hora.
     * </p>
     *
     * @param userId filtro opcional por usuario (null = todos los usuarios)
     * @param startDate fecha de inicio del filtro
     * @param endDate fecha de fin del filtro
     * @return serie temporal de importes por hora
     */
    @Query(value = """
    SELECT
        TO_CHAR(s.sale_time, 'HH24') AS label,
        COALESCE(SUM(s.total_amount), 0) AS value
    FROM core.sale s
    WHERE
        (
            :userId IS NULL OR
            s.user_id = :userId
        )
    AND
        s.sale_date BETWEEN :startDate AND :endDate
    GROUP BY TO_CHAR(s.sale_time, 'HH24')
    ORDER BY TO_CHAR(s.sale_time, 'HH24')
    """, nativeQuery = true)
    List<TimeSeriesProjection> getRevenuePerHour(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    );

    /**
     * Recupera el importe generado agrupado por día para la visualización de gráficos.
     *
     * <p>
     * El importe se calcula como la suma de los importes totales de las ventas
     * agrupadas por fecha de venta.
     * </p>
     *
     * <p>
     * Si {@code userId} es null, se incluyen las ventas de todos los usuarios.
     * </p>
     *
     * <p>
     * Los resultados se ordenan cronológicamente.
     * </p>
     *
     * @param userId filtro opcional por usuario (null = todos los usuarios)
     * @param startDate fecha de inicio del filtro
     * @param endDate fecha de fin del filtro
     * @return serie temporal de importes por día
     */
    @Query(value = """
    SELECT
        TO_CHAR(s.sale_date, 'YYYY-MM-DD') AS label,
        COALESCE(SUM(s.total_amount), 0) AS value
    FROM core.sale s
    WHERE
        (
            :userId IS NULL OR
            s.user_id = :userId
        )
    AND
        s.sale_date BETWEEN :startDate AND :endDate
    GROUP BY s.sale_date
    ORDER BY s.sale_date
    """, nativeQuery = true)
    List<TimeSeriesProjection> getRevenuePerDay(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    );

    /**
     * Recupera el importe generado agrupado por mes para la visualización de gráficos.
     *
     * <p>
     * El importe se calcula como la suma de los importes totales de las ventas
     * agrupadas por mes y año.
     * </p>
     *
     * <p>
     * Si {@code userId} es null, se incluyen las ventas de todos los usuarios.
     * </p>
     *
     * <p>
     * Los resultados se ordenan cronológicamente.
     * </p>
     *
     * @param userId filtro opcional por usuario (null = todos los usuarios)
     * @param startDate fecha de inicio del filtro
     * @param endDate fecha de fin del filtro
     * @return serie temporal de importes por mes
     */
    @Query(value = """
    SELECT
        TO_CHAR(s.sale_date, 'YYYY-MM') AS label,
        COALESCE(SUM(s.total_amount), 0) AS value
    FROM core.sale s
    WHERE
        (
            :userId IS NULL OR
            s.user_id = :userId
        )
    AND
        s.sale_date BETWEEN :startDate AND :endDate
    GROUP BY TO_CHAR(s.sale_date, 'YYYY-MM')
    ORDER BY TO_CHAR(s.sale_date, 'YYYY-MM')
    """, nativeQuery = true)
    List<TimeSeriesProjection> getRevenuePerMonth(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    );

    /**
     * Recupera el importe generado agrupado por año para la visualización de gráficos.
     *
     * <p>
     * El importe se calcula como la suma de los importes totales de las ventas
     * agrupadas por año.
     * </p>
     *
     * <p>
     * Si {@code userId} es null, se incluyen las ventas de todos los usuarios.
     * </p>
     *
     * <p>
     * Los resultados se ordenan cronológicamente.
     * </p>
     *
     * @param userId filtro opcional por usuario (null = todos los usuarios)
     * @param startDate fecha de inicio del filtro
     * @param endDate fecha de fin del filtro
     * @return serie temporal de importes por año
     */
    @Query(value = """
    SELECT
        TO_CHAR(s.sale_date, 'YYYY') AS label,
        COALESCE(SUM(s.total_amount), 0) AS value
    FROM core.sale s
    WHERE
        (
            :userId IS NULL OR
            s.user_id = :userId
        )
    AND
        s.sale_date BETWEEN :startDate AND :endDate
    GROUP BY TO_CHAR(s.sale_date, 'YYYY')
    ORDER BY TO_CHAR(s.sale_date, 'YYYY')
    """, nativeQuery = true)
    List<TimeSeriesProjection> getRevenuePerYear(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    );

    /**
     * Recupera la cantidad de ventas agrupada por hora para la visualización de gráficos.
     *
     * <p>
     * La cantidad de ventas se calcula como el número total de registros de venta
     * agrupados por hora de venta.
     * </p>
     *
     * <p>
     * Esta agregación se utiliza para rangos de fechas de un solo día,
     * proporcionando una visualización más detallada en los gráficos.
     * </p>
     *
     * <p>
     * Si {@code userId} es null, se incluyen las ventas de todos los usuarios.
     * </p>
     *
     * <p>
     * Los resultados se ordenan cronológicamente por hora.
     * </p>
     *
     * @param userId filtro opcional por usuario (null = todos los usuarios)
     * @param startDate fecha de inicio del filtro
     * @param endDate fecha de fin del filtro
     * @return serie temporal de cantidad de ventas por hora
     */
    @Query(value = """
    SELECT
        TO_CHAR(s.sale_time, 'HH24') AS label,
        CAST(COUNT(s.sale_id) AS NUMERIC) AS value
    FROM core.sale s
    WHERE
        (
            :userId IS NULL OR
            s.user_id = :userId
        )
    AND
        s.sale_date BETWEEN :startDate AND :endDate
    GROUP BY TO_CHAR(s.sale_time, 'HH24')
    ORDER BY TO_CHAR(s.sale_time, 'HH24')
    """, nativeQuery = true)
    List<TimeSeriesProjection> getSalesPerHour(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    );

    /**
     * Recupera la cantidad de ventas agrupada por día para la visualización de gráficos.
     *
     * <p>
     * La cantidad de ventas se calcula como el número total de registros de venta
     * agrupados por fecha de venta.
     * </p>
     *
     * <p>
     * Si {@code userId} es null, se incluyen las ventas de todos los usuarios.
     * </p>
     *
     * <p>
     * Los resultados se ordenan cronológicamente.
     * </p>
     *
     * @param userId filtro opcional por usuario (null = todos los usuarios)
     * @param startDate fecha de inicio del filtro
     * @param endDate fecha de fin del filtro
     * @return serie temporal de cantidad de ventas por día
     */
    @Query(value = """
    SELECT
        TO_CHAR(s.sale_date, 'YYYY-MM-DD') AS label,
        CAST(COUNT(s.sale_id) AS NUMERIC) AS value
    FROM core.sale s
    WHERE
        (
            :userId IS NULL OR
            s.user_id = :userId
        )
    AND
        s.sale_date BETWEEN :startDate AND :endDate
    GROUP BY s.sale_date
    ORDER BY s.sale_date
    """, nativeQuery = true)
    List<TimeSeriesProjection> getSalesPerDay(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    );

    /**
     * Recupera la cantidad de ventas agrupada por mes para la visualización de gráficos.
     *
     * <p>
     * La cantidad de ventas se calcula como el número total de registros de venta
     * agrupados por mes y año.
     * </p>
     *
     * <p>
     * Si {@code userId} es null, se incluyen las ventas de todos los usuarios.
     * </p>
     *
     * <p>
     * Los resultados se ordenan cronológicamente.
     * </p>
     *
     * @param userId filtro opcional por usuario (null = todos los usuarios)
     * @param startDate fecha de inicio del filtro
     * @param endDate fecha de fin del filtro
     * @return serie temporal de cantidad de ventas por mes
     */
    @Query(value = """
    SELECT
        TO_CHAR(s.sale_date, 'YYYY-MM') AS label,
        CAST(COUNT(s.sale_id) AS NUMERIC) AS value
    FROM core.sale s
    WHERE
        (
            :userId IS NULL OR
            s.user_id = :userId
        )
    AND
        s.sale_date BETWEEN :startDate AND :endDate
    GROUP BY TO_CHAR(s.sale_date, 'YYYY-MM')
    ORDER BY TO_CHAR(s.sale_date, 'YYYY-MM')
    """, nativeQuery = true)
    List<TimeSeriesProjection> getSalesPerMonth(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    );

    /**
     * Recupera la cantidad de ventas agrupada por año para la visualización de gráficos.
     *
     * <p>
     * La cantidad de ventas se calcula como el número total de registros de venta
     * agrupados por año.
     * </p>
     *
     * <p>
     * Si {@code userId} es null, se incluyen las ventas de todos los usuarios.
     * </p>
     *
     * <p>
     * Los resultados se ordenan cronológicamente.
     * </p>
     *
     * @param userId filtro opcional por usuario (null = todos los usuarios)
     * @param startDate fecha de inicio del filtro
     * @param endDate fecha de fin del filtro
     * @return serie temporal de cantidad de ventas por año
     */
    @Query(value = """
    SELECT
        TO_CHAR(s.sale_date, 'YYYY') AS label,
        CAST(COUNT(s.sale_id) AS NUMERIC) AS value
    FROM core.sale s
    WHERE
        (
            :userId IS NULL OR
            s.user_id = :userId
        )
    AND
        s.sale_date BETWEEN :startDate AND :endDate
    GROUP BY TO_CHAR(s.sale_date, 'YYYY')
    ORDER BY TO_CHAR(s.sale_date, 'YYYY')
    """, nativeQuery = true)
    List<TimeSeriesProjection> getSalesPerYear(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    );
}
