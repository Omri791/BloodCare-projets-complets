package com.example.projet_dev.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Le backend est un "resource server" : il ne gère aucun mot de passe,
 * il valide uniquement le JWT émis par Keycloak.
 *
 * Rôles Keycloak (realm roles) : ADMIN, HOPITAL, CENTRE_DON, DONNEUR
 * Un compte authentifié sans aucun de ces rôles (ex: inscription libre) est traité comme DONNEUR.
 *
 * >>> POUR LES COLLÈGUES (Centre de don / Donneur) : pour ouvrir un endpoint à votre acteur,
 * >>> ajoutez une ligne dans la section "ACCÈS PAR ACTEUR" ci-dessous, AVANT la règle
 * >>> ".requestMatchers("/api/**").hasRole("ADMIN")". Exemple :
 * >>>   .requestMatchers("/api/centres/*\/stocks/**").hasAnyRole("ADMIN", "CENTRE_DON")
 * >>> Pour limiter un centre/donneur à SES données : @PreAuthorize("@access.selfOrAdmin(#id)")
 * >>> sur la méthode du controller (voir HopitalController).
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())                       // API stateless + Bearer token
                .cors(Customizer.withDefaults())                    // utilise CorsConfig
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/error").permitAll()

                        // Profil de l'utilisateur connecté : les 4 acteurs
                        .requestMatchers("/api/me", "/api/me/**").hasAnyRole("ADMIN", "HOPITAL", "CENTRE_DON", "DONNEUR")

                        // ================= ACCÈS PAR ACTEUR =================

                        // --- Hôpital ---
                        // Espace hôpital : l'admin OU l'hôpital concerné
                        // (le "est-ce bien mon hôpital ?" est vérifié par @PreAuthorize dans HopitalController)
                        .requestMatchers(HttpMethod.GET, "/api/hopitaux/*").hasAnyRole("ADMIN", "HOPITAL")
                        .requestMatchers(HttpMethod.GET, "/api/hopitaux/*/patients").hasAnyRole("ADMIN", "HOPITAL")
                        .requestMatchers("/api/hopitaux/*/demandes").hasAnyRole("ADMIN", "HOPITAL")

                        // --- Centre de don ---
                        // Demandes, rendez-vous et dons : l'admin OU le centre concerné
                        // (le "est-ce bien mon centre ?" est vérifié par @PreAuthorize dans CentreDeDonController)
                        .requestMatchers(HttpMethod.GET, "/api/centres/*/stocks").hasAnyRole("ADMIN", "CENTRE_DON")
                        .requestMatchers(HttpMethod.PUT, "/api/centres/*/stocks").hasAnyRole("ADMIN", "CENTRE_DON")
                        .requestMatchers("/api/centres/*/demandes", "/api/centres/*/demandes/*").hasAnyRole("ADMIN", "CENTRE_DON")
                        .requestMatchers("/api/centres/*/rendez-vous", "/api/centres/*/rendez-vous/**").hasAnyRole("ADMIN", "CENTRE_DON")
                        .requestMatchers("/api/centres/*/creneaux", "/api/centres/*/creneaux/*", "/api/centres/*/appels").hasAnyRole("ADMIN", "CENTRE_DON")
                        // Le centre consulte la liste des donneurs
                        .requestMatchers(HttpMethod.GET, "/api/donneurs", "/api/donneurs/disponibles").hasAnyRole("ADMIN", "CENTRE_DON")

                        // --- Donneur ---
                        // (le "est-ce bien moi ?" est vérifié par @PreAuthorize dans DonneurController)
                        .requestMatchers(HttpMethod.GET, "/api/centres").hasAnyRole("ADMIN", "CENTRE_DON", "DONNEUR")
                        .requestMatchers(HttpMethod.PUT, "/api/donneurs/*/profil").hasAnyRole("ADMIN", "DONNEUR")
                        .requestMatchers(HttpMethod.GET, "/api/donneurs/*/eligibilite", "/api/donneurs/*/historique").hasAnyRole("ADMIN", "CENTRE_DON", "DONNEUR")
                        .requestMatchers("/api/donneurs/*/rendez-vous", "/api/donneurs/*/rendez-vous/**").hasAnyRole("ADMIN", "CENTRE_DON", "DONNEUR")
                        .requestMatchers("/api/donneurs/*/appels", "/api/donneurs/*/appels/**").hasAnyRole("ADMIN", "DONNEUR")

                        // Tout le reste de l'API : administrateur uniquement (pour l'instant)
                        .requestMatchers("/api/**").hasRole("ADMIN")

                        .anyRequest().denyAll()
                )
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(keycloakJwtConverter()))
                );
        return http.build();
    }

    @Value("${app.cors.allowed-origins}")
    private String[] allowedOrigins;

    /**
     * CORS au niveau Spring Security : le preflight (OPTIONS) doit être traité AVANT
     * l'authentification JWT. (Le CorsConfig existant reste en place, il n'est plus suffisant seul.)
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList(allowedOrigins));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
        config.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }

    private static final List<String> APP_ROLES = List.of("ADMIN", "HOPITAL", "CENTRE_DON", "DONNEUR");

    /** Transforme realm_access.roles du token Keycloak en ROLE_xxx Spring Security. */
    private JwtAuthenticationConverter keycloakJwtConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            Collection<?> roles = (realmAccess != null && realmAccess.get("roles") instanceof Collection<?> r)
                    ? r : List.of();
            List<GrantedAuthority> authorities = new java.util.ArrayList<>();
            roles.forEach(r -> authorities.add(new SimpleGrantedAuthority("ROLE_" + r)));
            // aucun rôle applicatif (ex: inscription libre) -> DONNEUR
            if (roles.stream().noneMatch(r -> APP_ROLES.contains(String.valueOf(r)))) {
                authorities.add(new SimpleGrantedAuthority("ROLE_DONNEUR"));
            }
            return authorities;
        });
        return converter;
    }
}
