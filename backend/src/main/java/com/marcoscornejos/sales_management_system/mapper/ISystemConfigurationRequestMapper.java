package com.marcoscornejos.sales_management_system.mapper;

import com.marcoscornejos.sales_management_system.dto.SystemConfigurationRequestDTO;
import com.marcoscornejos.sales_management_system.model.SystemConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

/**
 * Mapper para actualizar entidades {@link SystemConfiguration}
 * a partir de {@link SystemConfigurationRequestDTO}.
 *
 * <p>
 * Se encarga de transformar los datos de configuración entrantes
 * en una entidad de configuración del sistema existente.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface ISystemConfigurationRequestMapper {

    /**
     * Actualiza una entidad {@link SystemConfiguration} existente
     * utilizando los datos del DTO proporcionado.
     *
     * @param dto DTO de configuración del sistema con los valores actualizados
     * @param configuration entidad SystemConfiguration existente a actualizar
     */
    void updateSystemConfigurationFromDto(
            SystemConfigurationRequestDTO dto,
            @MappingTarget SystemConfiguration configuration
    );

    /**
     * Mapea una entidad {@link SystemConfiguration}
     * a un {@link SystemConfigurationRequestDTO}.
     *
     * <p>
     * Útil para precargar formularios en el cliente.
     * </p>
     *
     * @param configuration entidad SystemConfiguration
     * @return SystemConfigurationRequestDTO con los campos correspondientes
     */
    SystemConfigurationRequestDTO toDto(SystemConfiguration configuration);
}