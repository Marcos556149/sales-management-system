package com.marcoscornejos.sales_management_system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for receiving login data from the frontend.
 *
 * <p>This object contains the username and password entered
 * by the user on the login form.</p>
 *
 * <p>Validation ensures that neither field is left blank.</p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {

    /** Username entered by the user. Must not be blank. */
    @NotBlank(message = "Username cannot be blank")
    private String userName;

    /** Password entered by the user. Must not be blank. */
    @NotBlank(message = "Password cannot be blank")
    private String userPassword;
}