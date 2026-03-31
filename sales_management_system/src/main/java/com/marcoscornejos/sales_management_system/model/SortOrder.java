package com.marcoscornejos.sales_management_system.model;

import lombok.Getter;

@Getter
public enum SortOrder {
    ASCENDING("Ascending"),
    DESCENDING("Descending");

    private final String displayName;

    SortOrder(String displayName) {
        this.displayName = displayName;
    }
}
