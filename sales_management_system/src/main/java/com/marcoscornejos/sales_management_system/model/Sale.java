/**
 * Represents a sale transaction in the system.
 *
 * <p>Stores information about the sale such as date, time,
 * total amount, and the user responsible for the transaction.</p>
 *
 * <p>A sale is composed of multiple sale details,
 * each representing a product included in the transaction.</p>
 */

package com.marcoscornejos.sales_management_system.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@Entity
@Table(name = "sale", schema = "core")
@SequenceGenerator(name = "sale_seq",
        sequenceName = "core.sale_seq",
        allocationSize = 1)
public class Sale {

    /** Unique identifier of the sale. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "sale_seq")
    @Column(name = "sale_id")
    private Long saleId;

    /** Date when the sale was made. */
    @Column(name = "sale_date")
    private LocalDate saleDate;

    /** Time when the sale was made. */
    @Column(name = "sale_time")
    private LocalTime saleTime;

    /** Total monetary amount of the sale. */
    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    /**
     * User responsible for the sale.
     *
     * <p>Represents the user who performed the transaction.</p>
     *
     * <p>Loaded lazily to avoid unnecessary queries unless accessed.</p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * List of sale details associated with this sale.
     * Includes all products and quantities involved.
     *
     * <p>Cascade ALL ensures that changes to the sale
     * propagate to its details. Orphan removal ensures
     * that removed details are deleted from the database.</p>
     */
    @OneToMany(mappedBy = "sale",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<SaleDetail> saleDetails=new ArrayList<>();

    public Sale(LocalDate saleDate, LocalTime saleTime, BigDecimal totalAmount, User user) {
        this.saleDate = saleDate;
        this.saleTime = saleTime;
        this.totalAmount = totalAmount;
        this.user = user;
    }
}
