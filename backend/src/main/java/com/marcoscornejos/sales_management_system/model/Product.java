/**
 * Representa un producto disponible para la venta dentro del sistema.
 *
 * <p>
 * Almacena información básica del producto, como su nombre, precio,
 * stock, stock mínimo, estado y unidad de medida.
 * </p>
 *
 * <p>
 * Un producto puede estar asociado a múltiples detalles de venta,
 * representando su participación en diferentes ventas.
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
    /** Identificador único del producto. */
    @Id
    @Column(name = "product_code")
    private String productCode;

    /** Nombre del producto. */
    @Column(name = "product_name")
    private String productName;

    /** Precio unitario del producto. */
    @Column(name = "product_price")
    private BigDecimal productPrice;

    /** Cantidad disponible en stock. */
    @Column(name = "product_stock")
    private BigDecimal productStock;

    /**
     * Nivel mínimo de stock configurado para el producto.
     *
     * <p>
     * Se utiliza para determinar cuándo el producto debe considerarse
     * con bajo stock en las vistas e informes de inventario.
     * </p>
     */
    @Column(name = "minimum_stock")
    private BigDecimal minimumStock;

    /**
     * Estado lógico del producto (por ejemplo, ACTIVE o INACTIVe).
     *
     * <p>Se almacena como una cadena de texto en la base de datos para garantizar legibilidad y estabilidad.</p>
     */
    @Column(name = "product_status")
    @Enumerated(EnumType.STRING)
    private ProductStatus productStatus=ProductStatus.ACTIVE;


    /**
     * Unidad de medida del producto (por ejemplo, KILOGRAMS, UNITS o LITERS).
     *
     * <p>Se almacena como una cadena de texto en la base de datos para garantizar consistencia.</p>
     */
    @Column(name = "unit_of_measure")
    @Enumerated(EnumType.STRING)
    private UnitOfMeasure unitOfMeasure;

    /**
     * Detalles de venta asociados a este producto.
     * Representa todas las apariciones del producto en las ventas.
     */
    @OneToMany(mappedBy = "product")
    private List<SaleDetail> saleDetails=new ArrayList<>();
}
