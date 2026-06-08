package com.marcoscornejos.sales_management_system.mapper;

import com.marcoscornejos.sales_management_system.dto.SystemConfigurationResponseDTO;
import com.marcoscornejos.sales_management_system.model.SystemConfiguration;
import org.mapstruct.Mapper;

/**
 * Mapper responsable de convertir entidades {@link SystemConfiguration}
 * en {@link SystemConfigurationResponseDTO}.
 *
 * <p>
 * Maneja la transformación de la configuración global del sistema
 * a un formato adecuado para las respuestas del cliente.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface ISystemConfigurationResponseMapper {

    /**
     * Mapea una entidad {@link SystemConfiguration}
     * a un {@link SystemConfigurationResponseDTO}.
     *
     * @param configuration entidad SystemConfiguration
     * @return SystemConfigurationResponseDTO con los campos mapeados
     */
    SystemConfigurationResponseDTO toDto(SystemConfiguration configuration);
}
