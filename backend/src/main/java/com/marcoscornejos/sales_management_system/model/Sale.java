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
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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

    /**
     * Date when the sale was made.
     *
     * <p>Defaults to the current system date when the entity is created</p>
     */
    @Column(name = "sale_date")
    private LocalDate saleDate=LocalDate.now();

    /**
     * Time when the sale was made.
     *
     * <p>Defaults to the current system time when the entity is created</p>
     */
    @Column(name = "sale_time")
    private LocalTime saleTime=LocalTime.now();

    /** Total monetary amount of the sale. */
    @Column(name = "total_amount")
    private BigDecimal totalAmount=BigDecimal.ZERO;

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
     * Represents all products included in the sale and their quantities.
     *
     * <p>The configuration ensures the following behavior:</p>
     * <ul>
     *   <li><b>Persisting the sale:</b> Any new {@code SaleDetail} objects
     *       contained in this list are automatically persisted together with
     *       the parent sale.</li>
     *   <li><b>No automatic updates or removals:</b> Existing details are not
     *       merged or deleted through cascade operations, since sales are treated
     *       as historical records after registration.</li>
     *   <li><b>Relationship ownership:</b> The foreign key is managed by the
     *       {@code SaleDetail.sale} side, while this collection allows
     *       navigation from the sale to its details.</li>
     *   <li><b>Safe initialization:</b> The collection is initialized by default
     *       to avoid null references when adding details to a new sale.</li>
     * </ul>
     */
    @OneToMany(
            mappedBy = "sale",
            cascade = CascadeType.PERSIST
    )
    private List<SaleDetail> saleDetails = new ArrayList<>();

    public Sale(LocalDate saleDate, LocalTime saleTime, BigDecimal totalAmount, User user) {
        this.saleDate = saleDate;
        this.saleTime = saleTime;
        this.totalAmount = totalAmount;
        this.user = user;
    }
}
