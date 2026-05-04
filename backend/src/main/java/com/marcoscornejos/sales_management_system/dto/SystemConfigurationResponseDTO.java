package com.marcoscornejos.sales_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO used to represent system configuration data returned to the client.
 *
 * <p>
 * It includes global configuration values shared across all users,
 * such as the business name and business address.
 * </p>
 */
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class SystemConfigurationResponseDTO {

    /**
     * Name of the business.
     *
     * <p>
     * This value is global and shared across all users of the system.
     * </p>
     */
    private String businessName;

    /**
     * Address of the business.
     *
     * <p>
     * This value is global and shared across all users of the system.
     * </p>
     */
    private String businessAddress;
}