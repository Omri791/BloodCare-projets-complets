package com.example.projet_dev.service;


import com.example.projet_dev.entity.Hopital;
import com.example.projet_dev.entity.Patient;
import com.example.projet_dev.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository repository;
    private final HopitalService hopitalService;

    public List<Patient> findAll() {
        return repository.findAll();
    }

    public List<Patient> findByHopital(Long hopitalId) {
        return repository.findByHopitalId(hopitalId);
    }

    public Patient findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient introuvable"));
    }

    /** Body may contain "hopital": {"id": 1} */
    public Patient create(Patient patient) {
        patient.setId(null);
        patient.setHopital(resolveHopital(patient.getHopital()));
        return repository.save(patient);
    }

    public Patient update(Long id, Patient data) {
        Patient p = findById(id);
        p.setNom(data.getNom());
        p.setGroupeSanguin(data.getGroupeSanguin());
        if (data.getHopital() != null) p.setHopital(resolveHopital(data.getHopital()));
        return repository.save(p);
    }

    public void delete(Long id) {
        repository.delete(findById(id));
    }

    private Hopital resolveHopital(Hopital h) {
        if (h == null || h.getId() == null) return null;
        return hopitalService.findById(h.getId());
    }
}
