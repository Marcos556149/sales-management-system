package com.marcoscornejos.sales_management_system.model;

import lombok.Getter;

@Getter
public enum ProductSalesFilterType {
    SOLD("Sold products"),
    UNSOLD("Unsold products"),
    ALL("All");

    private final String displayName;

    ProductSalesFilterType(String displayName) {
        this.displayName = displayName;
    }
}
