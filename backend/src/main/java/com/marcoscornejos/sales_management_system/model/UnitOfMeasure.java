/**
 * Enumeración que define la unidad de medida de los productos.
 *
 * <p>
 * Se utiliza para representar cómo se cuantifica un producto en el
 * inventario y en las ventas, ya sea por unidades, kilogramos o litros.
 * </p>
 *
 * <p>
 * Esta enumeración es fundamental para gestionar correctamente las
 * cantidades de los productos, especialmente al integrarse con
 * funcionalidades como productos vendidos por peso
 * (por ejemplo, balanzas) o ventas a granel.
 * </p>
 *
 * <p>
 * Cada valor de la enumeración incluye un nombre descriptivo destinado
 * a su representación en la interfaz de usuario.
 * </p>
 */

package com.marcoscornejos.sales_management_system.model;

import lombok.Getter;

@Getter
public enum UnitOfMeasure {
    UNITS("Unidades","u"),
    KILOGRAMS("Kilogramos","kg"),
    LITERS("Litros","lt");

    private final String displayName;
    private final String abbreviation;

    UnitOfMeasure(String displayName, String abbreviation) {
        this.displayName = displayName;
        this.abbreviation = abbreviation;
    }
}
