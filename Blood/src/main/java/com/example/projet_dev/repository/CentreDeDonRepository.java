package com.example.projet_dev.repository;

import com.example.projet_dev.entity.CentreDeDon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CentreDeDonRepository extends JpaRepository<CentreDeDon, Long> {
    List<CentreDeDon> findByVilleIgnoreCase(String ville);
}
