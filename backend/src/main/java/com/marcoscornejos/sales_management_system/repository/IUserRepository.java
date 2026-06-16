/**
 * Repositorio para la gestión de entidades {@link User}.
 *
 * <p>
 * Extiende {@link JpaRepository}, proporcionando operaciones CRUD estándar
 * como guardar, eliminar, buscar por identificador y obtener todos los registros
 * sin requerir una implementación explícita.
 * </p>
 *
 * <p>
 * Este repositorio gestiona el acceso a datos de los usuarios.
 * Cuando sea necesario, aquí pueden definirse métodos de consulta personalizados.
 * </p>
 */

package com.marcoscornejos.sales_management_system.repository;

import com.marcoscornejos.sales_management_system.model.User;
import com.marcoscornejos.sales_management_system.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<User, Long> {

    /**
     * Busca un usuario por su nombre de usuario.
     *
     * @param userName nombre de usuario a buscar
     * @return un Optional que contiene el usuario si existe, o vacío en caso contrario
     */
    Optional<User> findByUserName(String userName);

    /**
     * Verifica si existe un usuario con el nombre de usuario indicado.
     *
     * @param userName nombre de usuario a verificar
     * @return true si existe un usuario con ese nombre, false en caso contrario
     */
    boolean existsByUserName(String userName);

    List<User> findByUserRole(UserRole userRole);

    Optional<User> findByUserIdAndUserRole(Long userId, UserRole userRole);


}
