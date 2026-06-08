/**
 * Representa la configuración global del sistema.
 *
 * <p>Almacena configuraciones compartidas a nivel del sistema, como el nombre y la dirección del negocio.</p>
 *
 */

package com.marcoscornejos.sales_management_system.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "system_configuration", schema = "core")
public class SystemConfiguration {

    /** Identificador único de la configuración del sistema. */
    @Id
    @Column(name = "system_configuration_id")
    private Long systemConfigurationId;

    /** Nombre del negocio, utilizado en los comprobantes y mostrado en todo el sistema. */
    @Column(name = "business_name")
    private String businessName;

    /** Dirección del negocio, utilizada en los comprobantes y mostrada en todo el sistema. */
    @Column(name = "business_address")
    private String businessAddress;
}