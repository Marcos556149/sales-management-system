package com.marcoscornejos.sales_management_system.model;

import lombok.Getter;

@Getter
public enum ProductStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive"),
    ALL("All");

    private final String displayName;

    ProductStatus(String displayName) {
        this.displayName = displayName;
    }
}
