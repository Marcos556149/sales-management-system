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

    /** Role assigned to the user (e.g., Administrator, Operator). */
    @Column(name = "user_role")
    private String userRole;

    /** Encrypted password of the user. */
    @Column(name = "user_password")
    private String userPassword;

    public User(String userName, String userRole, String userPassword) {
        this.userName = userName;
        this.userRole = userRole;
        this.userPassword = userPassword;
    }

}
