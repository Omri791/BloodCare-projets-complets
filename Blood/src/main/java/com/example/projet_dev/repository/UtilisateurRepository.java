package com.example.projet_dev.repository;

import com.example.projet_dev.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByEmail(String email);
    Optional<Utilisateur> findByEmailIgnoreCase(String email);
    boolean existsByEmail(String email);
}
