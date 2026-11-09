package com.example.projet_dev.repository;

import com.example.projet_dev.entity.AppelDon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppelDonRepository extends JpaRepository<AppelDon, Long> {
    List<AppelDon> findByCentreDeDonIdOrderByIdDesc(Long centreDeDonId);
    List<AppelDon> findByDonneurIdAndStatutOrderByIdDesc(Long donneurId, String statut);
    boolean existsByCentreDeDonIdAndDonneurIdAndStatut(Long centreDeDonId, Long donneurId, String statut);
}
