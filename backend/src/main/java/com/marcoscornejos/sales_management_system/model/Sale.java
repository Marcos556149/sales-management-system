/**
 * Representa una transacción de venta en el sistema.
 *
 * <p>Almacena información de la venta, como la fecha, hora,
 * importe total y el usuario responsable de la transacción.</p>
 *
 * <p>Una venta está compuesta por múltiples detalles de venta,
 * cada uno representando un producto incluido en la transacción.</p>
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

    /** Identificador único de la venta. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "sale_seq")
    @Column(name = "sale_id")
    private Long saleId;

    /**
     * Fecha en la que se realizó la venta.
     *
     * <p>Por defecto se asigna la fecha actual del sistema cuando la entidad es creada.</p>
     */
    @Column(name = "sale_date")
    private LocalDate saleDate;

    /**
     * Hora en la que se realizó la venta.
     *
     * <p>Por defecto se asigna la hora actual del sistema cuando la entidad es creada.</p>
     */
    @Column(name = "sale_time")
    private LocalTime saleTime;

    /** Importe monetario total de la venta. */
    @Column(name = "total_amount")
    private BigDecimal totalAmount=BigDecimal.ZERO;

    /**
     * Usuario responsable de la venta.
     *
     * <p>Representa al usuario que realizó la transacción.</p>
     *
     * <p>Se carga de forma diferida (lazy) para evitar consultas innecesarias
     * hasta que sea accedido.</p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * Lista de detalles de venta asociados a esta venta.
     * Representa todos los productos incluidos en la venta y sus cantidades.
     *
     * <p>La configuración garantiza el siguiente comportamiento:</p>
     * <ul>
     *   <li><b>Persistencia de la venta:</b> Cualquier objeto {@code SaleDetail}
     *       nuevo contenido en esta lista se persiste automáticamente junto con
     *       la venta padre.</li>
     *   <li><b>Sin actualizaciones ni eliminaciones automáticas:</b> Los detalles
     *       existentes no se fusionan ni eliminan mediante operaciones de cascada,
     *       ya que las ventas son tratadas como registros históricos después de su registro.</li>
     *   <li><b>Propiedad de la relación:</b> La clave foránea es gestionada por el lado
     *       {@code SaleDetail.sale}, mientras que esta colección permite navegar
     *       desde la venta hacia sus detalles.</li>
     *   <li><b>Inicialización segura:</b> La colección se inicializa por defecto
     *       para evitar referencias nulas al agregar detalles a una nueva venta.</li>
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
