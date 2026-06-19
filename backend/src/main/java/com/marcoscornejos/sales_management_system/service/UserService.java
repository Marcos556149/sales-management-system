package com.marcoscornejos.sales_management_system.service;

import com.marcoscornejos.sales_management_system.dto.*;
import com.marcoscornejos.sales_management_system.exception.InvalidUserDataException;
import com.marcoscornejos.sales_management_system.exception.UserAlreadyExistsException;
import com.marcoscornejos.sales_management_system.exception.UserNotFoundException;
import com.marcoscornejos.sales_management_system.mapper.IUserRequestMapper;
import com.marcoscornejos.sales_management_system.mapper.IUserResponseMapper;
import com.marcoscornejos.sales_management_system.model.User;
import com.marcoscornejos.sales_management_system.model.UserRole;
import com.marcoscornejos.sales_management_system.model.UserStatus;
import com.marcoscornejos.sales_management_system.repository.IUserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * Servicio encargado de la gestión de usuarios del sistema.
 *
 * <p>Implementa la lógica de negocio relacionada con:
 * <ul>
 *     <li>Creación de usuarios</li>
 *     <li>Consulta de usuarios</li>
 *     <li>Actualización de datos</li>
 *     <li>Cambio de estado de usuario</li>
 * </ul>
 * </p>
 *
 * <p>Reglas de negocio principales:
 * <ul>
 *     <li>Solo se gestionan usuarios con rol OPERATOR</li>
 *     <li>Los nombres de usuario deben ser únicos</li>
 *     <li>Las contraseñas se almacenan encriptadas</li>
 * </ul>
 * </p>
 */
@Service
@RequiredArgsConstructor
public class UserService implements IUserService{

    private final IUserRepository iUserRepository;
    private final IUserRequestMapper iUserRequestMapper;
    private final IUserResponseMapper iUserResponseMapper;
    private final PasswordEncoder passwordEncoder;


    /**
     * Crea un nuevo usuario en el sistema.
     *
     * <p>Valida que el nombre de usuario no exista previamente,
     * asigna valores por defecto y encripta la contraseña antes
     * de persistir el usuario.</p>
     *
     * @param request datos del usuario a crear
     * @return usuario creado en formato DTO
     * @throws UserAlreadyExistsException si el username ya está registrado
     */
    @Transactional
    @Override
    public UserResponseDTO createUser(UserRequestDTO request) {

        // Verificar si el nombre de usuario ya existe
        if (iUserRepository.existsByUserName(request.getUserName())) {
            throw new UserAlreadyExistsException(
                    "El nombre de usuario ya existe"
            );
        }

        // Convertir DTO a entidad
        User user = iUserRequestMapper.toEntity(request);

        // Asignar valores por defecto según las reglas de negocio
        user.setUserRole(UserRole.OPERATOR);
        user.setUserStatus(UserStatus.ACTIVE);

        // Cifrar contraseña
        user.setUserPassword(
                passwordEncoder.encode(user.getUserPassword())
        );

        // Guardar usuario
        user = iUserRepository.save(user);

        // Devolver respuesta
        return iUserResponseMapper.toDto(user);
    }

    /**
     * Obtiene todos los usuarios con rol OPERATOR.
     *
     * @return lista de usuarios en formato DTO
     */
    @Override
    public List<UserResponseDTO> getAllUsers() {

        List<User> users = iUserRepository.findByUserRole(
                UserRole.OPERATOR
        );

        return users.stream()
                .map(iUserResponseMapper::toDto)
                .toList();
    }

    /**
     * Busca un usuario por su ID, limitado a usuarios con rol OPERATOR.
     *
     * @param userId identificador del usuario
     * @return usuario encontrado en formato DTO
     * @throws UserNotFoundException si no existe o no es OPERATOR
     */
    @Override
    public UserResponseDTO getUserById(Long userId) {

        User user = iUserRepository
                .findByUserIdAndUserRole(userId, UserRole.OPERATOR)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "Usuario no encontrado"
                        )
                );

        return iUserResponseMapper.toDto(user);
    }

    /**
     * Actualiza los datos de un usuario existente.
     *
     * <p>Actualiza el nombre de usuario y opcionalmente la contraseña.
     * Valida la unicidad del nombre de usuario y encripta la nueva contraseña
     * cuando es proporcionada.</p>
     *
     * @param id identificador del usuario
     * @param request datos a actualizar
     * @return usuario actualizado en formato DTO
     * @throws UserNotFoundException si el usuario no existe o no es OPERATOR
     * @throws UserAlreadyExistsException si el nuevo username ya está en uso
     */
    @Transactional
    @Override
    public UserResponseDTO updateUser(Long id, UpdateUserRequestDTO request) {

        User user = iUserRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        // Regla de negocio: solo operadores pueden modificarse
        if (user.getUserRole() != UserRole.OPERATOR) {
            throw new UserNotFoundException("Usuario no encontrado");
        }

        // Validar nombre de usuario único si cambia
        if (!request.getUserName().equals(user.getUserName())) {

            boolean exists = iUserRepository.existsByUserName(request.getUserName());

            if (exists) {
                throw new UserAlreadyExistsException(
                        "El nombre de usuario '" + request.getUserName() + "' ya existe"
                );
            }

            user.setUserName(request.getUserName());
        }

        // Actualizar contraseña si se envía una nueva
        if (request.getUserPassword() != null && !request.getUserPassword().isBlank()) {
            user.setUserPassword(passwordEncoder.encode(request.getUserPassword()));
        }

        iUserRepository.save(user);

        return iUserResponseMapper.toDto(user);

    }

    /**
     * Cambia el estado de un usuario.
     *
     * <p>Permite activar o desactivar usuarios del sistema.</p>
     *
     * @param id identificador del usuario
     * @param request nuevo estado del usuario
     * @return usuario actualizado en formato DTO
     * @throws UserNotFoundException si el usuario no existe o no es OPERATOR
     */
    @Transactional
    @Override
    public UserResponseDTO changeUserStatus(Long id, ChangeUserStatusRequestDTO request) {

        User user = iUserRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(
                        "Usuario no encontrado"
                ));

        // Regla de negocio: solo operadores
        if (user.getUserRole() != UserRole.OPERATOR) {
            throw new UserNotFoundException("Usuario no encontrado");
        }

        // Evitar updates innecesarios
        if (user.getUserStatus() == request.getUserStatus()) {
            throw new InvalidUserDataException(
                    String.format("El usuario ya tiene el estado '%s'", request.getUserStatus().getDisplayName())
            );
        }

        user.setUserStatus(request.getUserStatus());

        iUserRepository.save(user);

        return iUserResponseMapper.toDto(user);
    }

    /**
     * Recupera los metadatos necesarios para las operaciones relacionadas con usuarios.
     *
     * <p>
     * Incluye los estados disponibles de usuario utilizados en formularios
     * de edición y gestión de usuarios.
     * </p>
     *
     * @return metadatos de usuarios
     */
    @Override
    public UserMetadataResponseDTO getUserMetadata() {

        List<EnumDTO> userStatusOptions = Arrays.stream(UserStatus.values())
                .map(status -> new EnumDTO(
                        status.name(),
                        status.getDisplayName()
                ))
                .toList();

        return new UserMetadataResponseDTO(userStatusOptions);
    }
}
