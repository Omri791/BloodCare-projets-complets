package com.example.projet_dev.service;


import com.example.projet_dev.entity.Utilisateur;
import com.example.projet_dev.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UtilisateurService {

    private final UtilisateurRepository repository;

    public List<Utilisateur> findAll() {
        return repository.findAll();
    }

    public Utilisateur findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));
    }

    public void delete(Long id) {
        repository.delete(findById(id));
    }

    /** +authentifier() — TODO: replace plain comparison by BCrypt (Spring Security) */
    public Utilisateur authentifier(String email, String motDePasse) {
        return repository.findByEmail(email)
                .filter(u -> u.getMotDePasse().equals(motDePasse))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email ou mot de passe incorrect"));
    }
}
