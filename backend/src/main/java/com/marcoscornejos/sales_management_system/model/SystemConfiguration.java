/**
 * Represents the global system configuration.
 *
 * <p>Stores system-wide settings such as the business name.</p>
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

    /** Unique identifier for the system configuration. */
    @Id
    @Column(name = "system_configuration_id")
    private Long systemConfigurationId;

    /** Name of the business, used in receipts and displayed across the system. */
    @Column(name = "business_name")
    private String businessName;
}