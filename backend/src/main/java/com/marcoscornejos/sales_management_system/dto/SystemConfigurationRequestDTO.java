package com.marcoscornejos.sales_management_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO used to update the global system configuration.
 *
 * <p>
 * Contains the configuration values that can be modified by the user.
 * These values are shared across the entire system.
 * </p>
 */
@Getter
@Setter
public class SystemConfigurationRequestDTO {

    /**
     * Name of the business.
     *
     * <p>
     * Must not be blank and must not exceed 100 characters.
     * This value is shared across all users.
     * </p>
     */
    @NotBlank(message = "Business name is required")
    @Size(max = 100, message = "Business name must not exceed 100 characters")
    private String businessName;

    /**
     * Address of the business.
     *
     * <p>
     * Must not be blank and must not exceed 200 characters.
     * This value is shared across all users.
     * </p>
     */
    @NotBlank(message = "Business address is required")
    @Size(max = 200, message = "Business address must not exceed 200 characters")
    private String businessAddress;
}