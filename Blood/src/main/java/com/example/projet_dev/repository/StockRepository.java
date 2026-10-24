package com.example.projet_dev.repository;

import com.example.projet_dev.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockRepository extends JpaRepository<Stock, Long> {
    List<Stock> findByCentreDeDonId(Long centreDeDonId);
    List<Stock> findByGroupeSanguin(String groupeSanguin);
}
