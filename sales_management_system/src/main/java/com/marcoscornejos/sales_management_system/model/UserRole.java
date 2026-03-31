package com.marcoscornejos.sales_management_system.model;

import lombok.Getter;

@Getter
public enum UserRole {
    ADMIN("Administrator"),
    OPERATOR("Operator");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }
}
