package com.marcoscornejos.sales_management_system.mapper;

import com.marcoscornejos.sales_management_system.dto.SystemConfigurationRequestDTO;
import com.marcoscornejos.sales_management_system.model.SystemConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

/**
 * Mapper for updating {@link SystemConfiguration} entities
 * from {@link SystemConfigurationRequestDTO}.
 *
 * <p>
 * Handles the transformation of incoming configuration data
 * into an existing system configuration entity.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface ISystemConfigurationRequestMapper {

    /**
     * Updates an existing {@link SystemConfiguration} entity
     * using data from the given DTO.
     *
     * @param dto the system configuration request DTO containing updated values
     * @param configuration the existing SystemConfiguration entity to be updated
     */
    void updateSystemConfigurationFromDto(
            SystemConfigurationRequestDTO dto,
            @MappingTarget SystemConfiguration configuration
    );

    /**
     * Maps a {@link SystemConfiguration} entity
     * to a {@link SystemConfigurationRequestDTO}.
     *
     * <p>
     * Useful for pre-filling forms on the client side.
     * </p>
     *
     * @param configuration the SystemConfiguration entity
     * @return a SystemConfigurationRequestDTO with corresponding fields set
     */
    SystemConfigurationRequestDTO toDto(SystemConfiguration configuration);
}