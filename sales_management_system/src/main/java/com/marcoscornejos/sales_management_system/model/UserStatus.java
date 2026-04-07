package com.marcoscornejos.sales_management_system.model;

import lombok.Getter;

@Getter
public enum UserStatus {
    ACTIVE("Active"),
    SUSPENDED("Suspended"),
    DELETED("Deleted");

    private final String displayName;

    UserStatus(String displayName) {
        this.displayName = displayName;
    }
}
