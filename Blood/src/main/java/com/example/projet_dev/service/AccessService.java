package com.example.projet_dev.service;

import com.example.projet_dev.entity.Admin;
import com.example.projet_dev.entity.CentreDeDon;
import com.example.projet_dev.entity.Donneur;
import com.example.projet_dev.entity.Hopital;
import com.example.projet_dev.entity.Utilisateur;
import com.example.projet_dev.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.Map;

/**
 * Fait le lien entre le compte Keycloak (JWT) et la table "utilisateur" (clé : email).
 * Utilisé aussi dans les expressions @PreAuthorize("@access.selfOrAdmin(#id)").
 */
@Service("access")
@RequiredArgsConstructor
public class AccessService {

    private final UtilisateurRepository repository;

    /** Utilisateur de la base correspondant au token courant (créé au 1er login si absent). */
    public Utilisateur currentUser() {
        Jwt jwt = currentJwt();
        String email = jwt.getClaimAsString("email");
        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Le token ne contient pas d'email");
        }
        return repository.findByEmailIgnoreCase(email).orElseGet(() -> provision(jwt, email));
    }

    /** true si l'appelant est ADMIN, ou si {@code id} est son propre identifiant. */
    public boolean selfOrAdmin(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean admin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return admin || currentUser().getId().equals(id);
    }

    // ------------------------------------------------------------------

    /** Premier login d'un compte Keycloak sans fiche en base : on crée la fiche selon son rôle. */
    private Utilisateur provision(Jwt jwt, String email) {
        Collection<?> roles = roles(jwt);
        Utilisateur u;
        if (roles.contains("ADMIN")) {
            u = new Admin();
        } else if (roles.contains("HOPITAL")) {
            u = new Hopital();
        } else if (roles.contains("CENTRE_DON")) {
            u = new CentreDeDon();
        } else {
            u = new Donneur();   // DONNEUR, ou compte sans rôle applicatif (inscription libre)
        }
        u.setEmail(email);
        u.setNom(displayName(jwt, email));
        try {
            return repository.save(u);
        } catch (DataIntegrityViolationException e) {
            // deux requêtes simultanées au 1er login : l'autre a déjà créé la fiche
            return repository.findByEmailIgnoreCase(email).orElseThrow(() -> e);
        }
    }

    private static String displayName(Jwt jwt, String email) {
        String name = jwt.getClaimAsString("name");
        if (name != null && !name.isBlank()) return name;
        return email.substring(0, email.indexOf('@') > 0 ? email.indexOf('@') : email.length());
    }

    private static Collection<?> roles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess != null && realmAccess.get("roles") instanceof Collection<?> r) return r;
        return java.util.List.of();
    }

    private static Jwt currentJwt() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) return jwt;
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Non authentifié");
    }
}
