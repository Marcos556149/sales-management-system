/**
 * Representa un usuario del sistema.
 *
 * <p>Almacena información de autenticación y autorización, como el nombre de usuario,
 * el rol y la contraseña.</p>
 *
 * <p>Los usuarios interactúan con el sistema de acuerdo con el rol que tengan asignado.</p>
 */

package com.marcoscornejos.sales_management_system.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@Entity
@Table(name = "user", schema = "core")
@SequenceGenerator(name = "user_seq",
        sequenceName = "core.user_seq",
        allocationSize = 1)
public class User {

    /** Identificador único del usuario. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "user_seq")
    @Column(name = "user_id")
    private Long userId;

    /** Nombre de usuario utilizado para la identificación/inicio de sesión. */
    @Column(name = "user_name")
    private String userName;

    /**
     * Rol asignado al usuario (por ejemplo, ADMINISTRATOR u OPERATOR).
     *
     * <p>Se almacena como una cadena de texto en la base de datos para mejorar
     * la legibilidad y la estabilidad.</p>
     */
    @Column(name = "user_role")
    @Enumerated(EnumType.STRING)
    private UserRole userRole=UserRole.OPERATOR;

    /** Contraseña cifrada del usuario. */
    @Column(name = "user_password")
    private String userPassword;

    /**
     * Estado lógico del usuario (por ejemplo, ACTIVE, SUSPENDED o DELETED).
     *
     * <p>Determina si el usuario puede acceder o interactuar con el sistema.</p>
     */
    @Column(name = "user_status")
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus=UserStatus.ACTIVE;

    public User(String userName, UserRole userRole, String userPassword, UserStatus userStatus) {
        this.userName = userName;
        this.userRole = userRole;
        this.userPassword = userPassword;
        this.userStatus=userStatus;
    }

}
