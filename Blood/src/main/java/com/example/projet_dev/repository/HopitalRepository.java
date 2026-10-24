package com.example.projet_dev.repository;

import com.example.projet_dev.entity.Hopital;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HopitalRepository extends JpaRepository<Hopital, Long> {
    List<Hopital> findByVilleIgnoreCase(String ville);
}
