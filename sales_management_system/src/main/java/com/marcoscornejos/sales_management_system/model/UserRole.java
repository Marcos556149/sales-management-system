/**
 * Enumeration that defines the roles assigned to system users.
 *
 * <p>
 * Used to control access and permissions within the application,
 * distinguishing between administrative users and regular operators.
 * </p>
 *
 * <p>
 * This enum plays a key role in authorization logic, determining
 * which actions a user is allowed to perform.
 * </p>
 *
 * <p>
 * Each enum value includes a human-readable display name intended for UI representation.
 * </p>
 */

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
