package com.marcoscornejos.sales_management_system.service;

import com.marcoscornejos.sales_management_system.dto.SystemConfigurationRequestDTO;
import com.marcoscornejos.sales_management_system.dto.SystemConfigurationResponseDTO;
import com.marcoscornejos.sales_management_system.exception.SystemConfigurationNotFoundException;
import com.marcoscornejos.sales_management_system.mapper.ISystemConfigurationRequestMapper;
import com.marcoscornejos.sales_management_system.mapper.ISystemConfigurationResponseMapper;
import com.marcoscornejos.sales_management_system.model.SystemConfiguration;
import com.marcoscornejos.sales_management_system.repository.ISystemConfigurationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SystemConfigurationService implements ISystemConfigurationService{

    private final ISystemConfigurationRepository iSystemConfigurationRepository;
    private final ISystemConfigurationResponseMapper iSystemConfigurationResponseMapper;
    private final ISystemConfigurationRequestMapper iSystemConfigurationRequestMapper;


    /**
     * Retrieves the global system configuration.
     *
     * <p>
     * This method fetches the unique system configuration record identified
     * by a fixed ID (1L). The configuration contains global values shared
     * across all users, such as the business name and business address.
     * </p>
     *
     * <p>
     * The existence of this record is guaranteed by database initialization.
     * If the configuration is not found, it indicates a system inconsistency
     * and an exception is thrown.
     * </p>
     *
     * @return the current system configuration mapped to a response DTO
     *
     * @throws SystemConfigurationNotFoundException if the configuration
     *         record does not exist in the database
     */
    @Override
    @Transactional
    public SystemConfigurationResponseDTO getConfiguration() {

        SystemConfiguration configuration = iSystemConfigurationRepository
                .findById(1L)
                .orElseThrow(() -> new SystemConfigurationNotFoundException(
                        "System configuration not found"
                ));

        return iSystemConfigurationResponseMapper.toDto(configuration);
    }

    /**
     * Updates the global system configuration.
     *
     * <p>
     * Retrieves the unique system configuration record (ID = 1),
     * applies the updated values, and persists the changes.
     * </p>
     *
     * <p>
     * This operation is executed within a transactional context
     * to ensure atomicity and consistency.
     * </p>
     *
     * @param request the new configuration values
     * @return the updated system configuration
     *
     * @throws SystemConfigurationNotFoundException if the configuration
     *         record does not exist in the database
     */
    @Override
    @Transactional
    public SystemConfigurationResponseDTO updateConfiguration(SystemConfigurationRequestDTO request) {

        // 1. Retrieve existing configuration (ID = 1)
        SystemConfiguration configuration = iSystemConfigurationRepository
                .findById(1L)
                .orElseThrow(() -> new SystemConfigurationNotFoundException(
                        "System configuration not found"
                ));

        // 2. Apply updates using MapStruct
        iSystemConfigurationRequestMapper
                .updateSystemConfigurationFromDto(request, configuration);

        // 3. Persist changes
        iSystemConfigurationRepository.save(configuration);

        // 4. Map directly from managed entity
        return iSystemConfigurationResponseMapper.toDto(configuration);
    }
}
