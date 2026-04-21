/**
 * Represents a system user.
 *
 * <p>Stores authentication and authorization data such as username,
 * role, and password.</p>
 *
 * <p>Users are responsible for interacting with the system
 * according to their assigned role.</p>
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

    /** Unique identifier of the user. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "user_seq")
    @Column(name = "user_id")
    private Long userId;

    /** Username used for identification/login. */
    @Column(name = "user_name")
    private String userName;

    /**
     * Role assigned to the user (e.g., ADMIN, OPERATOR).
     *
     * <p>Stored as a string in the database for readability and stability.</p>
     */
    @Column(name = "user_role")
    @Enumerated(EnumType.STRING)
    private UserRole userRole=UserRole.OPERATOR;

    /** Encrypted password of the user. */
    @Column(name = "user_password")
    private String userPassword;

    /**
     * Interface language preference for the user.
     *
     * <p>Stored as a string to ensure consistency with supported system languages.</p>
     */
    @Column(name = "language")
    @Enumerated(EnumType.STRING)
    private Language language=Language.EN;

    /**
     * Logical status of the user (e.g., ACTIVE, SUSPENDED, DELETED).
     *
     * <p>Determines whether the user can access or interact with the system.</p>
     */
    @Column(name = "user_status")
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus=UserStatus.ACTIVE;

    public User(String userName, UserRole userRole, String userPassword, Language language, UserStatus userStatus) {
        this.userName = userName;
        this.userRole = userRole;
        this.userPassword = userPassword;
        this.language = language;
        this.userStatus=userStatus;
    }

}
