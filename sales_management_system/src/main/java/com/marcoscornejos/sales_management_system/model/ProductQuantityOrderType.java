package com.marcoscornejos.sales_management_system.model;

import lombok.Getter;

@Getter
public enum ProductQuantityOrderType {
    MOST_TO_LEAST("Most sold → least sold"),
    LEAST_TO_MOST("Least sold → most sold");

    private final String displayName;

    ProductQuantityOrderType(String displayName) {
        this.displayName = displayName;
    }
}
