package com.example.projet_dev.repository;

import com.example.projet_dev.entity.Prediction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PredictionRepository extends JpaRepository<Prediction, Long> {
    List<Prediction> findByCentreDeDonId(Long centreDeDonId);
    List<Prediction> findByGroupeSanguin(String groupeSanguin);
}
