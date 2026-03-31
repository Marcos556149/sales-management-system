/**
 * Represents a line item within a sale.
 *
 * <p>Each sale detail links a product to a sale, including
 * the quantity sold, the price at the moment of the sale,
 * and the calculated subtotal.</p>
 *
 * <p>This entity preserves historical data, ensuring that
 * price changes in products do not affect past sales.</p>
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
     * */
    @ManyToOne
    @JoinColumn(name = "product_code")
    private Product product;

    /**
     * Sale to which this detail belongs.
     */
    @ManyToOne
    @JoinColumn(name = "sale_id")
    private Sale sale;

    /** Quantity of the product sold. */
    @Column(name = "product_quantity")
    private BigDecimal productQuantity;


    /**
     * Unit price of the product at the time of the sale.
     * Stored to preserve historical pricing.
     */
    @Column(name = "sale_price")
    private BigDecimal salePrice;

    /** Subtotal amount for this line (quantity * price). */
    private BigDecimal subtotal;

    public SaleDetail(Product product, Sale sale, BigDecimal productQuantity, BigDecimal salePrice, BigDecimal subtotal) {
        this.product = product;
        this.sale = sale;
        this.productQuantity = productQuantity;
        this.salePrice = salePrice;
        this.subtotal = subtotal;
    }


}
