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
     * Represents all products included in the sale and their quantities.
     *
     * <p>The configuration ensures the following behaviors:</p>
     * <ul>
     *   <li><b>Persisting or merging the sale:</b> Any new or updated SaleDetail objects
     *       in this list are automatically persisted or merged in the database.</li>
     *   <li><b>Removing a SaleDetail from the list:</b> If a detail is removed from this
     *       list and the sale is persisted, the removed detail is also deleted from the database
     *       (orphanRemoval = true).</li>
     *   <li><b>Deleting the sale:</b> When a Sale is deleted, the database automatically deletes
     *       all associated SaleDetail rows thanks to the @OnDelete annotation, avoiding extra
     *       queries from JPA.</li>
     *   <li><b>No CascadeType.REMOVE:</b> Hibernate does not issue explicit delete queries for
     *       children when the parent is removed; deletion is delegated to the database cascade.</li>
     * </ul>
     */
    @OneToMany(
            mappedBy = "sale",
            cascade = { CascadeType.PERSIST, CascadeType.MERGE }, // <- NO REMOVE
            orphanRemoval = true
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<SaleDetail> saleDetails=new ArrayList<>();

    public Sale(LocalDate saleDate, LocalTime saleTime, BigDecimal totalAmount, User user) {
        this.saleDate = saleDate;
        this.saleTime = saleTime;
        this.totalAmount = totalAmount;
        this.user = user;
    }
}
