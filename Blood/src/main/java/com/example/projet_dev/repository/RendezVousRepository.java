package com.example.projet_dev.repository;

import com.example.projet_dev.entity.RendezVous;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RendezVousRepository extends JpaRepository<RendezVous, Long> {
    List<RendezVous> findByDonneurId(Long donneurId);
    List<RendezVous> findByCentreDeDonId(Long centreDeDonId);
    List<RendezVous> findByStatut(String statut);
}