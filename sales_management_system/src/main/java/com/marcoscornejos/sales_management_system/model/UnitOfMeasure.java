package com.marcoscornejos.sales_management_system.model;

import lombok.Getter;

@Getter
public enum UnitOfMeasure {
    UNITS("Units"),
    KILOGRAMS("Kilograms"),
    LITERS("Liters");

    private final String displayName;

    UnitOfMeasure(String displayName) {
        this.displayName = displayName;
    }
}
