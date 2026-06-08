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
 * Controlador REST responsable de la gestión de la configuración global del sistema.
 *
 * <p>
 * Expone endpoints para obtener y actualizar valores de configuración a nivel sistema,
 * como el nombre del negocio y la dirección del mismo.
 * </p>
 */
@RestController
@RequestMapping("/api/configuration")
@RequiredArgsConstructor
public class SystemConfigurationController {

    private final ISystemConfigurationService iSystemConfigurationService;

    /**
     * Obtiene la configuración actual del sistema.
     *
     * <p>
     * Devuelve los valores de configuración global compartidos por todos los usuarios,
     * incluyendo el nombre del negocio y su dirección.
     * </p>
     *
     * @return la configuración actual del sistema
     */
    @GetMapping
    public ResponseEntity<SystemConfigurationResponseDTO> getConfiguration() {

        SystemConfigurationResponseDTO response =
                iSystemConfigurationService.getConfiguration();

        return ResponseEntity.ok(response);
    }

    /**
     * Actualiza la configuración global del sistema.
     *
     * <p>
     * Permite modificar valores de configuración a nivel sistema, como
     * el nombre del negocio y la dirección del mismo.
     * Estos valores son compartidos por todos los usuarios del sistema.
     * </p>
     *
     * <p>
     * Solo los usuarios con privilegios de administrador están autorizados
     * para realizar esta operación.
     * </p>
     *
     * <p>
     * El sistema valida los datos de entrada antes de aplicar los cambios.
     * Si la validación falla, se devuelve una respuesta de error indicando
     * los campos inválidos.
     * </p>
     *
     * @param request los nuevos valores de configuración a aplicar
     * @return una respuesta estándar de éxito que contiene la configuración actualizada
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
                        "Configuración del sistema actualizada correctamente",
                        updatedConfig
                );

        return ResponseEntity.ok(response);
    }

}