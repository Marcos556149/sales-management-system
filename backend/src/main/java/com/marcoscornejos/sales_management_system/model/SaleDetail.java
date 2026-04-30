/**
 * Represents a line item within a sale.
 *
 * <p>Each sale detail links a product to a sale, including
 * the quantity sold, the unit of measure at the time of the sale,
 * the sale price at the moment of the transaction, and the calculated subtotal.</p>
 *
 * <p>This entity preserves historical transactional data by storing
 * a snapshot of product information at the time of the sale, including:
 * product name, sale price, and unit of measure.</p>
 *
 * <p>This ensures that changes in product information (such as name,
 * price, or unit of measure) do not affect past sales records.</p>
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

    /** Unique identifier of the sale detail. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "sale_detail_seq")
    @Column(name = "sale_detail_id")
    private Long saleDetailId;

    /**
     * Product associated with this sale detail.
     * Represents the item being sold.
     * Loaded lazily to avoid unnecessary queries unless accessed.
     * */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_code")
    private Product product;

    /**
     * Sale to which this detail belongs.
     * Loaded lazily as it is typically accessed through the sale context.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_id")
    private Sale sale;

    /** Quantity of the product sold. */
    @Column(name = "product_quantity")
    private BigDecimal productQuantity;

    /**
     * Unit of measure of the product at the time of the sale.
     * This value is stored as an immutable snapshot of the product's unit of measure
     * when the transaction is performed, ensuring historical accuracy even if the
     * product configuration is modified later.
     */
    @Column(name = "unit_of_measure_at_sale")
    @Enumerated(EnumType.STRING)
    private UnitOfMeasure unitOfMeasureAtSale;


    /**
     * Product name at the time of the sale.
     * This value is stored as an immutable snapshot to preserve historical accuracy
     * even if the product name changes later in the product catalog.
     */
    @Column(name = "product_name_at_sale")
    private String productNameAtSale;

    /**
     * Unit price of the product at the time of the sale.
     * Stored to preserve historical pricing.
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
