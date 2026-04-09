package com.marcoscornejos.sales_management_system.dto;

import com.marcoscornejos.sales_management_system.dto.EnumDTO;
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

    /** Role assigned to the user (code + label). */
    private EnumDTO userRole;

    /** Preferred interface language (code + label). */
    private EnumDTO language;

}