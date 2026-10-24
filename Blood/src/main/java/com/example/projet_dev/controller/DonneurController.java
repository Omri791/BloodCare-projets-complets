package com.example.projet_dev.controller;

import com.example.projet_dev.entity.Don;
import com.example.projet_dev.entity.Donneur;
import com.example.projet_dev.entity.RendezVous;
import com.example.projet_dev.service.DonneurService;
import com.example.projet_dev.service.RendezVousService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/donneurs")
@RequiredArgsConstructor
public class DonneurController {

    private final DonneurService service;
    private final RendezVousService rendezVousService;

    @GetMapping
    public List<Donneur> getAll(@RequestParam(required = false) String ville,
                                @RequestParam(required = false) String groupeSanguin) {
        if (ville != null) return service.findByVille(ville);
        if (groupeSanguin != null) return service.findByGroupeSanguin(groupeSanguin);
        return service.findAll();
    }

    @GetMapping("/disponibles")
    public List<Donneur> getDisponibles() {
        return service.findDisponibles();
    }

    @GetMapping("/{id}")
    public Donneur getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Donneur create(@RequestBody Donneur donneur) {
        return service.create(donneur);
    }

    @PutMapping("/{id}")
    public Donneur update(@PathVariable Long id, @RequestBody Donneur donneur) {
        return service.update(id, donneur);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ----- relations -----

    /** Donneur.verifierEligibilite() */
    @GetMapping("/{id}/eligibilite")
    public Map<String, Boolean> verifierEligibilite(@PathVariable Long id) {
        return Map.of("eligible", service.verifierEligibilite(id));
    }

    /** Donneur.consulterHistorique() */
    @GetMapping("/{id}/historique")
    public List<Don> getHistorique(@PathVariable Long id) {
        return service.consulterHistorique(id);
    }

    /** Donneur prend RendezVous */
    @GetMapping("/{id}/rendez-vous")
    public List<RendezVous> getRendezVous(@PathVariable Long id) {
        return rendezVousService.findByDonneur(id);
    }

    /** Donneur.prendreRendezVous() */
    @PostMapping("/{id}/rendez-vous")
    @ResponseStatus(HttpStatus.CREATED)
    public RendezVous prendreRendezVous(@PathVariable Long id, @RequestBody RendezVous rdv) {
        return rendezVousService.prendre(id, rdv);
    }
}