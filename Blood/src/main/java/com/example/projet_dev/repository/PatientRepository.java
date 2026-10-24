package com.example.projet_dev.repository;

import com.example.projet_dev.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    List<Patient> findByHopitalId(Long hopitalId);
}
