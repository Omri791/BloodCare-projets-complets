package com.example.projet_dev.controller;



import com.example.projet_dev.entity.AppelDon;
import com.example.projet_dev.entity.CentreDeDon;
import com.example.projet_dev.entity.Creneau;
import com.example.projet_dev.entity.DemandeDeSang;
import com.example.projet_dev.entity.Don;
import com.example.projet_dev.entity.Prediction;
import com.example.projet_dev.entity.RendezVous;
import com.example.projet_dev.entity.Stock;
import com.example.projet_dev.service.AppelDonService;
import com.example.projet_dev.service.CentreDeDonService;
import com.example.projet_dev.service.CreneauService;
import com.example.projet_dev.service.DemandeDeSangService;
import com.example.projet_dev.service.PredictionService;
import com.example.projet_dev.service.RendezVousService;
import com.example.projet_dev.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/centres")
@RequiredArgsConstructor
public class CentreDeDonController {

    private final CentreDeDonService service;
    private final StockService stockService;
    private final PredictionService predictionService;
    private final DemandeDeSangService demandeService;
    private final RendezVousService rendezVousService;
    private final CreneauService creneauService;
    private final AppelDonService appelService;

    /** body de repondreDemande : {"statut": "ACCEPTEE", "unitesFournies": 3} */
    public record ReponseDemande(String statut, Integer unitesFournies) {}

    /** body de organiserDon : {"donneurEmail": "donneur@bloodcare.tn", "date": "2026-11-20T09:30"} */
    public record NouveauRendezVous(String donneurEmail, LocalDateTime date) {}

    /** body d'enregistrement d'un don : {"dateDon": "2026-11-20", "quantite": 450, "groupeSanguin": "O+"} */
    public record NouveauDon(LocalDate dateDon, Integer quantite, String groupeSanguin) {}

    /** body d'un créneau : {"date": "2026-11-20T09:30", "capacite": 4} */
    public record NouveauCreneau(LocalDateTime date, Integer capacite) {}

    /** body d'un appel : {"groupeSanguin": "O-", "message": "...", "donneurIds": [3]} (donneurIds vide = tous les disponibles) */
    public record NouvelAppel(String groupeSanguin, String message, List<Long> donneurIds) {}

    @GetMapping
    public List<CentreDeDon> getAll(@RequestParam(required = false) String ville) {
        return ville == null ? service.findAll() : service.findByVille(ville);
    }

    @GetMapping("/{id}")
    public CentreDeDon getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CentreDeDon create(@RequestBody CentreDeDon centre) {
        return service.create(centre);
    }

