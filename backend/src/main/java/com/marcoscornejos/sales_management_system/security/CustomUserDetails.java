package com.marcoscornejos.sales_management_system.security;

import com.marcoscornejos.sales_management_system.model.User;
import com.marcoscornejos.sales_management_system.model.UserStatus;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Implementación personalizada de {@link UserDetails} utilizada por Spring Security.
 *
 * <p>Encapsula la entidad {@link User} del sistema y la adapta al modelo
 * de autenticación y autorización de Spring Security.</p>
 *
 * <p>Permite definir:
 * <ul>
 *     <li>Credenciales del usuario</li>
 *     <li>Autoridades (roles)</li>
 *     <li>Estado de la cuenta (activa o inactiva)</li>
 * </ul>
 * </p>
 */
@Getter
public class CustomUserDetails implements UserDetails {

    private final User user;

    /**
     * Crea una instancia a partir de una entidad {@link User}.
     *
     * @param user entidad de dominio del sistema
     */
    public CustomUserDetails(User user) {
        this.user = user;
    }

    /**
     * Retorna la entidad de dominio asociada.
     *
     * @return usuario del sistema
     */
    public User getUser() {
        return user;
    }

    /**
     * Retorna las autoridades (roles) asignadas al usuario.
     *
     * <p>Se utiliza el formato "ROLE_" requerido por Spring Security.</p>
     *
     * @return colección de autoridades del usuario
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return List.of(
                new SimpleGrantedAuthority(
                        "ROLE_" + user.getUserRole().name()
                )
        );
    }

    /**
     * Retorna la contraseña del usuario.
     *
     * @return password encriptada
     */
    @Override
    public String getPassword() {
        return user.getUserPassword();
    }

    /**
     * Retorna el nombre de usuario utilizado para autenticación.
     *
     * @return username
     */
    @Override
    public String getUsername() {
        return user.getUserName();
    }

    /**
     * Indica si la cuenta está activa y puede autenticarse.
     *
     * <p>Una cuenta se considera no bloqueada si el estado es ACTIVE.</p>
     *
     * @return true si la cuenta está activa
     */
    @Override
    public boolean isAccountNonLocked() {
        return user.getUserStatus() == UserStatus.ACTIVE;
    }

    /**
     * Indica si la cuenta está habilitada.
     *
     * <p>Se basa en el estado del usuario.</p>
     *
     * @return true si la cuenta está habilitada
     */
    @Override
    public boolean isEnabled() {
        return user.getUserStatus() == UserStatus.ACTIVE;
    }

    /**
     * Indica si la cuenta no ha expirado.
     *
     * @return siempre true (no se maneja expiración de cuentas)
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indica si las credenciales no han expirado.
     *
     * @return siempre true (no se maneja expiración de credenciales)
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}