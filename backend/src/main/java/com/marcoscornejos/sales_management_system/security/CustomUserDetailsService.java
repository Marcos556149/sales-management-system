package com.marcoscornejos.sales_management_system.security;

import com.marcoscornejos.sales_management_system.model.User;
import com.marcoscornejos.sales_management_system.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Servicio personalizado de Spring Security encargado de cargar
 * los datos del usuario durante el proceso de autenticación.
 *
 * <p>Implementa {@link UserDetailsService} para permitir que Spring Security
 * obtenga la información del usuario desde la base de datos.</p>
 */
@RequiredArgsConstructor
@Service
public class CustomUserDetailsService
        implements UserDetailsService {

    private final IUserRepository iUserRepository;

    /**
     * Busca un usuario por su nombre de usuario y lo convierte
     * en un objeto {@link UserDetails}.
     *
     * <p>Este método es utilizado por Spring Security durante el proceso
     * de autenticación para validar credenciales.</p>
     *
     * @param username nombre de usuario ingresado en el login
     * @return detalles del usuario autenticado
     * @throws UsernameNotFoundException si el usuario no existe
     */
    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        User user = iUserRepository
                .findByUserName(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Usuario no encontrado"
                        )
                );

        return new CustomUserDetails(user);
    }
}