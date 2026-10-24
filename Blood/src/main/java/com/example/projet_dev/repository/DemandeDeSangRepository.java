package com.example.projet_dev.repository;


import com.example.projet_dev.entity.DemandeDeSang;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DemandeDeSangRepository extends JpaRepository<DemandeDeSang, Long> {
    List<DemandeDeSang> findByHopitalId(Long hopitalId);
    List<DemandeDeSang> findByCentreDeDonId(Long centreDeDonId);
    List<DemandeDeSang> findByStatut(String statut);
}
