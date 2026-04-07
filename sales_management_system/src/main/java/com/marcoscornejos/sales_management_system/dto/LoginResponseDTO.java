package com.marcoscornejos.sales_management_system.dto;

import com.marcoscornejos.sales_management_system.model.Language;
import com.marcoscornejos.sales_management_system.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO representing the information returned to the client
 * after a successful login.
 *
 * <p>Contains user-visible details such as username, role,
 * and preferred interface language.</p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {

    /** Username of the authenticated user. */
    private String userName;

    /** Role assigned to the user. */
    private UserRole userRole;

    /** Preferred interface language of the user. */
    private Language language;

}