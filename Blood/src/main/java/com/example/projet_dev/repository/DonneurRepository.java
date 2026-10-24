package com.example.projet_dev.repository;

import com.example.projet_dev.entity.Donneur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DonneurRepository extends JpaRepository<Donneur, Long> {
    List<Donneur> findByVilleIgnoreCase(String ville);
    List<Donneur> findByGroupeSanguin(String groupeSanguin);
    List<Donneur> findByDisponibiliteTrue();
}