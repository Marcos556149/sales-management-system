/**
 * Interfaz de repositorio para la gestión de entidades {@link SaleDetail}.
 *
 * <p>
 * Extiende {@link JpaRepository}, proporcionando operaciones CRUD estándar
 * como save, delete, findById y findAll sin requerir una implementación explícita.
 * </p>
 *
 * <p>
 * Este repositorio gestiona el acceso a los datos de los detalles de venta,
 * que representan los productos individuales incluidos en una venta.
 * Aquí pueden definirse métodos de consulta personalizados cuando sea necesario.
 * </p>
 */

package com.marcoscornejos.sales_management_system.repository;

import com.marcoscornejos.sales_management_system.model.SaleDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ISaleDetailRepository extends JpaRepository<SaleDetail, Long> {
}
