package com.example.projet_dev.repository;

import com.example.projet_dev.entity.Creneau;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CreneauRepository extends JpaRepository<Creneau, Long> {
    List<Creneau> findByCentreDeDonIdOrderByDateAsc(Long centreDeDonId);
}
