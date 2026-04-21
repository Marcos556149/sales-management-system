/**
 * Represents a product available for sale in the system.
 *
 * <p>
 * Stores basic product information such as name, price, stock,
 * minimum stock level, status, and unit of measure.
 * </p>
 *
 * <p>
 * A product can be associated with multiple sale details,
 * representing its participation in different sales.
 * </p>
 */

package com.marcoscornejos.sales_management_system.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "product",schema = "core")
public class Product {
    /** Unique identifier of the product. */
    @Id
    @Column(name = "product_code")
    private String productCode;

    /** Name of the product. */
    @Column(name = "product_name")
    private String productName;

    /** Unit price of the product. */
    @Column(name = "product_price")
    private BigDecimal productPrice;

    /** Available stock quantity. */
    @Column(name = "product_stock")
    private BigDecimal productStock;

    /**
     * Minimum stock level configured for the product.
     *
     * <p>
     * Used to determine when the product should be considered
     * low stock in inventory views and reports.
     * </p>
     */
    @Column(name = "minimum_stock")
    private BigDecimal minimumStock;

    /**
     * Logical status of the product (e.g., ACTIVE, INACTIVE).
     *
     * <p>Stored as a string in the database for readability and stability.</p>
     */
    @Column(name = "product_status")
    @Enumerated(EnumType.STRING)
    private ProductStatus productStatus=ProductStatus.ACTIVE;

    /**
     * Unit of measure for the product (e.g., KILOGRAMS, UNITS, LITERS).
     *
     * <p>Stored as a string in the database to ensure consistency.</p>
     */
    @Column(name = "unit_of_measure")
    @Enumerated(EnumType.STRING)
    private UnitOfMeasure unitOfMeasure;

    /**
     * Sale details associated with this product.
     * Represents all occurrences of the product in sales.
     */
    @OneToMany(mappedBy = "product")
    private List<SaleDetail> saleDetails=new ArrayList<>();
}
