/**
 * Repositorio para la gestión de entidades {@link SystemConfiguration}.
 *
 * <p>
 * Extiende {@link JpaRepository}, proporcionando operaciones CRUD estándar
 * como guardar, eliminar, buscar por identificador y obtener todos los registros
 * sin requerir una implementación explícita.
 * </p>
 *
 * <p>
 * Este repositorio es responsable del acceso a datos relacionado con la
 * configuración global del sistema. Cuando sea necesario, aquí pueden
 * definirse métodos de consulta personalizados.
 * </p>
 */

package com.marcoscornejos.sales_management_system.repository;

import com.marcoscornejos.sales_management_system.model.SystemConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ISystemConfigurationRepository extends JpaRepository<SystemConfiguration, Long> {
}
