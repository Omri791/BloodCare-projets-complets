package com.example.projet_dev.controller;

import com.example.projet_dev.entity.Don;
import com.example.projet_dev.entity.RendezVous;
import com.example.projet_dev.service.RendezVousService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rendez-vous")
@RequiredArgsConstructor
public class RendezVousController {

    private final RendezVousService service;

    @GetMapping
    public List<RendezVous> getAll(@RequestParam(required = false) String statut) {
        return statut == null ? service.findAll() : service.findByStatut(statut);
    }

    @GetMapping("/{id}")
    public RendezVous getById(@PathVariable Long id) {
        return service.findById(id);
    }

    /** CentreDeDon organise RendezVous */
    @GetMapping("/centre/{centreId}")
    public List<RendezVous> getByCentre(@PathVariable Long centreId) {
        return service.findByCentre(centreId);
    }

    // creation: POST /api/donneurs/{id}/rendez-vous

    @PutMapping("/{id}")
    public RendezVous update(@PathVariable Long id, @RequestBody RendezVous rdv) {
        return service.update(id, rdv);
    }

    /** body: {"statut": "ANNULE"} */
    @PatchMapping("/{id}/statut")
    public RendezVous changerStatut(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return service.changerStatut(id, body.get("statut"));
    }

    /** RendezVous aboutit à Don. body: {"dateDon": "2026-11-20", "quantite": 450} */
    @PostMapping("/{id}/don")
    @ResponseStatus(HttpStatus.CREATED)
    public RendezVous enregistrerDon(@PathVariable Long id, @RequestBody Don don) {
        return service.enregistrerDon(id, don);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}