/**
 * Enumeración que define los roles asignados a los usuarios del sistema.
 *
 * <p>
 * Se utiliza para controlar los accesos y permisos dentro de la aplicación,
 * distinguiendo entre usuarios administradores y operadores.
 * </p>
 *
 * <p>
 * Esta enumeración desempeña un papel fundamental en la lógica de autorización,
 * determinando qué acciones tiene permitido realizar cada usuario.
 * </p>
 *
 * <p>
 * Cada valor de la enumeración incluye un nombre descriptivo destinado a su
 * representación en la interfaz de usuario.
 * </p>
 */

package com.marcoscornejos.sales_management_system.model;

import lombok.Getter;

@Getter
public enum UserRole {
    ADMIN("Administrador"),
    OPERATOR("Operador");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }
}
