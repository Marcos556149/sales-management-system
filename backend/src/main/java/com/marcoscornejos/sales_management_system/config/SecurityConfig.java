package com.marcoscornejos.sales_management_system.config;

import com.marcoscornejos.sales_management_system.security.CustomAuthenticationEntryPoint;
import com.marcoscornejos.sales_management_system.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import com.marcoscornejos.sales_management_system.security.CustomAccessDeniedHandler;

/**
 * Configuración principal de seguridad de la aplicación.
 *
 * <p>Define los componentes necesarios para la autenticación y autorización
 * mediante Spring Security, incluyendo el codificador de contraseñas,
 * el proveedor de autenticación, el administrador de autenticación y
 * las reglas de acceso a los endpoints del sistema.
 */


@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    /**
     * Define el codificador de contraseñas utilizado por la aplicación.
     *
     * <p>Se utiliza el algoritmo BCrypt para almacenar y verificar
     * contraseñas de forma segura.
     *
     * @return instancia de {@link PasswordEncoder}
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configura el proveedor de autenticación basado en acceso a datos.
     *
     * <p>Utiliza {@link CustomUserDetailsService} para cargar los usuarios
     * y el {@link PasswordEncoder} para validar las contraseñas.
     *
     * @param userDetailsService servicio encargado de recuperar los datos
     *                           de autenticación de los usuarios
     * @return proveedor de autenticación configurado
     */
    @Bean
    public AuthenticationProvider authenticationProvider(
            CustomUserDetailsService userDetailsService
    ) {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    /**
     * Expone el administrador de autenticación de Spring Security.
     *
     * @param config configuración de autenticación proporcionada por Spring
     * @return instancia de {@link AuthenticationManager}
     * @throws Exception si ocurre un error al obtener el administrador
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {

        return config.getAuthenticationManager();
    }

    /**
     * Configura la cadena de filtros de seguridad de la aplicación.
     *
     * <p>Define las reglas de autorización para los endpoints, deshabilita
     * la protección CSRF y establece los manejadores personalizados para
     * errores de autenticación y autorización.
     *
     * <ul>
     *     <li>Permite acceso público al endpoint de inicio de sesión.</li>
     *     <li>Requiere autenticación para cualquier otro endpoint.</li>
     *     <li>Retorna respuestas personalizadas para errores 401 y 403.</li>
     * </ul>
     *
     * @param http objeto de configuración HTTP de Spring Security
     * @param accessDeniedHandler manejador para accesos denegados (403)
     * @param authenticationEntryPoint manejador para usuarios no autenticados (401)
     * @return cadena de filtros de seguridad configurada
     * @throws Exception si ocurre un error durante la configuración
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            CustomAccessDeniedHandler accessDeniedHandler,
            CustomAuthenticationEntryPoint authenticationEntryPoint
    ) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler(accessDeniedHandler)
                        .authenticationEntryPoint(authenticationEntryPoint)
                );

        return http.build();
    }
}
