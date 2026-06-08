package com.marcoscornejos.sales_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO utilizado para representar la configuración del sistema
 * enviada al cliente.
 *
 * <p>
 * Incluye valores globales compartidos entre todos los usuarios,
 * como el nombre del negocio y la dirección del negocio.
 * </p>
 */
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class SystemConfigurationResponseDTO {

    /**
     * Nombre del negocio.
     *
     * <p>
     * Este valor es global y compartido por todos los usuarios del sistema.
     * </p>
     */
    private String businessName;

    /**
     * Dirección del negocio.
     *
     * <p>
     * Este valor es global y compartido por todos los usuarios del sistema.
     * </p>
     */
    private String businessAddress;
}