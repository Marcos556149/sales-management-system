/**
 * Interfaz de repositorio para la gestión de entidades {@link Product}.
 *
 * <p>
 * Extiende {@link JpaRepository}, proporcionando operaciones CRUD estándar
 * como save, delete, findById y findAll sin requerir una implementación explícita.
 * </p>
 *
 * <p>
 * Este repositorio es responsable del acceso a datos relacionado con los productos.
 * Aquí pueden definirse métodos de consulta personalizados cuando sea necesario.
 * </p>
 */

package com.marcoscornejos.sales_management_system.repository;

import com.marcoscornejos.sales_management_system.model.Product;
import com.marcoscornejos.sales_management_system.model.ProductStatus;
import com.marcoscornejos.sales_management_system.projection.SoldProductProjection;
import com.marcoscornejos.sales_management_system.projection.TopProductsByQuantityProjection;
import com.marcoscornejos.sales_management_system.projection.TopProductsByRevenueProjection;
import com.marcoscornejos.sales_management_system.projection.UnsoldProductProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface IProductRepository extends JpaRepository<Product, String> {
    /**
     * Recupera productos aplicando opcionalmente búsqueda, filtrado por estado
     * y filtrado por nivel de stock, utilizando paginación y ordenamiento
     * a nivel de base de datos.
     *
     * <p>
     * La búsqueda se realiza sobre el nombre o código del producto
     * (sin distinguir mayúsculas de minúsculas y permitiendo coincidencias parciales).
     * Si {@code searchCodeOrName} es null, la búsqueda se ignora.
     * </p>
     *
     * <p>
     * Si {@code statusFilter} es ALL, no se aplica ningún filtro por estado.
     * </p>
     *
     * <p>
     * Si {@code stockFilter} es ALL, no se aplica ningún filtro por nivel de stock.
     * </p>
     *
     * <p>
     * La paginación y el ordenamiento se gestionan mediante {@link Pageable},
     * garantizando una recuperación eficiente de los datos directamente desde la base de datos
     * (paginación del lado del servidor).
     * </p>
     *
     * @param searchCodeOrName término de búsqueda opcional (nombre o código)
     * @param statusFilter filtro de estado del producto (ALL, ACTIVE, INACTIVE)
     * @param stockFilter filtro por nivel de stock (ALL, NORMAL, LOW, OUT_OF_STOCK)
     * @param pageable configuración de paginación y ordenamiento
     * @return resultado paginado de los productos que coinciden con los criterios
     */
    @Query("""
    SELECT p FROM Product p
    WHERE
        (
            :searchCodeOrName IS NULL OR
            LOWER(p.productName) LIKE LOWER(CONCAT('%', CAST(:searchCodeOrName AS string), '%')) OR
            LOWER(p.productCode) LIKE LOWER(CONCAT('%', CAST(:searchCodeOrName AS string), '%'))
        )
    AND
        (
            :statusFilter = 'ALL' OR
            p.productStatus = :statusFilter
        )
    AND
        (
            :stockFilter = 'ALL'
            OR (:stockFilter = 'NORMAL' AND p.productStock > p.minimumStock)
            OR (:stockFilter = 'LOW' AND p.productStock > 0 AND p.productStock <= p.minimumStock)
            OR (:stockFilter = 'OUT_OF_STOCK' AND p.productStock = 0)
        )
    """)
    Page<Product> findProducts(String searchCodeOrName,
                               ProductStatus statusFilter,
                               String stockFilter,
                               Pageable pageable);

    /**
     * Recupera los productos disponibles para la venta aplicando opcionalmente
     * una búsqueda, utilizando paginación y ordenamiento a nivel de base de datos.
     *
     * <p>
     * Solo se incluyen productos con estado ACTIVE y stock mayor que cero.
     * </p>
     *
     * <p>
     * La búsqueda se realiza sobre el nombre o código del producto
     * (sin distinguir mayúsculas de minúsculas y permitiendo coincidencias parciales).
     * Si {@code searchCodeOrName} es null, la búsqueda se ignora.
     * </p>
     *
     * <p>
     * La paginación y el ordenamiento se gestionan mediante {@link Pageable},
     * garantizando una recuperación eficiente de los datos directamente desde la base de datos
     * (paginación del lado del servidor).
     * </p>
     *
     * @param searchCodeOrName término de búsqueda opcional (nombre o código)
     * @param pageable configuración de paginación y ordenamiento
     * @return resultado paginado de los productos disponibles para la venta
     */
    @Query("""
    SELECT p FROM Product p
    WHERE
        (
            :searchCodeOrName IS NULL OR
            LOWER(p.productName) LIKE LOWER(CONCAT('%', CAST(:searchCodeOrName AS string), '%')) OR
            LOWER(p.productCode) LIKE LOWER(CONCAT('%', CAST(:searchCodeOrName AS string), '%'))
        )
    AND
        p.productStatus = 'ACTIVE'
    AND
        p.productStock > 0
    """)
    Page<Product> findProductsForSale(String searchCodeOrName,
                                      Pageable pageable);

    /**
     * Cuenta la cantidad total de productos disponibles para la venta.
     *
     * <p>
     * Solo se incluyen productos con estado ACTIVE y stock mayor
     * que el valor proporcionado.
     * </p>
     *
     * @param productStatus estado requerido del producto
     * @param productStock umbral mínimo exclusivo de stock
     * @return cantidad total de productos que cumplen los criterios
     */
    Long countByProductStatusAndProductStockGreaterThan(
            ProductStatus productStatus,
            BigDecimal productStock
    );

    /**
     * Recupera los 10 productos con mayor cantidad vendida
     * dentro de los filtros seleccionados.
     *
     * <p>
     * Los resultados se calculan utilizando datos de {@code SaleDetail},
     * agregando las cantidades de productos a través de todas las ventas
     * comprendidas en el rango de fechas especificado.
     * </p>
     *
     * <p>
     * La cantidad representa el total acumulado vendido y puede incluir
     * valores fraccionarios dependiendo del tipo de unidad de medida del producto
     * (por ejemplo, kilogramos, litros u otras unidades cuantificables).
     * </p>
     *
     * <p>
     * Si {@code userId} es null, se incluyen las ventas de todos los usuarios.
     * </p>
     *
     * <p>
     * Los resultados se agrupan por producto y se ordenan por cantidad total vendida
     * de forma descendente, devolviendo únicamente los 10 primeros productos.
     * </p>
     *
     * @param userId filtro opcional por usuario (null = todos los usuarios)
     * @param startDate fecha de inicio del filtro (incluida)
     * @param endDate fecha de fin del filtro (incluida)
     * @return los 10 productos con mayor cantidad vendida
     */
    @Query(value = """
    SELECT
        p.product_code AS productCode,
        p.product_name AS productName,
        SUM(sd.product_quantity) AS quantitySold
    FROM core.sale_detail sd
    INNER JOIN core.sale s ON s.sale_id = sd.sale_id
    INNER JOIN core.product p ON p.product_code = sd.product_code
    WHERE
        (:userId IS NULL OR s.user_id = :userId)
    AND
        s.sale_date BETWEEN :startDate AND :endDate
    GROUP BY p.product_code, p.product_name
    ORDER BY quantitySold DESC
    LIMIT 10
    """, nativeQuery = true)
    List<TopProductsByQuantityProjection> findTopByQuantity(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    );

    /**
     * Recupera los 10 productos con mayores ingresos generados
     * dentro de los filtros seleccionados.
     *
     * <p>
     * Los ingresos se calculan como la suma de:
     * {@code product_quantity * sale_price}
     * para todos los detalles de venta que coinciden con los criterios
     * dentro del rango de fechas especificado.
     * </p>
     *
     * <p>
     * Si {@code userId} es null, se incluyen las ventas de todos los usuarios.
     * </p>
     *
     * <p>
     * Los resultados se agrupan por producto y se ordenan por ingresos totales generados
     * de forma descendente, devolviendo únicamente los 10 primeros productos.
     * </p>
     *
     * @param userId filtro opcional por usuario (null = todos los usuarios)
     * @param startDate fecha de inicio del filtro (incluida)
     * @param endDate fecha de fin del filtro (incluida)
     * @return los 10 productos con mayores ingresos generados
     */
    @Query(value = """
    SELECT
        p.product_code AS productCode,
        p.product_name AS productName,
        SUM(
            COALESCE(sd.product_quantity, 0)
            *
            COALESCE(sd.sale_price, 0)
        ) AS revenueGenerated
    FROM core.sale_detail sd
    INNER JOIN core.sale s ON s.sale_id = sd.sale_id
    INNER JOIN core.product p ON p.product_code = sd.product_code
    WHERE
        (:userId IS NULL OR s.user_id = :userId)
    AND
        s.sale_date BETWEEN :startDate AND :endDate
    GROUP BY p.product_code, p.product_name
    ORDER BY
        SUM(
            COALESCE(sd.product_quantity, 0)
            *
            COALESCE(sd.sale_price, 0)
        ) DESC
    LIMIT 10
    """, nativeQuery = true)
    List<TopProductsByRevenueProjection> findTopByRevenue(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    );

    /**
     * Recupera un ranking paginado de productos vendidos
     * ordenado de mayor a menor según los valores obtenidos.
     *
     * <p>
     * El ranking puede calcularse utilizando:
     * <ul>
     *   <li>Cantidad vendida</li>
     *   <li>Ingresos generados</li>
     * </ul>
     * </p>
     *
     * <p>
     * La paginación se realiza directamente a nivel de base de datos.
     * </p>
     *
     * @param userId filtro opcional por usuario
     * @param startDate fecha de inicio del filtro
     * @param endDate fecha de fin del filtro
     * @param metric métrica utilizada para el ranking
     * @param pageable configuración de paginación
     * @return ranking paginado de productos vendidos
     */
    @Query(
            value = """
    SELECT
        p.product_code AS productCode,
        p.product_name AS productName,
    
        SUM(sd.product_quantity) AS quantitySold,
    
        SUM(
            COALESCE(sd.product_quantity,0)
            *
            COALESCE(sd.sale_price,0)
        ) AS revenueGenerated
    
    FROM core.sale_detail sd
    
    INNER JOIN core.sale s
        ON s.sale_id = sd.sale_id
    
    INNER JOIN core.product p
        ON p.product_code = sd.product_code
    
    WHERE
        (:userId IS NULL OR s.user_id = :userId)
    
    AND
        s.sale_date BETWEEN :startDate AND :endDate
    
    GROUP BY
        p.product_code,
        p.product_name
    
    ORDER BY
        CASE
            WHEN :metric='QUANTITY_SOLD'
            THEN SUM(sd.product_quantity)
    
            WHEN :metric='REVENUE_GENERATED'
            THEN SUM(
                COALESCE(sd.product_quantity,0)
                *
                COALESCE(sd.sale_price,0)
            )
        END DESC,
    
        p.product_code ASC
    """,

            countQuery = """
    SELECT COUNT(*)
    FROM (
    
        SELECT
            p.product_code
    
        FROM core.sale_detail sd
    
        INNER JOIN core.sale s
            ON s.sale_id = sd.sale_id
    
        INNER JOIN core.product p
            ON p.product_code = sd.product_code
    
        WHERE
            (:userId IS NULL OR s.user_id = :userId)
    
        AND
            s.sale_date BETWEEN :startDate AND :endDate
    
        GROUP BY p.product_code
    
    ) rankingCount
    """,

            nativeQuery = true
    )
    Page<SoldProductProjection> findRankingDesc(
            Long userId,
            LocalDate startDate,
            LocalDate endDate,
            String metric,
            Pageable pageable
    );

    /**
     * Recupera un ranking paginado de productos vendidos
     * ordenado de menor a mayor según los valores obtenidos.
     *
     * @param userId filtro opcional por usuario
     * @param startDate fecha de inicio del filtro
     * @param endDate fecha de fin del filtro
     * @param metric métrica utilizada para el ranking
     * @param pageable configuración de paginación
     * @return ranking paginado de productos vendidos
     */
    @Query(
            value = """
    SELECT
        p.product_code AS productCode,
        p.product_name AS productName,
    
        SUM(sd.product_quantity) AS quantitySold,
    
        SUM(
            COALESCE(sd.product_quantity,0)
            *
            COALESCE(sd.sale_price,0)
        ) AS revenueGenerated
    
    FROM core.sale_detail sd
    
    INNER JOIN core.sale s
        ON s.sale_id = sd.sale_id
    
    INNER JOIN core.product p
        ON p.product_code = sd.product_code
    
    WHERE
        (:userId IS NULL OR s.user_id = :userId)
    
    AND
        s.sale_date BETWEEN :startDate AND :endDate
    
    GROUP BY
        p.product_code,
        p.product_name
    
    ORDER BY
        CASE
            WHEN :metric='QUANTITY_SOLD'
            THEN SUM(sd.product_quantity)
    
            WHEN :metric='REVENUE_GENERATED'
            THEN SUM(
                COALESCE(sd.product_quantity,0)
                *
                COALESCE(sd.sale_price,0)
            )
        END ASC,
    
        p.product_code ASC
    """,

            countQuery = """
    SELECT COUNT(*)
    FROM (
    
        SELECT
            p.product_code
    
        FROM core.sale_detail sd
    
        INNER JOIN core.sale s
            ON s.sale_id = sd.sale_id
    
        INNER JOIN core.product p
            ON p.product_code = sd.product_code
    
        WHERE
            (:userId IS NULL OR s.user_id = :userId)
    
        AND
            s.sale_date BETWEEN :startDate AND :endDate
    
        GROUP BY p.product_code
    
    ) rankingCount
    """,

            nativeQuery = true
    )
    Page<SoldProductProjection> findRankingAsc(
            Long userId,
            LocalDate startDate,
            LocalDate endDate,
            String metric,
            Pageable pageable
    );

    /**
     * Recupera una lista paginada de productos que no han
     * registrado ventas dentro del rango de filtros seleccionado.
     *
     * <p>
     * Un producto se considera "no vendido" cuando no existen
     * registros de venta que coincidan con los filtros seleccionados.
     * </p>
     *
     * <p>
     * Filtros soportados:
     * <ul>
     *     <li>Filtro por usuario (opcional)</li>
     *     <li>Filtro por rango de fechas</li>
     * </ul>
     * </p>
     *
     * <p>
     * La consulta utiliza una cláusula NOT EXISTS para garantizar
     * que solo se devuelvan productos sin ventas asociadas que
     * coincidan con los criterios seleccionados.
     * </p>
     *
     * <p>
     * Los resultados se ordenan alfabéticamente por nombre de producto
     * y se paginan a nivel de base de datos.
     * </p>
     *
     * @param userId filtro opcional por usuario
     * @param startDate fecha de inicio del filtro
     * @param endDate fecha de fin del filtro
     * @param pageable configuración de paginación
     * @return lista paginada de productos no vendidos
     */
    @Query(
            value = """
    SELECT
        p.product_code AS productCode,
        p.product_name AS productName
    
    FROM core.product p
    
    WHERE NOT EXISTS (
    
        SELECT 1
        FROM core.sale_detail sd
    
        INNER JOIN core.sale s
            ON s.sale_id = sd.sale_id
    
        WHERE
            sd.product_code = p.product_code
        AND
            (:userId IS NULL OR s.user_id = :userId)
        AND
            s.sale_date BETWEEN :startDate AND :endDate
    )
    
    ORDER BY
        p.product_name ASC,
        p.product_code ASC
    """,

            countQuery = """
    SELECT COUNT(*)
    
    FROM core.product p
    
    WHERE NOT EXISTS (
    
        SELECT 1
        FROM core.sale_detail sd
    
        INNER JOIN core.sale s
            ON s.sale_id = sd.sale_id
    
        WHERE
            sd.product_code = p.product_code
        AND
            (:userId IS NULL OR s.user_id = :userId)
        AND
            s.sale_date BETWEEN :startDate AND :endDate
    )
    """,

            nativeQuery = true
    )
    Page<UnsoldProductProjection> findUnsoldProducts(
            Long userId,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    );
}
