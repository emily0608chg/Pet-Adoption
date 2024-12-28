package com.petadoption.config;

import com.petadoption.model.User;
import com.petadoption.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;


import java.util.List;

/**
 * SecurityConfig is a Spring Security configuration class that defines
 * security-related settings such as authentication, authorization, and JWT-based
 * OAuth2 resource server for the application.

 * It uses annotations like `@EnableWebSecurity` to enable Spring Security and
 * `@EnableMethodSecurity` to allow method-level security annotations like `@PreAuthorize`.

 * The class configures various endpoints with different access controls based on roles
 * and permissions, as well as a stateless session management policy for handling JWT
 * authentication.

 * Beans defined in this class include:
 * - SecurityFilterChain: Configures HTTP security settings for application endpoints.
 * - AuthenticationProvider: Handles authentication using a custom user details service
 *   and a password encoder.

 * The class also includes a custom implementation of JwtAuthenticationConverter to
 * extract roles and set them in the authentication object from the JWT token.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtDecoder jwtDecoder;

    @Autowired
    public SecurityConfig(UserService userService, PasswordEncoder passwordEncoder, JwtDecoder jwtDecoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtDecoder = jwtDecoder;
    }

    /**
     * Configures and creates a SecurityFilterChain, defining security rules and settings
     * for the application, including endpoint authorization, CSRF handling, OAuth2 resource
     * server configuration, and session management. This method constructs and returns the
     * resulting SecurityFilterChain instance.

     * The configuration includes:
     * - Permitting specific endpoints to public access.
     * - Role-based permissions for various API endpoints.
     * - Stateless session management.
     * - JWT token authentication and conversion of JWT claims to roles.

     * @param http the HttpSecurity object used to configure the security settings and rules.
     * @return the configured SecurityFilterChain instance.
     * @throws Exception if any error occurs during the security configuration process.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        Logger logger = LoggerFactory.getLogger(SecurityConfig.class);
        logger.info("Configuring SecurityFilterChain...");

        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/actuator/info").permitAll()
                        .requestMatchers("/actuator/mappings").permitAll()
                        .requestMatchers("/actuator/metrics").hasRole("ADMIN")
                        .requestMatchers("/api/auth/register").permitAll()
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/api/token/refresh").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/adoption/**").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/adoption/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/adoption/{id}/approve").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/adoption/{id}/reject").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/adoption/{id}").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/adoption/{id}").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/api/users/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/api/pets/**").hasAnyRole("ADMIN", "USER")
                        .anyRequest().authenticated() // Todo lo demás requiere autenticación
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt ->
                        jwt.decoder(jwtDecoder)
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                ))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }


    /**
     * Creates and configures a {@link JwtAuthenticationConverter} that extracts roles
     * from the JWT token using the "roles" claim and maps them to Spring Security's
     * granted authorities with the "ROLE_" prefix.

     * The converter uses a {@link JwtGrantedAuthoritiesConverter} to translate the
     * roles claim into granted authorities, ensuring proper logging of the JWT claims
     * and extracted*/
    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthoritiesClaimName("roles"); // Extraer de "roles"
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_"); // Prefijo de Spring Security

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            logger.info("JWT Claims: {}", jwt.getClaims());
            // Extraer roles antes de la conversión
            List<String> roles = jwt.getClaimAsStringList("roles");
            if (roles == null) {
                roles = List.of(); // Si no hay rol, retorna vacío
            }
            logger.info("Roles from the token: {}", roles); // Debug en logs
            return grantedAuthoritiesConverter.convert(jwt);
        });

        return converter;
    }


    /**
     * Configures and returns an {@link AuthenticationProvider} that uses a {@link DaoAuthenticationProvider}
     * for validating user credentials. This provider interacts with a {@link UserService} to fetch user details and
     * uses a {@link PasswordEncoder} for password comparison.

     * The user details are retrieved based on the provided username, and exceptions are thrown if the user
     * cannot be found. The roles associated with the user are also processed to create valid authorities
     * for authentication.

     * @return the configured {@link AuthenticationProvider} instance
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(username -> {
            User user = userService.getUserByUsername(username);
            if (user == null) {
                throw new UsernameNotFoundException("User not found");
            }
            return new org.springframework.security.core.userdetails.User(
                    user.getUsername(),
                    user.getPassword(),
                    user.getAuthorities() // Roles extraídos del usuario
            );
        });
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }
}
