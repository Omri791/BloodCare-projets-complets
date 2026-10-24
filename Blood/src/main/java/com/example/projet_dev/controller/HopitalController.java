package com.example.projet_dev.controller;



import com.example.projet_dev.entity.DemandeDeSang;
import com.example.projet_dev.entity.Hopital;
import com.example.projet_dev.entity.Patient;
import com.example.projet_dev.service.DemandeDeSangService;
import com.example.projet_dev.service.HopitalService;
import com.example.projet_dev.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hopitaux")
@RequiredArgsConstructor
public class HopitalController {

    private final HopitalService service;
    private final PatientService patientService;
    private final DemandeDeSangService demandeService;

    @GetMapping
    public List<Hopital> getAll(@RequestParam(required = false) String ville) {
        return ville == null ? service.findAll() : service.findByVille(ville);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@access.selfOrAdmin(#id)")
    public Hopital getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Hopital create(@RequestBody Hopital hopital) {
        return service.create(hopital);
    }

    @PutMapping("/{id}")
    public Hopital update(@PathVariable Long id, @RequestBody Hopital hopital) {
        return service.update(id, hopital);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ----- relations -----

    /** Hopital gere Patient */
    @GetMapping("/{id}/patients")
    @PreAuthorize("@access.selfOrAdmin(#id)")
    public List<Patient> getPatients(@PathVariable Long id) {
        return patientService.findByHopital(id);
    }

    /** Hopital.suivreDemande() */
    @GetMapping("/{id}/demandes")
    @PreAuthorize("@access.selfOrAdmin(#id)")
    public List<DemandeDeSang> getDemandes(@PathVariable Long id) {
        return demandeService.findByHopital(id);
    }

    /** Hopital.envoyerDemande() */
    @PostMapping("/{id}/demandes")
    @PreAuthorize("@access.selfOrAdmin(#id)")
    @ResponseStatus(HttpStatus.CREATED)
    public DemandeDeSang envoyerDemande(@PathVariable Long id, @RequestBody DemandeDeSang demande) {
        return demandeService.envoyer(id, demande);
    }
}
