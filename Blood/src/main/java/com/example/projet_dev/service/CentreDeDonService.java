package com.example.projet_dev.service;


import com.example.projet_dev.entity.CentreDeDon;
import com.example.projet_dev.repository.CentreDeDonRepository;
import com.example.projet_dev.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CentreDeDonService {

    private final CentreDeDonRepository repository;
    private final UtilisateurRepository utilisateurRepository;

    public List<CentreDeDon> findAll() {
        return repository.findAll();
    }

    public List<CentreDeDon> findByVille(String ville) {
        return repository.findByVilleIgnoreCase(ville);
    }

    public CentreDeDon findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Centre de don introuvable"));
    }

    public CentreDeDon create(CentreDeDon centre) {
        if (utilisateurRepository.existsByEmail(centre.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email déjà utilisé");
        }
        centre.setId(null);
        return repository.save(centre);
    }

    public CentreDeDon update(Long id, CentreDeDon data) {
        CentreDeDon c = findById(id);
        c.setNom(data.getNom());
        c.setEmail(data.getEmail());
        if (data.getMotDePasse() != null) c.setMotDePasse(data.getMotDePasse());
        c.setAdresse(data.getAdresse());
        c.setVille(data.getVille());
        c.setLatitude(data.getLatitude());
        c.setLongitude(data.getLongitude());
        c.setCapaciteStock(data.getCapaciteStock());
        c.setHoraires(data.getHoraires());
        return repository.save(c);
    }

    public void delete(Long id) {
        repository.delete(findById(id));
    }
}
