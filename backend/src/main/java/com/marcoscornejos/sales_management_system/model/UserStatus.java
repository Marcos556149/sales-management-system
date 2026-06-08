package com.marcoscornejos.sales_management_system.model;

/**
 * Enumeración que define los posibles estados de un usuario dentro del sistema.
 *
 * <p>
 * Se utiliza para determinar si un usuario puede acceder e interactuar con
 * las funcionalidades del sistema.
 * </p>
 *
 * <p>
 * Los usuarios con estado activo pueden iniciar sesión y utilizar el sistema,
 * mientras que los usuarios suspendidos o eliminados tienen el acceso restringido.
 * </p>
 *
 * <p>
 * Cada valor de la enumeración incluye un nombre descriptivo destinado a su
 * representación en la interfaz de usuario.
 * </p>
 */

import lombok.Getter;

@Getter
public enum UserStatus {
    ACTIVE("Activo"),
    SUSPENDED("Suspendido"),
    DELETED("Eliminado");

    private final String displayName;

    UserStatus(String displayName) {
        this.displayName = displayName;
    }
}
