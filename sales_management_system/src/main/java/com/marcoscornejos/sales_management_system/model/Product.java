/**
 * Represents a product available for sale in the system.
 *
 * <p>Stores basic product information such as name, price, stock,
 * status (active/inactive), and unit of measure.</p>
 *
 * <p>A product can be associated with multiple sale details,
 * representing its participation in different sales.</p>
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

    /** Logical status of the product (true = Active, false = Inactive). */
    @Column(name = "product_status")
    private boolean productStatus;

    /** Unit of measure (e.g., Kilograms, Units, Liters). */
    @Column(name = "unit_of_measure")
    private String unitOfMeasure;

    /**
     * Sale details associated with this product.
     * Represents all occurrences of the product in sales.
     */
    @OneToMany(mappedBy = "product")
    private List<SaleDetail> saleDetails=new ArrayList<>();
}