    @PutMapping("/{id}")
    public CentreDeDon update(@PathVariable Long id, @RequestBody CentreDeDon centre) {
        return service.update(id, centre);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ----- relations -----

    /** CentreDeDon.gererStock() */
    @GetMapping("/{id}/stocks")
    @PreAuthorize("@access.selfOrAdmin(#id)")
    public List<Stock> getStocks(@PathVariable Long id) {
        return stockService.findByCentre(id);
    }

    /** CentreDeDon.gererStock() — body: {"groupeSanguin": "O-", "quantiteDisponible": 12} */
    @PutMapping("/{id}/stocks")
    @PreAuthorize("@access.selfOrAdmin(#id)")
    public Stock definirStock(@PathVariable Long id, @RequestBody Stock body) {
        return stockService.definirQuantite(id, body.getGroupeSanguin(), body.getQuantiteDisponible());
    }

    /** CentreDeDon consulte Prediction */
    @GetMapping("/{id}/predictions")
    public List<Prediction> getPredictions(@PathVariable Long id) {
        return predictionService.findByCentre(id);
    }

    /** CentreDeDon traite DemandeDeSang : ses demandes + les nouvelles demandes en attente */
    @GetMapping("/{id}/demandes")
    @PreAuthorize("@access.selfOrAdmin(#id)")
    public List<DemandeDeSang> getDemandes(@PathVariable Long id) {
        return demandeService.findPourCentre(id);
    }

    /** CentreDeDon.repondreDemande() — body: {"statut": "ACCEPTEE" | "REFUSEE", "unitesFournies": 3} */
    @PatchMapping("/{id}/demandes/{demandeId}")
    @PreAuthorize("@access.selfOrAdmin(#id)")
    public DemandeDeSang repondreDemande(@PathVariable Long id, @PathVariable Long demandeId,
                                         @RequestBody ReponseDemande body) {
        return demandeService.repondre(id, demandeId, body.statut(), body.unitesFournies());
    }

    /** CentreDeDon organise RendezVous */
    @GetMapping("/{id}/rendez-vous")
    @PreAuthorize("@access.selfOrAdmin(#id)")
    public List<RendezVous> getRendezVous(@PathVariable Long id) {
        return rendezVousService.findByCentre(id);
    }

    /** CentreDeDon.organiserDon() */
    @PostMapping("/{id}/rendez-vous")
    @PreAuthorize("@access.selfOrAdmin(#id)")
    @ResponseStatus(HttpStatus.CREATED)
    public RendezVous organiserDon(@PathVariable Long id, @RequestBody NouveauRendezVous body) {
        return rendezVousService.organiser(id, body.donneurEmail(), body.date());
    }

    /** body: {"statut": "CONFIRME" | "ANNULE" | "ABSENT"} */
    @PatchMapping("/{id}/rendez-vous/{rdvId}")
    @PreAuthorize("@access.selfOrAdmin(#id)")
    public RendezVous changerStatutRendezVous(@PathVariable Long id, @PathVariable Long rdvId,
                                              @RequestBody Map<String, String> body) {
        return rendezVousService.changerStatutParCentre(id, rdvId, body.get("statut"));
    }

    /** RendezVous aboutit à Don */
    @PostMapping("/{id}/rendez-vous/{rdvId}/don")
    @PreAuthorize("@access.selfOrAdmin(#id)")
    @ResponseStatus(HttpStatus.CREATED)
    public RendezVous enregistrerDon(@PathVariable Long id, @PathVariable Long rdvId,
                                     @RequestBody NouveauDon body) {
        Don don = new Don();
        don.setDateDon(body.dateDon());
        don.setQuantite(body.quantite());
        return rendezVousService.enregistrerDonParCentre(id, rdvId, don, body.groupeSanguin());
    }

    /** Créneaux de collecte du centre (avec places restantes) */
    @GetMapping("/{id}/creneaux")
    @PreAuthorize("@access.selfOrAdmin(#id)")
    public List<Creneau> getCreneaux(@PathVariable Long id) {
        return creneauService.findByCentre(id);
    }

    @PostMapping("/{id}/creneaux")
    @PreAuthorize("@access.selfOrAdmin(#id)")
    @ResponseStatus(HttpStatus.CREATED)
    public Creneau ajouterCreneau(@PathVariable Long id, @RequestBody NouveauCreneau body) {
        return creneauService.create(id, body.date(), body.capacite());
    }

    @DeleteMapping("/{id}/creneaux/{creneauId}")
    @PreAuthorize("@access.selfOrAdmin(#id)")
    public ResponseEntity<Void> supprimerCreneau(@PathVariable Long id, @PathVariable Long creneauId) {
        creneauService.delete(id, creneauId);
        return ResponseEntity.noContent().build();
    }

    /** Appels envoyés par le centre */
    @GetMapping("/{id}/appels")
    @PreAuthorize("@access.selfOrAdmin(#id)")
    public List<AppelDon> getAppels(@PathVariable Long id) {
        return appelService.findByCentre(id);
    }

    /** CentreDeDon.lancerAppelDonneurs() — renvoie {"donneursAppeles": 5} */
    @PostMapping("/{id}/appels")
    @PreAuthorize("@access.selfOrAdmin(#id)")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Integer> lancerAppel(@PathVariable Long id, @RequestBody NouvelAppel body) {
        return Map.of("donneursAppeles", appelService.lancer(id, body.groupeSanguin(), body.message(), body.donneurIds()));
    }
}
