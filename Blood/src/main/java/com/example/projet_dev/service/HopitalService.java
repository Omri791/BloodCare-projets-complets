package com.example.projet_dev.service;


import com.example.projet_dev.entity.Hopital;
import com.example.projet_dev.repository.HopitalRepository;
import com.example.projet_dev.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HopitalService {

    private final HopitalRepository repository;
    private final UtilisateurRepository utilisateurRepository;

    public List<Hopital> findAll() {
        return repository.findAll();
    }

    public List<Hopital> findByVille(String ville) {
        return repository.findByVilleIgnoreCase(ville);
    }

    public Hopital findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hôpital introuvable"));
    }

    public Hopital create(Hopital hopital) {
        if (utilisateurRepository.existsByEmail(hopital.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email déjà utilisé");
        }
        hopital.setId(null);
        return repository.save(hopital);
    }

    public Hopital update(Long id, Hopital data) {
        Hopital h = findById(id);
        h.setNom(data.getNom());
        h.setEmail(data.getEmail());
        if (data.getMotDePasse() != null) h.setMotDePasse(data.getMotDePasse());
        h.setAdresse(data.getAdresse());
        h.setVille(data.getVille());
        h.setLatitude(data.getLatitude());
        h.setLongitude(data.getLongitude());
        h.setType(data.getType());
        h.setCapaciteLits(data.getCapaciteLits());
        return repository.save(h);
    }

    public void delete(Long id) {
        repository.delete(findById(id));
    }
}
