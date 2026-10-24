package com.example.projet_dev.service;

import com.example.projet_dev.entity.Don;
import com.example.projet_dev.entity.Donneur;
import com.example.projet_dev.repository.DonneurRepository;
import com.example.projet_dev.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DonneurService {

    private final DonneurRepository repository;
    private final UtilisateurRepository utilisateurRepository;

    public List<Donneur> findAll() {
        return repository.findAll();
    }

    public List<Donneur> findByVille(String ville) {
        return repository.findByVilleIgnoreCase(ville);
    }

    public List<Donneur> findByGroupeSanguin(String groupeSanguin) {
        return repository.findByGroupeSanguin(groupeSanguin);
    }

    public List<Donneur> findDisponibles() {
        return repository.findByDisponibiliteTrue();
    }

    public Donneur findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Donneur introuvable"));
    }

    public Donneur create(Donneur donneur) {
        if (utilisateurRepository.existsByEmail(donneur.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email déjà utilisé");
        }
        donneur.setId(null);
        if (donneur.getDisponibilite() == null) donneur.setDisponibilite(true);
        if (donneur.getTotalDons() == null) donneur.setTotalDons(0);
        return repository.save(donneur);
    }

    public Donneur update(Long id, Donneur data) {
        Donneur d = findById(id);
        d.setNom(data.getNom());
        d.setEmail(data.getEmail());
        if (data.getMotDePasse() != null) d.setMotDePasse(data.getMotDePasse());
        d.setDateNaissance(data.getDateNaissance());
        d.setSexe(data.getSexe());
        d.setGroupeSanguin(data.getGroupeSanguin());
        d.setTelephone(data.getTelephone());
        d.setVille(data.getVille());
        d.setLatitude(data.getLatitude());
        d.setLongitude(data.getLongitude());
        d.setPoids(data.getPoids());
        d.setHemoglobine(data.getHemoglobine());
        if (data.getDisponibilite() != null) d.setDisponibilite(data.getDisponibilite());
        return repository.save(d);
    }

    public void delete(Long id) {
        repository.delete(findById(id));
    }

    /** Donneur.verifierEligibilite() */
    public boolean verifierEligibilite(Long id) {
        Donneur d = findById(id);

        if (d.getDateNaissance() == null || d.getPoids() == null
                || d.getHemoglobine() == null || d.getSexe() == null) {
            return false;
        }

        int age = Period.between(d.getDateNaissance(), LocalDate.now()).getYears();
        if (age < 18 || age > 65) return false;
        if (d.getPoids() < 50) return false;

        boolean homme = "M".equalsIgnoreCase(d.getSexe());
        if (d.getHemoglobine() < (homme ? 13f : 12f)) return false;

        if (d.getDernierDon() != null) {
            long jours = ChronoUnit.DAYS.between(d.getDernierDon(), LocalDate.now());
            if (jours < (homme ? 90 : 120)) return false;
        }
        return true;
    }

    /** Donneur.consulterHistorique() */
    public List<Don> consulterHistorique(Long id) {
        return findById(id).getRendezVous().stream()
                .map(r -> r.getDon())
                .filter(Objects::nonNull)
                .toList();
    }
}