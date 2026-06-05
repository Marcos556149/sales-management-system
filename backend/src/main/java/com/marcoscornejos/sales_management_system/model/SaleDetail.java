/**
 * Representa una línea de detalle dentro de una venta.
 *
 * <p>Cada detalle de venta vincula un producto a una venta, incluyendo
 * la cantidad vendida, la unidad de medida al momento de la venta,
 * el precio de venta al momento de la transacción y el subtotal calculado.</p>
 *
 * <p>Esta entidad preserva datos históricos de la transacción almacenando
 * una instantánea de la información del producto al momento de la venta, incluyendo:
 * nombre del producto, precio de venta y unidad de medida.</p>
 *
 * <p>Esto garantiza que los cambios en la información del producto (como nombre,
 * precio o unidad de medida) no afecten los registros de ventas anteriores.</p>
 */

package com.marcoscornejos.sales_management_system.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor
@Entity
@Table(name = "sale_detail", schema = "core")
@SequenceGenerator(name = "sale_detail_seq",
        sequenceName = "core.sale_detail_seq",
        allocationSize = 1)
public class SaleDetail {

    /** Identificador único del detalle de venta. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "sale_detail_seq")
    @Column(name = "sale_detail_id")
    private Long saleDetailId;

    /**
     * Producto asociado a este detalle de venta.
     * Representa el artículo vendido.
     * Se carga de forma diferida (lazy) para evitar consultas innecesarias
     * hasta que sea accedido.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_code")
    private Product product;

    /**
     * Venta a la que pertenece este detalle.
     * Se carga de forma diferida (lazy), ya que normalmente se accede
     * a través del contexto de la venta.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_id")
    private Sale sale;

    /** Cantidad del producto vendido. */
    @Column(name = "product_quantity")
    private BigDecimal productQuantity;

    /**
     * Unidad de medida del producto al momento de la venta.
     * Este valor se almacena como una instantánea inmutable de la unidad de medida
     * del producto cuando se realiza la transacción, garantizando precisión histórica
     * incluso si la configuración del producto es modificada posteriormente.
     */
    @Column(name = "unit_of_measure_at_sale")
    @Enumerated(EnumType.STRING)
    private UnitOfMeasure unitOfMeasureAtSale;


    /**
     * Nombre del producto al momento de la venta.
     * Este valor se almacena como una instantánea inmutable para preservar
     * la precisión histórica incluso si el nombre del producto cambia posteriormente
     * en el catálogo de productos.
     */
    @Column(name = "product_name_at_sale")
    private String productNameAtSale;

    /**
     * Precio unitario del producto al momento de la venta.
     * Se almacena para preservar el historial de precios.
     */
    @Column(name = "sale_price")
    private BigDecimal salePrice;

    public SaleDetail(Product product, Sale sale, BigDecimal productQuantity, BigDecimal salePrice, UnitOfMeasure unitOfMeasureAtSale, String productNameAtSale) {
        this.product = product;
        this.sale = sale;
        this.productQuantity = productQuantity;
        this.salePrice = salePrice;
        this.unitOfMeasureAtSale = unitOfMeasureAtSale;
        this.productNameAtSale = productNameAtSale;
    }


}
