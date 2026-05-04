package com.marcoscornejos.sales_management_system.mapper;

import com.marcoscornejos.sales_management_system.dto.SystemConfigurationResponseDTO;
import com.marcoscornejos.sales_management_system.model.SystemConfiguration;
import org.mapstruct.Mapper;

/**
 * Mapper responsible for converting {@link SystemConfiguration}
 * entities into {@link SystemConfigurationResponseDTO}.
 *
 * <p>
 * Handles the transformation of global configuration data
 * to a format suitable for client responses.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface ISystemConfigurationResponseMapper {

    /**
     * Maps a {@link SystemConfiguration} entity
     * to a {@link SystemConfigurationResponseDTO}.
     *
     * @param configuration the SystemConfiguration entity
     * @return SystemConfigurationResponseDTO with mapped fields
     */
    SystemConfigurationResponseDTO toDto(SystemConfiguration configuration);
}
