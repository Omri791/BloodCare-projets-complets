package com.example.projet_dev.controller;

import com.example.projet_dev.entity.AppelDon;
import com.example.projet_dev.entity.Creneau;
import com.example.projet_dev.entity.Don;
import com.example.projet_dev.entity.Donneur;
import com.example.projet_dev.entity.RendezVous;
import com.example.projet_dev.service.AppelDonService;
import com.example.projet_dev.service.DonneurService;
import com.example.projet_dev.service.RendezVousService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/donneurs")
@RequiredArgsConstructor
public class DonneurController {

    private final DonneurService service;
    private final RendezVousService rendezVousService;
    private final AppelDonService appelService;

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

    /** Le donneur complète son profil (infos nécessaires à l'éligibilité, disponibilité) */
    @PutMapping("/{id}/profil")
    @PreAuthorize("@access.selfOrAdmin(#id)")
    public Donneur updateProfil(@PathVariable Long id, @RequestBody Donneur donneur) {
        return service.updateProfil(id, donneur);
    }

    /** Donneur.verifierEligibilite() — {"eligible": false, "raison": "..."} */
    @GetMapping("/{id}/eligibilite")
    @PreAuthorize("@access.selfOrAdmin(#id) or hasRole('CENTRE_DON')")
    public Map<String, Object> verifierEligibilite(@PathVariable Long id) {
        String raison = service.raisonNonEligible(id);
        Map<String, Object> body = new HashMap<>();
        body.put("eligible", raison == null);
        body.put("raison", raison);
        return body;
    }

    /** Donneur.consulterHistorique() */
    @GetMapping("/{id}/historique")
    @PreAuthorize("@access.selfOrAdmin(#id) or hasRole('CENTRE_DON')")
    public List<Don> getHistorique(@PathVariable Long id) {
        return service.consulterHistorique(id);
    }

    /** Donneur prend RendezVous */
    @GetMapping("/{id}/rendez-vous")
    @PreAuthorize("@access.selfOrAdmin(#id) or hasRole('CENTRE_DON')")
    public List<RendezVous> getRendezVous(@PathVariable Long id) {
        return rendezVousService.findByDonneur(id);
    }

    /** Donneur.prendreRendezVous() — body: {"date": "2026-11-20T09:30", "centreDeDon": {"id": 1}} */
    @PostMapping("/{id}/rendez-vous")
    @PreAuthorize("@access.selfOrAdmin(#id)")
    @ResponseStatus(HttpStatus.CREATED)
    public RendezVous prendreRendezVous(@PathVariable Long id, @RequestBody RendezVous rdv) {
        return rendezVousService.prendre(id, rdv);
    }

    /** Recevoir appel du centre : appels en attente */
    @GetMapping("/{id}/appels")
    @PreAuthorize("@access.selfOrAdmin(#id)")
    public List<AppelDon> getAppels(@PathVariable Long id) {
        return appelService.findEnAttente(id);
    }

    /** Créneaux libres proposés par le centre de l'appel */
    @GetMapping("/{id}/appels/{appelId}/creneaux")
    @PreAuthorize("@access.selfOrAdmin(#id)")
    public List<Creneau> getCreneauxAppel(@PathVariable Long id, @PathVariable Long appelId) {
        return appelService.creneauxPourAppel(id, appelId);
    }

    /** Accepter l'appel en choisissant un créneau — body: {"creneauId": 4} */
    @PostMapping("/{id}/appels/{appelId}/accepter")
    @PreAuthorize("@access.selfOrAdmin(#id)")
    @ResponseStatus(HttpStatus.CREATED)
    public RendezVous accepterAppel(@PathVariable Long id, @PathVariable Long appelId, @RequestBody Map<String, Long> body) {
        return appelService.accepter(id, appelId, body.get("creneauId"));
    }

    @PostMapping("/{id}/appels/{appelId}/refuser")
    @PreAuthorize("@access.selfOrAdmin(#id)")
    public AppelDon refuserAppel(@PathVariable Long id, @PathVariable Long appelId) {
        return appelService.refuser(id, appelId);
    }

    /** Le donneur annule un de ses rendez-vous */
    @PatchMapping("/{id}/rendez-vous/{rdvId}/annuler")
    @PreAuthorize("@access.selfOrAdmin(#id)")
    public RendezVous annulerRendezVous(@PathVariable Long id, @PathVariable Long rdvId) {
        return rendezVousService.annulerParDonneur(id, rdvId);
    }
}