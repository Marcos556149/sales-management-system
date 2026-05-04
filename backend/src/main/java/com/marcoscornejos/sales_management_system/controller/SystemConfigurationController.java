package com.marcoscornejos.sales_management_system.controller;

import com.marcoscornejos.sales_management_system.dto.SuccessResponseDTO;
import com.marcoscornejos.sales_management_system.dto.SystemConfigurationRequestDTO;
import com.marcoscornejos.sales_management_system.dto.SystemConfigurationResponseDTO;
import com.marcoscornejos.sales_management_system.service.ISystemConfigurationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller responsible for managing global system configuration.
 *
 * <p>
 * Provides endpoints to retrieve and update system-wide configuration values
 * such as business name and business address.
 * </p>
 */
@RestController
@RequestMapping("/api/configuration")
@RequiredArgsConstructor
public class SystemConfigurationController {

    private final ISystemConfigurationService iSystemConfigurationService;

    /**
     * Retrieves the current system configuration.
     *
     * <p>
     * Returns global configuration values shared across all users,
     * including business name and business address.
     * </p>
     *
     * @return The current system configuration
     */
    @GetMapping
    public ResponseEntity<SystemConfigurationResponseDTO> getConfiguration() {

        SystemConfigurationResponseDTO response =
                iSystemConfigurationService.getConfiguration();

        return ResponseEntity.ok(response);
    }

    /**
     * Updates the global system configuration.
     *
     * <p>
     * Allows modifying system-wide configuration values such as
     * business name and business address.
     * These values are shared across all users of the system.
     * </p>
     *
     * <p>
     * Only users with administrator privileges are authorized
     * to perform this operation.
     * </p>
     *
     * <p>
     * The system validates the input data before applying changes.
     * If validation fails, an error response is returned indicating
     * the invalid fields.
     * </p>
     *
     * @param request the new configuration values to be applied
     * @return a standardized success response containing the updated configuration
     */
    @PutMapping
    public ResponseEntity<SuccessResponseDTO<SystemConfigurationResponseDTO>> updateConfiguration(
            @Valid @RequestBody SystemConfigurationRequestDTO request
    ) {

        SystemConfigurationResponseDTO updatedConfig =
                iSystemConfigurationService.updateConfiguration(request);

        SuccessResponseDTO<SystemConfigurationResponseDTO> response =
                new SuccessResponseDTO<>(
                        "CONFIGURATION_UPDATED",
                        "System configuration successfully updated",
                        updatedConfig
                );

        return ResponseEntity.ok(response);
    }

}