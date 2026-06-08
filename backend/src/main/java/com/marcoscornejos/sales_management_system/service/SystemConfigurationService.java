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
     * Obtiene la configuración global del sistema.
     *
     * <p>
     * Este método recupera el único registro de configuración del sistema,
     * identificado por un ID fijo (1L). La configuración contiene valores
     * globales compartidos por todos los usuarios, como el nombre y la
     * dirección del negocio.
     * </p>
     *
     * <p>
     * La existencia de este registro está garantizada por la inicialización
     * de la base de datos. Si la configuración no se encuentra, se considera
     * una inconsistencia del sistema y se lanza una excepción.
     * </p>
     *
     * @return la configuración actual del sistema mapeada a un DTO de respuesta
     *
     * @throws SystemConfigurationNotFoundException si el registro de
     *         configuración no existe en la base de datos
     */
    @Override
    @Transactional
    public SystemConfigurationResponseDTO getConfiguration() {

        SystemConfiguration configuration = iSystemConfigurationRepository
                .findById(1L)
                .orElseThrow(() -> new SystemConfigurationNotFoundException(
                        "Configuración del sistema no encontrada"
                ));

        return iSystemConfigurationResponseMapper.toDto(configuration);
    }

    /**
     * Actualiza la configuración global del sistema.
     *
     * <p>
     * Recupera el único registro de configuración del sistema (ID = 1),
     * aplica los nuevos valores y persiste los cambios.
     * </p>
     *
     * <p>
     * Esta operación se ejecuta dentro de un contexto transaccional
     * para garantizar atomicidad y consistencia.
     * </p>
     *
     * @param request los nuevos valores de configuración
     * @return la configuración actualizada del sistema
     *
     * @throws SystemConfigurationNotFoundException si el registro de
     *         configuración no existe en la base de datos
     */
    @Override
    @Transactional
    public SystemConfigurationResponseDTO updateConfiguration(SystemConfigurationRequestDTO request) {

        // 1. Recuperar configuración existente (ID = 1)
        SystemConfiguration configuration = iSystemConfigurationRepository
                .findById(1L)
                .orElseThrow(() -> new SystemConfigurationNotFoundException(
                        "Configuración del sistema no encontrada"
                ));

        // 2. Aplicar actualizaciones mediante MapStruct
        iSystemConfigurationRequestMapper
                .updateSystemConfigurationFromDto(request, configuration);

        // 3. Persistir cambios
        iSystemConfigurationRepository.save(configuration);

        // 4. Mapear directamente desde la entidad gestionada
        return iSystemConfigurationResponseMapper.toDto(configuration);
    }
}
